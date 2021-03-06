package org.la.http;

import edu.byu.wso2.core.Wso2Credentials;
import edu.byu.wso2.core.provider.ClientCredentialOauthTokenProvider;
import edu.byu.wso2.core.provider.ClientCredentialsTokenHeaderProvider;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
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
public class ApacheCommonsHttpClient implements HttpRest {

    private static final Logger log = LoggerFactory.getLogger(ApacheCommonsHttpClient.class);

    private String consumerSecret;
    private boolean verbose;
    private Map<String, String> headers;
    private Map<String, String> queries;
    private String consumerKey;


    /* Constructor initializes values to defaults */
    public ApacheCommonsHttpClient() {
        verbose = false;
        headers = new HashMap<String, String>();
        consumerKey = "";
        consumerSecret = "";
    }


    public String httpGet(String url) {
        String strOut = "";
        HttpClient client = new HttpClient();
        HttpMethod getMethod = new GetMethod(url);
        BufferedReader buffer = null;
        String result = "";

        strOut = "----------------------- HTTP GET ------------------------";
        log.debug(strOut);
        if (verbose) System.out.println(strOut);

        strOut = "URL: " + url;
        log.debug(strOut);
        if (verbose) System.out.println(strOut);

        /* Set default headers */
//        getMethod.addRequestHeader("Accept-Charset", "utf-8");
//        getMethod.addRequestHeader("Accept","text/plain");
        getMethod.addRequestHeader("Accept","application/xml;application/json");

        /* Add headers from command line */
        if (!headers.isEmpty()) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                getMethod.addRequestHeader(header.getKey(), header.getValue());
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
            getMethod.addRequestHeader("Authorization", tokenHeaderProvider.getTokenHeaderValue());
        }

        /* Send request and get response */
        try {
            int responseCode = client.executeMethod(getMethod);

            strOut = "HTTP response code: " + responseCode;
            log.debug(strOut);
            if (verbose) System.out.println(strOut);

            strOut = "-------------------- Request Header ---------------------";
            log.debug(strOut);
            if (verbose) System.out.println(strOut);

            Header[] requestHeaders = getMethod.getRequestHeaders();

            for (Header reqHeader : requestHeaders) {
                strOut = reqHeader.getName() + ": " + reqHeader.getValue();
                log.debug(strOut);
                if (verbose) System.out.println(strOut);
            }

            if (responseCode == HttpStatus.SC_OK) {

                strOut = "-------------------- Response Header --------------------";
                log.debug(strOut);
                if (verbose) System.out.println(strOut);

                Header[] responseHeaders = getMethod.getResponseHeaders();

                for (Header respHeader : responseHeaders) {
                    strOut = respHeader.getName() + ": " + respHeader.getValue();
                    log.debug(strOut);
                    if (verbose) System.out.println(strOut);
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

                strOut = "--------------------- Response Body ---------------------\n" + stringBuilder.toString();
                log.debug(strOut);

                result = result + stringBuilder.toString(); // remove once the BufferedReader and StringBuilder is cleaned up like ApacheHttpComponentsHttpClient

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

    @Override
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public Map<String, String> getQueries() {
        return queries;
    }

    @Override
    public void setQueries(Map<String, String> queries) {
        this.queries = queries;
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
