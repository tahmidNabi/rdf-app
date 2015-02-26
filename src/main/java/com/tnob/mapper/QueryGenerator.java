package com.tnob.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        Map <String, String> attributesMap = nodeAttributes.getValue();

        String nodeType = attributesMap.remove(RDFConstants.TYPE);
        if (nodeType!= null) {
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

    private static String removeSpecialSubStrings(String attributeValue) {
        String[] specialSubStrings = {"http://", "https://"};
        for (String specialSubString : specialSubStrings) {
            if(attributeValue.contains(specialSubString)) {
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
}
