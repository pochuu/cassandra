package org.example;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;


public class Main {
    private static Cluster cluster;
private static final String PROPORTIES_FILENAME = "config.proporties";
    private static Session session;
    public static void main(String[] args) throws IOException {

    Properties prop=new Properties();
    String node=prop.getProperty("node1");
    String port = prop.getProperty("port");
    prop.load(Main.class.getClassLoader().getResourceAsStream(PROPORTIES_FILENAME));


    Cluster.Builder b = Cluster.builder().addContactPoint(node).withCredentials("cassandra", "cassandra");
    b.withPort(Integer.parseInt(port));
    cluster = b.build();
    session = cluster.connect();
    createKeyspace("test1", "SimpleStrategy",0);
     close();
}
    public Session getSession() {
        return this.session;
    }

    public static void close() {
        session.close();
        cluster.close();
    }
    public static void createKeyspace(
            String keyspaceName, String replicationStrategy, int replicationFactor) {
        StringBuilder sb =
                new StringBuilder("CREATE KEYSPACE IF NOT EXISTS ")
                        .append(keyspaceName).append(" WITH replication = {")
                        .append("'class':'").append(replicationStrategy)
                        .append("','replication_factor':").append(replicationFactor)
                        .append("};");

        String query = sb.toString();
        session.execute(query);
    }

}