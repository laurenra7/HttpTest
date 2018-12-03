package org.la;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;

/**
 * Created by laurenra on 9/7/17.
 */
public final class Jersey2RestTest {

    /**
     * From http://www.vogella.com/tutorials/REST/article.html
     */
    public static void testRest() {

        ClientConfig clientConfig = new ClientConfig();

        Client client = ClientBuilder.newClient(clientConfig);

        WebTarget webTarget = client.target(getBaseUri());

        /* http://orcasbay.com/test/rest/xml/blu-ray.xml */
//        String response = webTarget.path("blu-ray.xml")
        String response = webTarget.path("blu-ray.json")
                        .request()
                        .accept(MediaType.TEXT_PLAIN)
                        .get(Response.class)
                        .toString();

        String responsePlainText = webTarget.path("blu-ray.json")
                        .request()
                        .accept(MediaType.TEXT_PLAIN)
                        .get(String.class);

//        String responseXml = webTarget.path("blu-ray.xml")
//                        .request()
//                        .accept(MediaType.TEXT_XML)
//                        .get(String.class);
//
//        String responseHtml = webTarget.path("blu-ray.xml")
//                        .request()
//                        .accept(MediaType.TEXT_HTML)
//                        .get(String.class);

        System.out.println("----- RESPONSE -----");
        System.out.println(response);

        System.out.println("----- RESPONSE Plain Text -----");
        System.out.println(responsePlainText);

//        System.out.println("----- RESPONSE XML -----");
//        System.out.println(responseXml);
//
//        System.out.println("----- RESPONSE HTML -----");
//        System.out.println(responseHtml);

    }


    private static URI getBaseUri() {
        /* https://httpbin.org/get */
//        return UriBuilder.fromUri("https://httpbin.org").build();

        /* http://orcasbay.com/test/rest/xml/blu-ray.xml */
//        return UriBuilder.fromUri("http://orcasbay.com/test/rest/xml").build();

        /* http://orcasbay.com/test/rest/json/blu-ray.json */
        return UriBuilder.fromUri("http://orcasbay.com/test/rest/json").build();
    }


}
