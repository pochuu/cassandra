package org.example.backend.dealer;

import com.datastax.driver.core.BoundStatement;
import lombok.Getter;
import lombok.Setter;
import org.example.backend.BackendSession;

@Getter
@Setter
public class DealerThread implements Runnable{
    private final BackendSession backendSession;
    private int user_id;
    public DealerThread(BackendSession backendSession, int user_id) {
        this.backendSession = backendSession;
        this.user_id = user_id;
    }

    @Override
    public void run() {
        backendSession.giveRefundsToUsers();
    }

}
