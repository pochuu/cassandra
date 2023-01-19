package org.example.backend;

import com.datastax.driver.core.*;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.auction.Auction;
import org.example.backend.statements.BoundStatementFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Getter
public class BackendSession {
    private final String contactPoint;

    private final String keyspace;

    private final Cluster cluster;

    private final Session session;

    private final BoundStatementFactory statementFactory;

    private final int userId = 1;

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

    public boolean checkUserDebtAndRefundIfNeeded(int userId) {
        AtomicBoolean isAnyAuctionAvailable = new AtomicBoolean(false);
        BoundStatement selectAllBids = statementFactory.selectAllBids();
        BoundStatement selectDebtFromUser = statementFactory.selectDebtFromUser();
        ResultSet resultSetCheckTimestamp = session.execute(selectAllBids);

        selectDebtFromUser.bind(userId);
        ResultSet resultSetUpdateDebt = session.execute(selectDebtFromUser);

        long debt = resultSetUpdateDebt.one().getLong("debt");
        long currentWinningBids = addBidsFromWinningAuctions(userId);
        if (debt != currentWinningBids) {
            giveRefundToUser(userId, debt - currentWinningBids);
        }

        resultSetCheckTimestamp.forEach(
                row -> {
                    {
                        Date timestamp = row.getTimestamp("bid_end_time");
                        boolean hasExpired = checkIfExpired(timestamp);
                        if (!hasExpired) {
                            isAnyAuctionAvailable.set(true);
                        }
                    }
                }
        );

        return isAnyAuctionAvailable.get();
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

    private void giveRefundToUser(int userId, long amount) {
        BoundStatement bs1 = statementFactory.updateUserDebt();
        BoundStatement bs2 = statementFactory.updateUserBalance();
        bs1.bind(amount, userId);
        bs2.bind(amount, userId);
        session.execute(bs1); //updaty do countera nie da sie w batchu wyslac
        session.execute(bs2);
    }

    public boolean checkForAuctionsAndPlaceBidIfImNotTheWinner() throws NullPointerException {
        BoundStatement bs = statementFactory.selectAllBids();
        AtomicBoolean isAnyAuctionAvailable = new AtomicBoolean(false);
        ResultSet resultSet = session.execute(bs);
        resultSet.forEach(
                row -> {
                    {
                        Date timestamp = row.getTimestamp("bid_end_time");
                        boolean hasExpired = checkIfExpired(timestamp);
                        if (!hasExpired) {
                            isAnyAuctionAvailable.set(true);
                        }
                        if (row.getInt("winning_user_id") != userId) {
                            int auctionId = row.getInt("auction_id");
                            long currentPrice = row.getLong("current_price");
                            int minBidAmount = row.getInt("min_bid_amount");
                            if (!hasExpired && userHasMoney(currentPrice)) {
                                placeBid(auctionId, currentPrice + minBidAmount, currentPrice);
                            }
                        }
                    }
                }
        );
        return isAnyAuctionAvailable.get();
    }

    private boolean checkIfExpired(Date timestamp) {
        return timestamp.before(Date.from(Instant.now()));
    }

    private boolean userHasMoney(long amount) {
        BoundStatement bs = statementFactory.selectBalanceFromUser();
        bs.bind(userId);
        ResultSet resultSet = session.execute(bs);
        long balance = resultSet.one().getLong("balance");
        return balance >= amount;
    }

    private void placeBid(int auctionId, long newBid, long currentPrice) {
        BoundStatement updateBidBs = statementFactory.updateBid();
        BoundStatement insertIntoBidHistoryBs = statementFactory.insertIntoBidHistory();
        BoundStatement updateUserDebtBs = statementFactory.updateUserDebt();
        BoundStatement updateUserBalanceBs = statementFactory.updateUserBalance();
        List<BoundStatement> boundStatements;

        updateBidBs.bind(userId, newBid, auctionId, currentPrice);
        insertIntoBidHistoryBs.bind(userId, auctionId, newBid);
        updateUserDebtBs.bind( newBid, userId);//poki co hardkode
        updateUserBalanceBs.bind( newBid, userId);

        boundStatements = List.of(updateBidBs, insertIntoBidHistoryBs, updateUserDebtBs, updateUserBalanceBs);

        boundStatements.forEach(bs -> session.execute(bs));
    }

    public void close() {
        session.close();
        cluster.close();
    }

    public void createAuctions() {
        Date dateNow = Date.from(Instant.now().plus(10, ChronoUnit.SECONDS));
        final List<Auction> auctions = List.of(
                Auction.builder().auctionId(1).itemId(1).currentPrice(100).bidEndTime(dateNow).minBidAmount(10).build(),
                Auction.builder().auctionId(2).itemId(2).currentPrice(3000).bidEndTime(dateNow).minBidAmount(50).build(),
                Auction.builder().auctionId(3).itemId(3).currentPrice(2000).bidEndTime(dateNow).minBidAmount(100).build(),
                Auction.builder().auctionId(4).itemId(4).currentPrice(500).bidEndTime(dateNow).minBidAmount(55).build(),
                Auction.builder().auctionId(5).itemId(5).currentPrice(200).bidEndTime(dateNow).minBidAmount(40).build()
        );
        BoundStatement bs = statementFactory.insertIntoAuction();
        auctions.forEach(
                auction -> {
                    bs.bind(auction.getFields());
                    session.execute(bs);
                }
        );
    }

    public void truncateAuctions() {
        BoundStatement bs = statementFactory.truncateAuction();
        session.execute(bs);
    }
}
