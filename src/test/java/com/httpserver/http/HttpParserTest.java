package com.httpserver.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

// Test class for HttpParser
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HttpParserTest {

    private HttpParser httpParser;

    @BeforeAll
    public void beforeClass() {
        httpParser = new HttpParser();
    }

    // For testing valid input stream
    @Test
    void parseHttpRequest() {
        HttpRequest request = null;
        try {
            request = httpParser.parseHttpRequest(
                    generateValidGETTestCase());
        } catch (HttpParsingException e) {
            fail(e); // Fail the test if an exception is thrown during parsing of a valid request

        }
        assertNotNull(request);
        assertEquals(request.getMethod(), HttpMethod.GET);
        assertEquals(request.getRequestTarget(), "/");
        assertEquals(request.getOriginalHttpVersion(), "HTTP/1.1");
        assertEquals(request.getBestCompatibleHttpVersion(), HttpVersion.HTTP_1_1);
    }

    // For testing invalid input stream
    @Test
    void parseHttpRequestBadMethod1() {
        try {
            HttpRequest request = httpParser.parseHttpRequest(
                    generateBadTestCaseMethodName1());
            fail(); // Test should fail if exception is not thrown
        } catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED);
        }
    }

    // For testing bigger length HTTP method of input stream (which is invalid)
    @Test
    void parseHttpRequestBadMethod2() {
        try {
            HttpRequest request = httpParser.parseHttpRequest(
                    generateBadTestCaseMethodName2());
            fail(); // Test should fail if exception is not thrown
        } catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED);
        }
    }

    // For testing more than 3 items in the Request Line of input stream (which is invalid)
    @Test
    void parseHttpRequestInvNumItems1() {
        try {
            HttpRequest request = httpParser.parseHttpRequest(
                    generateBadTestCaseRequestLineInvNumItems1());
            fail(); // Test should fail if exception is not thrown
        } catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }
    }

    // For testing empty items in the Request Line of input stream (which is invalid)
    @Test
    void parseHttpEmptyRequestLine() {
        try {
            HttpRequest request = httpParser.parseHttpRequest(
                    generateBadTestCaseEmptyRequestLine());
            fail(); // Test should fail if exception is not thrown
        } catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }
    }

    // For testing only CR and not LF in the Request Line of input stream (which is invalid)
    @Test
    void parseHttpRequestLineCRnoLF() {
        try {
            HttpRequest request = httpParser.parseHttpRequest(
                    generateBadTestCaseRequestLineOnlyCRnoLF());
            fail(); // Test should fail if exception is not thrown
        } catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }
    }

    
    // For testing a request with an invalid HTTP version in the request line throws a 400 Bad Request error
    @Test
    void parseHttpRequestBadHttpVersion() {
        try {
            HttpRequest request = httpParser.parseHttpRequest(
                    generateBadHttpVersionTestCase());
            fail(); // Test should fail if exception is not thrown
        } catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }
    }

    // For testing a request with an unsupported HTTP version (e.g., HTTP/2.1) throws a 505 HTTP Version Not Supported error
    @Test
    void parseHttpRequestUnsupportedHttpVersion() {
        try {
            HttpRequest request = httpParser.parseHttpRequest(
                    generateUnsupportedHttpVersionTestCase());
            fail(); // Test should fail if exception is not thrown
        } catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCode.SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED);
        }
    }

    // For testing a request with a higher minor HTTP version (e.g., HTTP/1.2) is accepted and mapped to the best compatible supported version (HTTP/1.1)
    @Test
    void parseHttpRequestSupportedHttpVersion1() {
        try {
            HttpRequest request = httpParser.parseHttpRequest(
                    generateSupportedHttpVersionTestCase1());
                    assertNotNull(request);
                    assertEquals(request.getBestCompatibleHttpVersion(), HttpVersion.HTTP_1_1);
                    assertEquals(request.getOriginalHttpVersion(), "HTTP/1.2");
        } catch (HttpParsingException e) {
            fail();
        }
    }


    // ------ TEST INPUT STREAM ------

    private InputStream generateValidGETTestCase() {
        String rawData = "GET / HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "Cache-Control: max-age=0\r\n" +
                "sec-ch-ua: \"Not;A=Brand\";v=\"99\", \"Google Chrome\";v=\"139\", \"Chromium\";v=\"139\"\r\n" +
                "sec-ch-ua-mobile: ?0\r\n" +
                "sec-ch-ua-platform: \"macOS\"\r\n" +
                "Upgrade-Insecure-Requests: 1\r\n" +
                "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36\r\n"
                +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\r\n"
                +
                "Sec-Fetch-Site: cross-site\r\n" +
                "Sec-Fetch-Mode: navigate\r\n" +
                "Sec-Fetch-User: ?1\r\n" +
                "Sec-Fetch-Dest: document\r\n" +
                "Accept-Encoding: gzip, deflate, br, zstd\r\n" +
                "Accept-Language: en-US,en;q=0.9,hi;q=0.8" +
                "\r\n";

        InputStream inputStream = new ByteArrayInputStream(
                rawData.getBytes(
                        StandardCharsets.US_ASCII));
        return inputStream;
    }

    private InputStream generateBadTestCaseMethodName1() {
        String rawData = "GeT / HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Accept-Language: en-US,en;q=0.9,hi;q=0.8" +
                "\r\n";

        InputStream inputStream = new ByteArrayInputStream(
                rawData.getBytes(
                        StandardCharsets.US_ASCII));
        return inputStream;
    }

    private InputStream generateBadTestCaseMethodName2() {
        String rawData = "GETTTTTTTTT / HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Accept-Language: en-US,en;q=0.9,hi;q=0.8" +
                "\r\n";

        InputStream inputStream = new ByteArrayInputStream(
                rawData.getBytes(
                        StandardCharsets.US_ASCII));
        return inputStream;
    }

    private InputStream generateBadTestCaseRequestLineInvNumItems1() {
        String rawData = "GET / AAAAAA HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Accept-Language: en-US,en;q=0.9,hi;q=0.8" +
                "\r\n";

        InputStream inputStream = new ByteArrayInputStream(
                rawData.getBytes(
                        StandardCharsets.US_ASCII));
        return inputStream;
    }
    
    private InputStream generateBadTestCaseEmptyRequestLine() {
        String rawData = "\r\n" +
                "Host: localhost:8080\r\n" +
                "Accept-Language: en-US,en;q=0.9,hi;q=0.8" +
                "\r\n";

        InputStream inputStream = new ByteArrayInputStream(
                rawData.getBytes(
                        StandardCharsets.US_ASCII));
        return inputStream;
    }

    private InputStream generateBadTestCaseRequestLineOnlyCRnoLF() {
        String rawData = "GET / HTTP/1.1\r" + // <===== no LF
                "Host: localhost:8080\r\n" +
                "Accept-Language: en-US,en;q=0.9,hi;q=0.8" +
                "\r\n";

        InputStream inputStream = new ByteArrayInputStream(
                rawData.getBytes(
                        StandardCharsets.US_ASCII));
        return inputStream;
    }

    private InputStream generateBadHttpVersionTestCase() {
        String rawData = "GET / HTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "Cache-Control: max-age=0\r\n" +
                "sec-ch-ua: \"Not;A=Brand\";v=\"99\", \"Google Chrome\";v=\"139\", \"Chromium\";v=\"139\"\r\n" +
                "sec-ch-ua-mobile: ?0\r\n" + 
                "sec-ch-ua-platform: \"macOS\"\r\n" +
                "Upgrade-Insecure-Requests: 1\r\n" +
                "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36\r\n"
                +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\r\n"
                +
                "Sec-Fetch-Site: cross-site\r\n" +
                "Sec-Fetch-Mode: navigate\r\n" +
                "Sec-Fetch-User: ?1\r\n" +
                "Sec-Fetch-Dest: document\r\n" +
                "Accept-Encoding: gzip, deflate, br, zstd\r\n" +
                "Accept-Language: en-US,en;q=0.9,hi;q=0.8" +
                "\r\n";

        InputStream inputStream = new ByteArrayInputStream(
                rawData.getBytes(
                        StandardCharsets.US_ASCII));
        return inputStream;
    }

    private InputStream generateUnsupportedHttpVersionTestCase() {
        String rawData = "GET / HTTP/2.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "Cache-Control: max-age=0\r\n" +
                "sec-ch-ua: \"Not;A=Brand\";v=\"99\", \"Google Chrome\";v=\"139\", \"Chromium\";v=\"139\"\r\n" +
                "sec-ch-ua-mobile: ?0\r\n" + 
                "sec-ch-ua-platform: \"macOS\"\r\n" +
                "Upgrade-Insecure-Requests: 1\r\n" +
                "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36\r\n"
                +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\r\n"
                +
                "Sec-Fetch-Site: cross-site\r\n" +
                "Sec-Fetch-Mode: navigate\r\n" +
                "Sec-Fetch-User: ?1\r\n" +
                "Sec-Fetch-Dest: document\r\n" +
                "Accept-Encoding: gzip, deflate, br, zstd\r\n" +
                "Accept-Language: en-US,en;q=0.9,hi;q=0.8" +
                "\r\n";

        InputStream inputStream = new ByteArrayInputStream(
                rawData.getBytes(
                        StandardCharsets.US_ASCII));
        return inputStream;
    }

    private InputStream generateSupportedHttpVersionTestCase1() {
        String rawData = "GET / HTTP/1.2\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "Cache-Control: max-age=0\r\n" +
                "sec-ch-ua: \"Not;A=Brand\";v=\"99\", \"Google Chrome\";v=\"139\", \"Chromium\";v=\"139\"\r\n" +
                "sec-ch-ua-mobile: ?0\r\n" + 
                "sec-ch-ua-platform: \"macOS\"\r\n" +
                "Upgrade-Insecure-Requests: 1\r\n" +
                "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36\r\n"
                +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\r\n"
                +
                "Sec-Fetch-Site: cross-site\r\n" +
                "Sec-Fetch-Mode: navigate\r\n" +
                "Sec-Fetch-User: ?1\r\n" +
                "Sec-Fetch-Dest: document\r\n" +
                "Accept-Encoding: gzip, deflate, br, zstd\r\n" +
                "Accept-Language: en-US,en;q=0.9,hi;q=0.8" +
                "\r\n";

        InputStream inputStream = new ByteArrayInputStream(
                rawData.getBytes(
                        StandardCharsets.US_ASCII));
        return inputStream;
    }
}
