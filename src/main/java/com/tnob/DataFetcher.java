package com.tnob;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;

/**
 * Created by tahmid on 2/14/15.
 */
public class DataFetcher {

    public void fetchDataToFile(String url, String fileName) {

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);

        // add request header
        try {

            HttpResponse response = null;
            response = client.execute(request);

            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
                result.append("\n");
            }

            BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
            bw.write(result.toString());
            bw.close();
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void fetchDataUsingWget(String url) {
        String processName = "wget";

        Runtime rt = Runtime.getRuntime();
        try {
            Process proc = rt.exec(processName + " " + url);
            int exitVal = proc.waitFor();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
