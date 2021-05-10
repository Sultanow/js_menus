package de.jsmenues.backend.elasticsearch.dao;

import de.jsmenues.backend.elasticsearch.ElasticsearchConnector;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.client.indices.PutIndexTemplateRequest;
import org.elasticsearch.common.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.Arrays;

/**
 * DAO that wraps access to Elasticsearch.
 */
public class ElasticsearchDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchDao.class);

    private final ElasticsearchConnector connector;

    @Inject
    public ElasticsearchDao(ElasticsearchConnector connector) {
        this.connector = connector;
    }

    /**
     * Get indices name from elasticsearch
     *
     * @param patternIndexName example * or index name
     * @return name of indices
     */
    public String[] getIndexName(String patternIndexName) {
        GetIndexRequest request = new GetIndexRequest(patternIndexName);
        try {
            GetIndexResponse response;
            response = connector.getClient().indices().get(request,
                    RequestOptions.DEFAULT);
            return response.getIndices();
        } catch (IOException e) {
            LOGGER.error("Error fetching indices: {0}", e);
        }
        return new String[0];
    }

    /**
     * Get cluster health
     *
     * @return cluster status
     */
    public String getClusterHealth() {
        ClusterHealthRequest request = new ClusterHealthRequest();
        try {
            ClusterHealthResponse response = connector.getClient().cluster().health(request,
                    RequestOptions.DEFAULT);
            int numberOfNodes = response.getNumberOfDataNodes();
            return response.getStatus() + " numberOfNodes:" + numberOfNodes;
        } catch (IOException e) {
            LOGGER.error("Error getting cluster health: {0}", e);
        }
        return null;
    }

    /**
     * Delete index by name from elasticsearch
     *
     * @param indexName index name
     * @return response about delete request
     */
    public boolean deleteIndexByName(String indexName) {
        DeleteIndexRequest request = new DeleteIndexRequest(indexName);
        AcknowledgedResponse deleteIndexResponse;
        try {
            deleteIndexResponse = connector.getClient()
                    .indices().delete(request, RequestOptions.DEFAULT);
            return deleteIndexResponse.isAcknowledged();
        } catch (IOException e) {
            LOGGER.error("Error deleting index: {0}", e);
        }
        return false;
    }

    /**
     * set number of Reolicas of zero by single node cluster
     *
     * @return Whether setting the number of replicas succeeded.
     */
    public boolean setNumberOfReplicasToZeroIfSingleNode() {
        try {
            ClusterHealthRequest clusterRequest = new ClusterHealthRequest();
            ClusterHealthResponse clusterResponse = connector.getClient()
                    .cluster()
                    .health(clusterRequest, RequestOptions.DEFAULT);
            int numberOfNodes = clusterResponse.getNumberOfDataNodes();
            LOGGER.info("numberOfNodes " + numberOfNodes);
            if (numberOfNodes == 1) {
                PutIndexTemplateRequest putIndexTemplateRequest = new PutIndexTemplateRequest("settings-template");
                putIndexTemplateRequest.patterns(Arrays.asList("history*", "host*"));
                putIndexTemplateRequest.settings(Settings.builder().put("index.number_of_replicas", 0));
                AcknowledgedResponse putTemplateResponse = connector.getClient()
                        .indices()
                        .putTemplate(putIndexTemplateRequest, RequestOptions.DEFAULT);

                LOGGER.info("Successfully set index.number_of_replicas to 0.");
                return putTemplateResponse.isAcknowledged();
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("Error setting number of replicas: {0}", e);
        }
        return false;
    }
}
