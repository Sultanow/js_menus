package de.jsmenues.backend.elasticsearch.dao;

import de.jsmenues.backend.elasticsearch.ElasticsearchConnecter;
import de.jsmenues.backend.elasticsearch.service.HistoryServiece;
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
import org.codehaus.jackson.type.TypeReference;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
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
        List<Map<String, Object>> mapItems = null;
        try {
            List<Map<String, List<Object>>> hostsInfo = InformationHostDao.getAllHostInformation();
            insert: for (Map<String, List<Object>> hostInfo : hostsInfo) {
                Object hostName = hostInfo.get("name");
                List<Object> items = hostInfo.get("items");
                mapItems = HistoryServiece.MAPPER.convertValue(items, new TypeReference<List<Object>>() {
                });

                for (Map<String, Object> item : mapItems) {
                    Object itemid = item.get("itemid");
                    Object key = item.get("key_");
                    String stringKey = String.valueOf(key);

                    for (Map<String, Object> history : histories) {
                        Object historyItemid = history.get("itemid");
                        Object historyClock = history.get("clock");
                        String stringHistoryClock = String.valueOf(historyClock);
                        long longPostHistoryClock = Long.valueOf(stringHistoryClock);
                        Date date = new Date();
                        date.setTime(longPostHistoryClock * 1000);
                        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        isoFormat.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
                        String timestamp = isoFormat.format(date);
                        LocalDateTime dateTime = LocalDateTime.parse(timestamp, formatter);
                        long unixTime = System.currentTimeMillis() / 1000L;

                        if (itemid.equals(historyItemid)) {
                            if (stringKey.equals("cpu.usage")) {
                                Object historyValue = history.get("value");
                                String stringValue = String.valueOf(historyValue);
                                long longValue = Long.valueOf(stringValue);
                                history.replace("value", historyValue, longValue);
                            }

                            history.put("timestamp", dateTime);
                            history.put("hostName", hostName);
                            String IndexName = "history-" + hostName + "-" + stringKey;
                            history.put("indexname", IndexName);

                            GetRequest getRequest = new GetRequest(IndexName, stringHistoryClock);
                            boolean exists = ElasticsearchConnecter.restHighLevelClient.exists(getRequest,
                                    RequestOptions.DEFAULT);

                            // save years of OLD_OF_HISTORY_RECORDS
                            if (!exists && (unixTime - longPostHistoryClock < OLD_OF_HISTORY_RECORDS)) {
                                IndexRequest indexRequest = new IndexRequest("history-" + hostName + "-" + stringKey)
                                        .source(history).id(stringHistoryClock);
                                IndexResponse indexResponse = ElasticsearchConnecter.restHighLevelClient
                                        .index(indexRequest, RequestOptions.DEFAULT);

                                ClusterHealthRequest clusterHealthRequest = new ClusterHealthRequest(
                                        "history-" + hostName + "-" + stringKey);
                                clusterHealthRequest.waitForGreenStatus();

                                ElasticsearchConnecter.restHighLevelClient.cluster().health(clusterHealthRequest,
                                        RequestOptions.DEFAULT);
                                numberOfHistoryRecords++;
                                LOGGER.info(indexResponse + "\n" + numberOfHistoryRecords
                                        + " history records are inserted");
                            }
                        }
                        if (numberOfHistoryRecords == 100) {
                            LOGGER.info(numberOfHistoryRecords + " history records are inserted");
                            break insert;
                        }
                    }
                }
            }
            LOGGER.info(numberOfHistoryRecords + " history records are inserted");
        } catch (Exception e) {
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
}
