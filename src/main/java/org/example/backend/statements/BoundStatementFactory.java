package org.example.backend.statements;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Session;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.cassandra.db.RangeTombstone;

import static org.example.backend.statements.PreparedQueries.*;

@Getter
@Accessors(fluent = true)
public class BoundStatementFactory {

    private final BoundStatement updateBid;
    private final BoundStatement selectBalanceFromUser;
    private final BoundStatement selectAllBids;
    private final BoundStatement insertIntoBidHistory;
    private final BoundStatement insertIntoBidRefund;
    private final BoundStatement CheckUserBalance;
    private final BoundStatement UpdateUserMoney;
    private final BoundStatement SelectAllBidRefund;
    private final BoundStatement UpdateTransactionInBidRefund;
    private final BoundStatement SelectBidRefundWhereTransactionAndRefund;
    private final BoundStatement InsertBidRefundToTrue;


    public BoundStatementFactory(Session session) {
        updateBid = new BoundStatement(session.prepare(UPDATE_BID_ORDER));
        selectBalanceFromUser = new BoundStatement(session.prepare(SELECT_BALANCE_FROM_USER));
        selectAllBids = new BoundStatement(session.prepare(SELECT_ALL_FROM_BID_ORDER));
        insertIntoBidHistory = new BoundStatement(session.prepare(INSERT_INTO_BID_HISTORY));
        insertIntoBidRefund= new BoundStatement(session.prepare(INSERT_INTO_BID_REFUND));
        CheckUserBalance = new BoundStatement(session.prepare(CHECK_USER_BALANCE));
        SelectAllBidRefund = new BoundStatement(session.prepare(SELECT_ALL_FROM_BID_REFUND));
        UpdateTransactionInBidRefund = new BoundStatement(session.prepare(MARK_THE_RECORDS_IN_BID_REFUND));
        SelectBidRefundWhereTransactionAndRefund = new BoundStatement(session.prepare(SELECT_BID_HISTORY_WITH_KNOWN_UUID));
        InsertBidRefundToTrue = new BoundStatement(session.prepare(INSERT_REFUND_IS_DONE_TO_MARKED_USERS));
        UpdateUserMoney = new BoundStatement(session.prepare(UPDATE_USER_MONEY));
    }

}

