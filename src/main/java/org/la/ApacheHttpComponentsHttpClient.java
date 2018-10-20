package org.la;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * Created by laurenra on 5/12/17.
 */
public class ApacheHttpComponentsHttpClient implements HttpProcessor {

//    private static final Logger log = LoggerFactory.getLogger(ApacheHttpComponentsHttpClient.class);

    public String httpGet(String url, boolean modeVerbose) {
        String result = "";

        if (modeVerbose) {
            System.out.println("HTTP GET from URL: " + url);
        }

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(url);

        httpGet.addHeader("Accept","text/plain");
        httpGet.addHeader("Accept-Charset", "utf-8");

        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            int responseCode = httpResponse.getStatusLine().getStatusCode();

            if (modeVerbose) {
                System.out.println("HTTP response code: " + httpResponse.getStatusLine().getStatusCode());
            }

            if (responseCode == HttpStatus.SC_OK) {
                if (modeVerbose) {
                    System.out.println("---------- Request Header ----------");
                    org.apache.http.Header[] requestHeaders = httpGet.getAllHeaders();
                    for (org.apache.http.Header reqHeader : requestHeaders) {
                        System.out.println(reqHeader.getName() + ": " + reqHeader.getValue());
                    }

                    System.out.println("---------- Response Header ----------");
                    org.apache.http.Header[] responseHeaders = httpResponse.getAllHeaders();
                    for (org.apache.http.Header respHeader : responseHeaders) {
                        System.out.println(respHeader.getName() + ": " + respHeader.getValue());
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
                        System.out.println("---------- Response Body ----------");
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
