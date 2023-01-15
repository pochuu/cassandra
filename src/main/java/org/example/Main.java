package org.example;

import java.io.IOException;
import java.util.Properties;

import com.datastax.driver.core.ResultSet;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.BackendSession;
import org.example.backend.dealer.DealerService;
import org.example.backend.dealer.DealerThread;
import org.example.backend.user.UserBiddingThread;
import org.example.backend.user.UserService;

@Slf4j
public class Main {
    private static final String PROPERTIES_FILENAME = "config.properties";

    public static void main(String[] args) {
        try {
            BackendSession backendSession = loadPropertiesAndInitBackendSession();
            ResultSet rs = backendSession.getSession().execute("DELETE FROM bid_history WHERE user_id = 1;");
//            UserService userService = new UserService();
//            DealerService dealerService = new DealerService();
//            userService.execute(new UserBiddingThread(backendSession), 50);
//            dealerService.execute(new DealerThread(backendSession), 2);
            System.out.println("x");
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