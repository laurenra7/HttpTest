package org.la.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by laurenra on 5/12/17.
 */
public class ApacheHttpComponentsHttpClient implements HttpProcessor {

    private static final Logger log = LoggerFactory.getLogger(ApacheHttpComponentsHttpClient.class);

    public String httpGet(String url, boolean modeVerbose) {
        String result = "";

        if (modeVerbose) {
            System.out.println("Http GET from URL: " + url);
        }

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(url);

        httpGet.addHeader("Accept","text/plain");
        httpGet.addHeader("Accept-Charset", "utf-8");

        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            int responseCode = httpResponse.getStatusLine().getStatusCode();

            if (modeVerbose) {
                result = result + "HTTP response code: " + httpResponse.getStatusLine().getStatusCode() + "\n";
            }

            if (responseCode == HttpStatus.SC_OK) {
                if (modeVerbose) {
                    result = result + "---------- Request Header ----------\n";
                    org.apache.http.Header[] requestHeaders = httpGet.getAllHeaders();
                    for (org.apache.http.Header reqHeader : requestHeaders) {
                        result = result + reqHeader.getName() + ": " + reqHeader.getValue() + "\n";
                    }

                    result = result + "---------- Response Header ----------\n";
                    org.apache.http.Header[] responseHeaders = httpResponse.getAllHeaders();
                    for (org.apache.http.Header respHeader : responseHeaders) {
                        result = result + respHeader.getName() + ": " + respHeader.getValue() + "\n";
                    }
                }

                try (BufferedReader bufferedReader =
                             new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()))) {

                    StringBuffer responseBody = new StringBuffer();
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        responseBody.append("\n");
                        responseBody.append(line);
                    }

                    responseBody.deleteCharAt(0); // delete initial newline

                    if (modeVerbose) {
                        result = result + "---------- Response Body ----------\n";
                    }

                    result = result + responseBody;
                }

            }
            else {
                System.out.println("Problem with request. HTTP status code: " + responseCode);
            }

        }
        catch (IOException e) {
            System.out.println("Error fetching request: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }


}
