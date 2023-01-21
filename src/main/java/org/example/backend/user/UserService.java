package org.example.backend.user;

import org.example.backend.BackendSession;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserService {
    public ExecutorService execute(BackendSession backendSession, int numberOfThreads) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.execute(new UserBiddingThread(backendSession));
        }
        executorService.shutdown();
        System.out.println("ex2 skonczyl egzekucje");
        return executorService;
    }
}

