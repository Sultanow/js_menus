package de.jsmenues.backend.elasticsearch.dao;

import de.jsmenues.backend.elasticsearch.ElasticsearchConnecter;
import de.jsmenues.backend.elasticsearch.expectedvalue.ExpectedValues;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
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

public class HistoryDao {
    private static Logger LOGGER = LoggerFactory.getLogger(HistoryDao.class);

    public static final long OLD_OF_HISTORY_RECORDS = 60 * 60 * 24 * 30 * 12 * 2; // 2 years

    /**
     * insert 200 history records from zabbix to elasticsearch
     *
     * @param histories history records from zabbix
     * @return if new records are inserted or not
     */
    public static void insertHistory(List<Map<String, Object>> histories) throws IOException, ParseException {
        int numberOfHistoryRecords = 0;
        try {
            List<Map<String, Object>> hostsInfo = InformationHostDao.getAllHostInformation();
            insert: for (Map<String, Object> hostInfo : hostsInfo) {
                String hostName = String.valueOf(hostInfo.get("hostname"));

                String itemid = String.valueOf(hostInfo.get("itemid"));
                String key = String.valueOf(hostInfo.get("key_"));

                for (Map<String, Object> history : histories) {
                    Object historyItemid = history.get("itemid");
                    String historyClock = String.valueOf(history.get("clock"));
                    long longPostHistoryClock = Long.valueOf(historyClock);
                    Date date = new Date();
                    date.setTime(longPostHistoryClock * 1000);
                    SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    isoFormat.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
                    String timestamp = isoFormat.format(date);
                    LocalDateTime dateTime = LocalDateTime.parse(timestamp, formatter);
                    long unixTime = System.currentTimeMillis() / 1000L;

                    if (itemid.equals(historyItemid)) {
                        String actualValue = String.valueOf(history.get("value"));
                        String expectedValue = "";

                        expectedValue = ExpectedValues.getExpectedValueByHostnameAndKey(hostName, key);
                        if (expectedValue == "") {
                            expectedValue = actualValue;
                        }

                        history.put("expectedvalue", expectedValue);
                        history.put("key", key);
                        history.put("timestamp", dateTime);
                        history.put("hostname", hostName);
                        String IndexName = "history-" + hostName + "-" + key;

                        GetRequest getRequest = new GetRequest(IndexName, historyClock);
                        boolean exists = ElasticsearchConnecter.restHighLevelClient.exists(getRequest,
                                RequestOptions.DEFAULT);
                        String hostNameLowCase = hostName.toLowerCase();
                        // save years of OLD_OF_HISTORY_RECORDS
                        if (!exists && (unixTime - longPostHistoryClock < OLD_OF_HISTORY_RECORDS)) {
                            IndexRequest indexRequest = new IndexRequest("history-" + hostNameLowCase + "-" + key)
                                    .source(history).id(historyClock);
                            IndexResponse indexResponse = ElasticsearchConnecter.restHighLevelClient.index(indexRequest,
                                    RequestOptions.DEFAULT);

                            ClusterHealthRequest clusterHealthRequest = new ClusterHealthRequest(
                                    "history-" + hostNameLowCase + "-" + key);
                            clusterHealthRequest.waitForGreenStatus();

                            ElasticsearchConnecter.restHighLevelClient.cluster().health(clusterHealthRequest,
                                    RequestOptions.DEFAULT);
                            numberOfHistoryRecords++;
                            LOGGER.info(
                                    indexResponse + "\n" + numberOfHistoryRecords + " history records are inserted");
                        }
                    }
                    if (numberOfHistoryRecords == 100) {
                        LOGGER.info(numberOfHistoryRecords + " history records are inserted");
                        break insert;
                    }
                }
            }

            LOGGER.info(numberOfHistoryRecords + " history records are inserted");

        } catch(Exception e) {
            LOGGER.error(e.getMessage() + "\n elasticsearch is not avalible or something wrong with elasticsearch");
        }
    }

    /**
     * Get history records by index name from elasticsearch
     *
     * @param patternIndexName index pattern for selected indices or index name
     * @return list of history records
     */
    public static List<Map<String, Object>> getHistoryRecordsByIndex(String indexName) throws IOException {
        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.size(10000);
        searchRequest.source(searchSourceBuilder);
        searchRequest.scroll(TimeValue.timeValueSeconds(30L));
        SearchResponse searchResponse = ElasticsearchConnecter.restHighLevelClient.search(searchRequest,
                RequestOptions.DEFAULT);
        SearchHit[] searchHits = searchResponse.getHits().getHits();
        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        for (SearchHit hit : searchHits) {
            Map<String, Object> result = hit.getSourceAsMap();
            results.add(result);
        }
        return results;
    }

    /**
     * Get history records of an index between tow dates from elasticsearch
     *
     * @param unixDate1 the first date
     * @param unixDate2 the second date
     * @param indexname
     *
     * @return list of history records between tow selected Dates
     */
    public static List<Map<String, Object>> getHistoryRecordBetweenTowDatesByIndexName(String unixDatum1,
            String unixDatum2, String indexName) throws IOException {
        long longUnixDatum1 = Long.valueOf(unixDatum1);
        long longUnixDatum2 = Long.valueOf(unixDatum2);
        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> historyRecords = getHistoryRecordsByIndex(indexName);

        for (Map<String, Object> historyRecord : historyRecords) {
            Object clock = historyRecord.get("clock");
            String stringClock = String.valueOf(clock);
            long longClock = Long.valueOf(stringClock);

            if (longClock > longUnixDatum1 && longClock < longUnixDatum2) {
                results.add(historyRecord);
            }
        }
        return results;
    }

    /**
     * Delete history records after a certain time
     *
     */
    public static void DeletehistoryRecordsAfterCertainTime() {
        DeleteResponse deleteResponse = null;
        try {
            String[] historyIndexNames = ElasticsearchDao.getIdexName("history*");
            for (String index : historyIndexNames) {
                List<Map<String, Object>> AllHistoryRecords = getHistoryRecordsByIndex(index);
                long unixTime = System.currentTimeMillis() / 1000L;
                for (Map<String, Object> map : AllHistoryRecords) {
                    String stringClock = map.get("clock").toString();
                    long clock = Long.valueOf(stringClock);
                    if (unixTime - clock > OLD_OF_HISTORY_RECORDS) {
                        DeleteRequest deleteRequest = new DeleteRequest(index, stringClock);

                        deleteResponse = ElasticsearchConnecter.restHighLevelClient.delete(deleteRequest,
                                RequestOptions.DEFAULT);
                    }
                }
                if (deleteResponse != null) {
                    LOGGER.info(deleteResponse.toString());
                } else {
                    LOGGER.info("history records are not deleted");
                }

            }
        }catch(Exception e) {
            LOGGER.error(e.getMessage());
        }

    }
}
