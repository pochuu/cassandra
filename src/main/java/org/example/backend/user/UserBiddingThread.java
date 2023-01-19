package org.example.backend.user;

import org.example.backend.BackendSession;

public class UserBiddingThread extends Thread {
    private final BackendSession backendSession;

    public UserBiddingThread(BackendSession backendSession) {
        this.backendSession = backendSession;
    }

    @Override
    public void run() {
        boolean run = true;
        while (run) {
            run = backendSession.checkForAuctionsAndPlaceBidIfImNotTheWinner();
        }
        backendSession.close();
    }
}
