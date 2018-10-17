package org.la.jul;

import java.util.logging.Logger;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

/**
 * Created by laurenra on 9/7/17.
 */
public final class Jersey1RestTest {

//    private static final org.slf4j.Logger log = LoggerFactory.getLogger(Jersey1RestTest.class);

    /* Test using java.util.logging (JUL)
        FINEST  -> TRACE
        FINER   -> DEBUG
        FINE    -> DEBUG
        INFO    -> INFO
        WARNING -> WARN
        SEVERE  -> ERROR
    */

    private static final Logger jullog = Logger.getLogger(Jersey1RestTest.class.getName());

    /**
     * From https://blogs.oracle.com/enterprisetechtips/consuming-restful-web-services-with-the-jersey-client-api
     * See also: http://www.mkyong.com/webservices/jax-rs/restful-java-client-with-jersey-client/
     *
     */
    public static void testRest() {

        //
        jullog.fine("jullog Starting testRest (Jersey 1)...");
//        log.info("Starting testRest (Jersey 1)...");
//        log.debug("debugging testRest (Jersey 1)...");

        Client client = Client.create();
//        WebResource webResource = client.resource("https://httpbin.org/get");
//        WebResource webResource = client.resource("http://orcasbay.com/test/rest/xml/blu-ray.xml");
        WebResource webResource = client.resource("http://orcasbay.com/test/rest/json/blu-ray.json");

        String responseString = webResource.get(String.class);

        System.out.println("----- RESPONSE -----");
        System.out.println(responseString);
        jullog.fine("jullog ----- RESPONSE -----");
        jullog.fine("jullog " + responseString);
//        log.info("----- RESPONSE -----");
//        log.info(responseString);
    }

}
