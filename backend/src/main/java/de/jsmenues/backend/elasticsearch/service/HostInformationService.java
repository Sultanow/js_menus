package de.jsmenues.backend.elasticsearch.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import de.jsmenues.backend.elasticsearch.ElasticsearchConnecter;

public class HostInformationService {
    public static final String INDEX = "host_information";
    public static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Get host information by id
     * 
     * @param hostId
     * @return list of host information of id
     */
    public static List<Map<String, List<Object>>> getHostInformationById(String hostId) throws IOException {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("hostid", hostId));
        SearchRequest searchRequest = new SearchRequest(INDEX).source(searchSourceBuilder);
        SearchResponse response = ElasticsearchConnecter.restHighLevelClient.search(searchRequest,
                RequestOptions.DEFAULT);
        SearchHit[] searchHits = response.getHits().getHits();
        List<Map<String, Object>> tempMap = new ArrayList<Map<String, Object>>();

        for (SearchHit hit : searchHits) {
            Map<String, Object> result = hit.getSourceAsMap();
            tempMap.add(result);
        }
        List<Map<String, List<Object>>> results = MAPPER.convertValue(tempMap,
                new TypeReference<List<Map<String, Object>>>() {
                });
        return results;
    }

    /**
     * Get last value an item from a host
     * 
     * @param hostId
     * @param itemId
     * @return last value form an item
     */
    public static String getLastValuById(String hostId, String itemId) throws IOException {

        String lastVlaue = "";
        List<Map<String, List<Object>>> getHostInfoById = getHostInformationById(hostId);


        for (Map<String, List<Object>> tempMap : getHostInfoById) {
            List<Object> items = tempMap.get("items");

            for (Object item : items) {
                @SuppressWarnings("unchecked")
                Map<String, Object> mapItem = MAPPER.convertValue(item, Map.class);
                Object lastvalue = mapItem.get("lastvalue");
                Object itmeid = mapItem.get("itemid");
                String itmeId = String.valueOf(itmeid);

                if (itmeId.equals(itemId)) {

                    lastVlaue = String.valueOf(lastvalue);
                    break;
                }
            }
        }
        return lastVlaue;
    }

    /**
     * Get last update an item from a host
     * 
     * @param hostId
     * @param itemId
     * 
     * @return last update form an item
     */
    @SuppressWarnings("unchecked")

    public static long getLastUpdate(String hostId, String itemId) throws IOException {
        long lastUpdate = 0;
        List<Map<String, List<Object>>> getHostInfoById = getHostInformationById(hostId);

        for (Map<String, List<Object>> tempMap : getHostInfoById) {
            List<Object> items = tempMap.get("items");
            for (Object item : items) {
                Map<String, Object> mapItem = MAPPER.convertValue(item, Map.class);
                Object lastclock = mapItem.get("lastclock");
                Object itmeid = mapItem.get("itemid");
                String lastClock = String.valueOf(lastclock);

                String itmeId = String.valueOf(itmeid);

                if (itmeId.equals(itemId)) {

                    lastUpdate = Long.valueOf(lastClock);
                    break;
                }
            }
        }
        return lastUpdate;
    }

    /**
     * Get all host information from elasticsearch
     * 
     * @param hostId
     * @return  list of item from host
     */
    public static UpdateResponse UpdateHostById(String hostId, List<Object> items) throws IOException {

        UpdateRequest updateRequest = new UpdateRequest(HostInformationService.INDEX, hostId).doc("items", items);

        UpdateResponse updateResponse = ElasticsearchConnecter.restHighLevelClient.update(updateRequest,
                RequestOptions.DEFAULT);
        return updateResponse;
    }
}
