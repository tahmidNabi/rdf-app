package com.tnob;

import com.hp.hpl.jena.rdf.model.*;

/**
 * Created by tahmid on 2/15/15.
 */
public class LinkedMDBRDFResourceFetcher extends RDFModelIterator {

    public LinkedMDBRDFResourceFetcher(Model model) {
        super(model);
    }

    @Override
    protected void doInIterator(Statement stmt) {
        Resource subject = stmt.getSubject();
        Property predicate = stmt.getPredicate();
        RDFNode object = stmt.getObject();

        String resourceURL = subject.getURI();
        /**
         * need to do this to fetch rdf xml instead of html data
         */
        resourceURL = resourceURL.replace("resource", "data");

        System.out.println(resourceURL);

        //System.out.println(fileName);

        if (object.isLiteral()) {
            DataFetcher dataFetcher = new DataFetcher();
            dataFetcher.fetchDataUsingWget(resourceURL);
        }

    }
}
