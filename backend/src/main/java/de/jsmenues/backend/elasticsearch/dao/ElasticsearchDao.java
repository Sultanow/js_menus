package de.jsmenues.backend.elasticsearch.dao;

import java.io.IOException;
import java.util.Arrays;

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

import de.jsmenues.backend.elasticsearch.ElasticsearchConnector;
import de.jsmenues.listeners.Initiator;

public class ElasticsearchDao {
    private static Logger LOGGER = LoggerFactory.getLogger(ElasticsearchDao.class);

    /**
     * Get indices name from elasticsearch
     *
     * @param patternIndexName example * or index name
     * @return name of indices
     */
    public static String[] getIndexName(String patternIndexName) throws IOException {

        GetIndexRequest request = new GetIndexRequest(patternIndexName);
        GetIndexResponse response = ElasticsearchConnector.restHighLevelClient.indices().get(request,
                RequestOptions.DEFAULT);
        String[] indices = response.getIndices();
        return indices;
    }

    /**
     * Get cluster health
     * 
     * @return cluster status
     */
    public static String getClusterHealth() throws IOException {

        ClusterHealthRequest request = new ClusterHealthRequest();
        ClusterHealthResponse response = ElasticsearchConnector.restHighLevelClient.cluster().health(request,
                RequestOptions.DEFAULT);
        int numberOfNodes = response.getNumberOfDataNodes();
        String staus = response.getStatus().toString();
        return staus + " numberOfNodes:" + numberOfNodes;
    }

    /**
     * Delete index by name from elasticsearch
     *
     * @param indexName index name
     * @return response about delete request
     */
    public static boolean deleteIndexByName(String indexName) throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest(indexName);
        AcknowledgedResponse deleteIndexResponse = ElasticsearchConnector.restHighLevelClient.indices().delete(request,
                RequestOptions.DEFAULT);
        boolean acknowledged = deleteIndexResponse.isAcknowledged();
        return acknowledged;
    }

    /**
     * set number of Reolicas of zero by single node cluster
     * 
     * @return number of nodes
     */
    public static boolean setNumberOfReplicaOFZero() throws IOException {

        try {
            ClusterHealthRequest clusterRequest = new ClusterHealthRequest();
            ClusterHealthResponse clusterResponse = ElasticsearchConnector.restHighLevelClient.cluster()
                    .health(clusterRequest, RequestOptions.DEFAULT);
            int numberOfNodes = clusterResponse.getNumberOfDataNodes();
            LOGGER.info("numberOfNodes " + numberOfNodes);
            if (numberOfNodes == 1) {
                PutIndexTemplateRequest putIndexTemplateRequest = new PutIndexTemplateRequest("settings-template");
                putIndexTemplateRequest.patterns(Arrays.asList("history*", "host*"));
                putIndexTemplateRequest.settings(Settings.builder().put("index.number_of_replicas", 0));
                AcknowledgedResponse putTemplateResponse = ElasticsearchConnector.restHighLevelClient.indices()
                        .putTemplate(putIndexTemplateRequest, RequestOptions.DEFAULT);

                Initiator.firstCall = !putTemplateResponse.isAcknowledged();
                LOGGER.info("index number_of_replicas = 0 ");
            }
        }catch(Exception e) {
            LOGGER.error(e.getMessage() + "\n elasticsearch is not avalible");
        }
        return Initiator.firstCall;
    }
}
