package com.tnob.mapper;

import java.util.*;

/**
 * Created by tahmid on 2/25/15.
 */
public class QueryGenerator {
    /**
     * example insert query:
     * create (java:Language{name:\"Java\", type:\"procedural OOP\"}) return java
     */


    public static String generateInsertQuery(Map.Entry<String, Map<String, String>> nodeAttributes) {
        StringBuilder queryBuilder = new StringBuilder("CREATE (node:");

        String resourceURI = nodeAttributes.getKey();
        Map<String, String> attributesMap = nodeAttributes.getValue();

        String nodeType = attributesMap.remove(RDFConstants.TYPE);
        if (nodeType != null) {
            //String label = attributesMap.remove(RDFConstants.LABEL);

            queryBuilder.append(nodeType)
                    .append("{")
                    .append(RDFConstants.URI)
                    .append(":")
                    .append(addQuotes(resourceURI));

            if (attributesMap.isEmpty()) {
                queryBuilder.append("}");
            } else {
                for (String attributeName : attributesMap.keySet()) {
                    queryBuilder.append(" , ")
                            .append(attributeName)
                            .append(":")
                            .append(addQuotes(removeSpecialSubStrings(attributesMap.get(attributeName))));
                }

                queryBuilder.append("}");
            }


            queryBuilder.append(") return node");
            return queryBuilder.toString();
        } else
            return "";
    }

    private static String addQuotes(String attributeValue) {
        if (attributeValue.contains("\"")) {
            attributeValue = attributeValue.replace("\"", "\\\"");
        }
        return "\"" + attributeValue + "\"";
    }

    private static String addTicks(String relationshipName) {
        return "`" + relationshipName + "`";
    }

    private static String removeSpecialSubStrings(String attributeValue) {
        String[] specialSubStrings = {"http://", "https://"};
        for (String specialSubString : specialSubStrings) {
            if (attributeValue.contains(specialSubString)) {
                attributeValue = attributeValue.replaceAll(specialSubString, "");
            }
        }
        return attributeValue;
    }

    public static List<String> generateInsertQueries(Map<String, Map<String, String>> nodeAttributeMap) {
        List <String> insertQueries = new ArrayList<>();
        for (Map.Entry<String, Map<String, String>> nodeAttributeMapEntry : nodeAttributeMap.entrySet()) {
            String insertQuery = generateInsertQuery(nodeAttributeMapEntry);
            insertQueries.add(insertQuery);
        }
        return insertQueries;
    }

    /**
     * match (node1:film{URI:"http://data.linkedmdb.org/resource/film/2"}), (node2{URI:"http://data.linkedmdb.org/resource/film_cut/734"}) create (node1)-[r:film_cut]->(node2) return r;
     * Match (node1:film{URI:"http://data.linkedmdb.org/resource/film/2399"}) set node1.`http://data.linkedmdb.org/resource/movie/initial_release_date` = "1991-09-11"  return node1
     *
     * @param relations
     * @return
     */

    public static List<String> generateQueryForRelationAndAttributes(Map.Entry<String, Map<String, String>> relations,
                                                                     Map<String, String> attributesMap) {

        String resourceURI = relations.getKey();
        Map<String, String> relationsMap = relations.getValue();


        String nodeType = relationsMap.remove(RDFConstants.TYPE);
        if (nodeType != null) {
            List<String> updateQueries = new ArrayList<>();
            updateQueries.add(generateMergeQuery(resourceURI, nodeType));
            for (String relationName : relationsMap.keySet()) {
                String objectResourceURI = relationsMap.get(relationName);
                updateQueries.add(generateMergeQuery(objectResourceURI, extractObjectNodeType(objectResourceURI)));
                updateQueries.add(generateRelationQuery(resourceURI, nodeType, relationName, objectResourceURI));
            }

            StringBuilder queryBuilder = new StringBuilder("Match (node1:");

            //generate portion for finding source node (i.e. the node from which the edge goes out)
            queryBuilder.append(nodeType)
                    .append("{")
                    .append(RDFConstants.URI)
                    .append(":")
                    .append(addQuotes(resourceURI))
                    .append("})")
                    .append(" set ");

            if (attributesMap != null) {

                for (String attributeName : attributesMap.keySet()) {

                    queryBuilder.append("node1.")
                            .append(addTicks(attributeName))
                            .append(" = ")
                            .append(addQuotes(removeSpecialSubStrings(attributesMap.get(attributeName))))
                            .append(" , ");
                }
                String attributeUpdateQuery = queryBuilder.toString();
                attributeUpdateQuery = attributeUpdateQuery.substring(0, attributeUpdateQuery.length() - 2);


                updateQueries.add(attributeUpdateQuery);
            }
            return updateQueries;

        } else
            return Collections.EMPTY_LIST;
    }

    private static String generateRelationQuery(String resourceURI, String nodeType, String relationName, String objectResourceURI) {
        StringBuilder queryBuilder = new StringBuilder("Match (node1:");

        //generate portion for finding source node (i.e. the node from which the edge goes out)
        queryBuilder.append(nodeType)
                .append("{")
                .append(RDFConstants.URI)
                .append(":")
                .append(addQuotes(resourceURI))
                .append("})");
        //generate portion for finding sink node (i.e. the node in which the edge goes into)
        queryBuilder.append(", (node2{")
                .append(RDFConstants.URI)
                .append(":")
                .append(addQuotes(objectResourceURI))
                .append("})");
        //generate the actual relation creation query
        queryBuilder.append(" create (node1)-[r:")
                .append(addTicks(relationName))
                .append("]->(node2)")
                .append(" return r;");
        return queryBuilder.toString();
    }

    private static String generateMergeQuery(String resourceURI, String nodeType) {
        StringBuilder queryBuilder = new StringBuilder("Merge (node1");

        if(!nodeType.contains(".")) {
            queryBuilder.append(":");
            if (nodeType.contains("-")) {
                queryBuilder.append(addTicks(nodeType));
            } else {
                queryBuilder.append(nodeType);
            }
        }

        queryBuilder.append("{")
                .append(RDFConstants.URI)
                .append(":")
                .append(addQuotes(resourceURI))
                .append("}) return node1;");

        return queryBuilder.toString();
    }

    public static List<String> generateQueriesForRelation(Map<String, Map<String, String>> nodeRelationMap, Map<String, Map<String, String>> nodeAttributeMap) {
        List <String> relationAdditionQueryList = new ArrayList<>();
        for (Map.Entry<String, Map<String, String>> nodeRelationMapEntry : nodeRelationMap.entrySet()) {
            Map<String, String> attributesMap = nodeAttributeMap.get(nodeRelationMapEntry.getKey());
            relationAdditionQueryList.addAll(generateQueryForRelationAndAttributes(nodeRelationMapEntry, attributesMap));
        }
        return relationAdditionQueryList;
    }

    private static String extractObjectNodeType(String objectResourceURI) {
        String[] splitObjectResourceURI = objectResourceURI.split("/");
        if (splitObjectResourceURI.length > 1) {
            String objectNodeType = splitObjectResourceURI[splitObjectResourceURI.length - 2];
            return objectNodeType;
        } else {
            return splitObjectResourceURI[0];
        }
    }


}
