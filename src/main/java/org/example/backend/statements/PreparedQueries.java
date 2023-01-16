package org.example.backend.statements;

public class PreparedQueries {
    public static String SELECT_ALL_FROM_BID_ORDER = "SELECT * FROM bid_order_by_item_id";
    public static String SELECT_BALANCE_FROM_USER = "SELECT balance FROM user_by_id WHERE id=(?);";
    public static String UPDATE_BID_ORDER = "UPDATE bid_order_by_item_id "
            + "SET winning_user_id = (?), current_price = (?) WHERE item_id = (?) AND auction_id=(?);";
    public static String INSERT_INTO_BID_HISTORY = "INSERT INTO bid_history(user_id, auction_id, id, amount) VALUES " +
            "((?), (?), (?), (?));";
    public static String INSERT_INTO_BID_REFUND = "INSERT INTO bid_refund(refund, transaction, user_id, id, amount) " +
            "VALUES ((?), (?), (?), (?), (?));";
    public static String SELECT_ALL_FROM_BID_REFUND = "SELECT * FROM bid_refund"; // jezeli jest jakis rekord to ponizsze instrukcje
    public static String MARK_THE_RECORDS_IN_BID_REFUND = "UPDATE bid_refund SET transaction = (?) WHERE refund = false";
    public static String SELECT_BID_HISTORY_WITH_KNOWN_UUID = "SELECT * from bid_refund WHERE refund = false AND transaction = (?)";
    public static String UPDATE_USER_MONEY = "UPDATE user_by_id SET balance = (?) WHERE id = (?)";
    public static String INSERT_REFUND_IS_DONE_TO_MARKED_USERS = "INSERT INTO bid_refund(refund) VALUES (true) WHERE refund = false AND transaction = (?)";
    public static String CHECK_USER_BALANCE = "SELECT id,balance FROM user_by_id";
}
