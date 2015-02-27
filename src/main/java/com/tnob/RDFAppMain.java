package com.tnob;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;
import com.tnob.mapper.QueryGenerator;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Hello world!
 */
public class RDFAppMain {

    public static final String prefix = "http://data.linkedmdb.org/resource/movie";
    public static final String QUERY_FOLDER = "generated-queries/";
    public static final String INPUT_FOLDER = "linkedmdb-root/";


    public static void main(String[] args) {
        List<String> rdfFiles = new ArrayList<>();

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(INPUT_FOLDER))) {
            for (Path path : directoryStream) {
                rdfFiles.add(path.getFileName().toString());
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        long startTime = System.currentTimeMillis();

        rdfFiles.forEach(e -> {
            System.out.println(e);
        });

        for (String inputRDFFile : rdfFiles) {

            Model model = ModelFactory.createDefaultModel();

            InputStream in1 = FileManager.get().open(INPUT_FOLDER + inputRDFFile);
            if (in1 == null) {
                throw new IllegalArgumentException("File: " + inputRDFFile + " not found");
            }

            model.read(new InputStreamReader(in1), "");

        /*RDFModelIterator rdfModelIteratorPrinter = new RDFModelIteratorPrinter(model);
        rdfModelIteratorPrinter.iterateRDFModel();

        /*RDFModelIterator rdfModelIteratorPrinter = new LinkedMDBRDFResourceFetcher(model);
        rdfModelIteratorPrinter.iterateRDFModel();*/

            RDFNodeMapper rdfModelIterator = new RDFNodeMapper(model);
            rdfModelIterator.iterateRDFModel();
            //rdfModelIterator.printNode();
            //iterateRDFModel(model);
            //rdfModelIterator.printNode();

            Map<String, Map<String, String>> nodeAttributeMap = rdfModelIterator.getNodeAttributeMap();

            List<String> insertQueries = QueryGenerator.generateInsertQueries(nodeAttributeMap);
            writeQueriesToFile(insertQueries, inputRDFFile);
            Neo4jDao.batchInsert(insertQueries, inputRDFFile);
        }
        long endTime = System.currentTimeMillis();

        long timeForInsertion = (endTime - startTime)/1000;

        System.out.println("Migration took " + timeForInsertion + " seconds");
    }

    private static void writeQueriesToFile(List<String> insertQueries, String inputRDFFile) {
        Path queryFile = Paths.get(QUERY_FOLDER + inputRDFFile + "-cypher.txt");
        try {
            Files.write(queryFile, insertQueries, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

            if (subjectPredicates.containsKey(subject)) {
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
