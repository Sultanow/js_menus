package de.jsmenues.backend.elasticsearch.dao;

import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.search.SearchHit;

import de.jsmenues.backend.elasticsearch.ElasticsearchConnecter;
import de.jsmenues.backend.elasticsearch.service.HostInformationService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HostsDao {

    private static final String INDEX = "hosts";

    /**
     * Insert all hosts from zabbix to elasticsearch
     * 
<<<<<<< HEAD
     * @param hosts
     * @return status if new hosts are inserted, updated or not
=======
     * @param hosts hosts from zabbix
     * @return status if new hosts are inserted ,updated or not
>>>>>>> 86580d5fe7e41ab365dbf0070865896f051afdf1
     */
    public static String insertAllHosts(List<Map<String, Object>> hosts) throws IOException {

        String status = "";
        for (Map<String, Object> host : hosts) {
            Object hostid = host.get("hostid");
            String stringHostid = String.valueOf(hostid);
            IndexRequest indexRequest = new IndexRequest(INDEX).source(host).id(stringHostid);
            GetRequest getRequest = new GetRequest(INDEX, stringHostid);
            boolean exists = ElasticsearchConnecter.restHighLevelClient.exists(getRequest, RequestOptions.DEFAULT);
            if (!exists) {
                IndexResponse indexResponse = ElasticsearchConnecter.restHighLevelClient.index(indexRequest,
                        RequestOptions.DEFAULT);
                status += indexResponse + "\n";
            } else {
                status += "index exist" + "\n";
            }
        }
        return status;
    }

    /**
     * Get all hosts from elasticsearch
     *
     * @return list of hosts
     */
    public static List<Map<String, Object>> getAllHosts() throws IOException {

        SearchRequest searchRequest = new SearchRequest(INDEX);
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
     * Delete host by id from elasticsearch
<<<<<<< HEAD
     * @param hostId
=======
     *
>>>>>>> 86580d5fe7e41ab365dbf0070865896f051afdf1
     * @return response from delete request
     */
    public static String deleteHostById(String hostId) throws IOException {

        DeleteRequest deleteRequest = new DeleteRequest(INDEX, hostId);
        DeleteResponse deleteResponse = ElasticsearchConnecter.restHighLevelClient.delete(deleteRequest,
                RequestOptions.DEFAULT);
        return deleteResponse.toString();
    }

    /**
     * Delete host info by id from elasticsearch
<<<<<<< HEAD
     * @param hostId
=======
     *
>>>>>>> 86580d5fe7e41ab365dbf0070865896f051afdf1
     * @return response from delete request
     */
    public static String deleteHostInfoById(String hostId) throws IOException {
        DeleteRequest deleteRequestInfo = new DeleteRequest(HostInformationService.INDEX, hostId);
        DeleteResponse deleteResponse = ElasticsearchConnecter.restHighLevelClient.delete(deleteRequestInfo,
                RequestOptions.DEFAULT);
        return deleteResponse.toString();
    }
}
