package de.jsmenues.backend.elasticsearch.dao;

import de.jsmenues.backend.elasticsearch.ElasticsearchConnector;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.get.GetResult;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BatchDao {
    private static final String INDEX = "batch";
    private static Logger LOGGER = LoggerFactory.getLogger(BatchDao.class);

    /**
     * Insert batch to elasticsearch
     *
     * @param batch
     * @return status if new batch is inserted, updated or not
     */
    public static IndexResponse insertBatch(Map<String, Object> batch) throws IOException {
        IndexResponse indexResponse = null;
        String batchId = String.valueOf(batch.get("id"));
        IndexRequest indexRequest = new IndexRequest(INDEX).source(batch).id(String.valueOf(batchId));
        GetRequest getRequest = new GetRequest(INDEX, batchId);
        boolean exists = ElasticsearchConnector.restHighLevelClient.exists(getRequest, RequestOptions.DEFAULT);
        if (!exists) {
            indexResponse = ElasticsearchConnector.restHighLevelClient.index(indexRequest,
                    RequestOptions.DEFAULT);
            LOGGER.info(indexResponse + "\n batch is inserted");
        }
        if (indexResponse == null) LOGGER.info("batch existed");
        
        return indexResponse;
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

        SearchResponse response = ElasticsearchConnector.restHighLevelClient.search(searchRequest,
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
     *
     * @param batchId
     */
    public static String updateBatchById(String batchId, Map<String, Object> batchMap) throws IOException {
        UpdateRequest updateRequest = new UpdateRequest(INDEX, batchId);
        updateRequest.doc(batchMap);
        updateRequest.fetchSource(true); // enable source retrieval so we can fetch the source of updated doc
        UpdateResponse updateResponse = ElasticsearchConnector.restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        GetResult result = updateResponse.getGetResult(); 
        if (result.isExists()) {
            return result.sourceAsString();
        }
        else {
        	LOGGER.warn("updateBatchById: updateResponse result didnt exist!");
        	return updateResponse.getResult().toString();
        }
    }

    /**
     * Delete batch by id from elasticsearch
     *
     * @param batchId
     */
    public static String deleteBatchById(String batchId) throws IOException {

        DeleteRequest deleteRequest = new DeleteRequest(INDEX, batchId);
        DeleteResponse deleteResponse = ElasticsearchConnector.restHighLevelClient.delete(deleteRequest,
                RequestOptions.DEFAULT);
        return deleteResponse.getResult().toString();
    }

    public static String getBatchByID(String batchId) throws IOException {
    	GetRequest getRequest = new GetRequest(INDEX, batchId);
    	GetResponse getResponse = ElasticsearchConnector.restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
    
    	if (getResponse.isExists()) {
    	    return getResponse.getSourceAsString(); 
    	}
    	else {
    		LOGGER.warn("getBatchByID not existing get response for id " + batchId);
    		return "";
    	}
    }
}
