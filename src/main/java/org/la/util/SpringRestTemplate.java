package org.la.util;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class SpringRestTemplate implements HttpProcessor {

    private static final Logger log = LoggerFactory.getLogger(SpringRestTemplate.class);


    public String httpGet(String url, boolean modeVerbose) {
        if (modeVerbose) {
            return resultVerbose(httpGet(url));
        }
        else {
            return resultBody(httpGet(url));
        }
    }


    private String resultBody(ResponseEntity<String> response) {
        String resultString = "";
        if (response != null) {
            resultString = response.getBody();
        }
        return resultString;
    }


    private String resultVerbose(ResponseEntity<String> response) {
        String resultString = "";

        if (response != null) {
            resultString = resultString + "HTTP Response Code: " + String.valueOf(response.getStatusCode()) + "\n";

            HttpHeaders resultHeaders = response.getHeaders();

            resultString = resultString + "---------- Response Header ----------\n";
            for (Map.Entry<String, List<String>> header : resultHeaders.entrySet()) {
                resultString = resultString + header.getKey() + ": ";
                String values = "";
                for (String value : header.getValue()) {
                    resultString = resultString + value + ",";
                }
                resultString.replaceAll(",$", "");
                resultString = resultString + "\n";
            }

            resultString = resultString + "---------- Response Body ----------\n" + response.getBody();
        }

        return resultString;
    }


    private ResponseEntity<String> httpGet(String url) {

        ResponseEntity<String> response = null;

        RestTemplate httpService = new RestTemplate();

        /* begin - setup logging of HTTP request, response */
        List<ClientHttpRequestInterceptor> requestInterceptorList = new ArrayList<ClientHttpRequestInterceptor>();
        requestInterceptorList.add(new RestTemplateLoggingInterceptor());
        httpService.setInterceptors(requestInterceptorList);
        /* end - setup logging of HTTP request, response */

        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.setContentType(MediaType.TEXT_PLAIN);

        String showStr = "";
        for (Map.Entry<String, List<String>> header : httpHeaders.entrySet()) {
            showStr = showStr + header.getKey() + ": ";
            for (String key : header.getValue()) {
                showStr = showStr + key + ", ";
                showStr = showStr.replaceAll(", $", "");
            }
        }
        System.out.println("headers:\n" + showStr);

        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);

        String urltest1 = "https://calendar.google.com/calendar/ical/en.usa#holiday@group.v.calendar.google.com/public/basic.ics";
        String urltest2 = "https://calendar.google.com/calendar/ical/en.usa%23holiday@group.v.calendar.google.com/public/basic.ics";
        String urltest3 = "https://calendar.google.com/calendar/ical/en.usa%23holiday%40group.v.calendar.google.com/public/basic.ics";

        UriComponents uritest12 = UriComponentsBuilder.fromHttpUrl(urltest2).build();
        System.out.println("uri fromHttpUrl 2: " + uritest12.toUriString());
        System.out.println("  host:\t" + uritest12.getHost());
        System.out.println("  scheme:\t" + uritest12.getScheme());
        System.out.println("  fragment:\t"  + uritest12.getFragment());
        System.out.println("  path:\t" + uritest12.getPath());
        System.out.println("  pathSegments:\t" + uritest12.getPathSegments());
        System.out.println("  :\t" + uritest12.getQuery());

        UriComponents uritest13 = UriComponentsBuilder.fromHttpUrl(urltest3).build();
        System.out.println("uri fromHttpUrl 3: " + uritest13.toUriString());
        System.out.println("  host:\t" + uritest13.getHost());
        System.out.println("  scheme:\t" + uritest13.getScheme());
        System.out.println("  fragment:\t"  + uritest13.getFragment());
        System.out.println("  path:\t" + uritest13.getPath());
        System.out.println("  pathSegments:\t" + uritest13.getPathSegments());
        System.out.println("  :\t" + uritest13.getQuery());

        UriComponents uritest21 = UriComponentsBuilder.fromUriString(urltest1).build(true);
        System.out.println("uri fromUriString 1: " + uritest21.toUriString());
        String test_scheme = uritest21.getScheme();
        System.out.println("  scheme:\t" + test_scheme);
//        String test_host = uritest21.toUriString();
        String test_host = "calendar.google.com";
        System.out.println("  host:\t" + test_host);
        String test_fragment = uritest21.getFragment();
        System.out.println("  fragment:\t"  + test_fragment);
        String test_path = uritest21.getPath();
        System.out.println("  path:\t" + test_path);
//        System.out.println("  pathSegments:\t" + uritest21.getPathSegments());

        UriComponents myuricomp = UriComponentsBuilder.newInstance()
                .scheme(test_scheme)
                .host(test_host)
                .path(test_path)
                .fragment(test_fragment)
                .build();

        System.out.println("New URI Comps string:\t" + myuricomp.toString());

        URI uri21 = myuricomp.toUri();

        System.out.println("New URI string:\t" + uri21.toString());

        UriComponents uritest22 = UriComponentsBuilder.fromUriString(urltest2).build(false);
        System.out.println("uri fromUriString 2: " + uritest22.toUriString());
        UriComponents uritest23 = UriComponentsBuilder.fromUriString(urltest3).build(false);
        System.out.println("uri fromUriString 3: " + uritest23.toUriString());

//        UriComponents uritest11 = UriComponentsBuilder.fromHttpUrl(urltest1).build();
//        System.out.println("uri fromHttpUrl 1: " + uritest11.toUriString());

//        System.out.println("raw url:\t" + uritest.toString());
//        System.out.println("toUriString:\t" + uritest.toUriString());
//        System.out.println("encode:\t" + uritest.encode().toString());
//        System.out.println("encode toUriString:\t" + uritest.encode().toUriString());

//        URI testuri = UriComponentsBuilder.fromHttpUrl(url).build().toUri();
        URI testuri = UriComponentsBuilder.fromHttpUrl(url).build(false).toUri();
//        URI testuri = UriComponentsBuilder.fromUriString(url).build(false).toUri();
        System.out.println("----- URI -----");
        System.out.println("entered URL string:\t" + url);
        System.out.println("URI from URL string:\t" + testuri);
        System.out.println("URI from TEST URL string:\t" + uri21);

        try {
//            response = httpService.exchange(url, HttpMethod.GET, httpEntity, String.class);
            response = httpService.getForEntity(url, String.class);
//            response = httpService.getForEntity(uri21, String.class);
//            String resultObject = httpService.getForObject(url, String.class);
//            System.out.println("RESPONSE: " + resultObject);
        }
        catch (RestClientException e) {
            e.printStackTrace();
//            System.out.println("Error processing HTTP request. HTTP status code is " + e.getMessage());
//            System.out.println("Stack trace:");
//            System.out.println(e);
        }

        return response;
    }

}
