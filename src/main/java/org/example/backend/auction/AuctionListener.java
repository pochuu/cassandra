package org.example.backend.auction;

import com.datastax.driver.core.KeyspaceMetadata;
import com.datastax.driver.core.TableMetadata;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.BackendSession;

import static java.lang.Thread.sleep;

@Slf4j
public class AuctionListener {
    private final BackendSession backendSession;

    public AuctionListener(BackendSession backendSession) {
        this.backendSession = backendSession;
    }

    public void waitForAuctionsIfUnavailable() throws InterruptedException {
        log.info("Checking if auctions are available");
        boolean isAvailable = false;
        while (!isAvailable) {
            KeyspaceMetadata ks = backendSession.getCluster().getMetadata().getKeyspace(backendSession.getKeyspace());
            TableMetadata table = ks.getTable("bid_order_by_auction_id");
            isAvailable = (table != null);
            if (isAvailable) {
                //jezeli znajduje się jakikolwiek rekord w tabeli to wychodzi z petli
                isAvailable = backendSession.checkIfAuctionsAvailableYet();
                if (isAvailable) {
                    log.info("Auctions are available");
                }
            }
            sleep(500);
        }
    }
}
