package de.jsmenues.backend.authentication;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Recurring task that deletes any expired tokens from an {@link AuthenticationTokens} object.
 */
public class TimerToDeleteOldTokens {
    public static final String TIMER_TASK_NAME = "DeleteOldTokens";

    private final Timer timer = new Timer(TIMER_TASK_NAME);

    /**
     * Starts running the task regularly.
     */
    public void start() {
        timer.scheduleAtFixedRate(
                new TimerTask() {
                    @Override
                    public void run() {
                        // TODO: do not use singleton here.
                        AuthenticationTokens.getInstance().deleteOldTokens();
                    }
                },
                0L,
                AuthenticationTokens.VALIDITY_PERIOD
        );
    }
}
