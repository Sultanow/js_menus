package de.jsmenues.backend.elasticsearch.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import de.jsmenues.backend.elasticsearch.ElasticsearchConnector;
import de.jsmenues.backend.elasticsearch.dao.HistoryDao;

import javax.inject.Inject;

public class HistoryService {
    private final ElasticsearchConnector connector;

    @Inject
    public HistoryService(ElasticsearchConnector connector) {
        this.connector = connector;
    }

    /**
     * Get history indices name from elasticsearch
     *
     * @return list of history index name
     */
    public String[] getAllHistoryIndicesFromElasticsearch() throws IOException {

        GetIndexRequest request = new GetIndexRequest("history-*");
        GetIndexResponse response = connector.getClient().indices().get(request,
                RequestOptions.DEFAULT);

        return response.getIndices();
    }

    /**
     * Get all history records from elasticsearch
     *
     * @return list of history records
     */

    public List<Map<String, Object>> getAllHistoryRecords() throws IOException {

        List<Map<String, Object>> allResults = new ArrayList<>();
        String[] allHistoryIndices = getAllHistoryIndicesFromElasticsearch();

        for (String index : allHistoryIndices) {
            SearchRequest searchRequest = new SearchRequest(index);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.matchAllQuery());
            searchSourceBuilder.size(5000);
            searchRequest.source(searchSourceBuilder);
            searchRequest.scroll(TimeValue.timeValueSeconds(30L));
            SearchResponse searchResponse = connector.getClient().search(searchRequest,
                    RequestOptions.DEFAULT);
            SearchHit[] searchHits = searchResponse.getHits().getHits();
            List<Map<String, Object>> results = new ArrayList<>();

            for (SearchHit hit : searchHits) {
                Map<String, Object> result = hit.getSourceAsMap();
                results.add(result);
            }
            allResults.addAll(results);
        }
        return allResults;
    }

    /**
     * Delete history records after OLD_OF_HISTORY_RECORDS
     *
     * @return response about delete request
     */
    public String deleteHistoryRecordsByDate() throws IOException {
        DeleteResponse deleteResponse = null;
        List<Map<String, Object>> allHistoryRecords = getAllHistoryRecords();

        for (Map<String, Object> record : allHistoryRecords) {
            Object indexname = record.get("indexname");
            Object clock = record.get("clock");
            String indexName = String.valueOf(indexname);
            String stringClock = String.valueOf(clock);
            long longClock = Long.parseLong(stringClock);
            long unixTime = System.currentTimeMillis() / 1000L;

            if (unixTime > (longClock + HistoryDao.OLD_OF_HISTORY_RECORDS)) {
                DeleteRequest request = new DeleteRequest(indexName, stringClock);
                deleteResponse = connector.getClient().delete(request, RequestOptions.DEFAULT);
            }
        }
        return deleteResponse != null ? deleteResponse.toString() : null;
    }
}
