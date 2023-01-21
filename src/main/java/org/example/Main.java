package org.example;

import java.io.IOException;
import java.sql.Time;
import java.time.temporal.ChronoUnit;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.example.backend.BackendSession;
import org.example.backend.auction.Auction;
import org.example.backend.auction.AuctionListener;
import org.example.backend.dealer.DealerService;
import org.example.backend.user.UserBiddingThread;
import org.example.backend.user.UserService;

@Slf4j
public class Main {
    private static final String PROPERTIES_FILENAME = "config.properties";

    public static void main(String[] args) {
        try {
            ExecutorService ex1,ex2;
            BackendSession backendSessionBidder = loadPropertiesAndInitBackendSession();
            BackendSession backendSessionDealer = loadPropertiesAndInitBackendSession();
            BackendSession MainBackendSession= loadPropertiesAndInitBackendSession();
//            backendSessionBidder.truncateAuctions();
//            backendSessionBidder.createAuctions();
            DealerService dealerService = new DealerService();
            UserService userService = new UserService();
            AuctionListener auctionListener = new AuctionListener(MainBackendSession);
            while(true) {
                auctionListener.isAnyAuctionAvailable();
                ex1 = dealerService.execute(backendSessionDealer, 1);
                ex2 = userService.execute(backendSessionBidder, 1);

                ex1.awaitTermination(100, TimeUnit.SECONDS);
                System.out.println("ex1 skonczyl");
                ex2.awaitTermination(100, TimeUnit.SECONDS);
                System.out.println("ex2 skonczyl");
                System.out.println("skonczylem");
            }
        } catch (NumberFormatException e) {
            log.error("Could not parse int from properties: " + e.getMessage());
        } catch (IOException e) {
            log.error("Could not read properties file: " + e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static BackendSession loadPropertiesAndInitBackendSession() throws IOException, NumberFormatException {
        Properties prop = new Properties();
        prop.load(Main.class.getClassLoader().getResourceAsStream(PROPERTIES_FILENAME));
        String contactPoint = prop.getProperty("contactPoint");
        int port = Integer.parseInt(prop.getProperty("port"));
        String keySpace = prop.getProperty("keySpace");
        return new BackendSession(contactPoint, keySpace, port);
    }
}
