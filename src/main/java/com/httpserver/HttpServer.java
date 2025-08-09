package com.httpserver;

import java.io.IOException;

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
    }
}