package de.jsmenues.backend.statistic;

import de.jsmenues.redis.repository.ConfigurationRepository;
import de.jsmenues.redis.repository.ConfigurationRepositoryMock;
import de.jsmenues.redis.repository.IConfigurationRepository;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


class StatisticControllerTest extends JerseyTest {
    static final Logger LOGGER = LoggerFactory.getLogger(StatisticControllerTest.class);
    ConfigurationRepository repo = mock(ConfigurationRepository.class, RETURNS_DEEP_STUBS);
    StatisticService service = mock(StatisticService.class, RETURNS_DEEP_STUBS);
    IConfigurationRepository repoMock = spy(new ConfigurationRepositoryMock());

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();
        setRedisMock(repoMock);
    }

    @AfterEach
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        Field instance = ConfigurationRepository.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Override
    public Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);

        ResourceConfig rc = new ResourceConfig(StatisticController.class)
                .register(new AbstractBinder() {
                    @Override
                    public void configure() {
                        bind(service).to(StatisticService.class);
                    }
                });
        rc.register(MultiPartFeature.class);
        return rc;
    }

    @Override
    protected void configureClient(ClientConfig config) {
        config.register(MultiPartFeature.class);
    }

    @Test
    void testGetAllChartNames() {
        repoMock.save("statistic.allChartNames", "[]");
        String responseMsg = target("/statistic/allChartNames").request().get(String.class);
        verify(service, times(1)).getAllChartNames();
    }

    @Test
    void testGetChartDataForNameBlank() {
        initDefaultChartNames();
        String responseMsg = target("/statistic/chartData").request().get(String.class);
        assertEquals("No data", responseMsg);
    }

    @Test
    void testGetChartDataForName() {
        String responseMsg = target("/statistic/chartData").queryParam("chart", "Chart1").request().get(String.class);
        verify(service, times(1)).getChartDataForName(anyString(), anyMap(), anyBoolean());
    }

    @Test
    void testGetChartDataForNameWithStartDate() {
        String responseMsg = target("/statistic/chartData")
                .queryParam("chart", "Chart1")
                .queryParam("startdate", "2020-05-02")
                .request()
                .get(String.class);
        verify(service, times(1)).getChartDataForName(anyString(), anyMap(), anyBoolean());
    }

    @Test
    void testGetChartDataForNameWithStartAndEndDate() {
        String responseMsg = target("/statistic/chartData")
                .queryParam("chart", "Chart1")
                .queryParam("startdate", "2020-05-02")
                .queryParam("enddate", "2020-05-09")
                .request()
                .get(String.class);
        verify(service, times(1)).getChartDataForName(anyString(), anyMap(), anyBoolean());
    }

    @Test
    void testGetChartDataForNameWithoutStartDateWithEndDate() {
        Response response = target("/statistic/chartData")
                .queryParam("chart", "Chart1")
                .queryParam("enddate", "2020-05-02")
                .request()
                .get();
        verify(service, times(0)).getChartDataForName(anyString(), anyMap(), anyBoolean());
        assertEquals(400, response.getStatus());
    }

    @Test
    void testGetChartDataForNameWithNotVaildDates() {
        when(service.getChartDataForName(anyString(), anyMapOf(String.class, String.class), anyBoolean())).thenThrow(IllegalArgumentException.class);
        Response response = target("/statistic/chartData")
                .queryParam("chart", "Chart1")
                .queryParam("startdate", "2020-04-02")
                .request()
                .get();
        assertEquals(400, response.getStatus());
    }

    @Test
    void testUploadNewDataWithResultFalse() {
        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.bodyPart(new FormDataBodyPart("chartName", ""));
        multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
        when(service.updateData(anyString(), any(InputStream.class), any(FormDataContentDisposition.class))).thenReturn(false);
        Response response = target("/statistic/updateData").request().post(Entity.entity(multiPart, multiPart.getMediaType()), Response.class);
        assertEquals(401, response.getStatus());
    }

    @Test
    void testUploadNewDataWithResultTrue() {
        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.bodyPart(new FormDataBodyPart("chartName", ""));
        multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
        when(service.updateData(anyString(), any(InputStream.class), any(FormDataContentDisposition.class))).thenReturn(true);
        Response response = target("/statistic/updateData").request().post(Entity.entity(multiPart, multiPart.getMediaType()), Response.class);
        assertEquals(200, response.getStatus());
    }

    @Test
    void testCreateChartWithEmptyName() {
        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.bodyPart(new FormDataBodyPart("chartName", ""));
        multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
        Response response = target("/statistic/createChart").request().post(Entity.entity(multiPart, multiPart.getMediaType()), Response.class);

        assertEquals(400, response.getStatus());
    }

    @Test
    void testCreateChartWithNullChartName() {
        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
        multiPart.bodyPart(new FormDataBodyPart("someBody", ""));
        Response response = target("/statistic/createChart").request().post(Entity.entity(multiPart, multiPart.getMediaType()), Response.class);
        assertEquals(400, response.getStatus());
    }

    @Test
    void testCreateChartWithNullInputStream() {
        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.bodyPart(new FormDataBodyPart("chartName", "TestChart"));
        multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
        Response response = target("/statistic/createChart").request().post(Entity.entity(multiPart, multiPart.getMediaType()), Response.class);
        assertEquals(400, response.getStatus());
    }

    @Test
    void testCreateChartWithInputStreamAndChartName() {
        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.bodyPart(new FormDataBodyPart("chartName", "TestChart"));
        multiPart.bodyPart(new FormDataBodyPart("file", String.valueOf(new ByteArrayInputStream(new byte[0]))));
        multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
        Response mockResonse = Response.ok().build();
        when(service.createChart(anyString(), anyString(), anyString(), any(InputStream.class), any(FormDataContentDisposition.class))).thenReturn(mockResonse);
        Response response = target("/statistic/createChart").request().post(Entity.entity(multiPart, multiPart.getMediaType()), Response.class);
        verify(service, times(1)).createChart(anyString(), anyString(), anyString(), any(InputStream.class), any(FormDataContentDisposition.class));
        assertEquals(200, response.getStatus());
    }

    @Test
    void testDeleteChartWithoutChartName() {
        String responseMsg = target("/statistic/deleteChart").request().delete(String.class);
        LOGGER.info(responseMsg);
        verify(service, times(0)).deleteChart("");
    }

    @Test
    void testDeleteChartWithChartName() {
        initDefaultChartNames();
        String responseMsg = target("/statistic/deleteChart").queryParam("chart", "Timenotmultiple").request().delete(String.class);
        verify(service, times(1)).deleteChart("Timenotmultiple");
    }

    @Test
    void testGetAllGroups() {
        when(service.getAllGroupNames()).thenReturn("[\"Group1\"]");
        String responseMsg = target("/statistic/groups").request().get(String.class);
        LOGGER.info(responseMsg);
        assertEquals("[\"Group1\"]", responseMsg);
    }

    @Test
    void testGetTimeseriesDatesWithoutChart() {
        String responseMsg = target("/statistic/timeseriesDates")
                .queryParam("chart", "TestChart")
                .request()
                .get(String.class);
        when(service.getTimeseriesDates("TestChart")).thenReturn("");
        assertEquals("", responseMsg);
    }

    @Test
    void testGetTimeseriesDatesWithChart() {
        String responseMsg = target("/statistic/timeseriesDates")
                .request()
                .get(String.class);
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

    private void initDefaultChartNames() {
        String configValue = "[{\"groupName\":\"Group1\",\"charts\":{\"Chart1\":{\"accuracy\":\"none\",\"timeseries\":false,\"multiple\":false,\"scriptName\":\"/tmp/script1.py\",\"description\":\"Desc\",\"dbName\":\"Chart1\"},\"Chart2\":{\"accuracy\":\"none\",\"timeseries\":false,\"multiple\":false,\"scriptName\":\"/tmp/script2.py\",\"description\":\"Desc\",\"dbName\":\"Chart2\"}}},{\"groupName\":\"noGroup\",\"charts\":{\"Timemultiple\":{\"accuracy\":\"day\",\"timeseries\":true,\"multiple\":true,\"scriptName\":\"timetrace1.py\",\"description\":\"null\",\"dbName\":\"Timemultiple\"},\"Timenotmultiple\":{\"accuracy\":\"day\",\"timeseries\":true,\"multiple\":false,\"scriptName\":\"timetrace2.py\",\"description\":\"null\",\"dbName\":\"Timenotmultiple\"}}}]";
        repoMock.save("statistic.allChartNames", configValue);
    }

}
