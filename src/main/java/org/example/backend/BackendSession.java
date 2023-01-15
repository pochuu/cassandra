package org.example.backend;

import com.datastax.driver.core.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.statements.BoundStatementFactory;

@Slf4j
@Getter
public class BackendSession {
    private final String contactPoint;

    private final String keyspace;

    private final Cluster cluster;

    private final Session session;

    private final BoundStatementFactory statementFactory;

    private final long userId = 1L;

    public BackendSession(String contactPoint, String keyspace,
                          String replicationStrategy, int replicationFactor, int port)
    {
        this.contactPoint = contactPoint;
        this.keyspace= keyspace;
        cluster = Cluster.builder()
                .addContactPoint(contactPoint)
                .withCredentials("cassandra", "cassandra")
                .withPort(port)
                .build();
        session = cluster.connect(keyspace);
        this.statementFactory = new BoundStatementFactory(session);
//        createKeyspaceIfNotExists(replicationStrategy, replicationFactor);
    }


//    private void createKeyspaceIfNotExists(
//            String replicationStrategy, int replicationFactor) {
//
//        BoundStatement bs = statementFactory.createKeySpace();
//        bs.bind(replicationStrategy, replicationFactor);
//        session.execute(bs);
//    }

    public void checkForAuctionsAndPlaceBidIfImNotTheWinner() {
        BoundStatement bs = statementFactory.selectAllBids();
        ResultSet resultSet = session.execute(bs);
        resultSet.forEach(
                row -> {
                    if ((row.get("winning_user_id", Long.class) != userId)) {
                        long itemId = row.get("item_id", Long.class);
                        long auctionId = row.get("auction_id", Long.class);
                        long currentPrice = row.get("currentPrice", Long.class);
                        if (userHasMoney(currentPrice)) {
                            placeBid(itemId, auctionId, currentPrice + 500);
                        }
                    }
                }
        );
    }

    public boolean userHasMoney(long amount) {
        BoundStatement bs = statementFactory.selectBalanceFromUser();
        bs.bind(userId);
        ResultSet resultSet = session.execute(bs);
        long balance = resultSet.one().get("balance", Long.class);
        return balance - 500 >= amount;
    }

    public void placeBid(long itemId, long auctionId, long newBid) {
        BoundStatement updateBidBs = statementFactory.updateBid();
        updateBidBs.bind(userId, newBid, itemId, auctionId);
        BoundStatement insertIntoBidHistoryBs = statementFactory.insertIntoBidHistory();
        insertIntoBidHistoryBs.bind(userId, auctionId, newBid);

        session.execute(updateBidBs);
        session.execute(insertIntoBidHistoryBs);
    }
        public void close() {
        session.close();
        cluster.close();
    }
}
