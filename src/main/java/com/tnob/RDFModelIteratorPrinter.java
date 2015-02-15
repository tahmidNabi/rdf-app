package com.tnob;

import com.hp.hpl.jena.rdf.model.*;

/**
 * Created by tahmid on 2/14/15.
 */
public class RDFModelIteratorPrinter extends RDFModelIterator {

    public RDFModelIteratorPrinter(Model model) {
        super(model);
    }

    @Override
    protected void doInIterator(Statement stmt) {
        Resource subject = stmt.getSubject();
        Property predicate = stmt.getPredicate();
        RDFNode object = stmt.getObject();


        System.out.println("Subject: " + subject.toString());
        System.out.println("Predicate: " + predicate.toString());
        if (object instanceof Resource) {
            System.out.println("Resource Object: " + object.toString());
        } else {
            System.out.println("Literal Object: " + "\"" + object.toString() + "\"");
        }
        System.out.println("\n");
    }
}
