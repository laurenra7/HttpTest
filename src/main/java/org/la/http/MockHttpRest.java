package org.la.http;

import java.util.Map;

public class MockHttpRest implements HttpRest {

    @Override
    public String httpGet(String url) {
//        System.out.println("test url:    " + url);
//        System.out.println("test key:    " + getConsumerKey());
//        System.out.println("test secret: " + getConsumerSecret());
//        System.out.println("test headers: " + getHeaders().isEmpty());
//
//        for (Map.Entry<String, String[]> entry : getHeaders().entrySet()) {
//            System.out.println("header key: " + entry.getKey());
//        }
        return null;
    }

    @Override
    public boolean isVerbose() {
        return false;
    }

    @Override
    public void setVerbose(boolean verbose) {

    }

    @Override
    public Map<String, String> getHeaders() {
        return null;
    }

    @Override
    public void setHeaders(Map<String, String> headers) {

    }

    @Override
    public String getConsumerKey() {
        return null;
    }

    @Override
    public void setConsumerKey(String consumerKey) {

    }

    @Override
    public String getConsumerSecret() {
        return null;
    }

    @Override
    public void setConsumerSecret(String consumerSecret) {

    }

//    @Override
//    public String httpGet(String url, TokenHeaderProvider tokenHeaderProvider, String consumerKey, String consumerSecret) {
//        return null;
//    }
}
