package org.example.backend.statements;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.ConsistencyLevel;
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
    private final BoundStatement selectFromBidHistory;

    public BoundStatementFactory(Session session) {
        updateBid = new BoundStatement(session.prepare(UPDATE_BID_ORDER).setConsistencyLevel(ConsistencyLevel.QUORUM));
        selectBalanceFromUser = new BoundStatement(session.prepare(SELECT_BALANCE_FROM_USER));
        selectAllBids = new BoundStatement(session.prepare(SELECT_ALL_FROM_BID_ORDER));
        insertIntoBidHistory = new BoundStatement(session.prepare(INSERT_INTO_BID_HISTORY).setConsistencyLevel(ConsistencyLevel.QUORUM));
        updateUserDebt = new BoundStatement(session.prepare(UPDATE_USER_DEBT).setConsistencyLevel(ConsistencyLevel.QUORUM));
        selectAllUsers = new BoundStatement(session.prepare(SELECT_ALL_FROM_USERS));
        selectDebtFromUser = new BoundStatement(session.prepare(SELECT_DEBT_FROM_USER).setConsistencyLevel(ConsistencyLevel.ONE));
        updateUserBalance = new BoundStatement(session.prepare(UPDATE_USER_BALANCE).setConsistencyLevel(ConsistencyLevel.ONE));
        truncateAuction = new BoundStatement(session.prepare(TRUNCATE_AUCTION));
        insertIntoAuction = new BoundStatement(session.prepare(INSERT_INTO_AUCTION));
        selectFromBidHistory = new BoundStatement(session.prepare(SELECT_FROM_BID_HISTORY));
    }
}

