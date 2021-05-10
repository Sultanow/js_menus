package de.jsmenues.backend.elasticsearch;

import de.jsmenues.backend.elasticsearch.dao.HistoryDao;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Timer is used to delete the old data in elasticsearch
 */
public class DeleteHistoryTimer {
    private static final long PERIOD = TimeUnit.DAYS.toMillis(1);

    public void start(HistoryDao dao) {
        Timer timer = new Timer("DeleteOldHistoryRecords");

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                dao.deleteHistoryRecordsAfterCertainTime();
            }
        }, 0L, PERIOD);
    }
}