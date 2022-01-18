package de.jsmenues.backend.elasticsearch;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Singleton;
import java.io.IOException;


/**
 * this class builds the connection to elasticsearch.
 */
@Singleton
public class ElasticsearchConnector {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchConnector.class);

    private static final String HOST = "172.17.0.1"; // TODO: would be better if this were a hostname string
    private static final int PORT = 9200;
    private static final String SCHEME = "http";

    private RestHighLevelClient client;

    /**
     * Opens the connection to Elasticsearch. This being a bean method makes it easier
     * to manage the lifecycle without having to explicitly call these methods somewhere.
     */
    @PostConstruct
    private void openConnection() {
        LOGGER.info("Opening connection to Elasticsearch on {}://{}:{}", SCHEME, HOST, PORT);
        client = new RestHighLevelClient(RestClient.builder(
                new HttpHost(HOST, PORT, SCHEME)
        ));
    }

    /**
     * Closes an existing connection to Elasticsearch. This method is also called automatically.
     */
    @PreDestroy
    private void closeConnection() {
        LOGGER.info("Closing connection to Elasticsearch.");
        try {
            client.close();
        } catch (IOException e) {
            LOGGER.warn("Failed to close connection to Elasticsearch: {0}", e);
        }
    }

    public RestHighLevelClient getClient() {
        return client;
    }
}
