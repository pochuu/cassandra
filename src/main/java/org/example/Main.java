package org.example;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.example.backend.BackendSession;
import org.example.backend.auction.AuctionListener;
import org.example.backend.dealer.DealerService;
import org.example.backend.user.UserService;

@Slf4j
public class Main {
    private static final String PROPERTIES_FILENAME = "config.properties";

    public static void main(String[] args) {
        BackendSession backendSession = null;
        DealerService dealerService = new DealerService();
        UserService userService = new UserService();
        ExecutorService dealerExecutor, userExecutor;
        try {
            backendSession = loadPropertiesAndInitBackendSession();
            AuctionListener auctionListener = new AuctionListener(backendSession);
            while (true) {
                auctionListener.waitForAuctionsIfUnavailable(); // blocking method
                dealerExecutor = dealerService.execute(backendSession, 1);
                userExecutor = userService.execute(backendSession, 1);
                dealerExecutor.awaitTermination(100, TimeUnit.SECONDS);
                log.info("Dealer executor has terminated");
                userExecutor.awaitTermination(100, TimeUnit.SECONDS);
                log.info("User executor has terminated");
            }
        } catch (NumberFormatException e) {
            log.error("Could not parse int from properties: " + e.getMessage());
        } catch (IOException e) {
            log.error("Could not read properties file: " + e.getMessage());
        } catch (InterruptedException e) {
            log.error("A thread was interrupted: " + e.getMessage());
        } finally {
            if (backendSession != null) {
                log.info("Closing backend session");
                backendSession.close();
            }
        }
    }

    private static BackendSession loadPropertiesAndInitBackendSession() throws IOException, NumberFormatException {
        log.info("Loading properties");
        Properties prop = new Properties();
        prop.load(Main.class.getClassLoader().getResourceAsStream(PROPERTIES_FILENAME));
        String contactPoint = prop.getProperty("contactPoint");
        int port = Integer.parseInt(prop.getProperty("port"));
        int userId = Integer.parseInt(System.getProperty("user_id"));
        log.info("USER_ID=" + userId);
        String keySpace = prop.getProperty("keySpace");
        return new BackendSession(userId, contactPoint, keySpace, port);
    }
}
