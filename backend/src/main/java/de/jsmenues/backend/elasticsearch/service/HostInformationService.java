package de.jsmenues.backend.elasticsearch.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import de.jsmenues.backend.elasticsearch.ElasticsearchConnector;

public class HostInformationService {
    public static final String INDEX = "host_information";
    public static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Get host information by id
     * 
     * @param hostId
     * @return list of host information of id
     */
    public static List<Map<String, Object>> getHostInformationById(String hostId) throws IOException {
        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("hostid", hostId));
        SearchRequest searchRequest = new SearchRequest(INDEX).source(searchSourceBuilder);
        SearchResponse response = ElasticsearchConnector.restHighLevelClient.search(searchRequest,
                RequestOptions.DEFAULT);
        SearchHit[] searchHits = response.getHits().getHits();

        for (SearchHit hit : searchHits) {
            Map<String, Object> result = hit.getSourceAsMap();
            results.add(result);
        }
        return results;
    }

    /**
     * Get last update an item from a host
     * 
     * @param hostId
     * @param itemId
     * 
     * @return last update form an item
     */

    public static long getLastUpdate(String hostId, String itemId) throws IOException {
        long lastUpdate = 0;
        List<Map<String, Object>> hostInfoById = getHostInformationById(hostId);

        for (Map<String, Object> hostiInfo : hostInfoById) {
            Object lastclock = hostiInfo.get("lastclock");
            Object itmeid = hostiInfo.get("itemid");
            String lastClock = String.valueOf(lastclock);

            String itmeId = String.valueOf(itmeid);

            if (itmeId.equals(itemId)) {

                lastUpdate = Long.valueOf(lastClock);
                break;
            }
        }
        return lastUpdate;
    }
}

