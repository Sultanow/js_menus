package de.jsmenues.listeners;

import de.jsmenues.backend.authentication.TimerToDeleteOldTokens;
import de.jsmenues.backend.elasticsearch.DeleteHistoryTimer;
import de.jsmenues.backend.elasticsearch.ElasticsearchConnector;
import de.jsmenues.backend.elasticsearch.dao.*;
import de.jsmenues.backend.zabbixservice.ZabbixElasticsearchSynchronization;
import de.jsmenues.backend.zabbixservice.ZabbixService;
import de.jsmenues.redis.repository.IConfigurationRepository;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
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

        final String expectedValuesIndex = "expected_values";
        createIndexIfNotExists(expectedValuesIndex);

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

    private void createIndexIfNotExists(final String indexName) {
        GetIndexRequest checkIfIndexExistRequest = new GetIndexRequest(indexName);

        try {
            LOGGER.info("Checking if exists for: " + indexName);
            boolean exists = elasticsearchConnector.getClient()
                    .indices()
                    .exists(checkIfIndexExistRequest, RequestOptions.DEFAULT);
            if (!exists) {
                CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
                CreateIndexResponse createIndexResponse = elasticsearchConnector.getClient()
                        .indices()
                        .create(createIndexRequest, RequestOptions.DEFAULT);
                boolean acknowledged = createIndexResponse.isAcknowledged();
                LOGGER.info(indexName + " did not exist, createResponse: " + createIndexResponse);
                LOGGER.info(" was it acknowledged?: " + acknowledged);
            } else {
                LOGGER.info(indexName + " did already exist");
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
