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

        try {
            int responseCode = client.executeMethod(getMethod);

            if (modeVerbose) {
                result = result + "HTTP response code: " + responseCode + "\n";
            }
            if (responseCode == HttpStatus.SC_OK) {

                if (modeVerbose) {
                    result = result + "---------- Request Header ----------\n";
                    Header[] requestHeaders = getMethod.getRequestHeaders();
                    for (Header reqHeader : requestHeaders) {
                        result = result + reqHeader.getName() + ": " + reqHeader.getValue() + "\n";
                    }

                    result = result + "---------- Response Header ----------\n";
                    Header[] responseHeaders = getMethod.getResponseHeaders();
                    for (Header respHeader : responseHeaders) {
                        result = result + respHeader.getName() + ": " + respHeader.getValue() + "\n";
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
                    result = result + "---------- Response Body ----------\n";
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
