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
import org.elasticsearch.index.get.GetResult;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Singleton
public class BatchDao {
    private static final String INDEX = "batch";

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchDao.class);

    private final ElasticsearchConnector connector;

    @Inject
    public BatchDao(ElasticsearchConnector connector) {
        this.connector = connector;
    }

    /**
     * Insert batch to elasticsearch
     *
     * @param batch The batch to insert
     * @return status if new batch is inserted, updated or not
     */
    public IndexResponse insertBatch(Map<String, Object> batch) throws IOException {
        IndexResponse indexResponse = null;
        String batchId = String.valueOf(batch.get("id"));
        IndexRequest indexRequest = new IndexRequest(INDEX).source(batch).id(String.valueOf(batchId));
        GetRequest getRequest = new GetRequest(INDEX, batchId);
        boolean exists = connector.getClient().exists(getRequest, RequestOptions.DEFAULT);
        if (!exists) {
            indexResponse = connector.getClient().index(indexRequest,
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
    public List<Map<String, Object>> getAllBatches() throws IOException {
        SearchRequest searchRequest = new SearchRequest(INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.size(10000);
        searchRequest.source(searchSourceBuilder);
        searchRequest.scroll(TimeValue.timeValueSeconds(30L));

        SearchResponse response = connector.getClient().search(searchRequest,
                RequestOptions.DEFAULT);
        SearchHit[] searchHits = response.getHits().getHits();
        List<Map<String, Object>> results = new ArrayList<>();
        for (SearchHit hit : searchHits) {
            Map<String, Object> result = hit.getSourceAsMap();
            results.add(result);
        }
        return results;
    }

    /**
     * Update batch by id from elasticsearch
     *
     * @param batchId The ID of the batch to update.
     * @param batchMap The data to insert.
     */
    public String updateBatchById(String batchId, Map<String, Object> batchMap) throws IOException {
        UpdateRequest updateRequest = new UpdateRequest(INDEX, batchId);
        updateRequest.doc(batchMap);
        updateRequest.fetchSource(true); // enable source retrieval so we can fetch the source of updated doc
        UpdateResponse updateResponse = connector.getClient().update(updateRequest, RequestOptions.DEFAULT);
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
     * @param batchId The ID of the batch to delete
     */
    public String deleteBatchById(String batchId) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest(INDEX, batchId);
        DeleteResponse deleteResponse = connector.getClient().delete(deleteRequest,
                RequestOptions.DEFAULT);
        return deleteResponse.getResult().toString();
    }

    public String getBatchByID(String batchId) throws IOException {
    	GetRequest getRequest = new GetRequest(INDEX, batchId);
    	GetResponse getResponse = connector.getClient().get(getRequest, RequestOptions.DEFAULT);
    
    	if (getResponse.isExists()) {
    	    return getResponse.getSourceAsString(); 
    	}
    	else {
    		LOGGER.warn("getBatchByID not existing get response for id " + batchId);
    		return "";
    	}
    }
}
