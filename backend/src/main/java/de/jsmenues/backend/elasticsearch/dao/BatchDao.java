package de.jsmenues.backend.elasticsearch.dao;

import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.delete.UpdateRequest;
import org.elasticsearch.action.delete.UpdateResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jsmenues.backend.elasticsearch.ElasticsearchConnecter;
import de.jsmenues.backend.elasticsearch.controller.BatchController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BatchDao {
    private static Logger LOGGER = LoggerFactory.getLogger(BatchDao.class);
    private static final String INDEX = "batch";

    /**
     * Insert batch to elasticsearch
     * 
     * @param batches
     * @return status if new batch is inserted, updated or not
     */
    public static void insertBatch(Map<String, Object> batch) throws IOException {
        IndexResponse indexResponse = null;
        String batchId = String.valueOf(batch.get("batchid"));
        IndexRequest indexRequest = new IndexRequest(INDEX).source(batch).id(batch);
        GetRequest getRequest = new GetRequest(INDEX, batchId);
        boolean exists = ElasticsearchConnecter.restHighLevelClient.exists(getRequest, RequestOptions.DEFAULT);
        if (!exists) {
            indexResponse = ElasticsearchConnecter.restHighLevelClient.index(indexRequest,
                    RequestOptions.DEFAULT);
            LOGGER.info(indexResponse + "\n batch is inserted");
        } 
        if(indexResponse==null) LOGGER.info("batch existed");
    }

    /**
     * Get all batches from elasticsearch
     *
     * @return list of batches
     */
    public static List<Map<String, Object>> getAllBatches() throws IOException {

        SearchRequest searchRequest = new SearchRequest(INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.size(10000);
        searchRequest.source(searchSourceBuilder);
        searchRequest.scroll(TimeValue.timeValueSeconds(30L));

        SearchResponse response = ElasticsearchConnecter.restHighLevelClient.search(searchRequest,
                RequestOptions.DEFAULT);
        SearchHit[] searchHits = response.getHits().getHits();
        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        for (SearchHit hit : searchHits) {
            Map<String, Object> result = hit.getSourceAsMap();
            results.add(result);
        }
        return results;
    }
    
    /**
     * Update batch by id from elasticsearch
     * @param batchId
     */
    public static String updateBatchById(String batchId, Object batch) throws IOException {
        UpdateRequest updateRequest = new UpdateRequest(INDEX, batchId);
        updateRequest.doc(batch);
        UpdateResponse updateResponse = ElasticsearchConnecter.restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);

        return updateResponse.toString();
    }

    /**
     * Delete batch by id from elasticsearch
     * @param batchId
     */
    public static String deleteBatchById(String batchId) throws IOException {

        DeleteRequest deleteRequest = new DeleteRequest(INDEX, batchId);
        DeleteResponse deleteResponse = ElasticsearchConnecter.restHighLevelClient.delete(deleteRequest,
                RequestOptions.DEFAULT);
        return deleteResponse.toString();
    }
}
