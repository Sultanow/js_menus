package de.jsmenues.backend.zabbixservice;

import de.jsmenues.backend.elasticsearch.ElasticsearchConnecter;
import de.jsmenues.backend.elasticsearch.dao.ElasticsearchDao;
import de.jsmenues.backend.elasticsearch.dao.HistoryDao;
import de.jsmenues.backend.elasticsearch.dao.HostsDao;
import de.jsmenues.backend.elasticsearch.dao.InformationHostDao;
import de.jsmenues.listeners.Initiator;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.RequestOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Timer is used to held the synchronization between zabbix and elasticsearch
 *
 */
public class ZabbixElasticsearchSynchronization {
    private static Logger LOGGER = LoggerFactory.getLogger(ZabbixElasticsearchSynchronization.class);
    public static boolean stopSynchronization = false;
    long delay = 300000; // delay in milliseconds 5 minutes
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
                String kibanStatus = null;

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
                    ClusterHealthRequest request = new ClusterHealthRequest(".kibana_1");
                    ClusterHealthResponse response = ElasticsearchConnecter.restHighLevelClient.cluster()
                            .health(request, RequestOptions.DEFAULT);
                    kibanStatus = response.getStatus().toString();
                    LOGGER.info("Kibana Status: " + kibanStatus);
                } catch (IOException e1) {
                    LOGGER.error(e1.getMessage() + "\n Kibana is not avalible");
                }

                try {
                    if (kibanStatus.equals("GREEN")) {
                       // HistoryDao.DeletehistoryRecordsAfterTwoYears();
                        InformationHostDao.insertAllHostInformation(allHostsInfo);
                        HostsDao.insertAllHosts(allHosts);
                        HistoryDao.insertHistory(histories);
                        

                    } else {
                        LOGGER.error("\n Kibana hasn't been avalible yet ");
                    }
                } catch (Exception e) {
                    LOGGER.error(e.getMessage() + "\n elasticsearch is not avalible");
                }
            }else {
                LOGGER.info("Synchrinzation is stopped");
            }
        }
    }
}
