package org.la.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

/**
 * Created by laurenra on 5/9/17.
 */
public class RestTemplateLoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger log = LoggerFactory.getLogger(ClientHttpRequestInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] body, ClientHttpRequestExecution requestExecution) throws IOException {

        traceRequest(httpRequest, body);
        ClientHttpResponse response = requestExecution.execute(httpRequest, body);
        traceResponse(response);

        return response;
    }


    private void traceRequest(HttpRequest httpRequest, byte[] body) throws IOException {
        log.debug("---------- HTTP request begin ----------");
        log.debug("URI:\t" + httpRequest.getURI());
        log.debug("method:\t" + httpRequest.getMethod());

        String headersStr = "";
        for (Map.Entry<String, List<String>> header : httpRequest.getHeaders().entrySet()) {
            headersStr = headersStr + "\t" + header.getKey() + ": ";
            for (String key : header.getValue()) {
                headersStr = headersStr + key + ", ";
                headersStr = headersStr.replaceAll(", $", "") + "\n";
            }
        }

        log.debug("headers:\n" + headersStr);
        log.debug("body:\n" + getBodyAsString(body));
        log.debug("---------- HTTP request end ----------");
    }


    private void traceResponse(ClientHttpResponse httpResponse) throws IOException {
        log.debug("---------- HTTP response begin ----------");
        log.debug("status code:\t" + httpResponse.getStatusCode());
        log.debug("status text:\t" + httpResponse.getStatusText());

        String headersStr = "";
        for (Map.Entry<String, List<String>> header : httpResponse.getHeaders().entrySet()) {
            headersStr = headersStr + "\t" + header.getKey() + ": ";
            for (String key : header.getValue()) {
                headersStr = headersStr + key + ", ";
                headersStr = headersStr.replaceAll(", $", "") + "\n";
            }
        }

        log.debug("headers:\n" + headersStr);
        log.debug("body:\n" + httpResponse.getBody());
//        log.debug("body:\n" + getBodyAsString(httpResponse.getBody()));
//        log.debug("body:\n" + getBodyAsStringOrg(httpResponse.getBody()));
        log.debug("---------- HTTP response end ----------");

    }


    private String getBodyAsString(byte[] body) throws UnsupportedEncodingException {
        if (body != null && body.length > 0) {
            return (new String(body, "UTF-8"));
        }
        else {
            return null;
        }
    }


    private String getBodyAsString(InputStream inputStream) {
        StringBuilder stringBuilder = new StringBuilder();

        String line;

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private String getBodyAsStringOrg(InputStream inputStream) {
        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = new StringBuilder();

        String line;

        try {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            return stringBuilder.toString();
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
