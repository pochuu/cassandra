package org.example.backend.dealer;

import org.example.backend.BackendSession;

public class DealerThread implements Runnable{
    private final BackendSession backendSession;
    public DealerThread(BackendSession backendSession) {
        this.backendSession = backendSession;
    }

    @Override
    public void run() {
        System.out.println("test");
    }

}
