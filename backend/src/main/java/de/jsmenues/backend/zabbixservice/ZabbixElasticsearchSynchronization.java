package de.jsmenues.backend.zabbixservice;

import de.jsmenues.backend.elasticsearch.dao.ElasticsearchDao;
import de.jsmenues.backend.elasticsearch.dao.HistoryDao;
import de.jsmenues.backend.elasticsearch.dao.HostsDao;
import de.jsmenues.backend.elasticsearch.dao.InformationHostDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Timer is used to held the synchronization between zabbix and elasticsearch
 *
 */
public class ZabbixElasticsearchSynchronization {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZabbixElasticsearchSynchronization.class);

    // TODO: should not be static.
    public static boolean stopSynchronization = false;

    private static final long DELAY = TimeUnit.MINUTES.toMillis(4);

    private Timer timer = new Timer("Synchronization");

    private boolean setReplicasSucceeded = false;

    public void start(ZabbixService zabbixService,
                      ElasticsearchDao elasticsearchDao,
                      HistoryDao historyDao,
                      InformationHostDao informationHostDao,
                      HostsDao hostsDao) {
        timer.cancel();
        timer = new Timer("Synchronization");
        Date executionDate = new Date();
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                if (!stopSynchronization) {
                    LOGGER.info("Synchronization is starting");
                    List<Map<String, List<Object>>> allHostsInfo = zabbixService.getHostInfos();
                    List<Map<String, Object>> histories = zabbixService.getHistory();
                    List<Map<String, Object>> allHosts = zabbixService.getAllHosts();

                    if (allHostsInfo == null || histories == null) {
                        LOGGER.info("there is not correct connection with zabbix, hosts or histories was null");
                        return;
                    }

                    if(!setReplicasSucceeded) {
                        setReplicasSucceeded = elasticsearchDao.setNumberOfReplicasToZeroIfSingleNode();
                    }

                    try {
                        LOGGER.debug("Begin insert from zabbix");
                        LOGGER.debug("allHostInfo" + allHostsInfo);
                        LOGGER.debug("allHost" + allHosts.toString());
                        LOGGER.debug("histories" + histories);

                        informationHostDao.insertAllHostInformation(allHostsInfo);
                        hostsDao.insertAllHosts(allHosts);
                        historyDao.insertHistory(histories);

                    } catch (Exception e) {
                        LOGGER.error(e.getMessage() + "\n elasticsearch is not avalible");
                    }
                } else {
                    LOGGER.info("Synchronization is stopped");
                }
            }
        }, executionDate, DELAY);
    }
}
