package org.la.util;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class SpringRestTemplate {

    private static final Logger log = LoggerFactory.getLogger(SpringRestTemplate.class);

    public String httpGet(String url, boolean modeVerbose) {

        if (modeVerbose) {
            System.out.println("Http GET from URL: " + url);
            log.debug("Http GET from URL: " + url);
        }

        UriComponents uriComponents;

        if(url.contains("%")) {
            if (modeVerbose) {
                System.out.println("URL appears to already by encoded, so don't encode it again.");
                log.debug("URL appears to already by encoded, so don't encode it again.");
            }
            uriComponents = UriComponentsBuilder.fromHttpUrl(url).build(true);
        }
        else {
            if (modeVerbose) {
                System.out.println("URL is not encoded, so encode it.");
                log.debug("URL is not encoded, so encode it.");
            }
            uriComponents = UriComponentsBuilder.fromHttpUrl(url).build(false);
        }

        if (modeVerbose) {
            System.out.println("Entered URL string:\t" + url);
            System.out.println("URI built from URL:\t" + uriComponents.toUriString());
            System.out.println("    host:\t" + uriComponents.getHost());
            System.out.println("    scheme:\t" + uriComponents.getScheme());
            System.out.println("    fragment:\t"  + uriComponents.getFragment());
            System.out.println("    path:\t" + uriComponents.getPath());
            System.out.println("    pathSegments:\t" + uriComponents.getPathSegments());
            System.out.println("    query:\t" + uriComponents.getQuery());
            log.debug("Entered URL string:\t" + url);
            log.debug("URI built from URL:\t" + uriComponents.toUriString());
            log.debug("    host:\t" + uriComponents.getHost());
            log.debug("    scheme:\t" + uriComponents.getScheme());
            log.debug("    fragment:\t"  + uriComponents.getFragment());
            log.debug("    path:\t" + uriComponents.getPath());
            log.debug("    pathSegments:\t" + uriComponents.getPathSegments());
            log.debug("    query:\t" + uriComponents.getQuery());
        }

        return httpGet(uriComponents.toUri(), modeVerbose).getBody();
    }


    private ResponseEntity<String> httpGet(URI uri, boolean modeVerbose) {

        ResponseEntity<String> response = null;

        RestTemplate httpService = new RestTemplate();

        if (modeVerbose) {
            /* Set up logging of HTTP request and response to console */
            List<ClientHttpRequestInterceptor> requestInterceptorList = new ArrayList<ClientHttpRequestInterceptor>();
            requestInterceptorList.add(new RestTemplateLoggingInterceptor());
            httpService.setInterceptors(requestInterceptorList);
        }

//        HttpHeaders httpHeaders = new HttpHeaders();
//        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);

        try {
            response = httpService.getForEntity(uri, String.class);
        }
        catch (RestClientException e) {
            e.printStackTrace();
//            System.out.println("Error processing HTTP request. HTTP status code is " + e.getMessage());
//            System.out.println("Stack trace:");
//            System.out.println(e);
        }

        if (modeVerbose) {
            log.debug("response: {}", response);
        }
        return response;
    }

}
