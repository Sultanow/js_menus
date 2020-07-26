package de.jsmenues.backend.zabbixservice;

import de.jsmenues.backend.elasticsearch.ElasticsearchConnecter;
import de.jsmenues.backend.elasticsearch.dao.HistoryDao;
import de.jsmenues.backend.elasticsearch.dao.InformationHostDao;
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

            LOGGER.info("Timer is starting");
            ZabbixService zabbixService = new ZabbixService();
            List<Map<String, List<Object>>> allHostsInfo = zabbixService.getHostInfos();
            List<Map<String, Object>> histories = zabbixService.getHistory();
            if (allHostsInfo == null && histories == null) {
                LOGGER.info("there is not right connection with zabbix");
                return;
            }

            ElasticsearchConnecter.makeConnection();
            LOGGER.info("Connection opend with elasticsearch");
            ClusterHealthRequest request = new ClusterHealthRequest(".kibana_1");
            ClusterHealthResponse response = null;
            String kibanStatus = null;

            try {
                response = ElasticsearchConnecter.restHighLevelClient.cluster().health(request, RequestOptions.DEFAULT);
                kibanStatus = response.getStatus().toString();
                LOGGER.info("Kibana Status: " + kibanStatus);
            } catch (IOException e1) {
                LOGGER.error(e1.getMessage() + "\n Kibana is not avalible");
            }

            try {
                if (kibanStatus.equals("GREEN")) {
                    InformationHostDao.insertAllHostInformation(allHostsInfo);
                    HistoryDao.insertHistory(histories);

                } else {
                    LOGGER.error("\n Kibana hasn't been avalible yet ");
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage() + "\n elasticsearch is not avalible");
            }
        }
    }
}
