package org.example.backend.statements;

public class PreparedQueries {
    public static String SELECT_ALL_FROM_BID_ORDER = "SELECT * FROM bid_order_by_auction_id";
    public static String SELECT_BALANCE_FROM_USER = "SELECT balance FROM user_by_id_with_balance WHERE id=(?)";
    public static String SELECT_ALL_FROM_USERS = "SELECT * FROM user_by_id";
    public static String SELECT_DEBT_FROM_USER = "SELECT * FROM user_by_id_with_debt WHERE id = (?)";
    public static String UPDATE_BID_ORDER = "UPDATE bid_order_by_auction_id "
            + "SET winning_user_id = (?), current_price = (?) WHERE auction_id=(?) IF current_price = (?)";
    public static String INSERT_INTO_BID_HISTORY = "INSERT INTO bid_history(user_id, auction_id, id, amount, timestamp) " +
            "VALUES ((?), (?), uuid(), (?), dateof(now()))";
    public static String UPDATE_USER_DEBT = "UPDATE user_by_id_with_debt SET debt = debt - (?) WHERE id = (?)";
    public static String UPDATE_USER_BALANCE = "UPDATE user_by_id_with_balance SET balance = balance + (?) WHERE id = (?)";
    public static String TRUNCATE_AUCTION = "TRUNCATE bid_order_by_auction_id";
    public static String INSERT_INTO_AUCTION = "INSERT INTO bid_order_by_auction_id(auction_id, item_id, current_price, bid_end_time, min_bid_amount)" +
            " VALUES (?, ?, ?, ?, ?)";
    public static String SELECT_FROM_BID_HISTORY = "SELECT amount FROM bid_history WHERE user_id=(?) AND auction_id=(?)";
}
