package org.example.backend.dealer;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.example.backend.BackendSession;

@Getter
@Setter
public class DealerThread implements Runnable {
    private final BackendSession backendSession;
    private int user_id;

    public DealerThread(BackendSession backendSession, int user_id) {
        this.backendSession = backendSession;
        this.user_id = user_id;
    }

    @Override
    public void run() {
        boolean run = true;
        while(run){
        run = backendSession.checkUserDebtAndRefundIfNeeded(user_id);
        }

//        backendSession.close();
    }
}
