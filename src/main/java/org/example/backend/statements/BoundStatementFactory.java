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
    private final BoundStatement createKeySpace;
    private final BoundStatement CheckUserBalance;
    private final BoundStatement GiveBackMoneyToUser;


    public BoundStatementFactory(Session session) {
        updateBid = new BoundStatement(session.prepare(UPDATE_BID_ORDER));
        selectBalanceFromUser = new BoundStatement(session.prepare(SELECT_BALANCE_FROM_USER));
        selectAllBids = new BoundStatement(session.prepare(SELECT_ALL_FROM_BID_ORDER));
        insertIntoBidHistory = new BoundStatement(session.prepare(INSERT_INTO_BID_HISTORY));
        createKeySpace = new BoundStatement(session.prepare(CREATE_KEYSPACE_IF_NOT_EXISTS));
        CheckUserBalance = new BoundStatement(session.prepare(CHECK_USER_BALANCE));
        GiveBackMoneyToUser = new BoundStatement(session.prepare(GIVE_BACK_MONEY_TO_USER));
    }
}

