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

import static java.lang.Thread.sleep;

@Slf4j
@Getter
public class BackendSession {
    private final String contactPoint;

    private final String keyspace;

    private final Cluster cluster;

    private final Session session;

    private final BoundStatementFactory statementFactory;

    private final int userId;

    public BackendSession(int userId, String contactPoint, String keyspace, int port) {
        this.userId = userId;
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

    public void checkUserDebtAndRefundIfNeeded() throws InterruptedException {
        BoundStatement selectAllBids = statementFactory.selectAllBids();
        sleep(2000);

        ResultSet resultSetCheckTimestamp = session.execute(selectAllBids);
        AtomicLong amounttosubstract = new AtomicLong();
        resultSetCheckTimestamp.forEach(row -> {
                    if(row.getInt("winning_user_id") == userId){
                        long currentPrice = row.getLong("current_price");
                        amounttosubstract.getAndAdd(currentPrice);
                    }
                }
        );
    if (amounttosubstract.get()> 0) {
        BoundStatement updateUserBalanceBs = statementFactory.updateUserBalance();
        updateUserBalanceBs.bind(-amounttosubstract.get(), userId);
        session.execute(updateUserBalanceBs);
    }

    }


    public boolean checkForAuctionsAndPlaceBidIfImNotTheWinner() throws NullPointerException {
        BoundStatement bs = statementFactory.selectAllBids();
        AtomicBoolean isAnyAuctionAvailable = new AtomicBoolean(false);
        AtomicLong sumAllBids = new AtomicLong();
        ResultSet resultSet = session.execute(bs);
        ResultSet getAllPreviousBids = session.execute(bs);
        getAllPreviousBids.forEach(
                row ->
                {
                    if (row.getInt("winning_user_id") == userId) {
                         sumAllBids.getAndAdd(row.getLong("current_price"));
                    }
                }
        );

        resultSet.forEach(
                row -> {
                    {
                        Date timestamp = row.getTimestamp("bid_end_time");
                        boolean hasExpired = checkIfExpired(timestamp);
                        if (hasExpired) {
                            isAnyAuctionAvailable.set(true);
                        }
                        int auctionId = row.getInt("auction_id");
                        long currentPrice = row.getLong("current_price");
                        int minBidAmount = row.getInt("min_bid_amount");
                        if (row.getInt("winning_user_id") != userId) {
                            sumAllBids.getAndAdd(currentPrice + minBidAmount);
                            if (hasExpired && userHasMoney(sumAllBids.get())) {
                                placeBid(auctionId, currentPrice + minBidAmount, currentPrice);
                            }
                        }
                    }
                }
        );
        return isAnyAuctionAvailable.get();
    }

    private boolean checkIfExpired(Date timestamp) {

        Date Tim = Date.from(Instant.now().minus(10, ChronoUnit.SECONDS));
        return    timestamp.after(Date.from(Instant.now().minus(10, ChronoUnit.SECONDS)));
    }
    public boolean checkIfAnyAuctionAvailable(Row row)
    {
        Date timestamp = row.getTimestamp("bid_end_time");
        return checkIfExpired(timestamp);
    }

    public boolean checkIfAuctionsAvailableYet() {
        BoundStatement bs = statementFactory.selectAllBids();
        ResultSet rs = session.execute(bs);
        AtomicBoolean isAnyAuctionAvailable = new AtomicBoolean(false);
        if (rs.getAvailableWithoutFetching() > 0) {
            rs.forEach(
                    row -> {
                        if (checkIfAnyAuctionAvailable(row)) {
                            isAnyAuctionAvailable.set(true);
                        }
                    }
            );
        }
        return isAnyAuctionAvailable.get();
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
//        updateUserDebtBs.bind(-newBid, userId);//poki co hardkode
//        updateUserBalanceBs.bind(-newBid, userId);

        boundStatements = List.of(updateBidBs, insertIntoBidHistoryBs);

        boundStatements.forEach(session::execute);
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
