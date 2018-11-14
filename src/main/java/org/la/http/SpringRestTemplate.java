package org.la.http;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import edu.byu.wso2.core.Wso2Credentials;
import edu.byu.wso2.core.provider.ClientCredentialOauthTokenProvider;
import edu.byu.wso2.core.provider.ClientCredentialsTokenHeaderProvider;
import edu.byu.wso2.core.provider.TokenHeaderProvider;
import org.la.RestTemplateLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
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
public class SpringRestTemplate implements HttpGet {

    private static final Logger log = LoggerFactory.getLogger(SpringRestTemplate.class);

//    @Autowired
//    public TokenHeaderProvider tokenHeaderProvider;
    private AnnotationConfigApplicationContext springContext;



    @Override
    public String httpGet(String url,
                          boolean modeVerbose,
                          TokenHeaderProvider tokenHeaderProvider,
                          String consumerKey,
                          String consumerSecret) {

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

//        Wso2Credentials wso2Credentials = new Wso2Credentials(consumerKey, consumerSecret);
        ClientCredentialsTokenHeaderProvider tokenHeaderProviderA = new ClientCredentialsTokenHeaderProvider(
                new ClientCredentialOauthTokenProvider(
                        new Wso2Credentials(consumerKey, consumerSecret)
                )
        );

        return httpGet(uriComponents.toUri(), modeVerbose, tokenHeaderProviderA).getBody();

    }


    private ResponseEntity<String> httpGet(URI uri, boolean modeVerbose, TokenHeaderProvider tokenHeaderProvider) {

        ResponseEntity<String> response = null;

        RestTemplate httpService = new RestTemplate();

        /* Set authorization header */
        HttpHeaders headers = new HttpHeaders();
        System.out.println("Request header -> Authorization: " + tokenHeaderProvider.getTokenHeaderValue());
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", tokenHeaderProvider.getTokenHeaderValue());
//        headers.set("Acting-For", "someone");

        HttpEntity<String> httpEntity = new HttpEntity<>("parameters", headers);

        if (modeVerbose) {
            /* Set up logging of HTTP request and response to console */
            List<ClientHttpRequestInterceptor> requestInterceptorList = new ArrayList<ClientHttpRequestInterceptor>();
            requestInterceptorList.add(new RestTemplateLoggingInterceptor());
            httpService.setInterceptors(requestInterceptorList);
        }

//        HttpHeaders httpHeaders = new HttpHeaders();
//        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);

        try {
//            response = httpService.getForEntity(uri, String.class);
            response = httpService.exchange(uri, HttpMethod.GET, httpEntity, String.class);

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
