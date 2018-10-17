package org.la.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by laurenra on 5/12/17.
 */
public class ApacheCommonsHttpClient implements HttpProcessor {

    private static final Logger log = LoggerFactory.getLogger(ApacheCommonsHttpClient.class);

    public String httpGet(String url, boolean modeVerbose) {
        HttpClient client = new HttpClient();
        HttpMethod getMethod = new GetMethod(url);
        BufferedReader buffer = null;
        String result = "";

        if (modeVerbose) {
            System.out.println("HTTP GET from URL: " + url);
            log.debug("HTTP GET from URL: " + url);
        }

        try {
            int responseCode = client.executeMethod(getMethod);

            if (modeVerbose) {
                System.out.println("HTTP response code: " + responseCode);
                log.debug("HTTP response code: " + responseCode);
            }
            if (responseCode == HttpStatus.SC_OK) {

                if (modeVerbose) {
                    System.out.println("---------- Request Header ----------");
                    log.debug("---------- Request Header ----------");
                    Header[] requestHeaders = getMethod.getRequestHeaders();
                    for (Header reqHeader : requestHeaders) {
                        System.out.println(reqHeader.getName() + ": " + reqHeader.getValue());
                    }

                    System.out.println("---------- Response Header ----------");
                    log.debug("---------- Response Header ----------");
                    Header[] responseHeaders = getMethod.getResponseHeaders();
                    for (Header respHeader : responseHeaders) {
                        System.out.println(respHeader.getName() + ": " + respHeader.getValue());
                        log.debug(respHeader.getName() + ": " + respHeader.getValue());
                    }
                }


                InputStream inStream = getMethod.getResponseBodyAsStream();

                buffer = new BufferedReader(new InputStreamReader(inStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = buffer.readLine()) != null) {
                    stringBuilder.append("\n");
                    stringBuilder.append(line);
                }

                stringBuilder.deleteCharAt(0); // delete initial newline

                if (modeVerbose) {
                    System.out.println("---------- Response Body ----------");
                    log.debug("---------- Response Body ----------");
                }

                result = result + stringBuilder.toString();

            }
            else {
                System.out.println("Problem with request. HTTP status code: " + responseCode);
            }
        }
        catch (Exception e) {
            System.out.println("Error fetching request: " + e.getMessage());
        }
        finally {
            if (buffer != null) {
                try {
                    buffer.close();
                }
                catch (IOException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        }

        return result;
    }

}
