package org.example.backend.statements;

public class PreparedQueries {
    public static String SELECT_ALL_FROM_BID_ORDER = "SELECT * FROM bid_order_by_item_id";
    public static String SELECT_BALANCE_FROM_USER = "SELECT balance FROM user_by_id WHERE id=(?)";
    public static String SELECT_ALL_FROM_USERS = "SELECT * FROM user_by_id";
    public static String SELECT_DEBT_FROM_USER = "SELECT * FROM user_by_id_with_debt WHERE id = (?)";
    public static String UPDATE_BID_ORDER = "UPDATE bid_order_by_item_id "
            + "SET winning_user_id = (?), current_price = (?) WHERE auction_id=(?) IF current_price = (?)";
    public static String INSERT_INTO_BID_HISTORY = "INSERT INTO bid_history(user_id, auction_id, id, amount) " +
            "VALUES ((?), (?), uuid(), (?))";
    public static String UPDATE_USER_DEBT = "UPDATE user_by_id_with_debt SET debt = debt + (?) WHERE id = (?)";
    public static String SELECT_USER_DEBT  = "SELECT * FROM user_by_id_with_debt";

}