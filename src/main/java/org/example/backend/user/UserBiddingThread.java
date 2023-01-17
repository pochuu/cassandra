package org.example.backend.user;

import org.example.backend.BackendSession;

public class UserBiddingThread extends Thread {
    private final BackendSession backendSession;

    public UserBiddingThread(BackendSession backendSession) {
        this.backendSession = backendSession;
    }

    @Override
    public void run() {
        while (true) {
            backendSession.checkForAuctionsAndPlaceBidIfImNotTheWinner();
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
