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
import de.jsmenues.backend.elasticsearch.expectedvalue.ExpectedValues;
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
                List<Object> items = hostInfo.get("items");
                List<Object> groups = hostInfo.get("groups");

                String hostid = String.valueOf(hostInfo.get("hostid"));
                String hostname = String.valueOf(hostInfo.get("name"));

                List<Map<String, Object>> mapGroup = HostInformationService.MAPPER.convertValue(groups,
                        new TypeReference<List<Object>>() {
                        });

                for (Map<String, Object> group : mapGroup) {
                    String groupId = String.valueOf(group.get("groupid"));
                    String groupName = String.valueOf(group.get("name"));

                    List<Map<String, Object>> mapItems = HostInformationService.MAPPER.convertValue(items,
                            new TypeReference<List<Object>>() {
                            });

                    for (Map<String, Object> item : mapItems) {

                        String actualValue = String.valueOf(item.get("lastvalue"));
                        String key = String.valueOf(item.get("key_"));
                        String itemid = String.valueOf(item.get("itemid"));

                        String expectedValue = "";
                        expectedValue = ExpectedValues.getExpectedValueByHostnameAndKey(hostname, key);
                        if (expectedValue == "") {
                            expectedValue = actualValue;
                        }
                        item.put("hostname", hostname);
                        item.put("expectedvalue", expectedValue);
                        item.put("groupid", groupId);
                        item.put("groupname", groupName);
                        String docId = hostid + itemid;

                        GetRequest getRequest = new GetRequest(HostInformationService.INDEX, hostid);
                        boolean exists = ElasticsearchConnecter.restHighLevelClient.exists(getRequest,
                                RequestOptions.DEFAULT);

                        if (!exists) {
                            IndexRequest indexRequest = new IndexRequest(HostInformationService.INDEX).source(item)
                                    .id(docId);
                            indexResponse = ElasticsearchConnecter.restHighLevelClient.index(indexRequest,
                                    RequestOptions.DEFAULT);
                            LOGGER.info(indexResponse + "\n host information are inserted");
                            numberOfHosts++;
                            if (numberOfHosts == 100)
                                break insert;
                        } else {

                            String getLastValueById = getLastValuByKey(hostname, key);

                            if (!getLastValueById.equals(actualValue)) {
                                updateResponse = HostInformationService.UpdateHostById(hostid, item);
                                LOGGER.info(updateResponse + "\n" + key + "information are updated");
                                numberOfHosts++;
                                if (numberOfHosts == 100)
                                    break insert;
                            }
                        }

                    }
                }

            }
            if (indexResponse == null && updateResponse == null) {
                LOGGER.info("There haven't been any update yet");

            } else {
                LOGGER.info("Insert host information are finish");
            }
        }catch(Exception e) {
            LOGGER.error(e.getMessage() + "\n elasticsearch is not avalible or something wrong with elasticsearch");
        }
    }

    /**
     * Get all host information from elasticsearch
     *
     * @return list of all host information
     */
    public static List<Map<String, Object>> getAllHostInformation() throws IOException {
        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
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

            for (SearchHit hit : searchHits) {
                Map<String, Object> result = hit.getSourceAsMap();
                results.add(result);
            }

        }catch(IOException e) {
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
    public static List<Map<String, Object>> getHostInformationByHostName(String hostName) throws IOException {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("hostname", hostName));
        SearchRequest searchRequest = new SearchRequest(HostInformationService.INDEX).source(searchSourceBuilder);
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
     * Get all keys
     * 
     * @return list of keys
     */
    public static List<String> getAllKeys() throws IOException {
        List<String> keys = new ArrayList<String>();
        try {
            List<Map<String, Object>> hostInfos = getAllHostInformation();
            for (Map<String, Object> hostInfo : hostInfos) {
                String key = String.valueOf(hostInfo.get("key_"));
                if (!keys.contains(key))
                    keys.add(key);
            }
        }catch(Exception e) {
            LOGGER.error(e.getMessage() + "elasticsearch is not avalible");
        }
        return keys;
    }

    /**
     * Get last value an item from a host by key and hostname
     * 
     * @param hostName
     * @param itemKey
     * @return last value form an item
     */
    public static String getLastValuByKey(String hostName, String itemKey) throws IOException {

        String lastVlaue = "";
        List<Map<String, Object>> hostInfoByName = getHostInformationByHostName(hostName);
        for (Map<String, Object> item : hostInfoByName) {
        String itmeId = String.valueOf(item.get("key_"));
        if (itmeId.equals(itemKey)) {
            lastVlaue = String.valueOf(item.get("lastvalue"));
        }
        }
        return lastVlaue;
    }

    /**
     * Get item id an item from a host by key and hostname
     * 
     * @param hostName
     * @param itemKey
     * @return item id
     */
    public static String getItemId(String hostName, String itemKey) throws IOException {

        String itemId = "";
        List<Map<String, Object>> hostInfoByName = getHostInformationByHostName(hostName);
        for (Map<String, Object> item : hostInfoByName) {
            String itmeId = String.valueOf(item.get("key_"));
            if (itmeId.equals(itemKey)) {
                itemId = String.valueOf(item.get("itemid"));
            }
        }
        return itemId;

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
