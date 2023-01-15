package org.example.backend.statements;

public class PreparedQueries {
    public static String CREATE_KEYSPACE_IF_NOT_EXISTS = "CREATE KEYSPACE IF NOT EXISTS " +
            "aukcje WITH replication = {'class':'(?)','replication_factor':2};";
    public static String SELECT_ALL_FROM_BID_ORDER = "SELECT * FROM bid_order_by_item_id";
    public static String SELECT_BALANCE_FROM_USER = "SELECT balance FROM user_by_id WHERE id=(?);";
    public static String UPDATE_BID_ORDER = "UPDATE bid_order_by_item_id "
            + "SET winning_user_id = (?), current_price = (?) WHERE item_id = (?) AND auction_id=(?);";
    public static String INSERT_INTO_BID_HISTORY = "INSERT INTO bid_history(user_id, auction_id, id, amount) VALUES " +
            "((?), (?), uuid(), (?));";
    public static String CHECK_USER_BALANCE = "SELECT balance FROM user_by_id WHERE id = (?)";
    public static String GIVE_BACK_MONEY_TO_USER = "UPDATE user_by_id SET balance = (?) WHERE id = (?)";
}
