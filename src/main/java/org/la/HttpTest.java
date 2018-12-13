package org.la;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.status.StatusLogger;
import org.la.http.ApacheCommonsHttpClient;
import org.la.http.ApacheHttpComponentsHttpClient;
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

    private static void showBriefHelp(Options options) {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(88,"java -jar httptest.jar URL", "\nOptions:\n\n", options, "", true);
    }

    private static void showDetailedHelp(Options options) {
        String commandHelpHeader = "\nTest an HTTP REST request using common Java HTTP libraries.\n\n" +
                "Options:\n\n";

        String commandHelpFooter = "\nExamples:\n\n" +
                "  java -jar httptest.jar https://byu.edu/clubs\n\n" +
                "  java -jar httptest.jar https://byu.edu/clubs -m GET\n\n" +
                "  java -jar httptest.jar https://byu.edu/clubs -l CommonsHttpClient\n\n" +
                "  java -jar httptest.jar https://byu.edu/clubs -o myfile.txt -m GET\n\n" +
                "  java -jar httptest.jar https://byu.edu/clubs -a Accept=text/html,application/xml\n\n" +
                "  java -jar httptest.jar https://byu.edu/clubs -a Flavor=sweet -a Colors=red,green\n\n" +
                "  java -jar httptest.jar https://byu.edu/clubs -k myConsumerKey -o results.json\n\n" +
                "Notes:\n\n" +
                "  -Use commas to separate values in multi-valued header as shown in example.\n" +
                "  -If no HTTP method is specified, HTTP GET is used.\n" +
                "  -If no library is specified, SpringRestTemplate is used. The libraries are:\n\n" +
                HttpLib.prettyList() + "\n\n" +
                "  The Apache Commons HttpClient was widely used until a few years ago but it\n" +
                "  has been deprecated and replaced by HttpComponents HttpClient.\n\n" +
                "  You can use query parameters, but if it fails, URL encode the parameters\n" +
                "  and try again.\n\n";

        System.out.println("");
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(88,"java -jar httptest.jar URL", commandHelpHeader, options, commandHelpFooter, true);
    }


    /**
     * Program entry point
     * @param args
     */
    public static void main(String[] args) {

        int exitStatus = 0;

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

        /* Build command line options */
        Options clOptions = new Options();

        clOptions.addOption(Option.builder("a")
                .longOpt("add-header")
                .desc("Add header(s). Add multiple headers with additional -a. Use commas to separate multiple values in header. See examples.")
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
                .desc("Use Oauth/WSO2 consumer key (prompts for consumer secret)")
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

        clOptions.addOption(Option.builder("p")
                .longOpt("pretty")
                .desc("Pretty-print JSON response")
                .build());

        clOptions.addOption(Option.builder("q")
                .longOpt("quiet")
                .desc("Don't display HTTP response body")
                .build());

        clOptions.addOption(Option.builder("v")
                .longOpt("verbose")
                .desc("Show request/response details and processing messages")
                .build());

//        clOptions.addOption(Option.builder("y")
//                .longOpt("query")
//                .desc("(not fully implemented) Add query. Add multiple queries with additional -y. Use commas to separate multiple parameters in query. See examples.")
//                .numberOfArgs(2)
//                .valueSeparator()
//                .argName("query")
//                .build());

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
     *
     * Process the command line options, build the HTTP request and send it using the
     * specified Java REST library.
     *
     * @param args
     * @param clOptions
     * @return
     */
    private static int processCommandLine(String[] args, Options clOptions) {

        int executeStatus = 0;
        boolean verbose = false;
        HttpRest httpRestService;
        String url = "";
        String httpResponseBody = "";

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

                /* Remaining command line parameter (not associated with an option) is the URL */
                List<String> cmdLineUrl = commandLine.getArgList();
                if (cmdLineUrl.size() == 1) {
                    url = cmdLineUrl.get(0);
                }
                else {
                    System.err.println("Error: no URL provided");
                    showBriefHelp(clOptions);
                    executeStatus = 1;
                    System.exit(1);
                }

                /* Get HTTP library to use and instantiate it */
                if (commandLine.hasOption("library")) {
                    switch (commandLine.getOptionValue("library")) {
                        case HttpLib.CommonsHttpClient:
                            if (verbose) {
                                System.out.println("Using " + HttpLib.CommonsHttpClientClass + " for HTTP processing.");
                                log.debug("Using " + HttpLib.CommonsHttpClientClass + " for HTTP processing.");
                            }

                            httpRestService = new ApacheCommonsHttpClient();
                            break;
                        case HttpLib.HttpComponentsHttpClient:
                            if (verbose) {
                                System.out.println("Using " + HttpLib.HttpComponentsHttpClientClass + " for HTTP processing.");
                                log.debug("Using " + HttpLib.HttpComponentsHttpClientClass + " for HTTP processing.");
                            }
                            httpRestService = new ApacheHttpComponentsHttpClient();
                            break;
                        default:
                            log.debug("Error: unrecognized library. Using " + HttpLib.SpringRestTemplateClass + " for HTTP processing.");
                            if (verbose) {
                                System.out.println("Error: unrecognized library. Using " + HttpLib.SpringRestTemplateClass + " for HTTP processing.");
                            }
                            httpRestService = new SpringRestTemplate();
                            break;
                    }
                }
                else {
                    /* Otherwise default to Spring RestTemplate */
                    log.debug("Using " + HttpLib.SpringRestTemplateClass + " for HTTP processing.");
                    if (verbose) {
                        System.out.println("Using " + HttpLib.SpringRestTemplateClass + " for HTTP processing.");
                    }
                    httpRestService = new SpringRestTemplate();
                }

                /* Set verbose (true/false) in the HTTP REST service object */
                httpRestService.setVerbose(verbose);

                /* If quiet mode, override verbose, otherwise set v */
                if (commandLine.hasOption("quiet")) {
                    httpRestService.setVerbose(false);
                }
                else {
                    httpRestService.setVerbose(verbose);
                }


                /* Get consumer key and secret. */
                if (commandLine.hasOption("key")) {
                    /* Get a console to run from the command line to prompt for secret */
                    Console console = System.console();
                    if (console == null) {
                        System.err.println("No console");
                        executeStatus = 1;
                        System.exit(1);
                    }
                    else {
                        /* Get consumer key (ID) */
                        httpRestService.setConsumerKey(commandLine.getOptionValue("key"));

                        /* Prompt for consumer secret */
                        char[] enterSecret = console.readPassword("Consumer secret: ");
                        httpRestService.setConsumerSecret(new String(enterSecret));
                    }
                }

                /* Get headers and add them to the HTTP request */
                if (commandLine.hasOption("add-header")) {
                    Map<String, String> headersMap = mapEntries(commandLine.getOptionValues("add-header"));

                    /* Show headers entered from command line */
                    if (verbose) {
                        System.out.println("Headers to be added:");
                        for (Map.Entry<String, String> entry : headersMap.entrySet()) {
                            System.out.println("\t" + entry.getKey() + ": " + entry.getValue());
                        }
                    }

                    httpRestService.setHeaders(headersMap);
                }

                /* Get query(ies) and add them to the HTTP request */
                if (commandLine.hasOption("query")) {
                    Map<String, String> queriesMap = mapEntries(commandLine.getOptionValues("query"));

                    /* Show headers entered from command line */
                    if (verbose) {
                        System.out.println("Queries to be added:");
                        for (Map.Entry<String, String> entry : queriesMap.entrySet()) {
                            System.out.println("\t" + entry.getKey() + ": " + entry.getValue());
                        }
                    }

                    httpRestService.setQueries(queriesMap);
                }

                /* Get HTTP method and make HTTP request */
                if (commandLine.hasOption("method")) {
                    switch(commandLine.getOptionValue("method").toUpperCase()) {
                        case "GET":
                            httpRestService.httpGet(url);
                            break;
                        default:
                            System.err.println("Invalid HTTP method. (Only GET is valid so far...sorry.)");
                            break;
                    }
                }
                else {
                    /* Default to GET and make HTTP request */
                    httpResponseBody = httpRestService.httpGet(url);
                }

                /* Pretty-print JSON */
                if (commandLine.hasOption("pretty")) {
                    log.debug("pretty-printing JSON...");
                    if (verbose) {
                        System.out.println("pretty-printing JSON...");
                    }

                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    JsonParser jsonParser = new JsonParser();
                    JsonElement jsonElement = jsonParser.parse(httpResponseBody);
                    httpResponseBody = gson.toJson(jsonElement);
                }

                /* Show response body unless quiet mode */
                if (!commandLine.hasOption("quiet")) {
                    System.out.println(httpResponseBody);
                }

                /* Write response to file (regardless of quiet mode) */
                if (commandLine.hasOption("output")) {
                    if (verbose) {
                        System.out.println("Writing results to file " + commandLine.getOptionValue("output"));
                    }
                    writeStringToFile(httpResponseBody, commandLine.getOptionValue("output"));
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


    private static Map<String, String> mapEntries(String [] arrayEntries) {

        Map<String, String> entriesMap = new HashMap<String, String>();

        /* Even-numbered strings in array are keys, odd-numbered are values */
        int i = 0;
        String entryKey = "";
        String entryValue = "";
        for (String element: arrayEntries) {
            if (i % 2 == 0) {
                /* even is header name (key) */
                entryKey = element;
            }
            else {
                /* odd is header value */
                entryValue = element;
                /* replace commas separating values with semicolons (for valid multi-value headers) */
                entriesMap.put(entryKey, entryValue.replaceAll(",", ";"));
                entryKey = "";
                entryValue = "";
            }
            i++;
        }

        return entriesMap;
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

}
