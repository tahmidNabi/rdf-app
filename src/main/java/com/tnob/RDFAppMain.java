package com.tnob;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.FileManager;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Hello world!
 */
public class RDFAppMain {

    public static final String inputRDFFile = "all";


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

        /*RDFModelIterator rdfModelIteratorPrinter = new RDFModelIteratorPrinter(model);
        rdfModelIteratorPrinter.iterateRDFModel();
        */

        RDFModelIterator rdfModelIteratorPrinter = new LinkedMDBRDFModelIterator(model);
        rdfModelIteratorPrinter.iterateRDFModel();
        //iterateRDFModel(model);


        //model.write(System.out, "N-TRIPLES");
    }

    private static void iterateRDFModel(Model model) {
        StmtIterator iterator = model.listStatements();
        Map<String, Integer> predicates = new LinkedHashMap<String, Integer>();
        Map<String, Integer> subjects = new LinkedHashMap<String, Integer>();
        Map<String, Set<String>> subjectPredicates = new LinkedHashMap<String, Set<String>>();

        while (iterator.hasNext()) {
            Statement stmt = iterator.nextStatement();
            //subjects.add(stmt.getSubject().toString());
            //predicates.add(stmt.getPredicate().toString());
            String subject = stmt.getSubject().toString();
            String predicate = stmt.getPredicate().toString();

            predicates.merge(predicate, 1, (oldValue, one) -> oldValue + one);
            subjects.merge(subject, 1, (oldValue, one) -> oldValue + one);

            if(subjectPredicates.containsKey(subject)) {
                Set<String> predicateList = subjectPredicates.get(subject) == null ? new LinkedHashSet<>() : subjectPredicates.get(subject);
                predicateList.add(predicate);
            } else {
                Set<String> predicateList = new LinkedHashSet<>();
                predicateList.add(predicate);
                subjectPredicates.put(subject, predicateList);
            }

        }
/*        for (String predicate : predicates) {
            System.out.println(predicate);
        }*/
        //subjects.forEach((k, v) -> System.out.println(k + "=" + v));
        predicates.forEach((k, v) -> System.out.println(k + "=" + v));

        for (String subject : subjectPredicates.keySet()) {
            System.out.println("Subject: " + subject);
            for (String predicate : subjectPredicates.get(subject)) {
                System.out.println("\t\t Predicate: " + predicate);
            }
            System.out.println("\n");
        }
    }


}
