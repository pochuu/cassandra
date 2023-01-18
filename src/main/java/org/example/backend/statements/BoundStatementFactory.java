package org.example.backend.statements;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Session;
import lombok.Getter;
import lombok.experimental.Accessors;

import static org.example.backend.statements.PreparedQueries.*;

@Getter
@Accessors(fluent = true)
public class BoundStatementFactory {

    private final BoundStatement updateBid;
    private final BoundStatement selectBalanceFromUser;
    private final BoundStatement selectAllBids;
    private final BoundStatement insertIntoBidHistory;
    private final BoundStatement updateUserDebt;
    private final BoundStatement selectAllUsers;
    private final BoundStatement selectDebtFromUser;
    private final BoundStatement updateUserBalance;
    private final BoundStatement truncateAuction;
    private final BoundStatement insertIntoAuction;

    public BoundStatementFactory(Session session) {
        updateBid = new BoundStatement(session.prepare(UPDATE_BID_ORDER));
        selectBalanceFromUser = new BoundStatement(session.prepare(SELECT_BALANCE_FROM_USER));
        selectAllBids = new BoundStatement(session.prepare(SELECT_ALL_FROM_BID_ORDER));
        insertIntoBidHistory = new BoundStatement(session.prepare(INSERT_INTO_BID_HISTORY));
        updateUserDebt = new BoundStatement(session.prepare(UPDATE_USER_DEBT));
        selectAllUsers = new BoundStatement(session.prepare(SELECT_ALL_FROM_USERS));
        selectDebtFromUser = new BoundStatement(session.prepare(SELECT_DEBT_FROM_USER));
        updateUserBalance = new BoundStatement(session.prepare(UPDATE_USER_BALANCE));
        truncateAuction = new BoundStatement(session.prepare(TRUNCATE_AUCTION));
        insertIntoAuction = new BoundStatement(session.prepare(INSERT_INTO_AUCTION));
    }
}

