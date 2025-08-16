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


    // ------ TEST INPUT STREAM ------

    private InputStream generateValidGETTestCase() {
        String rawData = "GET / HTTP/1.1\r\n" + //
                "Host: localhost:8080\r\n" + //
                "Connection: keep-alive\r\n" + //
                "Cache-Control: max-age=0\r\n" + //
                "sec-ch-ua: \"Not;A=Brand\";v=\"99\", \"Google Chrome\";v=\"139\", \"Chromium\";v=\"139\"\r\n" + //
                "sec-ch-ua-mobile: ?0\r\n" + //
                "sec-ch-ua-platform: \"macOS\"\r\n" + //
                "Upgrade-Insecure-Requests: 1\r\n" + //
                "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36\r\n"
                + //
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\r\n"
                + //
                "Sec-Fetch-Site: cross-site\r\n" + //
                "Sec-Fetch-Mode: navigate\r\n" + //
                "Sec-Fetch-User: ?1\r\n" + //
                "Sec-Fetch-Dest: document\r\n" + //
                "Accept-Encoding: gzip, deflate, br, zstd\r\n" + //
                "Accept-Language: en-US,en;q=0.9,hi;q=0.8" + //
                "\r\n";

        InputStream inputStream = new ByteArrayInputStream(
                rawData.getBytes(
                        StandardCharsets.US_ASCII));
        return inputStream;
    }

    private InputStream generateBadTestCaseMethodName1() {
        String rawData = "GeT / HTTP/1.1\r\n" + //
                "Host: localhost:8080\r\n" + //
                "Accept-Language: en-US,en;q=0.9,hi;q=0.8" + //
                "\r\n";

        InputStream inputStream = new ByteArrayInputStream(
                rawData.getBytes(
                        StandardCharsets.US_ASCII));
        return inputStream;
    }

    private InputStream generateBadTestCaseMethodName2() {
        String rawData = "GETTTTTTTTT / HTTP/1.1\r\n" + //
                "Host: localhost:8080\r\n" + //
                "Accept-Language: en-US,en;q=0.9,hi;q=0.8" + //
                "\r\n";

        InputStream inputStream = new ByteArrayInputStream(
                rawData.getBytes(
                        StandardCharsets.US_ASCII));
        return inputStream;
    }

    private InputStream generateBadTestCaseRequestLineInvNumItems1() {
        String rawData = "GET / AAAAAA HTTP/1.1\r\n" + //
                "Host: localhost:8080\r\n" + //
                "Accept-Language: en-US,en;q=0.9,hi;q=0.8" + //
                "\r\n";

        InputStream inputStream = new ByteArrayInputStream(
                rawData.getBytes(
                        StandardCharsets.US_ASCII));
        return inputStream;
    }
    
    private InputStream generateBadTestCaseEmptyRequestLine() {
        String rawData = "\r\n" + //
                "Host: localhost:8080\r\n" + //
                "Accept-Language: en-US,en;q=0.9,hi;q=0.8" + //
                "\r\n";

        InputStream inputStream = new ByteArrayInputStream(
                rawData.getBytes(
                        StandardCharsets.US_ASCII));
        return inputStream;
    }

    private InputStream generateBadTestCaseRequestLineOnlyCRnoLF() {
        String rawData = "GET / HTTP/1.1\r" + // <===== no LF
                "Host: localhost:8080\r\n" + //
                "Accept-Language: en-US,en;q=0.9,hi;q=0.8" + //
                "\r\n";

        InputStream inputStream = new ByteArrayInputStream(
                rawData.getBytes(
                        StandardCharsets.US_ASCII));
        return inputStream;
    }
}
