package de.jsmenues.listeners;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jsmenues.backend.authentication.Password;
import de.jsmenues.backend.authentication.TimerToDeleteOldTokens;
import de.jsmenues.backend.elasticsearch.DeleteHistoryTimer;
import de.jsmenues.backend.elasticsearch.ElasticsearchConnector;
import de.jsmenues.backend.elasticsearch.dao.SnapshotDao;
import de.jsmenues.backend.zabbixservice.ZabbixElasticsearchSynchronization;
import de.jsmenues.redis.repository.ConfigurationRepository;

/**
 * Initiator is notified when the application is deployed on the server
 */
public class Initiator implements ServletContextListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(Initiator.class);
    private static final String DEFAULT_ROOT_PASSWORD = "1234";

    public static boolean firstCall = true;

    /**
     * This method is invoked when the Web Application has been removed and is no
     * longer able to accept requests
     */
    public void contextDestroyed(ServletContextEvent event) {
        try {
            LOGGER.info("Trying to close Elasticsearch connection.");
            ElasticsearchConnector.closeConnection();
        } catch (IOException e) {
            LOGGER.error("contextDestroyed error: " + e.getMessage());
        }
    }

    /**
     * This method is invoked when the Web Application is ready to service requests
     */
    public void contextInitialized(ServletContextEvent event) {
        LOGGER.info("Application start");

        ElasticsearchConnector.makeConnection();
        LOGGER.info("Connection opend with elasticsearch");

        final String expectedValuesIndex = "expected_values";
        createIndexIfNotExists(expectedValuesIndex);

        try {
            if (!SnapshotDao.ifRepositoryExist()) {
                SnapshotDao.creatRepository();
                LOGGER.info("backup repository is created");
            } else {
                LOGGER.info("backup repository exist");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "snopshot reopsitory ist not created");
        }

        try {
            if (SnapshotDao.createLifecycle()) {
                LOGGER.info("snapshot lifecycle is created");
            } else {
                LOGGER.info("snapshot lifecycle is not created");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "snapshot lifecycle ist not created");
        }

        try {
            if (SnapshotDao.startLifeCycle()) {
                LOGGER.info("lifecycle is started");
            } else {
                LOGGER.info("lifecycle is not started");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "lifecycle is not started");
        }

        // set once the rootPassword as "1234" when Web Application begins and there is
        // not a password
        String currentPassword = ConfigurationRepository.getRepo().getVal("password");
        if (currentPassword.equals("")) {
            Password.setRootPassword(DEFAULT_ROOT_PASSWORD);
            LOGGER.info("Set root password to default as it was not yet initialized.");
        }

        TimerToDeleteOldTokens timer = new TimerToDeleteOldTokens();
        timer.start();

        ZabbixElasticsearchSynchronization zabbixElasticsearchSynchronization = new ZabbixElasticsearchSynchronization();
        zabbixElasticsearchSynchronization.start();

        DeleteHistoryTimer deleteHistoryTimer = new DeleteHistoryTimer();
        deleteHistoryTimer.start();

    }

    private void createIndexIfNotExists(final String indexName) {
        GetIndexRequest checkIfIndexExistRequest = new GetIndexRequest(indexName);

        try {
            LOGGER.info("Checking if exists for: " + indexName);
            boolean exists = ElasticsearchConnector.restHighLevelClient.indices().exists(checkIfIndexExistRequest, RequestOptions.DEFAULT);
            if (!exists) {
                CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
                CreateIndexResponse createIndexResponse = ElasticsearchConnector.restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
                boolean acknowledged = createIndexResponse.isAcknowledged();
                LOGGER.info(indexName + " did not exist, createResponse: " + createIndexResponse);
                LOGGER.info(" was it acknowledged?: " + acknowledged);
            } else LOGGER.info(indexName + " did already exist");
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
