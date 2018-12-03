# HttpTest
Test making an HTTP request to a URL using different Java HTTP libraries.

If no library is specified, Spring Framework
[RestTemplate](https://docs.spring.io/spring/docs/current/javadoc-api/index.html?org/springframework/web/client/RestTemplate.html)
is used. If no HTTP method is specified, HTTP GET is used.

The other libraries are:

| Library                  | Class                                       |
| ------------------------ | ------------------------------------------- |
| CommonsHttpClient        | [org.apache.commons.httpclient.HttpClient](http://hc.apache.org/httpclient-3.x/)    |
| HttpComponentsHttpClient | [org.apache.http.client.HttpClient](https://hc.apache.org/httpcomponents-client-ga/httpclient/apidocs/index.html?org/apache/http/client/HttpClient.html)           |
| SpringRestTemplate       | [org.springframework.web.client.RestTemplate](https://docs.spring.io/spring/docs/current/javadoc-api/index.html?org/springframework/web/client/RestTemplate.html) |

See [REST in Spring 3: RestTemplate](https://spring.io/blog/2009/03/27/rest-in-spring-3-resttemplate)
for tutorial and examples.

Spring uses the [HttpClient](http://hc.apache.org/httpcomponents-client-ga/index.html)
from [Apache HttpComponents](http://hc.apache.org/) for the HTTP requests.

The Apache Commons HttpClient was widely used until a few years ago but
has been deprecated and replaced by HttpComponents HttpClient.

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
usage: java -jar httptest.jar URL [-a <header>] [-h] [-k <key>] [-l <library>] [-m
       <method>] [-o <filename>] [-p] [-q] [-v]

Test an HTTP REST request using common Java HTTP libraries.

Options:

 -a,--add-header <header>   Add header(s). Add multiple headers with additional -a. Use
                            commas to separate multiple values in header. See examples.
 -h,--help                  Show this help
 -k,--key <key>             Use Oauth/WSO2 consumer key (prompts for consumer secret)
 -l,--library <library>     JAVA library to use (default is SpringRestTemplate):
                            -CommonsHttpClient
                            -HttpComponentsHttpClient
                            -SpringRestTemplate
 -m,--method <method>       HTTP method GET, POST, PUT, DELETE (default is GET)
 -o,--output <filename>     Output file
 -p,--pretty                Pretty-print JSON response
 -q,--quiet                 Don't display HTTP response body
 -v,--verbose               Show request/response details and processing messages

Examples:

  java -jar httptest.jar https://byu.edu/clubs

  java -jar httptest.jar https://byu.edu/clubs -m GET

  java -jar httptest.jar https://byu.edu/clubs -l CommonsHttpClient

  java -jar httptest.jar https://byu.edu/clubs -o myfile.txt -m GET

  java -jar httptest.jar https://byu.edu/clubs -a Accept=text/html,application/xml

  java -jar httptest.jar https://byu.edu/clubs -a Flavor=sweet -a Colors=red,green

  java -jar httptest.jar https://byu.edu/clubs -k myConsumerKey -o results.json

Notes:

  -Use commas to separate values in multi-valued header as shown in example.
  -If no HTTP method is specified, HTTP GET is used.
  -If no library is specified, SpringRestTemplate is used. The libraries are:

  Library                     Class
  ------------------------    -------------------------------------------
  CommonsHttpClient           org.apache.commons.httpclient.HttpClient
  HttpComponentsHttpClient    org.apache.http.client.HttpClient
  SpringRestTemplate          org.springframework.web.client.RestTemplate

  The Apache Commons HttpClient was widely used until a few years ago but it
  has been deprecated and replaced by HttpComponents HttpClient.
```
