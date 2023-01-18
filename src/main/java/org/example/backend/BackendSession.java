package org.example.backend;

import com.datastax.driver.core.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.statements.BoundStatementFactory;

import java.time.Instant;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Getter
public class BackendSession {
    private final String contactPoint;

    private final String keyspace;

    private final Cluster cluster;

    private final Session session;

    private final BoundStatementFactory statementFactory;

    private final long userId = 1L;

    public BackendSession(String contactPoint, String keyspace, int port) {
        this.contactPoint = contactPoint;
        this.keyspace = keyspace;
        cluster = Cluster.builder()
                .addContactPoint(contactPoint)
                .withCredentials("cassandra", "cassandra")
                .withPort(port)
                .build();
        session = cluster.connect(keyspace);
        this.statementFactory = new BoundStatementFactory(session);
    }

    public void checkUserDebtAndRefundIfNeeded(int userId) {
        BoundStatement bs = statementFactory.selectDebtFromUser();
        bs.bind(userId);
        ResultSet resultSet = session.execute(bs);
        long debt = resultSet.one().getLong("debt");
        long currentWinningBids = addBidsFromWinningAuctions(userId);
        if (debt != currentWinningBids) {
            giveRefundToUser(userId, debt - currentWinningBids);
        }
    }

    private long addBidsFromWinningAuctions(int userId) {
        BoundStatement bs = statementFactory.selectAllBids();
        ResultSet resultSet = session.execute(bs);
        AtomicLong winningBidAmount = new AtomicLong();
        resultSet.forEach(row -> {
            if (row.getInt("winning_user_id") == userId) {
                winningBidAmount.getAndAdd(row.getLong("current_price"));
            }
        });
        return winningBidAmount.get();
    }

    private void giveRefundToUser(long userId, long amount) {
        BoundStatement bs = statementFactory.updateUserDebt();
        bs.bind(-amount, userId);
        session.execute(bs);

        bs = statementFactory.updateUserBalance();
        bs.bind(amount, userId);
        session.execute(bs);
    }

    public void checkForAuctionsAndPlaceBidIfImNotTheWinner() throws NullPointerException {
        BoundStatement bs = statementFactory.selectAllBids();
        ResultSet resultSet = session.execute(bs);
        resultSet.forEach(
                row -> {
                    {
                        if (row.getInt("winning_user_id") != userId) {
                            long itemId = row.getInt("item_id");
                            long auctionId = row.getInt("auction_id");
                            long currentPrice = row.getLong("currentPrice");
                            Date timestamp = row.getTimestamp("bid_end_time");
                            boolean hasExpired = checkIfExpired(timestamp);
                            if (!hasExpired && userHasMoney(currentPrice)) {
                                placeBid(itemId, auctionId, currentPrice + 500, currentPrice);
                            }
                        }
                    }
                }
        );
    }

    private boolean checkIfExpired(Date timestamp) {
        return timestamp.after(Date.from(Instant.now()));
    }

    public boolean userHasMoney(long amount) {
        BoundStatement bs = statementFactory.selectBalanceFromUser();
        bs.bind(userId);
        ResultSet resultSet = session.execute(bs);
        long balance = resultSet.one().getLong("balance");
        return balance - 500 >= amount;
    }

    public void placeBid(long itemId, long auctionId, long newBid, long currentPrice) {
        BoundStatement updateBidBs = statementFactory.updateBid();
        BoundStatement insertIntoBidHistoryBs = statementFactory.insertIntoBidHistory();
        BoundStatement updateUserDebtBs = statementFactory.updateUserDebt();
        BoundStatement updateUserBalanceBs = statementFactory.updateUserBalance();
        BatchStatement batchStatement = new BatchStatement();

        updateBidBs.bind(userId, newBid, itemId, auctionId, currentPrice);
        insertIntoBidHistoryBs.bind(userId, auctionId, newBid);
        updateUserDebtBs.bind(500, userId);//poki co hardkode
        updateUserBalanceBs.bind(500, userId);

        batchStatement.add(updateBidBs);
        batchStatement.add(insertIntoBidHistoryBs);
        batchStatement.add(updateUserBalanceBs);
        batchStatement.add(updateUserDebtBs);

        session.execute(batchStatement); //lepiej to w batchu wyslac, 3 tabelki lepiej zeby sie nie rozjechaly
    }

    public void close() {
        session.close();
        cluster.close();
    }

    public void createAuctions() {
    }
}
