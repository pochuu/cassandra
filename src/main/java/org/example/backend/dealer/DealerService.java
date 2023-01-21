package org.example.backend.dealer;


import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.ResultSet;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.BackendSession;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class DealerService {
    public ExecutorService execute(BackendSession backendSession, int numberOfThreads) throws InterruptedException {
        log.info("Initializing DealerExecutor");
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        BoundStatement selectAllUsersBs = backendSession.getStatementFactory().selectAllUsers();
        ResultSet rs = backendSession.getSession().execute(selectAllUsersBs);
        rs.forEach(
                row -> executorService.execute(new DealerThread(backendSession, row.get("id", int.class)))
        );
        executorService.shutdown();
        log.info("DealerExecutor has stopped execution");
        return executorService;
    }
}

