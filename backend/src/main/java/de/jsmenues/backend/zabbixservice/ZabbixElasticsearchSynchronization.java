package de.jsmenues.backend.zabbixservice;

import de.jsmenues.backend.elasticsearch.dao.ElasticsearchDao;
import de.jsmenues.backend.elasticsearch.dao.HistoryDao;
import de.jsmenues.backend.elasticsearch.dao.HostsDao;
import de.jsmenues.backend.elasticsearch.dao.InformationHostDao;
import de.jsmenues.listeners.Initiator;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Timer is used to held the synchronization between zabbix and elasticsearch
 *
 */
public class ZabbixElasticsearchSynchronization {
    private static Logger LOGGER = LoggerFactory.getLogger(ZabbixElasticsearchSynchronization.class);
    public static boolean stopSynchronization = false;
    long delay = 240000; // delay in milliseconds 4 minutes
    LoopTask task = new LoopTask();
    Timer timer = new Timer("Synchronization");

    public void start() {
        timer.cancel();
        timer = new Timer("Synchronization");
        Date executionDate = new Date();
        timer.scheduleAtFixedRate(task, executionDate, delay);
    }

    private class LoopTask extends TimerTask {

        public void run() {
            if (!stopSynchronization) {

                LOGGER.info("Synchronization is starting");
                ZabbixService zabbixService = new ZabbixService();
                List<Map<String, List<Object>>> allHostsInfo = zabbixService.getHostInfos();
                List<Map<String, Object>> histories = zabbixService.getHistory();
                List<Map<String, Object>> allHosts = zabbixService.getAllHosts();
                if (allHostsInfo == null && histories == null) {
                    LOGGER.info("there is not right connection with zabbix");
                    return;
                }

                try {
                    if (Initiator.firstCall)
                        Initiator.firstCall = ElasticsearchDao.setNumberOfReplicaOFZero();
                } catch (Exception e) {
                    LOGGER.error(e.getMessage() + "\n elasticsearch is not avalible");
                }

                try {
                    InformationHostDao.insertAllHostInformation(allHostsInfo);
                    HostsDao.insertAllHosts(allHosts);
                    HistoryDao.insertHistory(histories);

                } catch (Exception e) {
                    LOGGER.error(e.getMessage() + "\n elasticsearch is not avalible");
                }
            } else {
                LOGGER.info("Synchrinzation is stopped");
            }
        }
    }
}
