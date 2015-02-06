package com.tnob;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.FileManager;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Hello world!
 */
public class RDFAppMain {

    public static final String inputRDFFile = "imdb.rdf";


    public static void main(String[] args) {
        String personURI = "http://somewhere/JohnSmith";
        String givenName = "John";
        String familyName = "Smith";
        String fullName = givenName + " " + familyName;

        Model model = ModelFactory.createDefaultModel();

        InputStream in1 = FileManager.get().open(inputRDFFile);
        if (in1 == null) {
            throw new IllegalArgumentException("File: " + inputRDFFile + " not found");
        }

/*        Resource johnSmith = model.createResource(personURI)
                .addProperty(VCARD.FN, fullName)
                .addProperty(VCARD.N,
                        model.createResource()
                .addProperty(VCARD.Given, givenName)
                .addProperty(VCARD.Family, familyName));*/

        model.read(new InputStreamReader(in1), "");
        iterateRDFModel(model);


        //model.write(System.out, "N-TRIPLES");
    }

    private static void iterateRDFModel(Model model) {
        StmtIterator iterator = model.listStatements();
        Map<String, Integer> predicates = new LinkedHashMap<String, Integer>();
        Map<String, Integer> subjects = new LinkedHashMap<String, Integer>();

        while (iterator.hasNext()) {
            Statement stmt = iterator.nextStatement();
            //subjects.add(stmt.getSubject().toString());
            //predicates.add(stmt.getPredicate().toString());
            String subject = stmt.getSubject().toString();
            String predicate = stmt.getPredicate().toString();

            predicates.merge(predicate, 1, (oldValue, one) -> oldValue + one);
            subjects.merge(subject, 1, (oldValue, one) -> oldValue + one);

            printRDFStatement(stmt);
        }
/*        for (String predicate : predicates) {
            System.out.println(predicate);
        }*/
        //subjects.forEach((k, v) -> System.out.println(k + "=" + v));
    }

    private static void printRDFStatement(Statement stmt) {
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
