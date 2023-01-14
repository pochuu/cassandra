package org.example.backend;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Date;

import static java.lang.Thread.sleep;

@Slf4j
@Getter
public class BackendSession {
    private final String contactPoint;

    private final String keyspace;

    private final Cluster cluster;

    private final Session session;

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
        createKeyspaceIfNotExists(keyspace, replicationStrategy, replicationFactor);
        session = cluster.connect(keyspace);
    }


    private void createKeyspaceIfNotExists(
            String keyspaceName, String replicationStrategy, int replicationFactor) {

        String query = "CREATE KEYSPACE IF NOT EXISTS " +
                keyspaceName + " WITH replication = {" +
                "'class':'" + replicationStrategy +
                "','replication_factor':" + replicationFactor +
                "};";
        session.execute(query);
    }

    public void checkForAuctions() {
        String query = "SELECT * FROM bid_order_by_item_id";

        ResultSet resultSet = session.execute(query);
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
        String query = "SELECT balance FROM user_by_id "
                + "WHERE id=" + userId + ";";
        ResultSet resultSet = session.execute(query);
        long balance = resultSet.one().get("balance", Long.class);
        return balance - 500 >= amount;
    }

    public void placeBid(long itemId, long auctionId, long newBid) {
        String updateBidQuery = "UPDATE bid_order_by_item_id "
                + "SET winning_user_id=" + userId + ", current_price=" + newBid
                + " WHERE item_id=" + itemId + " AND auction_id=" + auctionId + ";";

        String updateBidHistoryQuery = "INSERT INTO bid_history "
                + "VALUES (" + userId + "," + auctionId + "," + newBid + ");";

        session.execute(updateBidQuery);
        session.execute(updateBidHistoryQuery);
    }

    public void selectAll() {
        ResultSet rs = session.execute("SELECT * FROM test");

//        checkForAuctions();
        rs.forEach(
                row -> {
                    Date data = row.get("czas", Date.class);


                    Date datamoja = new Date();
                    datamoja.setTime(datamoja.getTime()-10000);
                    long xd = System.currentTimeMillis()-10000;


                    System.out.println("LONG INT Z REKORDU: " + data.getTime());
                    System.out.println("System.currentTimeMillis()-10000 : " + xd);
                    System.out.println("Obiekt Date z cofnietym longiem o 10 sekund:" + datamoja.getTime());
                    System.out.println("Instant.now().minusSeconds(10)" + (Instant.now().minusSeconds(10)));
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                });
    }

    public void close() {
        session.close();
        cluster.close();
    }
}
