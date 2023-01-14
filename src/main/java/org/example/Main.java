package org.example;

import java.io.IOException;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.BackendSession;

@Slf4j
public class Main {
    private static final String PROPERTIES_FILENAME = "config.properties";

    public static void main(String[] args) {
        try {
            BackendSession backendSession = loadPropertiesAndInitBackendSession();
            backendSession.selectAll();
            backendSession.close();
        } catch (NumberFormatException e) {
            log.error("Could not parse int from properties: " + e.getMessage());
        } catch (IOException e) {
            log.error("Could not read properties file: " + e.getMessage());
        }
    }

    private static BackendSession loadPropertiesAndInitBackendSession() throws IOException, NumberFormatException {
        Properties prop = new Properties();
        prop.load(Main.class.getClassLoader().getResourceAsStream(PROPERTIES_FILENAME));
        String contactPoint = prop.getProperty("contactPoint");
        int port = Integer.parseInt(prop.getProperty("port"));
        String keySpace = prop.getProperty("keySpace");
        String replicationStrategy = prop.getProperty("replicationStrategy");
        int replicationFactor = Integer.parseInt(prop.getProperty("replicationFactor"));
        return new BackendSession(contactPoint, keySpace, replicationStrategy, replicationFactor, port);
    }
}