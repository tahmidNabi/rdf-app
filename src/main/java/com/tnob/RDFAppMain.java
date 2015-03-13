package com.tnob;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
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
    public static final String INPUT_FOLDER_ROOT = "linkedmdb-root/";
    public static final String INPUT_FOLDER_RELATIONS = "bams/";
    public static final int PARTITION = 10;
    public static final int PARTITION_UNIT = 10;


    public static void main(String[] args) {
        //migrateRootNodes(INPUT_FOLDER_ROOT);
        migrateRelations(INPUT_FOLDER_RELATIONS);
    }

    private static void migrateRootNodes(String inputFolder) {
        List<String> rdfFiles = new ArrayList<>();

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(inputFolder))) {
            for (Path path : directoryStream) {
                rdfFiles.add(path.getFileName().toString());
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        long startTime = System.currentTimeMillis();

        for (String inputRDFFile : rdfFiles) {

            Model model = ModelFactory.createDefaultModel();

            InputStream in1 = FileManager.get().open(inputFolder + inputRDFFile);
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
            int partitionSize =  (insertQueries.size()/PARTITION_UNIT) * PARTITION;
            //System.out.println(partitionSize);
            insertQueries = insertQueries.subList(0, partitionSize);

            Neo4jDao.batchInsert(insertQueries, inputRDFFile);
        }
        long endTime = System.currentTimeMillis();

        long timeForInsertion = (endTime - startTime)/1000;

        System.out.println("Migration took " + timeForInsertion + " seconds");
    }

    private static void migrateRelations(String inputFolder) {
        List<String> rdfFiles = new ArrayList<>();

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(inputFolder))) {
            for (Path path : directoryStream) {
                rdfFiles.add(path.getFileName().toString());
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        long startTime = System.currentTimeMillis();

        for (String inputRDFFile : rdfFiles) {

            Model model = ModelFactory.createDefaultModel();

            InputStream in1 = FileManager.get().open(inputFolder + inputRDFFile);
            if (in1 == null) {
                throw new IllegalArgumentException("File: " + inputRDFFile + " not found");
            }

            model.read(new InputStreamReader(in1), "");

        /*RDFModelIterator rdfModelIteratorPrinter = new RDFModelIteratorPrinter(model);
        rdfModelIteratorPrinter.iterateRDFModel();

        /*RDFModelIterator rdfModelIteratorPrinter = new LinkedMDBRDFResourceFetcher(model);
        rdfModelIteratorPrinter.iterateRDFModel();*/

            RDFNodeRelMapper rdfModelIterator = new RDFNodeRelMapper(model);
            rdfModelIterator.iterateRDFModel();
            //rdfModelIterator.printNode();
            //iterateRDFModel(model);
            //rdfModelIterator.printNode();

            Map<String, Map<String, String>> nodeRelationMap = rdfModelIterator.getNodeRelationMap();
            Map<String, Map<String, String>> nodeAttributesMap = rdfModelIterator.getNodeAttributeMap();


            List<String> relationQueries = QueryGenerator.generateQueriesForRelation(nodeRelationMap, nodeAttributesMap);
            writeQueriesToFile(relationQueries, inputRDFFile);
            int partitionSize =  (int)(Math.ceil(((double)relationQueries.size())/PARTITION_UNIT)) * PARTITION;
            //System.out.println(partitionSize + ":" + relationQueries.size());
            //relationQueries = relationQueries.subList(0, Math.min(partitionSize, relationQueries.size()));

            Neo4jDao.batchInsert(relationQueries, inputRDFFile);
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
}
