# HttpTest
Test making an HTTP request to a URL using different Java HTTP libraries.

If no library is specified, Spring Framework [RestTemplate](https://docs.spring.io/spring/docs/current/javadoc-api/index.html?org/springframework/web/client/RestTemplate.html) is used.

The other libraries are:

| Library                  | Class                                       |
| ------------------------ | ------------------------------------------- |
| CommonsHttpClient        | [org.apache.commons.httpclient.HttpClient](http://hc.apache.org/httpclient-3.x/)    |
| HttpComponentsHttpClient | [org.apache.http.client.HttpClient](https://hc.apache.org/httpcomponents-client-ga/httpclient/apidocs/index.html?org/apache/http/client/HttpClient.html)           |
| SpringRestTemplate       | [org.springframework.web.client.RestTemplate](https://docs.spring.io/spring/docs/current/javadoc-api/index.html?org/springframework/web/client/RestTemplate.html) |

See [REST in Spring 3: RestTemplate](https://spring.io/blog/2009/03/27/rest-in-spring-3-resttemplate) for tutorial and examples.

Spring uses the [HttpClient](http://hc.apache.org/httpcomponents-client-ga/index.html) from [Apache HttpComponents](http://hc.apache.org/) for the HTTP requests.

### Build

Build with [Maven](https://maven.apache.org/).

```
mvn clean install
```

Produces an executable .jar file

```
/target/httptest.jar
```


### Run

```
java -jar httptest.jar
```


### Options

```
usage: java -jar httptest.jar HttpMethod URL [-h] [-l <library>] [-o <filename>] [-v]

Test making an HTTP request to a URL using different Java HTTP libraries.
If no library is specified, SpringRestTemplate is used.

 -h,--help                Show this help
 -l,--library <library>   JAVA library to use (default: SpringRestTemplate):
                          CommonsHttpClient
                          HttpComponentsHttpClient
                          SpringRestTemplate
 -o,--output <filename>   output file
 -v,--verbose             show processing messages

Examples:

  java -jar httptest.jar https://someurl.com/get/stuff

  java -jar httptest.jar GET https://someurl.com/get/stuff

  java -jar httptest.jar -o myfile.txt GET https://someurl.com/get/stuff

*If no HTTP method is specified, HTTP GET is used.

*If no library is specified, SpringRestTemplate is used. The libraries are:

  CommonsHttpClient		org.apache.commons.httpclient.HttpClient
  HttpComponentsHttpClient	org.apache.http.client.HttpClient
  SpringRestTemplate		org.springframework.web.client.RestTemplate


The Apache Commons HttpClient was widely used until a few years ago but
has been deprecated and replaced by HttpComponents HttpClient.
```
