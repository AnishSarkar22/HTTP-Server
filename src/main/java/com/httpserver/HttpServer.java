package com.httpserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.httpserver.config.Configuration;
import com.httpserver.config.ConfigurationManager;

/**
 * 
 * Driver class for HTTP server
 * 
 */

public class HttpServer {
    public static void main(String[] args) throws IOException {
        System.out.println("Server Starting...");

        // ConfigurationManager.getInstance().loadConfigurationFile("src/main/resources/http.json");
        
        String configPath = HttpServer.class.getClassLoader().getResource("http.json").getPath();

        ConfigurationManager.getInstance().loadConfigurationFile(configPath);

        Configuration conf = ConfigurationManager.getInstance().getCurrentConfiguration();

        System.out.println("Using Port: " + conf.getPort());
        System.out.println("Using WebRoot: " + conf.getWebroot());

        try {
            ServerSocket serverSocket = new ServerSocket(conf.getPort());

            // for using `.accept()`, the server waits to get a connection, if no connection is accepted, then it does not execute further, it stays waiting for the connection
            Socket socket = serverSocket.accept(); // prompts the socket thats listening to a port to accept any connection that arise

            InputStream inputStream = socket.getInputStream(); // allowing the server to read data sent by the client
            OutputStream outputStream = socket.getOutputStream(); // allowing the server to send data back to the client

            // TODO: we would read

            String html = "<html><head>Java HTTP Server<title></title></head><body><h1>This page was served using my Java HTTP Server</h1></body></html>";

            final String CRLF = "\n\r"; // 13, 10

            String response = 
                    "HTTP/1.1 200 OK" + CRLF + // Status Line :  HTTP VERSION RESPONSE_CODE RESPONSE_MESSAGE
                    "Content-Length: " + html.getBytes().length + CRLF + //HEADER
                    CRLF + 
                    html + 
                    CRLF + CRLF;

            outputStream.write(response.getBytes());
            
            inputStream.close();
            outputStream.close();
            socket.close();
            serverSocket.close();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}