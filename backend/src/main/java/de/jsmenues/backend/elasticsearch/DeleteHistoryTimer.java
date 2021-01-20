package de.jsmenues.backend.elasticsearch;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import de.jsmenues.backend.elasticsearch.dao.HistoryDao;
/**
 * Timer is used to delete the old data in elasticsearch
 *
 */
public class DeleteHistoryTimer {
    long delay = (long) 24 * 60 * 60 * 1000; // delay in milliseconds-24 hours
    LoopTask task = new LoopTask();
    Timer timer = new Timer("Synchronization");

    public void start() {
        timer.cancel();
        timer = new Timer("Delete");
        Date executionDate = new Date();
        timer.scheduleAtFixedRate(task, executionDate, delay);
    }

    private class LoopTask extends TimerTask {

        public void run() {           
            HistoryDao.DeletehistoryRecordsAfterCertainTime();
        }
    }
}