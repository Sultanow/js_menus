package de.jsmenues.backend.elasticsearch;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * this class builds the connection to elasticsearch.
 *
 */
public class ElasticsearchConnecter {

    private static final String HOST = "elasticsearch";
    private static final int PORT_ONE = 9200;
    private static final String SCHEME = "http";
    public static RestHighLevelClient restHighLevelClient =null;

    public static synchronized RestHighLevelClient makeConnection() {

        RestClientBuilder builder = RestClient
                .builder(new HttpHost(HOST, PORT_ONE, SCHEME))
                .setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
                    @Override
                    public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
                        return requestConfigBuilder.setConnectTimeout(180000).setSocketTimeout(180000)
                                .setMaxRedirects(180000);
                    }
                });

        if (restHighLevelClient == null) {
            restHighLevelClient = new RestHighLevelClient(builder);
        }
        return restHighLevelClient;
    }

    public static synchronized void closeConnection() throws IOException {
        restHighLevelClient.close();
        restHighLevelClient = null;
    }

}
