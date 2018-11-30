package org.la.http;

import edu.byu.wso2.core.Wso2Credentials;
import edu.byu.wso2.core.provider.ClientCredentialOauthTokenProvider;
import edu.byu.wso2.core.provider.ClientCredentialsTokenHeaderProvider;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
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
public class ApacheHttpComponentsHttpClient implements HttpRest {

    private static final Logger log = LoggerFactory.getLogger(ApacheHttpComponentsHttpClient.class);
    
    private String consumerSecret;
    private boolean verbose;
    private Map<String, String> headers;
    private String consumerKey;

    
    /* Constructor initializes values to defaults */
    public ApacheHttpComponentsHttpClient() {
        verbose = false;
        headers = new HashMap<String, String>();
        consumerKey = "";
        consumerSecret = "";
    }


    @Override
    public String httpGet(String url) {
        String result = "";

        log.debug("Http GET from URL: " + url);
        if (verbose) {
            System.out.println("Http GET from URL: " + url);
        }

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(url);

        /* Set default headers */
//        httpGet.addHeader("Accept-Charset", "utf-8");
//        httpGet.addHeader("Accept","text/plain");
        httpGet.addHeader("Accept","application/xml;application/json");

        /* Add headers from command line */
        if (!headers.isEmpty()) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                httpGet.addHeader(header.getKey(), header.getValue());
            }
        }

        if (consumerKey != null && !consumerKey.equals("") ) {
            /* Generate WSO2 authorization header from consumer key and secret. */
            ClientCredentialsTokenHeaderProvider tokenHeaderProvider = new ClientCredentialsTokenHeaderProvider(
                    new ClientCredentialOauthTokenProvider(
                            new Wso2Credentials(consumerKey, consumerSecret)
                    )
            );

            /* Set authorization header. */
            log.debug("Request header -> Authorization: " + tokenHeaderProvider.getTokenHeaderValue());
            if (verbose) {
                System.out.println("Request header -> Authorization: " + tokenHeaderProvider.getTokenHeaderValue());
            }
            httpGet.addHeader("Authorization", tokenHeaderProvider.getTokenHeaderValue());
        }


        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            int responseCode = httpResponse.getStatusLine().getStatusCode();

            if (verbose) {
                System.out.println("HTTP response code: " + httpResponse.getStatusLine().getStatusCode());
            }

            if (responseCode == HttpStatus.SC_OK) {
                if (verbose) {
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

                    if (verbose) {
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

    @Override
    public boolean isVerbose() {
        return verbose;
    }

    @Override
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public String getConsumerKey() {
        return consumerKey;
    }

    @Override
    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    @Override
    public String getConsumerSecret() {
        return consumerSecret;
    }

    @Override
    public void setConsumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }

}
