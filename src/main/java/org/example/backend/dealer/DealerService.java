package org.example.backend.dealer;


import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.ResultSet;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.BackendSession;
import org.example.backend.user.UserBiddingThread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class DealerService {
    public ExecutorService execute(BackendSession backendSession, int numberOfThreads) throws InterruptedException {
        log.info("Initializing DealerExecutor");
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.execute(new DealerThread(backendSession));
        }
        executorService.shutdown();
        log.info("DealerExecutor started executing all threads.");
        return executorService;
    }
}

