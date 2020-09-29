package de.jsmenues.backend.elasticsearch.dao;

import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
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
     * Insert all batches to elasticsearch
     * 
     * @param batches
     * @return status if new batches are inserted, updated or not
     */
    public static void insertAllBatches(List<Map<String, Object>> batches) throws IOException {
        IndexResponse indexResponse = null;
        for (Map<String, Object> batches : batches) {
            // String batchId = String.valueOf( batch.get("batchid"));
            IndexRequest indexRequest = new IndexRequest(INDEX).source(batch).id(batch);
            GetRequest getRequest = new GetRequest(INDEX, batchId);
            boolean exists = ElasticsearchConnecter.restHighLevelClient.exists(getRequest, RequestOptions.DEFAULT);
            if (!exists) {
                indexResponse = ElasticsearchConnecter.restHighLevelClient.index(indexRequest,
                        RequestOptions.DEFAULT);
                LOGGER.info(indexResponse + "\n batches are inserted");
            } 
        }
        if(indexResponse==null)
        LOGGER.info("batches existed");
    }

    /**
     * Get all batches from elasticsearch
     *
     * @return list of batches
     */
    public static List<Map<String, Object>> getAllbatches() throws IOException {

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
     * Get all batchNames from elasticsearch
     *
     * @return list of batchNames
     */
    public static ArrayList<String> getAllBatchName() {
        ArrayList<String> batchNames = new ArrayList<String>();
     
        try {
            List<Map<String, Object>> allBatch = getAllBatches();
            for(Map<String, Object> batch : allBatch ) {
                // String batchName = String.valueOf(batch.get("batch"));
                batchNames.add(batch);
                }
        }catch(IOException e) {
          
            e.printStackTrace();
        }    
        return batchNames;
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
