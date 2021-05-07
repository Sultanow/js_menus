package de.jsmenues.backend.statistic;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.jsmenues.redis.repository.ConfigurationRepository;
import de.jsmenues.redis.repository.ConfigurationRepositoryMock;
import de.jsmenues.redis.repository.IConfigurationRepository;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.Test;

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
    private final HttpClient httpClient = mock(HttpClient.class, RETURNS_DEEP_STUBS);
    private final IConfigurationRepository configurationRepository = spy(new ConfigurationRepositoryMock());
    
    private final StatisticService service = new StatisticService(httpClient, configurationRepository);

    @BeforeEach
    public void setUp() {
        setRedisMock(configurationRepository);
    }

    @AfterEach
    public void tearDown() throws Exception {
        Field instance = ConfigurationRepository.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    // All Chart Names
    @Test
    public void getAllChartNames() {
        //given
        initDefaultChartNames();
        //when
        String chartNames = service.getAllChartNames();
        //then
        assertNotEquals("", chartNames);
    }

    // Update Data Tests
    @Test
    public void updateDataWithEmptyChartName() {
        //given
        //when
        boolean result = service.updateData("", null, null);
        //then
        assertFalse(result);
    }

    @Test
    public void updateDataWithNullChartName() {
        //given
        //when
        boolean result = service.updateData(null, null, null);
        //then
        assertFalse(result);
    }

    @Test
    public void updateDataWithChartNameWithNullInputStream() {
        //given
        //when
        boolean result = service.updateData("Chart1", null, null);
        //then
        assertFalse(result);
    }

    @Test
    public void updateDataWithChartNameWithInputStreamWithMetaDataForNotTimeseriesChart() {
        //given
        initDefaultChartNames();
        initMockSuccessResponseFromPythonService();
        FormDataContentDisposition metaData = FormDataContentDisposition.name("testData").fileName("test.xlsx").build();
        //when
        boolean result = service.updateData("Chart1", new ByteArrayInputStream(new byte[0]), metaData);
        //then
        assertTrue(result);
    }

    @Test
    public void updateDataWithChartNameWithInputStreamWithNullMetaData() {
        //given
        //when
        boolean result = service.updateData("Chart1", new ByteArrayInputStream(new byte[0]), null);
        //then
        assertFalse(result);
    }

    @Test
    public void updateDataWithChartNameWithInputStreamWithMetaDataForTimeseriesChartWithNoGroup() {
        //given
        initDefaultChartNames();
        initMockSuccessResponseFromPythonService();
        FormDataContentDisposition metaData = FormDataContentDisposition.name("testData").fileName("test.xlsx").build();
        //when
        boolean result = service.updateData("TimeMultiple", new ByteArrayInputStream(new byte[0]), metaData);
        //then
        assertTrue(result);
    }

    @Test
    public void updateDataWithChartNameWithInputStreamWithMetaDataForTimeseriesChartWithNoGroupResultEmpty() {
        //given
        initDefaultChartNames();
        initMockSuccessResponseFromPythonServiceEmptyResult();
        FormDataContentDisposition metaData = FormDataContentDisposition.name("testData").fileName("test.xlsx").build();
        //when
        boolean result = service.updateData("TimeMultiple", new ByteArrayInputStream(new byte[0]), metaData);
        //then
        assertFalse(result);
    }

    @Test
    public void updateDataWithNotExistingChartName() {
        //given
        initDefaultChartNames();
        initMockSuccessResponseFromPythonService();
        FormDataContentDisposition metaData = FormDataContentDisposition.name("testData").fileName("test.xlsx").build();
        //when
        boolean result = service.updateData("NotExistChart", new ByteArrayInputStream(new byte[0]), metaData);
        //then
        assertFalse(result);
    }

    @Test
    public void updateDataWithWithGroupWithFailingServiceAnswer() {
        //given
        initDefaultChartNames();
        initMockFailureResponseFromPythonService();
        FormDataContentDisposition metaData = FormDataContentDisposition.name("testData").fileName("test.xlsx").build();
        //when
        boolean result = service.updateData("Chart1", new ByteArrayInputStream(new byte[0]), metaData);
        //then
        assertFalse(result);
    }

    @Test
    public void updateDataWithDeprecatedStatisticChartInfo() {
//given
        initDeprecatedChartNames();
        initMockSuccessResponseFromPythonService();
        configurationRepository.save("statistic.chart.OldChart1.script", "testScript.py");
        FormDataContentDisposition metaData = FormDataContentDisposition.name("testData").fileName("test.xlsx").build();
        //when
        service.updateData("OldChart1", new ByteArrayInputStream(new byte[0]), metaData);
        //then
        assertEquals(
                "[{\"groupName\":\"Test\",\"charts\":{\"OldChart1\":{\"accuracy\":\"none\",\"timeseries\":false,\"multiple\":false,\"scriptName\":\"testScript.py\",\"description\":\"\",\"dbName\":\"\"}}}]",
                configurationRepository.getVal("statistic.allChartNames")
        );
    }

    @Test
    public void updateDataWithTimeseriesResponseDay() {
        //given
        initDefaultChartNames();
        initMockSuccessResponseFromPythonServiceTimeseriesNoMultipleAccuracyDay();
        FormDataContentDisposition metaData = FormDataContentDisposition.name("testData").fileName("test.xlsx").build();
        //when
        boolean result = service.updateData("Timenotmultiple", new ByteArrayInputStream(new byte[0]), metaData);
        //then
        assertTrue(result);
    }

    @Test
    public void updateDataWithTimeseriesResponseMonth() {
        //given
        initDefaultChartNames();
        initMockSuccessResponseFromPythonServiceTimeSeriesNoMultipleAccuracyMonth();
        FormDataContentDisposition metaData = FormDataContentDisposition.name("testData").fileName("test.xlsx").build();
        //when
        boolean result = service.updateData("Timenotmultiple", new ByteArrayInputStream(new byte[0]), metaData);
        //then
        assertTrue(result);
    }

    @Test
    public void updateDataWithTimeseriesResponseYear() {
        //given
        initDefaultChartNames();
        initMockSuccessResponseFromPythonServiceTimeSeriesNoMultipleAccuracyYear();
        FormDataContentDisposition metaData = FormDataContentDisposition.name("testData").fileName("test.xlsx").build();
        //when
        boolean result = service.updateData("Timenotmultiple", new ByteArrayInputStream(new byte[0]), metaData);
        //then
        assertTrue(result);
    }

    @Test
    public void updateDataWithTimeseriesResponseWeek() {
        //given
        initDefaultChartNames();
        initMockSuccessResponseFromPythonServiceTimeSeriesNoMultipleAccuracyWeek();
        FormDataContentDisposition metaData = FormDataContentDisposition.name("testData").fileName("test.xlsx").build();
        //when
        boolean result = service.updateData("Timenotmultiple", new ByteArrayInputStream(new byte[0]), metaData);
        //then
        assertTrue(result);
    }

    @Test
    public void updateDataWithOldScriptName() {
        //given
        initChartNameWithoutScript();
        initMockSuccessResponseFromPythonService();
        FormDataContentDisposition metaData = FormDataContentDisposition.name("testData").fileName("test.xlsx").build();
        //when
        boolean result = service.updateData("NoScriptTest", new ByteArrayInputStream(new byte[0]), metaData);
        //then
        assertTrue(result);
        assertEquals("", configurationRepository.getVal("statistic.chart.NoScriptTest.script"));
    }

    // Get all GroupNames
    @Test
    public void getAllGroupNamesWithEmptyDB() {
        //given
        when(configurationRepository.getVal("statistic.allChartNames")).thenReturn("");
        //when
        String result = service.getAllGroupNames();
        //then
        assertEquals("[]", result);
    }

    @Test
    public void getAllGroupNames() {
        //given
        initDefaultChartNames();
        //when
        String result = service.getAllGroupNames();
        //then
        assertEquals("[\"Group1\"]", result);
    }

    // Delete Chart Tests
    @Test
    public void deleteChart() {
        //given
        initDefaultChartNames();
        //when
        service.deleteChart("Chart1");
        //then
        String groups = configurationRepository.getVal("statistic.allChartNames");
        assertFalse(groups.matches("Chart1"));
    }

    // Create Chart Tests
    @Test
    public void createChartWithNull() {
        //given
        //when
        Response response = service.createChart(null, null, null, null, null);
        //then
        assertEquals(401, response.getStatus());
    }

    @Test
    public void createChartWithEmptyChartName() {
        //given
        //when
        Response response = service.createChart("", null, null, null, null);
        //then
        assertEquals(401, response.getStatus());
    }

    @Test
    public void createChartWithAlreadyExistingChart() {
        //given
        initDefaultChartNames();
        //when
        Response response = service.createChart("Chart1", null, null, null, null);
        //then
        assertEquals(401, response.getStatus());
    }

    @Test
    public void createChartWithChartNameWithFailurePythonResponse() {
        //given
        initDefaultChartNames();
        initMockFailureResponseFromPythonService();
        FormDataContentDisposition metaData = FormDataContentDisposition.name("testScript").fileName("test.py").build();
        //when
        Response response = service.createChart("NewChart", null, null, new ByteArrayInputStream(new byte[0]), metaData);
        //then
        assertEquals(401, response.getStatus());
        verify(httpClient, times(1)).sendFileDataToPythonService(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    public void createChartWithChartNameWithoutGroup() {
        //given
        initRepoEmptyChartNames();
        initMockSuccessResponseFromPythonService();
        FormDataContentDisposition metaData = FormDataContentDisposition.name("testScript").fileName("test.py").build();
        //when
        Response response = service.createChart("NewChart", null, null, new ByteArrayInputStream(new byte[0]), metaData);
        //then
        assertEquals(200, response.getStatus());
    }

    @Test
    public void createChartWithNewGroupName() {
        //given
        initRepoEmptyChartNames();
        initMockSuccessResponseFromPythonService();
        FormDataContentDisposition metaData = FormDataContentDisposition.name("testScript").fileName("test.py").build();
        //when
        Response response = service.createChart("NewChart", "NewGroup", null, new ByteArrayInputStream(new byte[0]), metaData);
        //then
        assertEquals(200, response.getStatus());
    }

    @Test
    public void createChartWithExistingGroupName() {
        //given
        initDefaultChartNames();
        initMockSuccessResponseFromPythonService();
        FormDataContentDisposition metaData = FormDataContentDisposition.name("testScript").fileName("test.py").build();
        //when
        Response response = service.createChart("NewChart", "Group1", null, new ByteArrayInputStream(new byte[0]), metaData);
        //then
        assertEquals(200, response.getStatus());
    }

    // Get Chart Data for Chart Name
    @Test
    public void getChartDataForName() {
        //given
        initDefaultChartNames();
        //when
        String response = service.getChartDataForName(null, null, false);
        //then
        assertEquals("", response);
    }

    @Test
    public void getChartDataForNameNoTimeseriesChart() {
        //given
        initDefaultChartNames();
        String configValue = "{\"title\":\"Anwenderzahlen\",\"traces\":[{\"x\":[\"05:00:00\",\"23:00:00\"],\"y\":[1833.0,46.0],\"mode\":\"lines\",\"type\":\"scatter\",\"name\":\"2020-05-04\",\"line\":{\"width\":2},\"connectgaps\":true,\"hoverinfo\":\"x+y+text\"}],\"layout\":{\"xaxis\":{\"showline\":true,\"showgrid\":true,\"showticklabels\":true,\"linewidth\":2},\"yaxis\":{\"showgrid\":true,\"zeroline\":true,\"showline\":true,\"showticklabels\":true},\"autosize\":true,\"margin\":{\"autoexpand\":true,\"l\":100,\"r\":20,\"t\":110},\"showlegend\":true,\"title\":\"Anwenderzahlen\",\"separators\":\".,\"},\"updateTime\":\"27.06.2020(21:49)\"}";
        configurationRepository.save("statistic.chart.Chart1.data", configValue);
        //when
        String responseMsg = service.getChartDataForName("Chart1", null, false);
        //then
        StatisticPlotly plotly = getStatisticPlotlyFromString(responseMsg);
        assertEquals("Anwenderzahlen", plotly.getTitle());
        assertNull(plotly.getStartDate());
    }

    @Test
    public void getChartDataForNameTimeseriesNoMultipleNoUpdate() {
        //given
        initDefaultChartNames();
        initTimeseriesChartNoMultiple();
        //when
        String responseMsg = service.getChartDataForName("Timenotmultiple", new HashMap<>(), false);
        //then
        StatisticPlotly plotly = getStatisticPlotlyFromString(responseMsg);
        assertNotNull(plotly.getStartDate());
        assertNull(plotly.getNextTrace());
    }

    @Test
    public void getChartDataForNameTimeseriesNoMultipleNoUpdateWithStart() {
        //given
        initDefaultChartNames();
        initTimeseriesChartNoMultiple();
        Map<String, String> dates = new HashMap<>();
        dates.put("start", "2020-05-04");
        //when
        String responseMsg = service.getChartDataForName("Timenotmultiple", dates, false);
        //then
        StatisticPlotly plotly = getStatisticPlotlyFromString(responseMsg);
        assertNull(plotly.getPrevTrace());
        assertNotEquals("", plotly.getTitle());
    }

    @Test
    public void getChartDataForNameTimeseriesNoMultipleUpdateWithStartAtTimerangeBeginn() {
        //given
        initDefaultChartNames();
        initTimeseriesChartNoMultiple();
        Map<String, String> dates = new HashMap<>();
        dates.put("start", "2020-05-04");
        //when
        String responseMsg = service.getChartDataForName("Timenotmultiple", dates, true);
        //then
        StatisticPlotly plotly = getStatisticPlotlyFromString(responseMsg);
        assertNull(plotly.getTitle());
        assertNull(plotly.getLayout());
        assertNull(plotly.getUpdateTime());
        assertNull(plotly.getPrevTrace());
        assertNotNull(plotly.getTraces());
        assertNotNull(plotly.getNextTrace());
    }

    @Test
    public void getChartDataForNameTimeseriesMultiple() {
        //given
        initDefaultChartNames();
        initTimeseriesChartMultiple();
        //when
        String responseMsg = service.getChartDataForName("TimeMultiple", new HashMap<>(), false);
        //then
        StatisticPlotly plotly = getStatisticPlotlyFromString(responseMsg);
        assertEquals(5, plotly.getTraces().size());
    }

    @Test
    public void getChartDataForNameTimeseriesMultipleStartEndChanged() {
        //given
        initDefaultChartNames();
        initTimeseriesChartMultiple();
        Map<String, String> dates = new HashMap<>();
        dates.put("start", "2020-05-06");
        dates.put("end", "2020-05-08");
        //when
        String responseMsg = service.getChartDataForName("TimeMultiple", dates, false);
        //then
        StatisticPlotly plotly = getStatisticPlotlyFromString(responseMsg);
        assertEquals(3, plotly.getTraces().size());
    }

    @Test
    public void getChartDataForNameTimeseriesMultipleWithEndBeforeStart() {
        //given
        initDefaultChartNames();
        initTimeseriesChartMultiple();
        Map<String, String> dates = new HashMap<>();
        dates.put("start", "2020-05-08");
        dates.put("end", "2020-05-06");
        //when
        //then
        assertThrows(IllegalArgumentException.class, () -> service.getChartDataForName("TimeMultiple", dates, false));
    }

    @Test
    public void getChartDataForNameTimeseriesMultipleWithNotExistStartAndEnd() {
        //given
        initDefaultChartNames();
        initTimeseriesChartMultiple();
        Map<String, String> dates = new HashMap<>();
        dates.put("start", "2020-04-08");
        dates.put("end", "2020-04-09");
        //when
        //then
        assertThrows(IllegalArgumentException.class, () -> service.getChartDataForName("TimeMultiple", dates, false));
    }

    @Test
    public void getTimeseriesDatesWithoutChart() {
        //given
        initTimeseriesChartNoMultiple();
        //when
        String responseMsg = service.getTimeseriesDates("");
        //then
        assertEquals("", responseMsg);
    }


    private void initTimeseriesChartNoMultiple() {
        configurationRepository.save("statistic.chart.Timenotmultiple.title", "Timenotmultiple");
        configurationRepository.save("statistic.chart.Timenotmultiple.updateTime", "time");
        configurationRepository.save("statistic.chart.Timenotmultiple.layout", "{}");
        configurationRepository.save("statistic.chart.Timenotmultiple.savedTimes", "[\"2020-05-04\",\"2020-05-07\",\"2020-05-08\",\"2020-05-05\",\"2020-05-06\",\"2020-05-09\",\"2020-05-10\"]");
        configurationRepository.save("statistic.chart.Timenotmultiple.data.2020-05-04", "[{\"Date\": \"04\"}]");
        configurationRepository.save("statistic.chart.Timenotmultiple.data.2020-05-05", "[{\"Date\": \"05\"}]");
        configurationRepository.save("statistic.chart.Timenotmultiple.data.2020-05-06", "[{\"Date\": \"06\"}]");
        configurationRepository.save("statistic.chart.Timenotmultiple.data.2020-05-07", "[{\"Date\": \"07\"}]");
        configurationRepository.save("statistic.chart.Timenotmultiple.data.2020-05-08", "[{\"Date\": \"08\"}]");
        configurationRepository.save("statistic.chart.Timenotmultiple.data.2020-05-09", "[{\"Date\": \"09\"}]");
        configurationRepository.save("statistic.chart.Timenotmultiple.data.2020-05-10", "[{\"Date\": \"10\"}]");
    }

    private void initTimeseriesChartMultiple() {
        configurationRepository.save("statistic.chart.TimeMultiple.title", "TimeMultiple");
        configurationRepository.save("statistic.chart.TimeMultiple.updateTime", "time");
        configurationRepository.save("statistic.chart.TimeMultiple.layout", "{}");
        configurationRepository.save("statistic.chart.TimeMultiple.savedTimes", "[\"2020-05-04\",\"2020-05-07\",\"2020-05-08\",\"2020-05-05\",\"2020-05-06\",\"2020-05-09\",\"2020-05-10\"]");
        configurationRepository.save("statistic.chart.TimeMultiple.data.2020-05-04", "[{\"Date\": \"04\"}]");
        configurationRepository.save("statistic.chart.TimeMultiple.data.2020-05-05", "[{\"Date\": \"05\"}]");
        configurationRepository.save("statistic.chart.TimeMultiple.data.2020-05-06", "[{\"Date\": \"06\"}]");
        configurationRepository.save("statistic.chart.TimeMultiple.data.2020-05-07", "[{\"Date\": \"07\"}]");
        configurationRepository.save("statistic.chart.TimeMultiple.data.2020-05-08", "[{\"Date\": \"08\"}]");
        configurationRepository.save("statistic.chart.TimeMultiple.data.2020-05-09", "[{\"Date\": \"09\"}]");
        configurationRepository.save("statistic.chart.TimeMultiple.data.2020-05-10", "[{\"Date\": \"10\"}]");
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
        configurationRepository.save("statistic.allChartNames", configValue);
    }

    private void initChartNameWithoutScript() {
        String configValue = "[{\"groupName\":\"noGroup\",\"charts\":{\"NoScriptTest\":{\"accuracy\":\"none\",\"timeseries\":false,\"multiple\":false,\"scriptName\":null,\"description\":\"null\",\"dbName\":\"null\"}}}]";
        configurationRepository.save("statistic.allChartNames", configValue);
        configurationRepository.save("statistic.chart.NoScriptTest.script", "Test.py");
    }

    private void initDeprecatedChartNames() {
        Gson gson = new Gson();
        StatisticGroupOld group = new StatisticGroupOld();
        group.groupName = "Test";
        group.charts.add("OldChart1");
        List<StatisticGroupOld> list = new ArrayList<>();
        list.add(group);
        configurationRepository.save("statistic.allChartNames", gson.toJson(list));
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
        configurationRepository.save("statistic.allChartNames", "");
    }
}
