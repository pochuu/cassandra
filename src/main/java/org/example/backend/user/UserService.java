package org.example.backend.user;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class UserService {
    public void execute(UserBiddingThread UserThread, int numberOfThreads) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.execute(UserThread);
        }
    }
}

