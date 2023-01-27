package org.example.backend.user;

import lombok.extern.slf4j.Slf4j;
import org.example.backend.BackendSession;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class UserService {
    public ExecutorService execute(BackendSession backendSession, int numberOfThreads) throws InterruptedException {
        log.info("Initializing UserExecutor");
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.execute(new UserBiddingThread(backendSession));
        }
        executorService.shutdown();
        log.info("UserExecutor started executing all threads.");
        return executorService;
    }
}

