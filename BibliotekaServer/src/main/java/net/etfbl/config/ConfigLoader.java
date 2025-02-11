package net.etfbl.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import net.etfbl.logger.ServerLogger;

public class ConfigLoader {
	private static ConfigLoader instance;
    private Properties properties = new Properties();


    private ConfigLoader() {
        loadProperties();
    }

    public static ConfigLoader getInstance() {
        if (instance == null) {
            instance = new ConfigLoader();
        }
        
        return instance;
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("../config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }
            properties.load(input);
        } catch (IOException ex) {
        	ServerLogger.logger.severe("Error: " + ex);
        }
    }
    
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

}
