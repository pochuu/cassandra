package org.example.backend.dealer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class DealerService {
    public void execute(DealerThread scenario, int numberOfThreads) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.execute(scenario);
        }
    }
}

