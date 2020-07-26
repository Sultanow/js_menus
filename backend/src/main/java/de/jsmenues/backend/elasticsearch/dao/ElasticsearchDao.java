package de.jsmenues.backend.elasticsearch.dao;

import java.io.IOException;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;

import de.jsmenues.backend.elasticsearch.ElasticsearchConnecter;

public class ElasticsearchDao {

    /**
     * Get indices name from elasticsearch
     *
     * @param patternIndexName example * or index name
     * @return name of indices
     */
    public static String[] getIdexName(String patternIndexName) throws IOException {

        GetIndexRequest request = new GetIndexRequest(patternIndexName);
        GetIndexResponse response = ElasticsearchConnecter.restHighLevelClient.indices().get(request,
                RequestOptions.DEFAULT);
        String[] indices = response.getIndices();
        return indices;
    }

    /**
     * Get cluster health
     * 
     * @param patternIndexName example * or index name
     * @return index status
     */

    public static String getClusterHealth(String patternIndexName) throws IOException {

        ClusterHealthRequest request = new ClusterHealthRequest(patternIndexName);
        ClusterHealthResponse response = ElasticsearchConnecter.restHighLevelClient.cluster().health(request,
                RequestOptions.DEFAULT);

        int numberOfNodes = response.getNumberOfDataNodes();
        String staus = response.getStatus().toString();
        return staus + "  " + numberOfNodes;
    }

    /**
     * Delete index by name from elasticsearch
     *
     * @param indexName index name
     * @return response about delete request
     */
    public static boolean deleteIndexByName(String indexName) throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest(indexName);
        AcknowledgedResponse deleteIndexResponse = ElasticsearchConnecter.restHighLevelClient.indices().delete(request,
                RequestOptions.DEFAULT);
        boolean acknowledged = deleteIndexResponse.isAcknowledged();
        return acknowledged;
    }
}
