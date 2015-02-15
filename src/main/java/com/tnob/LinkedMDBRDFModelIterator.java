package com.tnob;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

/**
 * Created by tahmid on 2/14/15.
 */
public class LinkedMDBRDFModelIterator extends RDFModelIterator {
    public LinkedMDBRDFModelIterator(Model model) {
        super(model);
    }

    @Override
    protected void doInIterator(Statement stmt) {
        Resource subject = stmt.getSubject();
        RDFNode object = stmt.getObject();

        String resourceURL = subject.getURI();


        if (!(object instanceof Resource)) {
            String literalValue = object.toString();
            if (literalValue.contains("List of all instances: ")) {
                String fileName = literalValue.replace("List of all instances: ", "");
                System.out.println(resourceURL);
                System.out.println(fileName);

                DataFetcher dataFetcher = new DataFetcher();
                dataFetcher.fetchDataUsingWget(resourceURL);
                
            }
        }
    }
}
