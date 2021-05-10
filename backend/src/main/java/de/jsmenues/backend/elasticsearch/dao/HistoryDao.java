package de.jsmenues.backend.elasticsearch.dao;

import de.jsmenues.backend.elasticsearch.ElasticsearchConnector;
import de.jsmenues.backend.elasticsearch.expectedvalue.ExpectedValues;
import de.jsmenues.redis.repository.IConfigurationRepository;
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

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Singleton
public class HistoryDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(HistoryDao.class);

    public static String stringNumberOfYears = "2"; //ConfigurationRepository.getRepo().getVal("configuration.delete.history.data");
    static int numberOfYears = Integer.parseInt(stringNumberOfYears.trim());

    public static final long OLD_OF_HISTORY_RECORDS = (long) 60 * 60 * 24 * 30 * 12 * numberOfYears;

    private final ElasticsearchConnector connector;

    private final ElasticsearchDao dao;

    private final IConfigurationRepository configurationRepository;

    private final InformationHostDao informationHostDao;

    private final ExpectedValues expectedValues;

    @Inject
    public HistoryDao(ElasticsearchConnector connector,
                      ElasticsearchDao dao,
                      IConfigurationRepository configurationRepository,
                      InformationHostDao informationHostDao, ExpectedValues expectedValues) {
        this.connector = connector;
        this.dao = dao;
        this.configurationRepository = configurationRepository;
        this.informationHostDao = informationHostDao;
        this.expectedValues = expectedValues;
    }

    /**
     * insert 200 history records from zabbix to elasticsearch
     *
     * @param histories history records from zabbix
     */
    public void insertHistory(List<Map<String, Object>> histories) {
        int numberOfHistoryRecords = 0;
        LOGGER.info("Trying to insertHistory");
        LOGGER.info("from repo:" + configurationRepository.getVal("configuration.delete.history.data" + "||"));
        LOGGER.info("nryears" + numberOfYears);
        try {
            List<Map<String, Object>> hostsInfo = informationHostDao.getAllHostInformation();
            insert:
            for (Map<String, Object> hostInfo : hostsInfo) {
                String hostName = String.valueOf(hostInfo.get("hostname"));

                String itemid = String.valueOf(hostInfo.get("itemid"));
                String key = String.valueOf(hostInfo.get("key_"));

                for (Map<String, Object> history : histories) {
                    Object historyItemid = history.get("itemid");
                    String historyClock = String.valueOf(history.get("clock"));
                    long longPostHistoryClock = Long.parseLong(historyClock);
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
                        String expectedValue;

                        expectedValue = expectedValues.getExpectedValueByHostnameAndKey(hostName, key);
                        if (expectedValue.equals("")) {
                            expectedValue = actualValue;
                        }

                        history.put("expectedvalue", expectedValue);
                        history.put("key", key);
                        history.put("timestamp", dateTime);
                        history.put("hostname", hostName);
                        String IndexName = "history-" + hostName + "-" + key;

                        GetRequest getRequest = new GetRequest(IndexName, historyClock);
                        boolean exists = connector.getClient().exists(getRequest,
                                RequestOptions.DEFAULT);
                        String hostNameLowCase = hostName.toLowerCase();
                        // save years of OLD_OF_HISTORY_RECORDS
                        if (!exists && (unixTime - longPostHistoryClock < OLD_OF_HISTORY_RECORDS)) {
                            IndexRequest indexRequest = new IndexRequest("history-" + hostNameLowCase + "-" + key)
                                    .source(history).id(historyClock);
                            IndexResponse indexResponse = connector.getClient().index(indexRequest,
                                    RequestOptions.DEFAULT);

                            ClusterHealthRequest clusterHealthRequest = new ClusterHealthRequest(
                                    "history-" + hostNameLowCase + "-" + key);
                            clusterHealthRequest.waitForGreenStatus();

                            connector.getClient().cluster().health(clusterHealthRequest,
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

        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "\n elasticsearch is not avalible or something wrong with elasticsearch");
        }
    }

    /**
     * Get history records by index name from elasticsearch
     *
     * @param indexName index pattern for selected indices or index name
     * @return list of history records
     */
    public List<Map<String, Object>> getHistoryRecordsByIndex(String indexName) throws IOException {
        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.size(10000);
        searchRequest.source(searchSourceBuilder);
        searchRequest.scroll(TimeValue.timeValueSeconds(30L));
        SearchResponse searchResponse = connector.getClient().search(searchRequest,
                RequestOptions.DEFAULT);
        SearchHit[] searchHits = searchResponse.getHits().getHits();
        List<Map<String, Object>> results = new ArrayList<>();
        for (SearchHit hit : searchHits) {
            Map<String, Object> result = hit.getSourceAsMap();
            results.add(result);
        }
        return results;
    }

    /**
     * Get history records of an index between tow dates from elasticsearch
     *
     * @param startTime Start time in UNIX time
     * @param endTime End time in UNIX time
     * @param indexName  The index to operate on
     * @return list of history records between tow selected Dates
     */
    public List<Map<String, Object>> getHistoryRecordsBetweenDates(long startTime,
                                                                   long endTime,
                                                                   String indexName) throws IOException {
        List<Map<String, Object>> results = new ArrayList<>();
        List<Map<String, Object>> historyRecords = getHistoryRecordsByIndex(indexName);

        for (Map<String, Object> historyRecord : historyRecords) {
            long recordTime = Long.parseLong(String.valueOf(historyRecord.get("clock")));

            if (recordTime > startTime && recordTime < endTime) {
                results.add(historyRecord);
            }
        }
        return results;
    }

    /**
     * Delete history records after a certain time
     */
    public void deleteHistoryRecordsAfterCertainTime() {
        DeleteResponse deleteResponse = null;
        try {
            String[] historyIndexNames = dao.getIndexName("history");
            for (String index : historyIndexNames) {
                List<Map<String, Object>> AllHistoryRecords = getHistoryRecordsByIndex(index);
                long unixTime = System.currentTimeMillis() / 1000L;
                for (Map<String, Object> map : AllHistoryRecords) {
                    String stringClock = map.get("clock").toString();
                    long clock = Long.parseLong(stringClock);
                    if (unixTime - clock > OLD_OF_HISTORY_RECORDS) {
                        DeleteRequest deleteRequest = new DeleteRequest(index, stringClock);

                        deleteResponse = connector.getClient().delete(deleteRequest,
                                RequestOptions.DEFAULT);
                    }
                }
                if (deleteResponse != null) {
                    LOGGER.info(deleteResponse.toString());
                } else {
                    LOGGER.info("history records are not deleted");
                }

            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
