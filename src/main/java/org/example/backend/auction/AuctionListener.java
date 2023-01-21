package org.example.backend.auction;

import com.datastax.driver.core.KeyspaceMetadata;
import com.datastax.driver.core.TableMetadata;
import org.example.backend.BackendSession;

import static java.lang.Thread.sleep;

public class AuctionListener {
    private final BackendSession backendSession;
    public AuctionListener(BackendSession backendSession) {
        this.backendSession = backendSession;
    }

    public void isAnyAuctionAvailable() throws InterruptedException {
        boolean isAvailable = false;
        while(!isAvailable){
            KeyspaceMetadata ks = backendSession.getCluster().getMetadata().getKeyspace(backendSession.getKeyspace());
            TableMetadata table = ks.getTable("bid_order_by_auction_id");
             isAvailable = (table != null);
             if (isAvailable) {
                 isAvailable = (backendSession.getSession().execute(backendSession.getStatementFactory().selectAllBids()).one() != null);
                 //jezeli znajduje się jakikolwiek rekord w tabeli to wychodzi z petli
             }
             sleep(500);
        }
    }
}
