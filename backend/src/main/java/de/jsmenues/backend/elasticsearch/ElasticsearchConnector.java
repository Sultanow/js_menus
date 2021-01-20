package de.jsmenues.backend.elasticsearch;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jsmenues.backend.elasticsearch.dao.HostsDao;


/**
 * this class builds the connection to elasticsearch.
 *
 */
public class ElasticsearchConnector {

    private static final String HOST = "172.17.0.1"; // TODO: would be better if this were a hostname string
    private static final int PORT = 9200;
    private static final String SCHEME = "http";
    public static RestHighLevelClient restHighLevelClient =null;
    private static Logger LOGGER = LoggerFactory.getLogger(ElasticsearchConnector.class);

    public static synchronized RestHighLevelClient makeConnection() {

    	if (restHighLevelClient == null) {
            restHighLevelClient = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost(HOST, PORT, SCHEME)));
        }
        return restHighLevelClient;
    }

    public static synchronized void closeConnection() throws IOException {
        restHighLevelClient.close();
        restHighLevelClient = null;
    }

}
