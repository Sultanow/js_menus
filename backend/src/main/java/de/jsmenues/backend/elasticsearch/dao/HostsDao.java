package de.jsmenues.backend.elasticsearch.dao;

import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jsmenues.backend.elasticsearch.ElasticsearchConnector;
import de.jsmenues.backend.elasticsearch.service.HostInformationService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Singleton
public class HostsDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(HostsDao.class);

    private static final String INDEX = "hosts";

    private final ElasticsearchConnector connector;

    @Inject
    public HostsDao(ElasticsearchConnector connector) {
        this.connector = connector;
    }

    /**
     * Insert all hosts from zabbix to elasticsearch
     *
     * @param hosts
     */
    public void insertAllHosts(List<Map<String, Object>> hosts) throws IOException {
        IndexResponse indexResponse = null;
        for (Map<String, Object> host : hosts) {
            String hostId = String.valueOf(host.get("hostid"));
            IndexRequest indexRequest = new IndexRequest(INDEX).source(host).id(hostId);
            GetRequest getRequest = new GetRequest(INDEX, hostId);
            boolean exists = connector.getClient().exists(getRequest, RequestOptions.DEFAULT);
            if (!exists) {
                indexResponse = connector.getClient().index(indexRequest,
                        RequestOptions.DEFAULT);
                LOGGER.info(indexResponse + "\n host are inserted");
            }
        }
        if (indexResponse == null)
            LOGGER.info("hosts existed");
    }

    /**
     * Get all hosts from elasticsearch
     *
     * @return list of hosts
     */
    public List<Map<String, Object>> getAllHosts() throws IOException {

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
     * Get all hostNames from elasticsearch
     *
     * @return list of hostNames
     */
    public ArrayList<String> getAllHostName() {
        ArrayList<String> hostNames = new ArrayList<>();

        try {
            List<Map<String, Object>> allHost = getAllHosts();
            for (Map<String, Object> host : allHost) {
                String hostName = String.valueOf(host.get("host"));
                hostNames.add(hostName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hostNames;
    }


    /**
     * Delete host by id from elasticsearch
     *
     * @param hostId
     */
    public String deleteHostById(String hostId) throws IOException {

        DeleteRequest deleteRequest = new DeleteRequest(INDEX, hostId);
        DeleteResponse deleteResponse = connector.getClient().delete(deleteRequest,
                RequestOptions.DEFAULT);
        return deleteResponse.toString();
    }

    /**
     * Delete host info by id from elasticsearch
     *
     * @param hostId
     * @return reponse aboout host delete
     */
    public String deleteHostInfoById(String hostId) throws IOException {
        DeleteRequest deleteRequestInfo = new DeleteRequest(HostInformationService.INDEX, hostId);
        DeleteResponse deleteResponse = connector.getClient().delete(deleteRequestInfo,
                RequestOptions.DEFAULT);
        return deleteResponse.toString();
    }
}
