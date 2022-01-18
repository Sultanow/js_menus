package de.jsmenues.backend.elasticsearch.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
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

import de.jsmenues.backend.elasticsearch.ElasticsearchConnecter;
import de.jsmenues.backend.elasticsearch.dao.HistoryDao;

public class HistoryServiece {

    public static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Get history indices name from elasticsearch
     *
     * @return list of history index name
     */
    public static String[] getAllHistoryIndicesFromElasticsearch() throws IOException {

        GetIndexRequest request = new GetIndexRequest("history-*");
        GetIndexResponse response = ElasticsearchConnecter.restHighLevelClient.indices().get(request,
                RequestOptions.DEFAULT);
        String[] indices = response.getIndices();

        return indices;
    }

    /**
     * Get all history records from elasticsearch
     *
     * @return list of history records
     */

    public static List<Map<String, Object>> getAllHistoryRcords() throws IOException {

        List<Map<String, Object>> allResults = new ArrayList<Map<String, Object>>();
        String[] allHistoryIndecies = getAllHistoryIndicesFromElasticsearch();

        for (String index : allHistoryIndecies) {
            SearchRequest searchRequest = new SearchRequest(index);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.matchAllQuery());
            searchSourceBuilder.size(5000);
            searchRequest.source(searchSourceBuilder);
            searchRequest.scroll(TimeValue.timeValueSeconds(30L));
            SearchResponse searchResponse = ElasticsearchConnecter.restHighLevelClient.search(searchRequest,
                    RequestOptions.DEFAULT);
            SearchHit[] searchHits = searchResponse.getHits().getHits();
            List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();

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
    public static String deleteHistoryRecordsBydate() throws IOException {
        DeleteResponse deleteResponse = null;
        List<Map<String, Object>> allHistoryRcords = HistoryServiece.getAllHistoryRcords();

        for (Map<String, Object> record : allHistoryRcords) {
            Object indexname = record.get("indexname");
            Object clock = record.get("clock");
            String indexName = String.valueOf(indexname);
            String stringClock = String.valueOf(clock);
            long longClock = Long.valueOf(stringClock);
            long unixTime = System.currentTimeMillis() / 1000L;

            if (unixTime > (longClock + HistoryDao.OLD_OF_HISTORY_RECORDS)) {
                DeleteRequest request = new DeleteRequest(indexName, stringClock);
                deleteResponse = ElasticsearchConnecter.restHighLevelClient.delete(request, RequestOptions.DEFAULT);
            }
        }
        return deleteResponse.toString();
    }
}
