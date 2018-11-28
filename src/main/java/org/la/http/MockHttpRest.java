package org.la.http;

public class MockHttpRest extends HttpRestBase implements HttpRest {

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

//    @Override
//    public String httpGet(String url, TokenHeaderProvider tokenHeaderProvider, String consumerKey, String consumerSecret) {
//        return null;
//    }
}
