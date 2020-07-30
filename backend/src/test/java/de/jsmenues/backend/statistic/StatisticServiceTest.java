package de.jsmenues.backend.statistic;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.jsmenues.redis.data.Configuration;
import de.jsmenues.redis.repository.ConfigurationRepository;
import de.jsmenues.redis.repository.ConfigurationRepositoryMock;
import de.jsmenues.redis.repository.IConfigurationRepository;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StatisticServiceTest {
    static final Logger LOGGER = LoggerFactory.getLogger(StatisticControllerTest.class);
    IConfigurationRepository repo = mock(ConfigurationRepository.class, RETURNS_DEEP_STUBS);
    HttpClient httpClient = mock(HttpClient.class, RETURNS_DEEP_STUBS);
    StatisticService service = new StatisticService(httpClient);
    IConfigurationRepository repoMock = spy(new ConfigurationRepositoryMock());

    @BeforeEach
    public void setUp() {
        setRedisMock(repoMock);
    }

    @AfterEach
    public void tearDown() throws Exception {
        Field instance = ConfigurationRepository.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    // All Chart Names
    @Test
    public void testGetAllChartNames() {
        initDefaultChartNames();
        String chartNames = service.getAllChartNames();
        LOGGER.info(chartNames);
        assertNotEquals("", chartNames);
    }

    // Update Data Tests
    @Test
    void updateDataWithEmptyChartName() {
        boolean result = service.updateData("", null, null);
        assertFalse(result);
    }

    @Test
    void updateDataWithNullChartName() {
        boolean result = service.updateData(null, null, null);
        assertFalse(result);
    }

    @Test
    void updateDataWithChartNameWithNullInputStream() {
        boolean result = service.updateData("Chart1", null, null);
        assertFalse(result);
    }

    @Test
    void updateDataWithChartNameWithInputStreamWithMetaDataForNotTimeseriesChart() {
        initDefaultChartNames();
        initMockSuccessResponseFromPythonService();
        FormDataContentDisposition metaData = FormDataContentDisposition.name("testData").fileName("test.xlsx").build();
        boolean result = service.updateData("Chart1", new ByteArrayInputStream(new byte[0]), metaData);
        assertTrue(result);
    }

    @Test
    void updateDataWithChartNameWithInputStreamWithNullMetaData() {
        boolean result = service.updateData("Chart1", new ByteArrayInputStream(new byte[0]), null);
        assertFalse(result);
    }

    @Test
    void updateDataWithChartNameWithInputStreamWithMetaDataForTimeseriesChartWithNoGroup() {
        initDefaultChartNames();
        initMockSuccessResponseFromPythonService();
        FormDataContentDisposition metaData = FormDataContentDisposition.name("testData").fileName("test.xlsx").build();
        boolean result = service.updateData("TimeMultiple", new ByteArrayInputStream(new byte[0]), metaData);
        assertTrue(result);
    }

    @Test
    void updateDataWithChartNameWithInputStreamWithMetaDataForTimeseriesChartWithNoGroupResultEmpty() {
        initDefaultChartNames();
        initMockSuccessResponseFromPythonServiceEmptyResult();
        FormDataContentDisposition metaData = FormDataContentDisposition.name("testData").fileName("test.xlsx").build();
        boolean result = service.updateData("TimeMultiple", new ByteArrayInputStream(new byte[0]), metaData);
        assertFalse(result);
    }

    @Test
    void updateDataWithNotExistingChartName() {
        initDefaultChartNames();
        initMockSuccessResponseFromPythonService();
        FormDataContentDisposition metaData = FormDataContentDisposition.name("testData").fileName("test.xlsx").build();
        boolean result = service.updateData("NotExistChart", new ByteArrayInputStream(new byte[0]), metaData);
        assertFalse(result);
    }

    @Test
    void updateDataWithWithGroupWithFailingServiceAnswer() {
        initDefaultChartNames();
        initMockFailureResponseFromPythonService();
        FormDataContentDisposition metaData = FormDataContentDisposition.name("testData").fileName("test.xlsx").build();
        boolean result = service.updateData("Chart1", new ByteArrayInputStream(new byte[0]), metaData);
        assertFalse(result);
    }

    @Test
    void updateDataWithDeprecatedStatisticChartInfo() {
        initDeprecatedChartNames();
        initMockSuccessResponseFromPythonService();
        repoMock.save(new Configuration("statistic.chart.OldChart1.script", "testScript.py"));
        FormDataContentDisposition metaData = FormDataContentDisposition.name("testData").fileName("test.xlsx").build();
        boolean result = service.updateData("OldChart1", new ByteArrayInputStream(new byte[0]), metaData);
        assertEquals(
                new Configuration("statistic.allChartNames", "[{\"groupName\":\"Test\",\"charts\":{\"OldChart1\":{\"accuracy\":\"none\",\"timeseries\":false,\"multiple\":false,\"scriptName\":\"testScript.py\",\"description\":\"\",\"dbName\":\"\"}}}]"),
                repoMock.get("statistic.allChartNames")
        );
    }

    @Test
    void updateDataWithTimeseriesResponseDay() {
        initDefaultChartNames();
        initMockSuccessResponseFromPythonServiceTimeseriesNoMultipleAccuracyDay();
        FormDataContentDisposition metaData = FormDataContentDisposition.name("testData").fileName("test.xlsx").build();
        boolean result = service.updateData("Timenotmultiple", new ByteArrayInputStream(new byte[0]), metaData);
        assertTrue(result);
    }

    @Test
    void updateDataWithTimeseriesResponseMonth() {
        initDefaultChartNames();
        initMockSuccessResponseFromPythonServiceTimeSeriesNoMultipleAccuracyMonth();
        FormDataContentDisposition metaData = FormDataContentDisposition.name("testData").fileName("test.xlsx").build();
        boolean result = service.updateData("Timenotmultiple", new ByteArrayInputStream(new byte[0]), metaData);
        assertTrue(result);
    }

    @Test
    void updateDataWithTimeseriesResponseYear() {
        initDefaultChartNames();
        initMockSuccessResponseFromPythonServiceTimeSeriesNoMultipleAccuracyYear();
        FormDataContentDisposition metaData = FormDataContentDisposition.name("testData").fileName("test.xlsx").build();
        boolean result = service.updateData("Timenotmultiple", new ByteArrayInputStream(new byte[0]), metaData);
        assertTrue(result);
    }

    @Test
    void updateDataWithTimeseriesResponseWeek() {
        initDefaultChartNames();
        initMockSuccessResponseFromPythonServiceTimeSeriesNoMultipleAccuracyWeek();
        FormDataContentDisposition metaData = FormDataContentDisposition.name("testData").fileName("test.xlsx").build();
        boolean result = service.updateData("Timenotmultiple", new ByteArrayInputStream(new byte[0]), metaData);
        assertTrue(result);
    }

    @Test
    void updateDataWithOldScriptName() {
        initChartNameWithoutScript();
        initMockSuccessResponseFromPythonService();
        FormDataContentDisposition metaData = FormDataContentDisposition.name("testData").fileName("test.xlsx").build();
        boolean result = service.updateData("NoScriptTest", new ByteArrayInputStream(new byte[0]),metaData);
        assertTrue(result);
        assertEquals("", repoMock.getVal("statistic.chart.NoScriptTest.script"));
    }

    // Get all GroupNames
    @Test
    void getAllGroupNamesWithEmptyDB() {
        when(repo.get("statistic.allChartNames")).thenReturn(new Configuration("statistic.allChartNames", ""));
        String result = service.getAllGroupNames();
        assertEquals("[]", result);
    }

    @Test
    void getAllGroupNames() {
        initDefaultChartNames();
        String result = service.getAllGroupNames();
        assertEquals("[\"Group1\"]", result);
    }

    // Delete Chart Tests
    @Test
    void deleteChart() {
        initDefaultChartNames();
        service.deleteChart("Chart1");
        String groups = repoMock.getVal("statistic.allChartNames");
        assertFalse(groups.matches("Chart1"));
    }

    // Create Chart Tests
    @Test
    void createChartWithNull() {
        Response response = service.createChart(null, null, null, null, null);
        assertEquals(401, response.getStatus());
    }

    @Test
    void createChartWithEmptyChartName() {
        Response response = service.createChart("", null, null, null, null);
        assertEquals(401, response.getStatus());
    }

    @Test
    void createChartWithAlreadyExistingChart() {
        initDefaultChartNames();
        Response response = service.createChart("Chart1", null, null, null, null);
        assertEquals(401, response.getStatus());
    }

    @Test
    void createChartWithChartNameWithFailurePythonResponse() {
        initDefaultChartNames();
        initMockFailureResponseFromPythonService();
        FormDataContentDisposition metaData = FormDataContentDisposition.name("testScript").fileName("test.py").build();
        Response response = service.createChart("NewChart", null, null, new ByteArrayInputStream(new byte[0]), metaData);
        assertEquals(401, response.getStatus());
        verify(httpClient, times(1)).sendFileDataToPythonService(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void createChartWithChartNameWithoutGroup() {
        initRepoEmptyChartNames();
        initMockSuccessResponseFromPythonService();
        FormDataContentDisposition metaData = FormDataContentDisposition.name("testScript").fileName("test.py").build();
        Response response = service.createChart("NewChart", null, null, new ByteArrayInputStream(new byte[0]), metaData);
        assertEquals(200, response.getStatus());
    }

    @Test
    void createChartWithNewGroupName() {
        initRepoEmptyChartNames();
        initMockSuccessResponseFromPythonService();
        FormDataContentDisposition metaData = FormDataContentDisposition.name("testScript").fileName("test.py").build();
        Response response = service.createChart("NewChart", "NewGroup", null, new ByteArrayInputStream(new byte[0]), metaData);
        assertEquals(200, response.getStatus());
    }

    @Test
    void createChartWithExistingGroupName() {
        initDefaultChartNames();
        initMockSuccessResponseFromPythonService();
        FormDataContentDisposition metaData = FormDataContentDisposition.name("testScript").fileName("test.py").build();
        Response response = service.createChart("NewChart", "Group1", null, new ByteArrayInputStream(new byte[0]), metaData);
        assertEquals(200, response.getStatus());
    }

    // Get Chart Data for Chart Name
    @Test
    void getChartDataForName() {
        initDefaultChartNames();
        String response = service.getChartDataForName(null, null, false);
        assertEquals("", response);
    }

    @Test
    void getChartDataForNameNoTimeseriesChart() {
        initDefaultChartNames();
        String configValue = "{\"title\":\"Anwenderzahlen\",\"traces\":[{\"x\":[\"05:00:00\",\"23:00:00\"],\"y\":[1833.0,46.0],\"mode\":\"lines\",\"type\":\"scatter\",\"name\":\"2020-05-04\",\"line\":{\"width\":2},\"connectgaps\":true,\"hoverinfo\":\"x+y+text\"}],\"layout\":{\"xaxis\":{\"showline\":true,\"showgrid\":true,\"showticklabels\":true,\"linewidth\":2},\"yaxis\":{\"showgrid\":true,\"zeroline\":true,\"showline\":true,\"showticklabels\":true},\"autosize\":true,\"margin\":{\"autoexpand\":true,\"l\":100,\"r\":20,\"t\":110},\"showlegend\":true,\"title\":\"Anwenderzahlen\",\"separators\":\".,\"},\"updateTime\":\"27.06.2020(21:49)\"}";
        repoMock.save(new Configuration("statistic.chart.Chart1.data", configValue));
        String responseMsg = service.getChartDataForName("Chart1", null, false);
        StatisticPlotly plotly = getStatisticPlotlyFromString(responseMsg);
        LOGGER.info(plotly.toString());
        assertEquals("Anwenderzahlen", plotly.getTitle());
        assertNull(plotly.getStartDate());
    }

    @Test
    void getChartDataForNameTimeseriesNoMultipleNoUpdate() {
        initDefaultChartNames();
        initTimeseriesChartNoMultiple();
        String responseMsg = service.getChartDataForName("Timenotmultiple", new HashMap<>(), false);
        StatisticPlotly plotly = getStatisticPlotlyFromString(responseMsg);
        LOGGER.info(plotly.toString());
        assertNotNull(plotly.getStartDate());
        assertNull(plotly.getNextTrace());
    }

    @Test
    void getChartDataForNameTimeseriesNoMultipleNoUpdateWithStart() {
        initDefaultChartNames();
        initTimeseriesChartNoMultiple();
        Map<String, String> dates = new HashMap<>();
        dates.put("start", "2020-05-04");
        String responseMsg = service.getChartDataForName("Timenotmultiple", dates, false);
        StatisticPlotly plotly = getStatisticPlotlyFromString(responseMsg);
        LOGGER.info(plotly.toString());
        assertNull(plotly.getPrevTrace());
        assertNotEquals("", plotly.getTitle());
    }

    @Test
    void getChartDataForNameTimeseriesNoMultipleUpdateWithStartAtTimerangeBeginn() {
        initDefaultChartNames();
        initTimeseriesChartNoMultiple();
        Map<String, String> dates = new HashMap<>();
        dates.put("start", "2020-05-04");
        String responseMsg = service.getChartDataForName("Timenotmultiple", dates, true);
        StatisticPlotly plotly = getStatisticPlotlyFromString(responseMsg);
        LOGGER.info(plotly.toString());
        assertNull(plotly.getTitle());
        assertNull(plotly.getLayout());
        assertNull(plotly.getUpdateTime());
        assertNull(plotly.getPrevTrace());
        assertNotNull(plotly.getTraces());
        assertNotNull(plotly.getNextTrace());
    }

    @Test
    void getChartDataForNameTimeseriesMultiple() {
        initDefaultChartNames();
        initTimeseriesChartMultiple();
        String responseMsg = service.getChartDataForName("TimeMultiple", new HashMap<>(),false);
        StatisticPlotly plotly = getStatisticPlotlyFromString(responseMsg);
        assertEquals(5, plotly.getTraces().size());
    }

    @Test
    void getChartDataForNameTimeseriesMultipleStartEndChanged() {
        initDefaultChartNames();
        initTimeseriesChartMultiple();
        Map<String, String> dates = new HashMap<>();
        dates.put("start", "2020-05-06");
        dates.put("end", "2020-05-08");
        String responseMsg = service.getChartDataForName("TimeMultiple", dates,false);
        StatisticPlotly plotly = getStatisticPlotlyFromString(responseMsg);
        assertEquals(3, plotly.getTraces().size());
    }

    @Test
    void getChartDataForNameTimeseriesMultipleWithEndBeforeStart() {
        initDefaultChartNames();
        initTimeseriesChartMultiple();
        Map<String, String> dates = new HashMap<>();
        dates.put("start", "2020-05-08");
        dates.put("end", "2020-05-06");
        assertThrows(IllegalArgumentException.class, () -> service.getChartDataForName("TimeMultiple", dates, false));
    }

    @Test
    void getChartDataForNameTimeseriesMultipleWithNotExistStartAndEnd() {
        initDefaultChartNames();
        initTimeseriesChartMultiple();
        Map<String, String> dates = new HashMap<>();
        dates.put("start", "2020-04-08");
        dates.put("end", "2020-04-09");
        assertThrows(IllegalArgumentException.class, () -> service.getChartDataForName("TimeMultiple", dates, false));
    }

    private void initTimeseriesChartNoMultiple() {
        repoMock.save(new Configuration("statistic.chart.Timenotmultiple.title", "Timenotmultiple"));
        repoMock.save(new Configuration("statistic.chart.Timenotmultiple.updateTime", "time"));
        repoMock.save(new Configuration("statistic.chart.Timenotmultiple.layout", "{}"));
        repoMock.save(new Configuration("statistic.chart.Timenotmultiple.savedTimes", "[\"2020-05-04\",\"2020-05-07\",\"2020-05-08\",\"2020-05-05\",\"2020-05-06\",\"2020-05-09\",\"2020-05-10\"]"));
        repoMock.save(new Configuration("statistic.chart.Timenotmultiple.data.2020-05-04", "[{\"Date\": \"04\"}]"));
        repoMock.save(new Configuration("statistic.chart.Timenotmultiple.data.2020-05-05", "[{\"Date\": \"05\"}]"));
        repoMock.save(new Configuration("statistic.chart.Timenotmultiple.data.2020-05-06", "[{\"Date\": \"06\"}]"));
        repoMock.save(new Configuration("statistic.chart.Timenotmultiple.data.2020-05-07", "[{\"Date\": \"07\"}]"));
        repoMock.save(new Configuration("statistic.chart.Timenotmultiple.data.2020-05-08", "[{\"Date\": \"08\"}]"));
        repoMock.save(new Configuration("statistic.chart.Timenotmultiple.data.2020-05-09", "[{\"Date\": \"09\"}]"));
        repoMock.save(new Configuration("statistic.chart.Timenotmultiple.data.2020-05-10", "[{\"Date\": \"10\"}]"));
    }

    private void initTimeseriesChartMultiple() {
        repoMock.save("statistic.chart.TimeMultiple.title", "TimeMultiple");
        repoMock.save("statistic.chart.TimeMultiple.updateTime", "time");
        repoMock.save("statistic.chart.TimeMultiple.layout", "{}");
        repoMock.save("statistic.chart.TimeMultiple.savedTimes", "[\"2020-05-04\",\"2020-05-07\",\"2020-05-08\",\"2020-05-05\",\"2020-05-06\",\"2020-05-09\",\"2020-05-10\"]");
        repoMock.save("statistic.chart.TimeMultiple.data.2020-05-04", "[{\"Date\": \"04\"}]");
        repoMock.save("statistic.chart.TimeMultiple.data.2020-05-05", "[{\"Date\": \"05\"}]");
        repoMock.save("statistic.chart.TimeMultiple.data.2020-05-06", "[{\"Date\": \"06\"}]");
        repoMock.save("statistic.chart.TimeMultiple.data.2020-05-07", "[{\"Date\": \"07\"}]");
        repoMock.save("statistic.chart.TimeMultiple.data.2020-05-08", "[{\"Date\": \"08\"}]");
        repoMock.save("statistic.chart.TimeMultiple.data.2020-05-09", "[{\"Date\": \"09\"}]");
        repoMock.save("statistic.chart.TimeMultiple.data.2020-05-10", "[{\"Date\": \"10\"}]");
    }

    @Test
    void getTimeseriesDatesWithoutChart() {
        initTimeseriesChartNoMultiple();
        String responseMsg = service.getTimeseriesDates("");
        assertEquals("", responseMsg);
    }


    private void setRedisMock(IConfigurationRepository mock) {
        try {
            Field instance = ConfigurationRepository.class.getDeclaredField("instance");
            instance.setAccessible(true);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private StatisticPlotly getStatisticPlotlyFromString(String item) {
        Gson gson = new Gson();
        return gson.fromJson(item, new TypeToken<StatisticPlotly>() {
        }.getType());
    }

    private void initDefaultChartNames() {
        String configValue = "[{\"groupName\":\"Group1\",\"charts\":{\"Chart1\":{\"accuracy\":\"none\",\"timeseries\":false,\"multiple\":false,\"scriptName\":\"/tmp/script1.py\",\"description\":\"Desc\",\"dbName\":\"Chart1\"},\"Chart2\":{\"accuracy\":\"none\",\"timeseries\":false,\"multiple\":false,\"scriptName\":\"/tmp/script2.py\",\"description\":\"Desc\",\"dbName\":\"Chart2\"}}},{\"groupName\":\"noGroup\",\"charts\":{\"TimeMultiple\":{\"accuracy\":\"day\",\"timeseries\":true,\"multiple\":true,\"scriptName\":\"timetrace1.py\",\"description\":\"null\",\"dbName\":\"null\"},\"Timenotmultiple\":{\"accuracy\":\"day\",\"timeseries\":true,\"multiple\":false,\"scriptName\":\"timetrace2.py\",\"description\":\"null\",\"dbName\":\"Timenotmultiple\"}}}]";
        repoMock.save("statistic.allChartNames", configValue);
    }

    private void initChartNameWithoutScript() {
        String configValue = "[{\"groupName\":\"noGroup\",\"charts\":{\"NoScriptTest\":{\"accuracy\":\"none\",\"timeseries\":false,\"multiple\":false,\"scriptName\":null,\"description\":\"null\",\"dbName\":\"null\"}}}]";
        repoMock.save("statistic.allChartNames", configValue);
        repoMock.save("statistic.chart.NoScriptTest.script", "Test.py");
    }
    private void initDeprecatedChartNames() {
        Gson gson = new Gson();
        StatisticGroupOld group = new StatisticGroupOld();
        group.groupName = "Test";
        group.charts.add("OldChart1");
        List<StatisticGroupOld> list = new ArrayList<>();
        list.add(group);
        repoMock.save("statistic.allChartNames", gson.toJson(list));
    }

    private String getChartDataForNonTimeseriesChart() {
        return "{\"title\":\"Anwenderzahlen\",\"traces\":[{\"x\":[\"05:00:00\",\"06:00:00\"],\"y\":[399.0,9091.0],\"mode\":\"lines\",\"type\":\"scatter\",\"name\":\"2020-03-16\",\"line\":{\"width\":2},\"connectgaps\":true,\"hoverinfo\":\"x+y+text\"}],\"layout\":{\"xaxis\":{},\"yaxis\":{},\"autosize\":true,\"margin\":{},\"showlegend\":true,\"title\":\"Anwenderzahlen\",\"separators\":\".,\"},\"updateTime\":\"24.07.2020(12:24)\"}";
    }

    private void initMockSuccessResponseFromPythonService() {
        initMockResponseFromPythonService(200, "{}");
    }

    private void initMockSuccessResponseFromPythonServiceEmptyResult() {
        initMockResponseFromPythonService(200, "");
    }

    private void initMockSuccessResponseFromPythonServiceTimeseriesNoMultipleAccuracyDay() {
        String responseBody = "{\"title\":\"AccuracyDayNotMultiple\",\"traces\":[{\"time\":\"2019-12-01\",\"timetraces\":[{\"x\":[\"line1\",\"line2\"],\"y\":[123614.0,423.0],\"type\":\"bar\",\"hoverinfo\":\"x+y+text\"}]},{\"time\":\"2019-12-02\",\"timetraces\":[{\"x\":[\"line1\",\"line2\"],\"y\":[2034311.0,25473.0],\"type\":\"bar\",\"hoverinfo\":\"x+y+text\"}]},{\"time\":\"2020-12-03\",\"timetraces\":[{\"x\":[\"line1\",\"line2\"],\"y\":[1231.0,635.0],\"type\":\"bar\",\"hoverinfo\":\"x+y+text\"}]}],\"layout\":{\"xaxis\":{\"showline\":true,\"showgrid\":true,\"showticklabels\":true},\"yaxis\":{\"showgrid\":true,\"zeroline\":true,\"showline\":true},\"autosize\":true,\"showlegend\":true,\"title\":\"AccuracyDayNotMultiple\"},\"updateTime\":\"21.07.2020(11:16)\",\"timeseries\":true,\"accuracy\":\"day\",\"multiple\":false,\"options\":[]}";
        initMockResponseFromPythonService(200, responseBody);
    }

    private void initMockSuccessResponseFromPythonServiceTimeSeriesNoMultipleAccuracyMonth() {
        String responseBody = "{\"title\":\"AccuracyMonthNotMultiple\",\"traces\":[{\"time\":\"2020-01\",\"timetraces\":[{\"x\":[\"line1\",\"line2\"],\"y\":[123614.0,423.0],\"type\":\"bar\",\"hoverinfo\":\"x+y+text\"}]},{\"time\":\"2020-02\",\"timetraces\":[{\"x\":[\"line1\",\"line2\"],\"y\":[2034311.0,25473.0],\"type\":\"bar\",\"hoverinfo\":\"x+y+text\"}]},{\"time\":\"2020-03\",\"timetraces\":[{\"x\":[\"line1\",\"line2\"],\"y\":[1231.0,635.0],\"type\":\"bar\",\"hoverinfo\":\"x+y+text\"}]}],\"layout\":{\"xaxis\":{\"showline\":true,\"showgrid\":true,\"showticklabels\":true},\"yaxis\":{\"showgrid\":true,\"zeroline\":true,\"showline\":true},\"autosize\":true,\"showlegend\":true,\"title\":\"AccuracyMonthNotMultiple\"},\"updateTime\":\"21.07.2020(11:16)\",\"timeseries\":true,\"accuracy\":\"month\",\"multiple\":false,\"options\":[]}";
        initMockResponseFromPythonService(200, responseBody);
    }

    private void initMockSuccessResponseFromPythonServiceTimeSeriesNoMultipleAccuracyYear() {
        String responseBody = "{\"title\":\"AccuracyYearNotMultiple\",\"traces\":[{\"time\":\"2020\",\"timetraces\":[{\"x\":[\"line1\",\"line2\"],\"y\":[123614.0,423.0],\"type\":\"bar\",\"hoverinfo\":\"x+y+text\"}]},{\"time\":\"2019\",\"timetraces\":[{\"x\":[\"line1\",\"line2\"],\"y\":[2034311.0,25473.0],\"type\":\"bar\",\"hoverinfo\":\"x+y+text\"}]},{\"time\":\"2018\",\"timetraces\":[{\"x\":[\"line1\",\"line2\"],\"y\":[1231.0,635.0],\"type\":\"bar\",\"hoverinfo\":\"x+y+text\"}]}],\"layout\":{\"xaxis\":{\"showline\":true,\"showgrid\":true,\"showticklabels\":true},\"yaxis\":{\"showgrid\":true,\"zeroline\":true,\"showline\":true},\"autosize\":true,\"showlegend\":true,\"title\":\"AccuracyYearNotMultiple\"},\"updateTime\":\"21.07.2020(11:16)\",\"timeseries\":true,\"accuracy\":\"year\",\"multiple\":false,\"options\":[]}";
        initMockResponseFromPythonService(200, responseBody);
    }

    private void initMockSuccessResponseFromPythonServiceTimeSeriesNoMultipleAccuracyWeek() {
        String responseBody = "{\"title\":\"AccuracyWeekNotMultiple\",\"traces\":[{\"time\":\"2020-01\",\"timetraces\":[{\"x\":[\"line1\",\"line2\"],\"y\":[123614.0,423.0],\"type\":\"bar\",\"hoverinfo\":\"x+y+text\"}]},{\"time\":\"2020-02\",\"timetraces\":[{\"x\":[\"line1\",\"line2\"],\"y\":[2034311.0,25473.0],\"type\":\"bar\",\"hoverinfo\":\"x+y+text\"}]},{\"time\":\"2020-03\",\"timetraces\":[{\"x\":[\"line1\",\"line2\"],\"y\":[1231.0,635.0],\"type\":\"bar\",\"hoverinfo\":\"x+y+text\"}]}],\"layout\":{\"xaxis\":{\"showline\":true,\"showgrid\":true,\"showticklabels\":true},\"yaxis\":{\"showgrid\":true,\"zeroline\":true,\"showline\":true},\"autosize\":true,\"showlegend\":true,\"title\":\"AccuracyWeekNotMultiple\"},\"updateTime\":\"21.07.2020(11:16)\",\"timeseries\":true,\"accuracy\":\"week\",\"multiple\":false,\"options\":[]}";
        initMockResponseFromPythonService(200, responseBody);
    }

    private void initMockFailureResponseFromPythonService() {
        initMockResponseFromPythonService(401, "");
    }

    private void initMockResponseFromPythonService(int status, String responseBody) {
        Response response = mock(Response.class, RETURNS_DEEP_STUBS);
        when(response.getStatus()).thenReturn(status);
        when(response.readEntity(any(Class.class))).thenReturn(responseBody);
        when(httpClient.sendFileDataToPythonService(anyString(), anyString(), anyString(), anyString())).thenReturn(response);
    }

    private void initRepoEmptyChartNames() {
        repoMock.save("statistic.allChartNames", "");
    }
}
