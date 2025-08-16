package com.httpserver.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.httpserver.core.io.ReadFileException;
import com.httpserver.core.io.WebRootHandler;
import com.httpserver.core.io.WebRootNotFoundException;
import com.httpserver.http.HttpParser;
import com.httpserver.http.HttpParsingException;
import com.httpserver.http.HttpRequest;

public class HttpConnectionWorkerThread extends Thread {

    private final static Logger LOGGER = LoggerFactory.getLogger(HttpConnectionWorkerThread.class);
    private Socket socket;
    private String webroot;

    public HttpConnectionWorkerThread(Socket socket, String webroot) {
        this.socket = socket;
        this.webroot = webroot;
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            inputStream = socket.getInputStream(); // allowing the server to read data sent by the client
            outputStream = socket.getOutputStream(); // allowing the server to send data back to the client

            // Parse the HTTP request
            HttpParser httpParser = new HttpParser();
            HttpRequest request = httpParser.parseHttpRequest(inputStream);

            // Create WebRootHandler to serve files
            WebRootHandler webRootHandler = new WebRootHandler(webroot);

            // Get the requested path
            String requestTarget = request.getRequestTarget();

            try {
                // Get file content and MIME type
                byte[] fileContent = webRootHandler.getFileByteArrayData(requestTarget);
                String mimeType = webRootHandler.getFileMimeType(requestTarget);

                // Send successful response
                sendResponse(outputStream, 200, "OK", mimeType, fileContent);

            } catch (FileNotFoundException e) {
                // Send 404 Not Found response
                String notFoundHtml = "<html><body><h1>404 - File Not Found</h1></body></html>";
                sendResponse(outputStream, 404, "Not Found", "text/html", notFoundHtml.getBytes());

            } catch (ReadFileException e) {
                // Send 500 Internal Server Error
                String errorHtml = "<html><body><h1>500 - Internal Server Error</h1></body></html>";
                sendResponse(outputStream, 500, "Internal Server Error", "text/html", errorHtml.getBytes());
            }

            LOGGER.info(" * Connection Processing Finished.");

        } catch (IOException e) {
            LOGGER.error("Problem with communication", e);
        } catch (HttpParsingException e) {
            LOGGER.error("Problem parsing HTTP request", e);
            try {
                // Send error response based on the parsing exception
                String errorHtml = "<html><body><h1>" + e.getErrorCode().STATUS_CODE + " - " + e.getErrorCode().MESSAGE
                        + "</h1></body></html>";
                sendResponse(outputStream, e.getErrorCode().STATUS_CODE, e.getErrorCode().MESSAGE, "text/html",
                        errorHtml.getBytes());
            } catch (IOException ioException) {
                LOGGER.error("Failed to send error response", ioException);
            }
        } catch (WebRootNotFoundException e) {
            LOGGER.error("WebRoot not found", e);
        } finally {
            closeResources(inputStream, outputStream);
        }
    }

    private void sendResponse(OutputStream outputStream, int statusCode, String statusMessage,
            String contentType, byte[] content) throws IOException {
        final String CRLF = "\r\n";

        String response = "HTTP/1.1 " + statusCode + " " + statusMessage + CRLF +
                "Content-Type: " + contentType + CRLF +
                "Content-Length: " + content.length + CRLF +
                // Add CORS headers
                "Access-Control-Allow-Origin: *" + CRLF +
                "Access-Control-Allow-Methods: GET, HEAD, OPTIONS" + CRLF +
                "Access-Control-Allow-Headers: Content-Type, Authorization" + CRLF +
                "Access-Control-Max-Age: 3600" + CRLF +
                CRLF;

        outputStream.write(response.getBytes());
        outputStream.write(content);
    }

    private void closeResources(InputStream inputStream, OutputStream outputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                LOGGER.error("Error closing input stream", e);
            }
        }

        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                LOGGER.error("Error closing output stream", e);
            }
        }

        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                LOGGER.error("Error closing socket", e);
            }
        }
    }

}
