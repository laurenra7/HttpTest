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
        String strOut = "";
        StringBuffer responseBody = new StringBuffer();

        strOut = "----------------------- HTTP GET ------------------------";
        log.debug(strOut);
        if (verbose) System.out.println(strOut);

        strOut = "URL: " + url;
        log.debug(strOut);
        if (verbose) System.out.println(strOut);

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
            strOut = strOut + "Get Authorization token: " + tokenHeaderProvider.getTokenHeaderValue() + "\n";
            httpGet.addHeader("Authorization", tokenHeaderProvider.getTokenHeaderValue());
        }

        /* Send request and get response */
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            int responseCode = httpResponse.getStatusLine().getStatusCode();

            strOut = "HTTP response code: " + responseCode;
            log.debug(strOut);
            if (verbose) System.out.println(strOut);

            strOut = "-------------------- Request Header ---------------------";
            log.debug(strOut);
            if (verbose) System.out.println(strOut);

            org.apache.http.Header[] requestHeaders = httpGet.getAllHeaders();

            for (org.apache.http.Header reqHeader : requestHeaders) {
                strOut = reqHeader.getName() + ": " + reqHeader.getValue();
                log.debug(strOut);
                if (verbose) System.out.println(strOut);
            }

            if (responseCode == HttpStatus.SC_OK) {

                strOut = "-------------------- Response Header --------------------";
                log.debug(strOut);
                if (verbose) System.out.println(strOut);

                org.apache.http.Header[] responseHeaders = httpResponse.getAllHeaders();

                for (org.apache.http.Header respHeader : responseHeaders) {
                    strOut = respHeader.getName() + ": " + respHeader.getValue();
                    log.debug(strOut);
                    if (verbose) System.out.println(strOut);
                }

                try (BufferedReader bufferedReader =
                             new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()))) {

                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        responseBody.append("\n");
                        responseBody.append(line);
                    }

                    responseBody.deleteCharAt(0); // delete initial newline

                    strOut = "--------------------- Response Body ---------------------\n" + responseBody.toString();
                    log.debug(strOut);

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

        /* Show debug statements. */
//        log.debug(strOut);
//        if (verbose) {
//            System.out.println(strOut);
//        }

        return responseBody.toString();
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
