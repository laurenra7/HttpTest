package org.la.http;

import java.util.HashMap;
import java.util.Map;

abstract class HttpRestBase {

    private boolean verbose;
    private Map<String, String[]> headers;
    private String consumerKey;
    private String consumerSecret;

    public HttpRestBase() {
        verbose = false;
        headers = new HashMap<String, String[]>();
        consumerKey = "";
        consumerSecret = "";
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public Map<String, String[]> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String[]> headers) {
        this.headers = headers;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

    public void setConsumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }
}
