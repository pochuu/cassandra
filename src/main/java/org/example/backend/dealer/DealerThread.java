package org.example.backend.dealer;

import org.example.backend.BackendSession;

public class DealerThread extends Thread{
    private final BackendSession backendSession;
    public DealerThread(BackendSession backendSession) {
        this.backendSession = backendSession;
    }

    @Override
    public void run() {
        while(true) {
            break;
        }
        backendSession.close();
    }

}
