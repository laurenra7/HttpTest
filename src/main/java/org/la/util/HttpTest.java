package org.la.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

/**
 * Created by laurenra on 5/12/17.
 */
public class HttpTest {
    private static final Logger log = LoggerFactory.getLogger(SpringRestTemplate.class);

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

        log.info("Logger is working...");

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
                .desc("show processing messages")
                .build());

        if(args.length == 0) {
            showCommandHelp(clOptions);
        }
        else {
            exitStatus = processCommandLine(args, clOptions);
        }

        System.exit(exitStatus);

    }


    private static int processCommandLine(String[] args, Options clOptions) {

        int executeStatus = 0;
        boolean modeVerbose = false;
        String url = "";
        String httpMethod = "";

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
                                optionGet(commandLine, url, modeVerbose);
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


    private static void optionGet(CommandLine commandLine, String url, boolean modeVerbose) {
        // Use Java Http library specified
        if (commandLine.hasOption("library")) {
            switch (commandLine.getOptionValue("library")) {
                case HttpLib.CommonsHttpClient:
                    if (modeVerbose) {
                        System.out.println("Using " + HttpLib.CommonsHttpClientClass + " for HTTP processing.");
                    }
                    doGetApacheCommons(url, commandLine, modeVerbose);
                    break;
                case HttpLib.HttpComponentsHttpClient:
                    if (modeVerbose) {
                        System.out.println("Using " + HttpLib.HttpComponentsHttpClientClass + " for HTTP processing.");
                    }
                    doGetApacheHttpComponents(url, commandLine, modeVerbose);
                    break;
                default:
                    if (modeVerbose) {
                        System.out.println("Using " + HttpLib.SpringRestTemplateClass + " for HTTP processing.");
                    }
                    doGetSpringRestTemplate(url, commandLine, modeVerbose);
                    break;
            }
        }
        // Otherwise default to Spring RestTemplate
        else {
            if (modeVerbose) {
                System.out.println("Using " + HttpLib.SpringRestTemplateClass + " for HTTP processing.");
            }
            doGetSpringRestTemplate(url, commandLine, modeVerbose);
        }
    }


    private static void doGetApacheCommons(String url, CommandLine commandLine, boolean modeVerbose) {
        ApacheCommonsHttpClient httpClient = new ApacheCommonsHttpClient();
        String response = httpClient.httpGet(url, modeVerbose);
        if (response != null && response.length() > 0) {
            if (commandLine.hasOption("output")) {
                writeStringToFile(response, commandLine.getOptionValue("filename"), modeVerbose);
            }
            else {
                System.out.println(response);
            }
        }
    }


    private static void doGetApacheHttpComponents(String url, CommandLine commandLine, boolean modeVerbose) {
        ApacheHttpComponentsHttpClient httpClient = new ApacheHttpComponentsHttpClient();
        String response = httpClient.httpGet(url, modeVerbose);
        if (response != null && response.length() > 0) {
            if (commandLine.hasOption("output")) {
                writeStringToFile(response, commandLine.getOptionValue("filename"), modeVerbose);
            }
            else {
                System.out.println(response);
            }
        }
    }


    private static void doGetSpringRestTemplate(String url, CommandLine commandLine, boolean modeVerbose) {
        SpringRestTemplate httpClient = new SpringRestTemplate();
        String response = httpClient.httpGet(url, modeVerbose);
        if (response != null && response.length() > 0) {
            if (commandLine.hasOption("output")) {
                writeStringToFile(response, commandLine.getOptionValue("filename"), modeVerbose);
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
            System.out.println("Output file: " + outputFilename);
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
