package org.la;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.ConsoleAppender;
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

    /* Log4j Logger, used with logback.xml */
    private static final Logger logger = LoggerFactory.getLogger(ClientHttpRequestInterceptor.class);


    /* Logback Logger, used with configureLogging() method below */
    private ch.qos.logback.classic.Logger lblogger;


//    /* Configure Logback logging in constructor */
//    public RestTemplateLoggingInterceptor() {
//        /* Send LogBack output to specified LogBack logger file */
//        configureLogging();
//    }


    /**
     * Configure logging to specified file or console rather than using the
     * settings in logback.xml.
     */
    private void configureLogging() {
        /* Send LogBack output to specified LogBack logger file */
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        /* Set log file logging pattern */
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(loggerContext);
        encoder.setPattern("%d{ISO8601} [%thread] %-5level %logger{36} - %msg%n");
        encoder.start();

//        FileAppender<ILoggingEvent> fileAppender = new FileAppender<ILoggingEvent>();
//        fileAppender.setFile(logFileName);
//        fileAppender.setEncoder(encoder);
//        fileAppender.setContext(loggerContext);
//        fileAppender.start();
//
//        Logger slf4jLogger = (Logger) LoggerFactory.getLogger("org.springframework.http.client");
//        slf4jLogger.addAppender(fileAppender);
//        slf4jLogger.setLevel(Level.DEBUG);
//        slf4jLogger.setAdditive(false);
//
//        logger = slf4jLogger;

        ConsoleAppender consoleAppender = new ConsoleAppender();
        consoleAppender.setContext(loggerContext);
        consoleAppender.setEncoder(encoder);
//        consoleAppender.setName("STDOUT");
        consoleAppender.start();

        ch.qos.logback.classic.Logger slf4jLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("org.springframework.http.client");
        slf4jLogger.addAppender(consoleAppender);
        slf4jLogger.setLevel(Level.DEBUG);
        slf4jLogger.setAdditive(false);

        lblogger = slf4jLogger;

    }


    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] body, ClientHttpRequestExecution requestExecution) throws IOException {

        traceRequest(httpRequest, body);
        ClientHttpResponse response = requestExecution.execute(httpRequest, body);
        traceResponse(response);

        return response;
    }


    private void traceRequest(HttpRequest httpRequest, byte[] body) throws IOException {
        logger.debug("---------- HTTP request begin ----------");
        logger.debug("URI:\t" + httpRequest.getURI());
        logger.debug("method:\t" + httpRequest.getMethod());

        String headersStr = "";
        for (Map.Entry<String, List<String>> header : httpRequest.getHeaders().entrySet()) {
            headersStr = headersStr + "\t" + header.getKey() + ": ";
            for (String key : header.getValue()) {
                headersStr = headersStr + key + ", ";
                headersStr = headersStr.replaceAll(", $", "") + "\n";
            }
        }

        logger.debug("headers:\n" + headersStr);
        logger.debug("body:\n" + getBodyAsString(body));
        logger.debug("----------- HTTP request end -----------");
    }


    private void traceResponse(ClientHttpResponse httpResponse) throws IOException {
        logger.debug("--------- HTTP response begin ----------");
        logger.debug("status code:\t" + httpResponse.getStatusCode());
        logger.debug("status text:\t" + httpResponse.getStatusText());

        String headersStr = "";
        for (Map.Entry<String, List<String>> header : httpResponse.getHeaders().entrySet()) {
            headersStr = headersStr + "\t" + header.getKey() + ": ";
            for (String key : header.getValue()) {
                headersStr = headersStr + key + ", ";
                headersStr = headersStr.replaceAll(", $", "") + "\n";
            }
        }

        logger.debug("headers:\n" + headersStr);
        logger.debug("body:\n" + httpResponse.getBody());
//        logger.debug("body:\n" + getBodyAsString(httpResponse.getBody()));
//        logger.debug("body:\n" + getBodyAsStringOrg(httpResponse.getBody()));
        logger.debug("---------- HTTP response end -----------");

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
