package de.jsmenues.backend.elasticsearch;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Helper class for common Elasticsearch interactions.
 */
public class ElasticsearchHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchHelper.class);

    /**
     * Creates an index if it does not yet exist.
     *
     * @param client    The client to use to access the database.
     * @param indexName The index to create.
     * @throws IOException If there is an error during communication with Elasticsearch.
     */
    public static void createIndexIfNotExists(RestHighLevelClient client, String indexName) throws IOException {
        GetIndexRequest checkIfIndexExistRequest = new GetIndexRequest(indexName);

        LOGGER.info("Checking if exists for: " + indexName);
        boolean exists = client
                .indices()
                .exists(checkIfIndexExistRequest, RequestOptions.DEFAULT);
        if (!exists) {
            CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
            CreateIndexResponse createIndexResponse = client
                    .indices()
                    .create(createIndexRequest, RequestOptions.DEFAULT);
            boolean acknowledged = createIndexResponse.isAcknowledged();
            LOGGER.info(indexName + " did not exist, createResponse: " + createIndexResponse);
            LOGGER.info(" was it acknowledged?: " + acknowledged);
        } else {
            LOGGER.info(indexName + " did already exist");
        }
    }
}
