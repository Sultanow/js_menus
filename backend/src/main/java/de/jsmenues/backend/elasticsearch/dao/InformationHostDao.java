package de.jsmenues.backend.elasticsearch.dao;

import org.codehaus.jackson.type.TypeReference;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jsmenues.backend.elasticsearch.ElasticsearchConnecter;
import de.jsmenues.backend.elasticsearch.service.HostInformationService;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InformationHostDao {

    private static Logger LOGGER = LoggerFactory.getLogger(InformationHostDao.class);

    /**
     * Insert all hosts information from zabbix to elasticsearch
     * 
     * @param hostInfo host info from zabbix
     * @return if host information are inserted ,updated or not
     */
    public static void insertAllHostInformation(List<Map<String, List<Object>>> hostsInfo)
            throws IOException, ParseException {
        IndexResponse indexResponse = null;
        UpdateResponse updateResponse = null;
        int numberOfHosts = 0;
        try {
            insert: for (Map<String, List<Object>> hostInfo : hostsInfo) {
                Object hostid = hostInfo.get("hostid");
                List<Object> items = hostInfo.get("items");
                String stringHostid = String.valueOf(hostid);

                GetRequest getRequest = new GetRequest(HostInformationService.INDEX, stringHostid);
                boolean exists = ElasticsearchConnecter.restHighLevelClient.exists(getRequest, RequestOptions.DEFAULT);

                if (!exists) {
                    IndexRequest indexRequest = new IndexRequest(HostInformationService.INDEX).source(hostInfo)
                            .id(stringHostid);
                    indexResponse = ElasticsearchConnecter.restHighLevelClient.index(indexRequest,
                            RequestOptions.DEFAULT);
                    LOGGER.info(indexResponse + "\n host information are inserted");
                    numberOfHosts++;
                    if (numberOfHosts == 100)
                        break insert;
                } else {
                    List<Map<String, Object>> mapItems = HostInformationService.MAPPER.convertValue(items,
                            new TypeReference<List<Object>>() {
                            });

                    for (Map<String, Object> item : mapItems) {

                        Object itemid = item.get("itemid");
                        Object lastvalue = item.get("lastvalue");
                        String stringItemId = String.valueOf(itemid);
                        String lastValue = String.valueOf(lastvalue);
                        String getLastValueById = HostInformationService.getLastValuById(stringHostid, stringItemId);

                        if (!getLastValueById.equals(lastValue)) {
                            updateResponse = HostInformationService.UpdateHostById(stringHostid, items);
                            LOGGER.info(updateResponse + "\n" + stringItemId + "information are updated");
                            numberOfHosts++;
                            if (numberOfHosts == 100)
                                break insert;
                        }
                    }
                }

            }
            if (indexResponse == null && updateResponse == null) {
                LOGGER.info("There haven't been any update yet");

            } else {
                LOGGER.info("Insert host information are finish");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "\n elasticsearch is not avalible or something wrong with elasticsearch");
        }
    }

    /**
     * Get all host information from elasticsearch
     *
     * @return list of all host information
     */
    public static List<Map<String, List<Object>>> getAllHostInformation() throws IOException {
        List<Map<String, List<Object>>> results = null;
        try {
            SearchRequest searchRequest = new SearchRequest(HostInformationService.INDEX);
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
            results = HostInformationService.MAPPER.convertValue(tempMap,
                    new TypeReference<List<Map<String, Object>>>() {
                    });

        } catch (IOException e) {
            LOGGER.error(e.getMessage() + "something wrong with elasticsearch");
        }
        return results;
    }

    /**
     * Get all host information by host name from elasticsearch
     * 
     * @param hostName
     * @return list of host information
     */
    public static List<Map<String, List<Object>>> getHostInformationByHostName(String hostName) throws IOException {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("name", hostName));
        SearchRequest searchRequest = new SearchRequest(HostInformationService.INDEX).source(searchSourceBuilder);
        SearchResponse response = ElasticsearchConnecter.restHighLevelClient.search(searchRequest,
                RequestOptions.DEFAULT);
        SearchHit[] searchHits = response.getHits().getHits();
        List<Map<String, Object>> tempMap = new ArrayList<Map<String, Object>>();

        for (SearchHit hit : searchHits) {
            Map<String, Object> result = hit.getSourceAsMap();
            tempMap.add(result);
        }
        List<Map<String, List<Object>>> results = HostInformationService.MAPPER.convertValue(tempMap,
                new TypeReference<List<Map<String, Object>>>() {
                });
        return results;
    }

    /**
     * Delete host information by id
     *
     * @param hostId
     * @return response about delete
     */
    public static String deleteHostInformationById(String hostId) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest(HostInformationService.INDEX, hostId);

        DeleteResponse deleteResponse = ElasticsearchConnecter.restHighLevelClient.delete(deleteRequest,
                RequestOptions.DEFAULT);
        return deleteResponse.toString();
    }
}
