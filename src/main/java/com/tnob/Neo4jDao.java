package com.tnob;

import org.neo4j.rest.graphdb.RestAPI;
import org.neo4j.rest.graphdb.RestAPIFacade;
import org.neo4j.rest.graphdb.query.QueryEngine;
import org.neo4j.rest.graphdb.query.RestCypherQueryEngine;
import org.neo4j.rest.graphdb.util.QueryResult;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by tahmid on 2/25/15.
 */
public class Neo4jDao {

    public static final String REST_URI = "http://localhost:7474/db/data";

    public static void batchInsert(List<String> insertQueries) {
        RestAPI graphDb = new RestAPIFacade(REST_URI);

        QueryEngine engine = new RestCypherQueryEngine(graphDb);

        for (String insertQuery: insertQueries) {
            if (!insertQuery.equals("")) {
                engine.query(insertQuery, Collections.EMPTY_MAP);
                System.out.println("Inserted");
            }
        }
        System.out.println("Inserted " + insertQueries.size() + "nodes");
        graphDb.close();
        System.out.println("Closed");
    }
}
