package de.jsmenues.backend.elasticsearch.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import de.jsmenues.backend.elasticsearch.ElasticsearchConnector;

import javax.inject.Inject;

public class HostInformationService {
    public static final String INDEX = "host_information";
    public static final ObjectMapper MAPPER = new ObjectMapper();
    
    private final ElasticsearchConnector connector;

    @Inject
    public HostInformationService(ElasticsearchConnector connector) {
        this.connector = connector;
    }

    /**
     * Get host information by id
     * 
     * @param hostId
     * @return list of host information of id
     */
    public List<Map<String, Object>> getHostInformationById(String hostId) throws IOException {
        List<Map<String, Object>> results = new ArrayList<>();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("hostid", hostId));
        SearchRequest searchRequest = new SearchRequest(INDEX).source(searchSourceBuilder);
        SearchResponse response = connector.getClient().search(searchRequest,
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
     * @param requiredItemId
     * 
     * @return last update form an item
     */
    public long getLastUpdate(String hostId, String requiredItemId) throws IOException {
        List<Map<String, Object>> hostInfoById = getHostInformationById(hostId);

        for (Map<String, Object> hostInfo : hostInfoById) {
            long lastClock = Long.parseLong(String.valueOf(hostInfo.get("lastclock")));
            String itemId = String.valueOf(hostInfo.get("itemid"));

            if (requiredItemId.equals(itemId)) {
                return lastClock;
            }
        }
        return 0L;
    }
}

