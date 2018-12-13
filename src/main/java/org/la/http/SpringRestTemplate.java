package org.la.http;

import edu.byu.wso2.core.Wso2Credentials;
import edu.byu.wso2.core.provider.ClientCredentialOauthTokenProvider;
import edu.byu.wso2.core.provider.ClientCredentialsTokenHeaderProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.la.RestTemplateLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Component
//public class SpringRestTemplate extends HttpRestBase implements HttpRest {
public class SpringRestTemplate implements HttpRest {

    private static final Logger log = LoggerFactory.getLogger(SpringRestTemplate.class);

    private boolean verbose;
    private Map<String, String> headers;
    private Map<String, String> queries;
    private String consumerKey;
    private String consumerSecret;


    /* Constructor initializes values to defaults */
    public SpringRestTemplate() {
        verbose = false;
        headers = new HashMap<String, String>();
        consumerKey = "";
        consumerSecret = "";
    }


    @Override
    public String httpGet(String url) {

        ResponseEntity<String> response = null;
        UriComponents uriComponents;
        RestTemplate httpService = new RestTemplate();

        log.debug("Http GET from URL: " + url);
        if (verbose) {
            System.out.println("Http GET from URL: " + url);
        }

        if(url.contains("%")) {
            log.debug("URL appears to already by encoded, so don't encode it again.");
            if (verbose) {
                System.out.println("URL appears to already by encoded, so don't encode it again.");
            }
            uriComponents = UriComponentsBuilder.fromHttpUrl(url).build(true);
        }
        else {
            log.debug("URL is not encoded, so encode it.");
            if (verbose) {
                System.out.println("URL is not encoded, so encode it.");
            }
            uriComponents = UriComponentsBuilder.fromHttpUrl(url).build(false);
        }

        log.debug("Entered URL string:\t" + url);
        log.debug("URI built from URL:\t" + uriComponents.toUriString());
        log.debug("    host:\t" + uriComponents.getHost());
        log.debug("    scheme:\t" + uriComponents.getScheme());
        log.debug("    fragment:\t"  + uriComponents.getFragment());
        log.debug("    path:\t" + uriComponents.getPath());
        log.debug("    pathSegments:\t" + uriComponents.getPathSegments());
        log.debug("    query:\t" + uriComponents.getQuery());

        if (verbose) {
            System.out.println("Entered URL string:\t" + url);
            System.out.println("URI built from URL:\t" + uriComponents.toUriString());
            System.out.println("    host:\t" + uriComponents.getHost());
            System.out.println("    scheme:\t" + uriComponents.getScheme());
            System.out.println("    fragment:\t"  + uriComponents.getFragment());
            System.out.println("    path:\t" + uriComponents.getPath());
            System.out.println("    pathSegments:\t" + uriComponents.getPathSegments());
            System.out.println("    query:\t" + uriComponents.getQuery());
        }

        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON));

        /* Add headers from command line */
        if (!headers.isEmpty()) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                httpHeaders.set(header.getKey(), header.getValue());
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
            log.debug("Request header -> Authorization: " + tokenHeaderProvider.getTokenHeaderValue());
            if (verbose) {
                System.out.println("Request header -> Authorization: " + tokenHeaderProvider.getTokenHeaderValue());
            }
            httpHeaders.set("Authorization", tokenHeaderProvider.getTokenHeaderValue());
        }

        HttpEntity<String> httpEntity = new HttpEntity<>("parameters", httpHeaders);

        if (log.isDebugEnabled()) {
            /* Set up logging of HTTP request and response to console */
            List<ClientHttpRequestInterceptor> requestInterceptorList = new ArrayList<ClientHttpRequestInterceptor>();
            requestInterceptorList.add(new RestTemplateLoggingInterceptor());
            httpService.setInterceptors(requestInterceptorList);
        }

        try {
//            response = httpService.getForEntity(uri, String.class);
            response = httpService.exchange(uriComponents.toUri(), HttpMethod.GET, httpEntity, String.class);

        }
        catch (RestClientException e) {
//            e.printStackTrace();
            System.out.println("Error processing HTTP request. HTTP status code is " + e.getMessage());
            System.out.println("Stack trace:");
            System.out.println(e);
        }

        log.debug("response: {}", response.getBody());

        return response.getBody();

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
