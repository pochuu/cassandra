package org.example;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import org.apache.cassandra.tools.nodetool.ResetLocalSchema;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.Properties;

import static java.lang.Thread.sleep;


public class Main {
    private static Cluster cluster;
    private static final String PROPERTIES_FILENAME = "config.properties";
    private static Session session;

    private static long userId = 1L;

    public static void main(String[] args) throws IOException {

        Properties prop = new Properties();
        prop.load(Main.class.getClassLoader().getResourceAsStream(PROPERTIES_FILENAME));
        String contactPoint = prop.getProperty("contactPoint");
        String port = prop.getProperty("port");
        Cluster.Builder b = Cluster.builder().addContactPoint(contactPoint).withCredentials("cassandra", "cassandra");
        b.withPort(Integer.parseInt(port));
        cluster = b.build();
        session = cluster.connect();
        session.execute("use aukcje;");
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
                    System.out.println("Instant.now().toEpochMilli()-10000" + (Instant.now().minusSeconds(10)));
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                    });
        close();
    }

    public Session getSession() {
        return session;
    }

    public static void close() {
        session.close();
        cluster.close();
    }

    public static void createKeyspace(
            String keyspaceName, String replicationStrategy, int replicationFactor) {

        String query = "CREATE KEYSPACE IF NOT EXISTS " +
                keyspaceName + " WITH replication = {" +
                "'class':'" + replicationStrategy +
                "','replication_factor':" + replicationFactor +
                "};";
        session.execute(query);
    }

    private static void checkForAuctions() {
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

    private static boolean userHasMoney(long amount) {
        String query = "SELECT balance FROM user_by_id "
                + "WHERE id=" + userId + ";";
        ResultSet resultSet = session.execute(query);
        long balance = resultSet.one().get("balance", Long.class);
        return balance - 500 >= amount;
    }

    private static void placeBid(long itemId, long auctionId, long newBid) {
        String updateBidQuery = "UPDATE bid_order_by_item_id "
                + "SET winning_user_id=" + userId + ", current_price=" + newBid
                + " WHERE item_id=" + itemId + " AND auction_id=" + auctionId + ";";

        String updateBidHistoryQuery = "INSERT INTO bid_history "
                + "VALUES (" + userId + "," + auctionId + "," + newBid + ");";

        session.execute(updateBidQuery);
        session.execute(updateBidHistoryQuery);
    }
}