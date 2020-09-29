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
    void getAllChartNames() {
        //given
        repoMock.save("statistic.allChartNames", "[]");
        //when
        target("/statistic/allChartNames").request().get(String.class);
        //then
        verify(service, times(1)).getAllChartNames();
    }

    @Test
    void getChartDataForNameBlank() {
        //given
        initDefaultChartNames();
        ///when
        String responseMsg = target("/statistic/chartData").request().get(String.class);
        //then
        assertEquals("No data", responseMsg);
    }

    @Test
    void getChartDataForName() {
        //given
        //when
        String responseMsg = target("/statistic/chartData").queryParam("chart", "Chart1").request().get(String.class);
        //then
        verify(service, times(1)).getChartDataForName(anyString(), anyMap(), anyBoolean());
    }

    @Test
    void getChartDataForNameWithStartDate() {
        //given
        //when
        String responseMsg = target("/statistic/chartData")
                .queryParam("chart", "Chart1")
                .queryParam("startdate", "2020-05-02")
                .request()
                .get(String.class);
        //then
        verify(service, times(1)).getChartDataForName(anyString(), anyMap(), anyBoolean());
    }

    @Test
    void getChartDataForNameWithStartAndEndDate() {
        //given
        //when
        String responseMsg = target("/statistic/chartData")
                .queryParam("chart", "Chart1")
                .queryParam("startdate", "2020-05-02")
                .queryParam("enddate", "2020-05-09")
                .request()
                .get(String.class);
        //then
        verify(service, times(1)).getChartDataForName(anyString(), anyMap(), anyBoolean());
    }

    @Test
    void getChartDataForNameWithoutStartDateWithEndDate() {
        //given
        //wehn
        Response response = target("/statistic/chartData")
                .queryParam("chart", "Chart1")
                .queryParam("enddate", "2020-05-02")
                .request()
                .get();
        //then
        verify(service, times(0)).getChartDataForName(anyString(), anyMap(), anyBoolean());
        assertEquals(400, response.getStatus());
    }

    @Test
    void getChartDataForNameWithNotVaildDates() {
        //given
        when(service.getChartDataForName(anyString(), anyMapOf(String.class, String.class), anyBoolean())).thenThrow(IllegalArgumentException.class);
        //when
        Response response = target("/statistic/chartData")
                .queryParam("chart", "Chart1")
                .queryParam("startdate", "2020-04-02")
                .request()
                .get();
        //then
        assertEquals(400, response.getStatus());
    }

    @Test
    void uploadNewDataWithResultFalse() {
        //given
        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.bodyPart(new FormDataBodyPart("chartName", ""));
        multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
        when(service.updateData(anyString(), any(InputStream.class), any(FormDataContentDisposition.class))).thenReturn(false);
        //when
        Response response = target("/statistic/updateData").request().post(Entity.entity(multiPart, multiPart.getMediaType()), Response.class);
        //then
        assertEquals(401, response.getStatus());
    }

    @Test
    void uploadNewDataWithResultTrue() {
        //given
        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.bodyPart(new FormDataBodyPart("chartName", ""));
        multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
        when(service.updateData(anyString(), any(InputStream.class), any(FormDataContentDisposition.class))).thenReturn(true);
        //when
        Response response = target("/statistic/updateData").request().post(Entity.entity(multiPart, multiPart.getMediaType()), Response.class);
        //then
        assertEquals(200, response.getStatus());
    }

    @Test
    void createChartWithEmptyName() {
        //given
        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.bodyPart(new FormDataBodyPart("chartName", ""));
        multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
        //when
        Response response = target("/statistic/createChart").request().post(Entity.entity(multiPart, multiPart.getMediaType()), Response.class);
        //then
        assertEquals(400, response.getStatus());
    }

    @Test
    void createChartWithNullChartName() {
        //given
        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
        multiPart.bodyPart(new FormDataBodyPart("someBody", ""));
        //when
        Response response = target("/statistic/createChart").request().post(Entity.entity(multiPart, multiPart.getMediaType()), Response.class);
        //then
        assertEquals(400, response.getStatus());
    }

    @Test
    void createChartWithNullInputStream() {
        //given
        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.bodyPart(new FormDataBodyPart("chartName", "TestChart"));
        multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
        //when
        Response response = target("/statistic/createChart").request().post(Entity.entity(multiPart, multiPart.getMediaType()), Response.class);
        //then
        assertEquals(400, response.getStatus());
    }

    @Test
    void createChartWithInputStreamAndChartName() {
        //given
        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.bodyPart(new FormDataBodyPart("chartName", "TestChart"));
        multiPart.bodyPart(new FormDataBodyPart("file", String.valueOf(new ByteArrayInputStream(new byte[0]))));
        multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
        Response mockResonse = Response.ok().build();
        when(service.createChart(anyString(), anyString(), anyString(), any(InputStream.class), any(FormDataContentDisposition.class))).thenReturn(mockResonse);
        Response response = target("/statistic/createChart").request().post(Entity.entity(multiPart, multiPart.getMediaType()), Response.class);
        //when
        verify(service, times(1)).createChart(anyString(), anyString(), anyString(), any(InputStream.class), any(FormDataContentDisposition.class));
        //then
        assertEquals(200, response.getStatus());
    }

    @Test
    void deleteChartWithoutChartName() {
        //given
        //when
        target("/statistic/deleteChart").request().delete(String.class);
        //then
        verify(service, times(0)).deleteChart("");
    }

    @Test
    void deleteChartWithChartName() {
        //given
        initDefaultChartNames();
        //when
        target("/statistic/deleteChart").queryParam("chart", "Timenotmultiple").request().delete(String.class);
        //then
        verify(service, times(1)).deleteChart("Timenotmultiple");
    }

    @Test
    void getAllGroups() {
        //given
        when(service.getAllGroupNames()).thenReturn("[\"Group1\"]");
        //when
        String responseMsg = target("/statistic/groups").request().get(String.class);
        //then
        assertEquals("[\"Group1\"]", responseMsg);
    }

    @Test
    void getTimeseriesDatesWithoutChart() {
        //given
        when(service.getTimeseriesDates("TestChart")).thenReturn("");
        //when
        String responseMsg = target("/statistic/timeseriesDates")
                .queryParam("chart", "TestChart")
                .request()
                .get(String.class);
        //then
        assertEquals("", responseMsg);
    }

    @Test
    void getTimeseriesDatesWithChart() {
        //given
        //when
        String responseMsg = target("/statistic/timeseriesDates")
                .request()
                .get(String.class);
        //then
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
