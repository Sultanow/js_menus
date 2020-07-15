package de.jsmenues.backend.authentication;

import java.util.Date;

/**
 *this timer is used to delete old token 
 * 
 */
import java.util.Timer;
import java.util.TimerTask;

public class TimerToDeleteOldTokens {
    long timestamp = System.currentTimeMillis() / 1000L;
    // delay in seconds
    long delay = AuthenticationTokens.VALID_PERIOD;

    LoopTask task = new LoopTask();
    Timer timer = new Timer("TaskName");

    /**
     * start function to start this timer
     * 
     */
    public void start() {
        timer.cancel();
        timer = new Timer("TaskName");
        Date executionDate = new Date();
        timer.scheduleAtFixedRate(task, executionDate, delay);
    }

    private class LoopTask extends TimerTask {
        public void run() {
            AuthenticationTokens.getInstance().deleteOldTokens();
        }
    }
}
