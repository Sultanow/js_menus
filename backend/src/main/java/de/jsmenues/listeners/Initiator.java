package de.jsmenues.listeners;

import de.jsmenues.backend.authentication.TimerToDeleteOldTokens;
import de.jsmenues.backend.elasticsearch.DeleteHistoryTimer;
import de.jsmenues.backend.elasticsearch.ElasticsearchConnector;
import de.jsmenues.backend.elasticsearch.ElasticsearchHelper;
import de.jsmenues.backend.elasticsearch.dao.*;
import de.jsmenues.backend.elasticsearch.expectedvalue.ExpectedValues;
import de.jsmenues.backend.zabbixservice.ZabbixElasticsearchSynchronization;
import de.jsmenues.backend.zabbixservice.ZabbixService;
import de.jsmenues.redis.repository.IConfigurationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;

/**
 * Initiator is notified when the application is deployed on the server
 */
public class Initiator implements ServletContextListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(Initiator.class);

    private static final String DEFAULT_ROOT_PASSWORD = "1234";

    // The list of injected variables tells you that most of these tasks should be done
    // in other classes by using the capabilities of the Java EE environment (automatically
    // scheduled tasks as an example).
    @Inject
    private IConfigurationRepository configurationRepository;

    @Inject
    private ZabbixService zabbixService;

    @Inject
    private ElasticsearchConnector elasticsearchConnector;

    @Inject
    private ElasticsearchDao elasticsearchDao;

    @Inject
    private HistoryDao historyDao;

    @Inject
    private InformationHostDao informationHostDao;

    @Inject
    private HostsDao hostsDao;

    @Inject
    private SnapshotDao snapshotDao;

    /**
     * This method is invoked when the Web Application is ready to service requests
     */
    public void contextInitialized(ServletContextEvent event) {
        LOGGER.info("Application start");

        try {
            ElasticsearchHelper.createIndexIfNotExists(elasticsearchConnector.getClient(),
                    ExpectedValues.INDEX_NAME);
            LOGGER.info("Created index for ExpectedValues.");
        } catch (IOException e) {
            LOGGER.error("Error creating indices in Elasticsearch: {0}", e);
        }

        try {
            if (!snapshotDao.doesBackupRepositoryExist()) {
                snapshotDao.createRepository();
                LOGGER.info("backup repository is created");
            } else {
                LOGGER.info("backup repository exist");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "snopshot reopsitory ist not created");
        }

        try {
            if (snapshotDao.createLifecycle()) {
                LOGGER.info("snapshot lifecycle is created");
            } else {
                LOGGER.info("snapshot lifecycle is not created");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "snapshot lifecycle ist not created");
        }

        try {
            if (snapshotDao.startLifeCycle()) {
                LOGGER.info("lifecycle is started");
            } else {
                LOGGER.info("lifecycle is not started");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "lifecycle is not started");
        }

        // set once the rootPassword as "1234" when Web Application begins and there is
        // not a password
        String currentPassword = configurationRepository.getVal("password");
        if (currentPassword.equals("")) {
            configurationRepository.save("password", DEFAULT_ROOT_PASSWORD);
            LOGGER.info("Set root password to default as it was not yet initialized.");
        }

        TimerToDeleteOldTokens timer = new TimerToDeleteOldTokens();
        timer.start();

        ZabbixElasticsearchSynchronization zabbixElasticsearchSynchronization
                = new ZabbixElasticsearchSynchronization();
        zabbixElasticsearchSynchronization.start(zabbixService,
                elasticsearchDao,
                historyDao,
                informationHostDao,
                hostsDao);

        DeleteHistoryTimer deleteHistoryTimer = new DeleteHistoryTimer();
        deleteHistoryTimer.start(historyDao);
    }
}
