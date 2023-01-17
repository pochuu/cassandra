package org.example.backend;

import com.datastax.driver.core.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.statements.BoundStatementFactory;

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

    public BackendSession(String contactPoint, String keyspace, int port)
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

    public void checkUserDebtAndRefundIfNeeded(int userId) {
        BoundStatement bs = statementFactory.selectDebtFromUser();
        bs.bind(userId);
        ResultSet resultSet = session.execute(bs);
        long debt = resultSet.one().get("debt", Long.class);
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
            if (row.get("winning_user_id", Long.class) == userId) {
                winningBidAmount.getAndAdd(row.get("current_price", Long.class));
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

    public void checkForAuctionsAndPlaceBidIfImNotTheWinner() throws NullPointerException{
        BoundStatement bs = statementFactory.selectAllBids();
        ResultSet resultSet = session.execute(bs);
        resultSet.forEach(
                row -> {
                    if (row.get("winning_user_id", int.class) != userId) {
                        long itemId = row.get("item_id", int.class);
                        long auctionId = row.get("auction_id", int.class);
                        long currentPrice = row.get("currentPrice", long.class);
                        if (userHasMoney(currentPrice)) {
                            placeBid(itemId, auctionId, currentPrice + 500, currentPrice);
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

}
