package de.jsmenues.backend.elasticsearch.expectedvalue;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.codehaus.jackson.type.TypeReference;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jsmenues.backend.elasticsearch.ElasticsearchConnecter;
import de.jsmenues.backend.elasticsearch.dao.InformationHostDao;
import de.jsmenues.backend.elasticsearch.service.HostInformationService;

public class ExpectedValues {
    private static Logger LOGGER = LoggerFactory.getLogger(ExpectedValues.class);
    public static final String INDEX = "expectedvalues";

    /**
     * insert expected values in Elasticseach from frontend
     * 
     * @param hostName
     * @param key
     * @param expectedValue
     * @return respons about history data
     */
    public static final void insertExpectedValues(String hostName, String key, String expectedValue) throws Exception {
        Date date = new Date();
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        isoFormat.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
        String timestamp = isoFormat.format(date);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(timestamp, formatter);
        String unixTime = String.valueOf(System.currentTimeMillis() / 1000L);
        Map<String, Object> itemMap = new HashMap<>();
        String lastValue = InformationHostDao.getLastValuByKey(hostName, key);
        String itemId = InformationHostDao.getItemId(hostName, key);
        itemMap.put("clock", unixTime);
        itemMap.put("itemid", itemId);
        itemMap.put("value", lastValue);
        itemMap.put("expectedvalue", expectedValue);
        itemMap.put("key", key);
        itemMap.put("timestamp", dateTime);
        itemMap.put("hostname", hostName);
        GetRequest getRequest = new GetRequest(INDEX, hostName + "-" + key);
        boolean exists = ElasticsearchConnecter.restHighLevelClient.exists(getRequest, RequestOptions.DEFAULT);
        if (!exists) {
            IndexRequest indexRequest = new IndexRequest(INDEX).source(itemMap).id(hostName + "-" + key);
            IndexResponse indexResponse = ElasticsearchConnecter.restHighLevelClient.index(indexRequest,
                    RequestOptions.DEFAULT);
            LOGGER.info(indexResponse + "\n item is  inserted");

        } else {
            String lastexpectedValue = getExpectedValueByHostnameAndKey(hostName, key);
            if (!lastexpectedValue.equals(expectedValue)) {
                UpdateRequest updateRequest = new UpdateRequest(INDEX, hostName + "-" + key).doc(itemMap);
                UpdateResponse updateResponse = ElasticsearchConnecter.restHighLevelClient.update(updateRequest,
                        RequestOptions.DEFAULT);
                LOGGER.info(updateResponse + "\n item is updated");
            }
        }
        IndexRequest indexHistoryRequest = new IndexRequest("history-" + hostName + "-" + key).source(itemMap)
                .id(unixTime);

        IndexResponse indexResponse = ElasticsearchConnecter.restHighLevelClient.index(indexHistoryRequest,
                RequestOptions.DEFAULT);
        LOGGER.info(indexResponse.toString());
    }

    /**
     * Get all expected Value from Elasticsearch
     * 
     * @return expectedValue
     */
    public static final List<Map<String, Object>> getExpectedValues() throws Exception {

        SearchRequest searchRequest = new SearchRequest(INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.size(10000);
        searchRequest.source(searchSourceBuilder);
        searchRequest.scroll(TimeValue.timeValueMinutes(1L));
        SearchResponse searchResponse = ElasticsearchConnecter.restHighLevelClient.search(searchRequest,
                RequestOptions.DEFAULT);
        SearchHit[] searchHits = searchResponse.getHits().getHits();
        List<Map<String, Object>> tempMap = new ArrayList<Map<String, Object>>();

        for (SearchHit hit : searchHits) {
            Map<String, Object> result = hit.getSourceAsMap();
            tempMap.add(result);
        }
        List<Map<String, Object>> results = HostInformationService.MAPPER.convertValue(tempMap,
                new TypeReference<List<Map<String, Object>>>() {
                });

        return results;
    }

    /**
     * Get an expected value from Elasticsearch by host name and key
     * 
     * @param hostName
     * @param key
     * @return expected value
     * @throws Exception
     */
    public static final String getExpectedValueByHostnameAndKey(String hostName, String key) {

        String expectedValue = "";
        try {
            List<Map<String, Object>> expectedValues = getExpectedValues();

            for (Map<String, Object> tempMap : expectedValues) {
                String host = String.valueOf(tempMap.get("hostname"));
                String key_ = String.valueOf(tempMap.get("key"));
                if (host.equals(hostName) && key_.equals(key)) {
                    expectedValue = String.valueOf(tempMap.get("expectedvalue"));
                }
            }
            return expectedValue;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return expectedValue;
        }
    }
}
