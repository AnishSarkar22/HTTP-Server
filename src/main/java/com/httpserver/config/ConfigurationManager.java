package com.httpserver.config;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.httpserver.util.Json;

public class ConfigurationManager {
    private static ConfigurationManager myConfigurationManager;
    private static Configuration myCurrentConfiguration; 

    private ConfigurationManager(){

    }

    public static ConfigurationManager getInstance(){
        if (myConfigurationManager==null)
            myConfigurationManager = new ConfigurationManager();
        return myConfigurationManager;
    }

    /** 
     * Used to load a configuration file by path provided
     * @throws IOException 
    */
    public void loadConfigurationFile(String filePath) throws IOException{
        StringBuffer sb = new StringBuffer();
        int i;
        try (FileReader fileReader = new FileReader(filePath)) {
            while ((i = fileReader.read()) != -1){
                sb.append((char)i);
            }
        } catch (FileNotFoundException e){
            throw new HttpConfigurationException(e);
        } catch (IOException e) {
            throw new HttpConfigurationException(e);
        }
        JsonNode conf = null;
        try {
            conf = Json.parse(sb.toString());
        } catch (IOException e) {
            throw new HttpConfigurationException("Error parsing the Configuration File.", e);
    }
        try {
            myCurrentConfiguration = Json.fromJson(conf, Configuration.class);
        } catch (JsonProcessingException e) {
            throw new HttpConfigurationException("Error parsing the Configuration File, internal", e);
        }
    }

    /**
     * Returns the current loaded configuration
     * @return 
     */
    public Configuration getCurrentConfiguration(){
        if ( myCurrentConfiguration == null){
            throw new HttpConfigurationException("No current configuration set.");
        }
        return myCurrentConfiguration;
    }
}
