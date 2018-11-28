package org.la.http;

import java.util.HashMap;
import java.util.Map;
import org.junit.Ignore;
import org.junit.Test;

public class MockHttpRestTest {

//    HttpRest httpRest;
//
//    @Before
//    public void init() {
//        httpRest = new MockHttpRest();
//        System.out.println("key:    " + ((MockHttpRest) httpRest).getConsumerKey());
//        System.out.println("secret: " + ((MockHttpRest) httpRest).getConsumerSecret());
//    }

    @Ignore
    @Test
    public void httpGet() {
        HttpRest httpRest = new MockHttpRest();

        // Show default settings
        if (((MockHttpRest) httpRest).getConsumerKey() == null | ((MockHttpRest) httpRest).getConsumerKey() == "" ) {
            System.out.println("<no consumer key>");
        }

        System.out.println("key:    " + ((MockHttpRest) httpRest).getConsumerKey());
        System.out.println("secret: " + ((MockHttpRest) httpRest).getConsumerSecret());

        if (((MockHttpRest) httpRest).getHeaders().isEmpty()) {
            System.out.println("headers: <none>");
        }

        for (Map.Entry<String, String> entry : ((MockHttpRest) httpRest).getHeaders().entrySet()) {
            System.out.println("header key: " + entry.getKey());
        }

        // Set some fields
        ((MockHttpRest) httpRest).setConsumerKey("johnnyKey");
        ((MockHttpRest) httpRest).setConsumerSecret("johhnySecret");

        // Set headers
        Map headers = new HashMap<String, String[]>();
        headers.put("Accept", new String[] {"text/html", "application/json", "application/xml"});
        headers.put("Authorization", new String[] {"superCoolAuthority"});
        headers.put("Accept-Charset", new String[] {"utf-8"});
        ((MockHttpRest) httpRest).setHeaders(headers);

        // Show fields
        System.out.println("key:    " + ((MockHttpRest) httpRest).getConsumerKey());
        System.out.println("secret: " + ((MockHttpRest) httpRest).getConsumerSecret());

        System.out.println("headers: ");
        for (Map.Entry<String, String> entry : ((MockHttpRest) httpRest).getHeaders().entrySet()) {
//            System.out.println("header key: " + entry.getKey());
//            String valueConcat = "";
//            for (String value : entry.getValue()) {
//                valueConcat = valueConcat + value + ", ";
//            }
//            System.out.println("    " + entry.getKey() + ": " + valueConcat.replaceAll(", $", ""));
            System.out.println("header " + entry.getKey() + ": " + entry.getValue());
        }


        String result = httpRest.httpGet("http://my.test.url/this");
    }
}