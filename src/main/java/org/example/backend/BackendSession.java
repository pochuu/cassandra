package org.example.backend;

import com.datastax.driver.core.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.cassandra.db.RangeTombstone;
import org.apache.cassandra.db.Slice;
import org.example.backend.statements.BoundStatementFactory;

import java.util.UUID;

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
    }

    public void giveRefundsToUsers() {
//        BoundStatement selectAllFromBidRefund = new BoundStatement(statementFactory.SelectAllBidRefund());
//        SELECT_ALL_FROM_BID_REFUND // jezeli sa jakies rekordy to leci
//                //generate UUID
//        MARK_THE_RECORDS_IN_BID_REFUND //przypisujemy wygenerowane uuid do rekordow z refundem na false
//        SELECT_BID_HISTORY_WITH_KNOWN_UUID
//        UPDATE_USER_MONEY // oddajemy kaske
//        INSERT_REFUND_IS_DONE_TO_MARKED_USERS // ustawiamy w tabeli refund na true, dla wygenerowanego UUID
    }

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
        BoundStatement insertIntoBidHistoryBs = statementFactory.insertIntoBidHistory();
        BoundStatement insertIntoBidRefundBs = statementFactory.insertIntoBidRefund();
        UUID uuid = UUID.randomUUID();
        updateBidBs.bind(userId, newBid, itemId, auctionId);
        insertIntoBidHistoryBs.bind(userId, auctionId,uuid, newBid);
        insertIntoBidRefundBs.bind(false, uuid, userId, uuid, newBid); //2uuid wypełniamy bo tak
                                                                                // trzeba, ale nie ma to
        session.execute(updateBidBs);
        session.execute(insertIntoBidHistoryBs);
        session.execute(insertIntoBidRefundBs);
    }
        public void close() {
        session.close();
        cluster.close();
    }

    public void UpdateBidRefund() {
    }
}
