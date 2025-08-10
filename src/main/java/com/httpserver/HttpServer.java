package com.httpserver;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.httpserver.config.Configuration;
import com.httpserver.config.ConfigurationManager;
import com.httpserver.core.ServerListenerThread;

/**
 * 
 * Driver class for HTTP server
 * 
 */

public class HttpServer {

    private final static Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);
    public static void main(String[] args) throws IOException {
        LOGGER.info("Server Starting... ");

        // ConfigurationManager.getInstance().loadConfigurationFile("src/main/resources/http.json");
        String configPath = HttpServer.class.getClassLoader().getResource("http.json").getPath();
        ConfigurationManager.getInstance().loadConfigurationFile(configPath);

        Configuration conf = ConfigurationManager.getInstance().getCurrentConfiguration();

        LOGGER.info("Using Port: " + conf.getPort());
        LOGGER.info ("Using WebRoot: " + conf.getWebroot());

        try {
            ServerListenerThread serverListenerThread = new ServerListenerThread(conf.getPort(), conf.getWebroot());
            serverListenerThread.start();
        } catch (IOException e) {
            // TODO: handle later
            e.printStackTrace();
        }
    }
}