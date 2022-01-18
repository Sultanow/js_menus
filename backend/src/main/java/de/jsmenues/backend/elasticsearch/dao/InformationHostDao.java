package de.jsmenues.backend.elasticsearch.dao;

import de.jsmenues.backend.elasticsearch.ElasticsearchConnector;
import de.jsmenues.backend.elasticsearch.expectedvalue.ExpectedValues;
import de.jsmenues.backend.elasticsearch.service.HostInformationService;
import org.codehaus.jackson.type.TypeReference;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
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

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// TODO: this class and ExpectedValues form a circular dependency and cannot use injection.
//  This dependency is a sign that these classes are somewhat connected which should not be that way.
//  The part for getting the last values from a host should probably be extracted to another class
//  and both InformationHostDao and ExpectedValues should inject an instance of that class instead.
@Singleton
public class InformationHostDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(InformationHostDao.class);

    private final ElasticsearchConnector connector;

    @Inject
    public InformationHostDao(ElasticsearchConnector connector) {
        this.connector = connector;
    }

    /**
     * Insert all hosts information from zabbix to elasticsearch
     *
     * @param hostsInfo host info from zabbix
     */
    public void insertAllHostInformation(List<Map<String, List<Object>>> hostsInfo) {
        IndexResponse indexResponse;
        int numberOfHosts = 0;
        try {
            insert:
            for (Map<String, List<Object>> hostInfo : hostsInfo) {
                List<Object> items = hostInfo.get("items");
                List<Object> groups = hostInfo.get("groups");

                String hostId = String.valueOf(hostInfo.get("hostid"));
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
                        String lastClock = String.valueOf(item.get("lastclock"));

                        // Ugly hack to break a circular dependency between these classes.
                        String expectedValue = new ExpectedValues(connector, this)
                                .getExpectedValueByHostnameAndKey(hostname, key);

                        if (expectedValue.equals("")) {
                            expectedValue = actualValue;
                        }
                        item.put("hostname", hostname);
                        item.put("expectedvalue", expectedValue);
                        item.put("groupid", groupId);
                        item.put("groupname", groupName);
                        String docId = hostId + itemid;

                        GetRequest getRequest = new GetRequest(HostInformationService.INDEX, docId);
                        boolean exists = connector.getClient().exists(getRequest,
                                RequestOptions.DEFAULT);

                        if (!exists) {
                            IndexRequest indexRequest = new IndexRequest(HostInformationService.INDEX).source(item)
                                    .id(docId);
                            indexResponse = connector.getClient().index(indexRequest,
                                    RequestOptions.DEFAULT);
                            LOGGER.info(indexResponse + "\n host information are inserted");
                            numberOfHosts++;
                            if (numberOfHosts == 100) {
                                break insert;
                            }
                        } else {

                            String getLastValueByKey = getLastValueByKey(hostname, key);

                            String getExpectedValue = getExpectedValueByKey(hostname, key);

                            if (!getLastValueByKey.equals(actualValue)) {
                                UpdateRequest updateActualValue = new UpdateRequest(HostInformationService.INDEX, docId)
                                        .doc("lastvalue", actualValue, "lastclock", lastClock, "prevvalue", getLastValueByKey);
                                UpdateResponse updateResponseActualValue = connector.getClient()
                                        .update(updateActualValue, RequestOptions.DEFAULT);
                                LOGGER.info(updateResponseActualValue.toString());
                                numberOfHosts++;
                                if (numberOfHosts == 100)
                                    break insert;
                            }
                            if (!getExpectedValue.equals(expectedValue)) {
                                UpdateRequest updateExpectdValue = new UpdateRequest(HostInformationService.INDEX,
                                        docId).doc("expectedvalue", expectedValue);
                                UpdateResponse updateResponseExpectdValue = connector.getClient()
                                        .update(updateExpectdValue, RequestOptions.DEFAULT);
                                LOGGER.info(updateResponseExpectdValue.toString());
                                numberOfHosts++;
                                if (numberOfHosts == 100)
                                    break insert;
                            }
                        }

                    }
                }

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
    public List<Map<String, Object>> getAllHostInformation() {
        List<Map<String, Object>> results = new ArrayList<>();
        try {
            SearchRequest searchRequest = new SearchRequest(HostInformationService.INDEX);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.matchAllQuery());
            searchSourceBuilder.size(10000);
            searchRequest.source(searchSourceBuilder);
            searchRequest.scroll(TimeValue.timeValueMinutes(1L));
            SearchResponse searchResponse = connector.getClient()
                    .search(searchRequest, RequestOptions.DEFAULT);
            SearchHit[] searchHits = searchResponse.getHits().getHits();

            for (SearchHit hit : searchHits) {
                Map<String, Object> result = hit.getSourceAsMap();
                results.add(result);
            }

        } catch (IOException e) {
            LOGGER.error(e.getMessage() + "something wrong with elasticsearch");
        }
        return results;
    }

    /**
     * Get all host information by host name from elasticsearch
     *
     * @param hostName The name of the host to inspect
     * @return list of host information
     */
    public List<Map<String, Object>> getHostInformationByHostName(String hostName) throws IOException {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("hostname", hostName));
        SearchRequest searchRequest = new SearchRequest(HostInformationService.INDEX).source(searchSourceBuilder);
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
     * Get all keys
     *
     * @return list of keys
     */
    public List<String> getAllKeys() {
        List<String> keys = new ArrayList<>();
        try {
            List<Map<String, Object>> hostInfos = getAllHostInformation();
            for (Map<String, Object> hostInfo : hostInfos) {
                String key = String.valueOf(hostInfo.get("key_"));
                if (!keys.contains(key)) {
                    keys.add(key);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "elasticsearch is not avalible");
        }
        return keys;
    }

    /**
     * Get last value an item from a host by key and hostname
     *
     * @param hostName The name of the host to get information about
     * @param itemKey The key of the value to retrieve
     * @return last value form an item
     */
    public String getLastValueByKey(String hostName, String itemKey) throws IOException {
        String lastValue = "";
        List<Map<String, Object>> hostInfoByName = getHostInformationByHostName(hostName);
        for (Map<String, Object> item : hostInfoByName) {
            String itemId = String.valueOf(item.get("key_"));
            if (itemId.equals(itemKey)) {
                lastValue = String.valueOf(item.get("lastvalue"));
            }
        }
        return lastValue;
    }

    /**
     * Get expected value an item from host_information index by key and hostname
     * fro
     *
     * @param hostName The name of the host to get information about
     * @param itemKey The key of the value to retrieve
     * @return expected value
     */
    public String getExpectedValueByKey(String hostName, String itemKey) throws IOException {
        String expectedvalue = "";
        List<Map<String, Object>> hostInfoByName = getHostInformationByHostName(hostName);
        for (Map<String, Object> item : hostInfoByName) {
            String itemId = String.valueOf(item.get("key_"));
            if (itemId.equals(itemKey)) {
                expectedvalue = String.valueOf(item.get("expectedvalue"));
            }
        }
        return expectedvalue;
    }

    /**
     * Get item id an item from a host by key and hostname
     *
     * @param hostName The host name to get information about
     * @param itemKey The key to retrieve a value for
     * @return item id
     */
    public String getItemId(String hostName, String itemKey) throws IOException {
        List<Map<String, Object>> hostInfoByName = getHostInformationByHostName(hostName);
        for (Map<String, Object> item : hostInfoByName) {
            String itemId = String.valueOf(item.get("key_"));
            if (itemId.equals(itemKey)) {
                return String.valueOf(item.get("itemid"));
            }
        }
        return "";

    }

    /**
     * Delete host information by Id
     *
     * @param docId : docId = "hostid"+"itemid"
     * @return response about delete
     */
    public String deleteHostInformationById(String docId) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest(HostInformationService.INDEX, docId);

        DeleteResponse deleteResponse = connector.getClient()
                .delete(deleteRequest, RequestOptions.DEFAULT);
        return deleteResponse.toString();
    }
}
