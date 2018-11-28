package org.la;

import java.io.BufferedWriter;
import java.io.Console;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.status.StatusLogger;
import org.la.http.HttpRest;
import org.la.http.SpringRestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

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

//        public static String[] list() {
//            String[] list = {CommonsHttpClient, HttpComponentsHttpClient, SpringRestTemplate};
//            return list;
//        }

        public static String helpList() {
            String libs = "\n-" + CommonsHttpClient + "\n" +
                    "-" + HttpComponentsHttpClient + "\n" +
                    "-" + SpringRestTemplate;
            return libs;
        }

        public static String prettyList() {
            String prettyList =
                    "  " + "Library                     Class\n" +
//                    "  " + "HttpComponentsHttpClient" + "\t\t" + "org.springframework.web.client.RestTemplate" +
                    "  " + "------------------------    -------------------------------------------\n" +
//                    "  " + "________________________    ___________________________________________\n" +
                    "  " + CommonsHttpClient + "           " + CommonsHttpClientClass + "\n" +
                    "  " + HttpComponentsHttpClient + "    " + HttpComponentsHttpClientClass + "\n" +
                    "  " + SpringRestTemplate + "          " + SpringRestTemplateClass;
            return prettyList;
        }
    }


    /**
     * Program entry point
     * @param args
     */
    public static void main(String[] args) {

        int exitStatus = 0;
//
//        /* Spring configuration method 1, for single config file */
//        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(Wso2Config.class);
//
//        /* Spring configuration method 2, for multiple config files */
//        AnnotationConfigApplicationContext springContext = new AnnotationConfigApplicationContext();
//
////        springContext.register(Wso2Config.class);
////        ctx.register(SpringRestTemplate.class);
//        springContext.scan("org.la.spring");
//        springContext.refresh();


        // Build command line options
        Options clOptions = new Options();

        clOptions.addOption(Option.builder("a")
                .longOpt("add-header")
                .desc("Add header(s)")
                .numberOfArgs(2)
                .valueSeparator()
                .argName("header")
                .build());

        clOptions.addOption(Option.builder("h")
                .longOpt("help")
                .desc("Show this help")
                .build());

        clOptions.addOption(Option.builder("k")
                .longOpt("key")
                .desc("Use WSO2 consumer key (prompts for consumer secret)")
                .hasArg()
                .argName("key")
                .build());

        clOptions.addOption(Option.builder("l")
                .longOpt("library")
                .desc("JAVA library to use (default is SpringRestTemplate):" + HttpLib.helpList())
                .hasArg()
                .argName("library")
                .build());

        clOptions.addOption(Option.builder("m")
                .longOpt("method")
                .desc("HTTP method GET, POST, PUT, DELETE (default is GET)")
                .hasArg()
                .argName("method")
                .build());

        clOptions.addOption(Option.builder("o")
                .longOpt("output")
                .desc("Output file")
                .hasArg()
                .argName("filename")
                .build());

        clOptions.addOption(Option.builder("u")
                .required()
                .longOpt("url")
                .desc("URL")
                .hasArg()
                .argName("url")
                .build());

        clOptions.addOption(Option.builder("v")
                .longOpt("verbose")
                .desc("Show request/response details and processing messages")
                .build());

        if(args.length == 0) {
            showDetailedHelp(clOptions);
        }
        else {
            exitStatus = processCommandLine(args, clOptions);
        }

        System.exit(exitStatus);

    }


    /**
     * processCommandLine
     * @param args
     * @param clOptions
     * @return
     */
    private static int processCommandLine(String[] args, Options clOptions) {

        int executeStatus = 0;
        boolean verbose = false;
        HttpRest httpRestService;
        String httpResponse = "";

        CommandLineParser clParser = new DefaultParser();

        try {
            CommandLine commandLine = clParser.parse(clOptions, args);

            if (commandLine.hasOption("help")) {
                showDetailedHelp(clOptions);
            }
            else {

                if (commandLine.hasOption("verbose")) {
                    verbose = true;
                }

                // Get HTTP library to use and instantiate it
                if (commandLine.hasOption("library")) {
                    // Use Java Http library specified
                    switch (commandLine.getOptionValue("library")) {
//                        case HttpLib.CommonsHttpClient:
//                            if (verbose) {
//                                System.out.println("Using " + HttpLib.CommonsHttpClientClass + " for HTTP processing.");
//                                log.debug("Using " + HttpLib.CommonsHttpClientClass + " for HTTP processing.");
//                            }
//
//                            doGetApacheCommons(url, outputFilename, verbose);
//                            break;
//                        case HttpLib.HttpComponentsHttpClient:
//                            if (verbose) {
//                                System.out.println("Using " + HttpLib.HttpComponentsHttpClientClass + " for HTTP processing.");
//                                log.debug("Using " + HttpLib.HttpComponentsHttpClientClass + " for HTTP processing.");
//                            }
//                            doGetApacheHttpComponents(url, outputFilename, verbose);
//                            break;
                        default:
                            if (verbose) {
                                System.out.println("Using " + HttpLib.SpringRestTemplateClass + " for HTTP processing.");
                                log.debug("Using " + HttpLib.SpringRestTemplateClass + " for HTTP processing.");
                            }
                            httpRestService = new SpringRestTemplate();
                            break;
                    }
                }
                else {
                    // Otherwise default to Spring RestTemplate
                    if (verbose) {
                        System.out.println("Using " + HttpLib.SpringRestTemplateClass + " for HTTP processing.");
                        log.debug("Using " + HttpLib.SpringRestTemplateClass + " for HTTP processing.");
                    }
                    httpRestService = new SpringRestTemplate();
                }

                // Set verbose (true/false) in the Http REST service object.
                httpRestService.setVerbose(verbose);

                // Get consumer key and secret.
                if (commandLine.hasOption("key")) {
                    // Get a console to run from the command line to prompt for secret.
                    Console console = System.console();
                    if (console == null) {
                        System.err.println("No console");
                        System.exit(1);
                    }
                    else {
                        // Get consumer key (ID)
                        httpRestService.setConsumerKey(commandLine.getOptionValue("key"));

                        // Prompt for consumer secret
                        char[] enterSecret = console.readPassword("Consumer secret: ");
                        httpRestService.setConsumerSecret(new String(enterSecret));
                        System.out.println("input key:    " + httpRestService.getConsumerKey());
                        System.out.println("input secret: " + httpRestService.getConsumerSecret());
                    }
                }

                // Get headers
                if (commandLine.hasOption("add-header")) {
                    System.out.println("Add Header values:\n");
                    String[] headers = commandLine.getOptionValues("add-header");
                    for (String header: headers) {
                        System.out.println("\t" + header);
                    }
                }

                // Get HTTP method and make HTTP request.
                if (commandLine.hasOption("method")) {
                    switch(commandLine.getOptionValue("method").toUpperCase()) {
                        case "GET":
                            httpRestService.httpGet(commandLine.getOptionValue("url"));
                            break;
                        default:
                            System.err.println("Invalid HTTP method. (Only GET is valid so far...sorry.)");
                            break;
                    }
                }
                else {
                    // Default to GET and make HTTP request.
                    httpResponse = httpRestService.httpGet(commandLine.getOptionValue("url"));
                }

                // Display response.
                System.out.println("---------- HTTP response body ----------");
                System.out.println(httpResponse);

                // Write response to file.
                if (commandLine.hasOption("filename")) {
                    System.out.println("Got filename: " + commandLine.getOptionValue("filename"));
                    if (verbose) {
                        System.out.println("Writing results to file " + commandLine.getOptionValue("filename"));
                    }
                    writeStringToFile(httpResponse, commandLine.getOptionValue("filename"));
                }

                if (commandLine.hasOption("output")) {
                    System.out.println("Got output filename: " + commandLine.getOptionValue("output"));
                    if (verbose) {
                        System.out.println("Writing results to file " + commandLine.getOptionValue("output"));
                    }
                    writeStringToFile(httpResponse, commandLine.getOptionValue("output"));
                }
            }
        }
        catch (ParseException e) {
            System.err.println("Command line parsing failed. Error: " + e.getMessage() + "\n");
            showBriefHelp(clOptions);
            executeStatus = 1;
        }

        return executeStatus;
    }


    private static void doGetApacheCommons(String url, String outputFilename, boolean modeVerbose) {
        ApacheCommonsHttpClient httpClient = new ApacheCommonsHttpClient();
        String response = httpClient.httpGet(url, modeVerbose);
        if (response != null && response.length() > 0) {
            if (outputFilename != null && outputFilename.length() > 0) {
//                writeStringToFile(response, outputFilename, modeVerbose);
                writeStringToFile(response, outputFilename);
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
//                writeStringToFile(response, outputFilename, modeVerbose);
                writeStringToFile(response, outputFilename);
            }
            else {
                System.out.println(response);
            }
        }
    }


    private static int writeStringToFile(String outputString, String outputFilename) {
        int status = 0;
        BufferedWriter bufferedWriter = null;
        FileWriter fileWriter = null;

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


    private static void showBriefHelp(Options options) {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(88,"java -jar httptest.jar", "\nOptions:\n\n", options, "", true);
    }


    private static void showDetailedHelp(Options options) {
        String commandHelpHeader = "\nTest an HTTP REST request using common Java HTTP libraries.\n\n" +
                "Options:\n\n";

        String commandHelpFooter = "\nExamples:\n\n" +
                "  java -jar httptest.jar -u https://byu.edu/clubs\n\n" +
                "  java -jar httptest.jar -u https://byu.edu/clubs -m GET\n\n" +
                "  java -jar httptest.jar -u https://byu.edu/clubs -l CommonsHttpClient\n\n" +
                "  java -jar httptest.jar -u https://byu.edu/clubs -o myfile.txt -m GET\n\n" +
                "  java -jar httptest.jar -u https://byu.edu/clubs -a Accept=text/html,application/xml\n\n" +
                "  java -jar httptest.jar -u https://byu.edu/clubs -a Flavor=sweet -a Colors=red,green\n\n" +
                "  java -jar httptest.jar -u https://byu.edu/clubs -k myConsumerKey -o results.json\n\n" +
                "Notes:\n\n" +
                "  If no HTTP method is specified, HTTP GET is used.\n" +
                "  If no library is specified, SpringRestTemplate is used. The libraries are:\n\n" +
                HttpLib.prettyList() + "\n\n" +
                "The Apache Commons HttpClient was widely used until a few years ago but it\n" +
                "has been deprecated and replaced by HttpComponents HttpClient.\n\n";

        System.out.println("");
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(88,"java -jar httptest.jar", commandHelpHeader, options, commandHelpFooter, true);
    }

}
