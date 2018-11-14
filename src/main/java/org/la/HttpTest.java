package org.la;

import java.io.BufferedWriter;
import java.io.Console;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import edu.byu.wso2.core.provider.ClientCredentialsTokenHeaderProvider;
import edu.byu.wso2.core.provider.TokenHeaderProvider;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.status.StatusLogger;
import org.la.http.SpringRestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

/**
 * Created by laurenra on 5/12/17.
 */
@Configuration
public class HttpTest {

    /**
     * Log4j got included with the BYU WSO2 dependency so set its log level off
     * so we don't see this meaningless error:
     *
     * ERROR StatusLogger No log4j2 configuration file found. Using default configuration
     *
     * Friends don't let friends use Log4j anymore. Use Logback instead.
     */
    static {
        StatusLogger.getLogger().setLevel(Level.OFF);
    }

    private static final Logger log = LoggerFactory.getLogger(HttpTest.class);

    private static class HttpLib {
        public static final String CommonsHttpClient = "CommonsHttpClient";
        public static final String HttpComponentsHttpClient = "HttpComponentsHttpClient";
        public static final String SpringRestTemplate = "SpringRestTemplate";

        public static final String CommonsHttpClientClass = "org.apache.commons.httpclient.HttpClient";
        public static final String HttpComponentsHttpClientClass = "org.apache.http.client.HttpClient";
        public static final String SpringRestTemplateClass = "org.springframework.web.client.RestTemplate";

        public static String[] list() {
            String[] list = {CommonsHttpClient, HttpComponentsHttpClient, SpringRestTemplate};
            return list;
        }

        public static void showPrettyList() {
            System.out.println("  " + CommonsHttpClient + "\t\t" + CommonsHttpClientClass);
            System.out.println("  " + HttpComponentsHttpClient + "\t" + HttpComponentsHttpClientClass);
            System.out.println("  " + SpringRestTemplate + "\t\t" + SpringRestTemplateClass);
        }
    }


    public static void main(String[] args) {

        int exitStatus = 0;


//
//        /* Spring configuration method 1, for single config file */
//        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(Wso2Config.class);
//
        /* Spring configuration method 2, for multiple config files */
        AnnotationConfigApplicationContext springContext = new AnnotationConfigApplicationContext();

//        springContext.register(Wso2Config.class);
//        ctx.register(SpringRestTemplate.class);
        springContext.scan("org.la.spring");
        springContext.refresh();

//        Wso2Credentials credentials = (Wso2Credentials) springContext.getBean("wso2Credentials");
//        System.out.println("client id: " + credentials.getClientId());

//        System.out.println("wso2Credentials client id: " + wso2Credentials.getClientId());

//        for (String beanName : springContext.getBeanDefinitionNames()) {
//            System.out.println("registered bean: " + beanName);
//        }

        TokenHeaderProvider tokenHeaderProvider = (TokenHeaderProvider) springContext.getBean("tokenHeaderProvider");

        // Build command line options
        Options clOptions = new Options();
        clOptions.addOption(Option.builder("h")
                .longOpt("help")
                .desc("Show this help")
                .build());

        String libs = "";
        for (String lib : HttpLib.list()) {
            libs = libs + "\n\t" + lib;
        }

        clOptions.addOption(Option.builder("a")
                .longOpt("add-header")
                .desc("Add header:" + libs)
                .numberOfArgs(2)
                .valueSeparator()
                .argName("header")
                .build());
        clOptions.addOption(Option.builder("l")
                .longOpt("library")
                .desc("JAVA library to use (default: SpringRestTemplate):" + libs)
                .hasArg()
                .argName("library")
                .build());
        clOptions.addOption(Option.builder("o")
                .longOpt("output")
                .desc("output file")
                .hasArg()
                .argName("filename")
                .build());
        clOptions.addOption(Option.builder("v")
                .longOpt("verbose")
                .desc("show request/response details and processing messages")
                .build());
        clOptions.addOption(Option.builder("w")
                .desc("use WSO2 key (prompts for consumer key and secret)")
                .build());

        if(args.length == 0) {
            showCommandHelp(clOptions);
        }
        else {
            exitStatus = processCommandLine(springContext, args, clOptions);
        }

        System.exit(exitStatus);

    }


    private static int processCommandLine(AnnotationConfigApplicationContext springContext, String[] args, Options clOptions) {

        int executeStatus = 0;
        boolean modeVerbose = false;
        String url = "";
        String httpMethod = "";
        String consumerKey = "";
        String consumerSecret = "";

        CommandLineParser clParser = new DefaultParser();


        try {
            CommandLine commandLine = clParser.parse(clOptions, args);

            if (commandLine.hasOption("help")) {
                showCommandHelp(clOptions);
            }
            else {
                if (commandLine.hasOption("verbose")) {
                    modeVerbose = true;
                }

                if (commandLine.hasOption("w")) {
                    // Get a console to run from the command line.
                    Console console = System.console();
                    if (console == null) {
                        System.err.println("No console");
                        System.exit(1);
                    }
                    else {
                        // Get consumer id
                        char[] enterKey = console.readPassword("Consumer key: ");
                        consumerKey = new String(enterKey);

                        // Get consumer secret
                        char[] enterSecret = console.readPassword("Consumer secret: ");
                        consumerSecret = new String(enterSecret);
                    }

                }

                if (commandLine.hasOption("add-header")) {
                    System.out.println("Add Header values:\n");
                    String[] headers = commandLine.getOptionValues("add-header");
                    for (String header: headers) {
                        System.out.println("\t" + header);
                    }
                }

                // Remaining command line parameters, if any, are HTTP Method (GET, POST, etc.) and URL
                List<String> cmdLineUrl = commandLine.getArgList();
                if(cmdLineUrl.size() > 0) {

                    ArrayList<String> httpMethods = new ArrayList<String>(Arrays.asList(
                            HttpMethod.GET.name(),
                            HttpMethod.POST.name(),
                            HttpMethod.PUT.name(),
                            HttpMethod.DELETE.name()));

                    // Single parameter should be URL, default to HTTP GET method
                    if (cmdLineUrl.size() == 1) {
                        if (httpMethods.contains(cmdLineUrl.get(0).toUpperCase())) {
                            System.err.println("Error: no URL");
                        }
                        else {
                            url = cmdLineUrl.get(0);
                        }
                    }
                    // Two parameters (or more) should be URL and HTTP method
                    else {
                        if (httpMethods.contains(cmdLineUrl.get(0).toUpperCase())) {
                            httpMethod = cmdLineUrl.get(0).toUpperCase(); // 1st arg is HTTP method
                            url = cmdLineUrl.get(1); // Assume 2nd arg is URL
                        }
                        else if (httpMethods.contains(cmdLineUrl.get(1).toUpperCase())) {
                            httpMethod = cmdLineUrl.get(1).toUpperCase(); // 2nd arg is HTTP method
                            url = cmdLineUrl.get(0); // Assume 1st arg is URL
                        }
                        else {
                            System.err.println("Invalid HTTP method.");
                        }
                    }

                    if (url.length() > 0) {
                        if (httpMethod.length() == 0) {
                            // Default to GET if no HTTP methods on command line
                            httpMethod = HttpMethod.GET.name();
                        }

                        switch (httpMethod) {
                            case "GET":
                                optionGet(springContext, commandLine, url, modeVerbose, consumerKey, consumerSecret);
                                break;
                            default:
                                System.err.println("Invalid HTTP method. Only GET is valid so far. Sorry.");
                                break;
                        }
                    }
                }
                else {
                    System.out.println("Error: no URL");
                    showCommandHelp(clOptions);
                }
            }
        }
        catch (ParseException e) {
            System.err.println("Command line parsing failed. Error: " + e.getMessage() + "\n");
            showCommandHelp(clOptions);
            executeStatus = 1;
        }

        return executeStatus;
    }


    private static void optionGet(AnnotationConfigApplicationContext springContext,
                                  CommandLine commandLine,
                                  String url,
                                  boolean modeVerbose,
                                  String consumerKey,
                                  String consumerSecret) {
        String outputFilename = null;

        if (commandLine.hasOption("output")) {
            outputFilename = commandLine.getOptionValue("output");
        }

        if (commandLine.hasOption("library")) {
            // Use Java Http library specified
            switch (commandLine.getOptionValue("library")) {
                case HttpLib.CommonsHttpClient:
                    if (modeVerbose) {
                        System.out.println("Using " + HttpLib.CommonsHttpClientClass + " for HTTP processing.");
                        log.debug("Using " + HttpLib.CommonsHttpClientClass + " for HTTP processing.");
                    }
                    doGetApacheCommons(url, outputFilename, modeVerbose);
                    break;
                case HttpLib.HttpComponentsHttpClient:
                    if (modeVerbose) {
                        System.out.println("Using " + HttpLib.HttpComponentsHttpClientClass + " for HTTP processing.");
                        log.debug("Using " + HttpLib.HttpComponentsHttpClientClass + " for HTTP processing.");
                    }
                    doGetApacheHttpComponents(url, outputFilename, modeVerbose);
                    break;
                default:
                    if (modeVerbose) {
                        System.out.println("Using " + HttpLib.SpringRestTemplateClass + " for HTTP processing.");
                        log.debug("Using " + HttpLib.SpringRestTemplateClass + " for HTTP processing.");
                    }
                    doGetSpringRestTemplate(springContext, url, outputFilename, modeVerbose, consumerKey, consumerSecret);
                    break;
            }
        }
        else {
            // Otherwise default to Spring RestTemplate
            if (modeVerbose) {
                System.out.println("Using " + HttpLib.SpringRestTemplateClass + " for HTTP processing.");
                log.debug("Using " + HttpLib.SpringRestTemplateClass + " for HTTP processing.");
            }
            doGetSpringRestTemplate(springContext, url, outputFilename, modeVerbose, consumerKey, consumerSecret);
        }

//        Jersey1RestTest.testRest();
//        Jersey2RestTest.testRest();
    }


    private static void doGetApacheCommons(String url, String outputFilename, boolean modeVerbose) {
        ApacheCommonsHttpClient httpClient = new ApacheCommonsHttpClient();
        String response = httpClient.httpGet(url, modeVerbose);
        if (response != null && response.length() > 0) {
            if (outputFilename != null && outputFilename.length() > 0) {
                writeStringToFile(response, outputFilename, modeVerbose);
            }
            else {
                System.out.println(response);
            }
        }
    }


    private static void doGetApacheHttpComponents(String url, String outputFilename, boolean modeVerbose) {
        ApacheHttpComponentsHttpClient httpClient = new ApacheHttpComponentsHttpClient();
        String response = httpClient.httpGet(url, modeVerbose);
        if (response != null && response.length() > 0) {
            if (outputFilename != null && outputFilename.length() > 0) {
                writeStringToFile(response, outputFilename, modeVerbose);
            }
            else {
                System.out.println(response);
            }
        }
    }


    private static void doGetSpringRestTemplate(AnnotationConfigApplicationContext springContext,
                                                String url,
                                                String outputFilename,
                                                boolean modeVerbose,
                                                String consumerKey,
                                                String consumerSecret) {
        ClientCredentialsTokenHeaderProvider tokenHeaderProvider = (ClientCredentialsTokenHeaderProvider) springContext.getBean("tokenHeaderProvider");
        SpringRestTemplate httpClient = new SpringRestTemplate();
        String response = httpClient.httpGet(url, modeVerbose, tokenHeaderProvider, consumerKey, consumerSecret);
        if (response != null && response.length() > 0) {
            if (outputFilename != null && outputFilename.length() > 0) {
                writeStringToFile(response, outputFilename, modeVerbose);
            }
            else {
                System.out.println(response);
            }
        }
    }


    private static int writeStringToFile(String outputString, String outputFilename, boolean modeVerbose) {
        int status = 0;
        BufferedWriter bufferedWriter = null;
        FileWriter fileWriter = null;

        if (modeVerbose) {
            System.out.println("Writing results to file " + outputFilename);
        }

        try {
            fileWriter = new FileWriter(outputFilename);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(outputString);

        }
        catch (IOException e) {
            System.out.println("Problem writing to file. Error: " + e.getMessage());
            status = 1;
        }
        finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
                if (fileWriter != null) {
                    fileWriter.close();
                }
            }
            catch (IOException ioErr) {
                System.out.println("Problem closing file. Error: " + ioErr.getMessage());
                status = 1;
            }
        }

        return status;
    }


    private static void showCommandHelp(Options options) {
        String commandHelpHeader = "\nTest making an HTTP request to a URL using different Java HTTP libraries.\n" +
                "If no library is specified, SpringRestTemplate is used.\n\n";

        String commandHelpFooter = "\nExamples:\n\n" +
                "  java -jar httptest.jar https://someurl.com/get/stuff\n\n" +
                "  java -jar httptest.jar GET https://someurl.com/get/stuff\n\n" +
                "  java -jar httptest.jar -o myfile.txt GET https://someurl.com/get/stuff\n\n";

        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(88,"java -jar httptest.jar HttpMethod URL", commandHelpHeader, options, commandHelpFooter, true);
        System.out.println("*If no HTTP method is specified, HTTP GET is used.");
        System.out.println("*If no library is specified, SpringRestTemplate is used. The libraries are:\n");
        HttpLib.showPrettyList();
        System.out.println("\nThe Apache Commons HttpClient was widely used until a few years ago but\n" +
        "has been deprecated and replaced by HttpComponents HttpClient.");
    }

}
