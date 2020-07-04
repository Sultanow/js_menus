package de.jsmenues.backend.authentication;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TimerToDeleteOldTokens {
    long timestamp = System.currentTimeMillis();
    // delay in seconds
    long delay = AuthenticationTokens.VALID_PERIOD;

    LoopTask task = new LoopTask();
    Timer timer = new Timer("TaskName");

    public void start() {
        timer.cancel();
        timer = new Timer("TaskName");
        Date executionDate = new Date();
        timer.scheduleAtFixedRate(task, executionDate, delay);
    }
    //timer to delet old tokens from map
    private class LoopTask extends TimerTask {
        public void run() {
            AuthenticationTokens.getInstance().deleteOldTokens();
        }
    }
}
