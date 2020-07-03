package de.jsmenues.backend.statistic;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;


class StatisticControllerTest extends JerseyTest {

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @AfterEach
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Override
    public Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);

        ResourceConfig rc =  new ResourceConfig(StatisticController.class);
        rc.register(MultiPartFeature.class);
        return rc;
    }

    @Test
    void getAllChartNames() {
        String responseMsg = target("/statistic/").request().get(String.class);
        assertEquals("test", responseMsg);
        //[{"groupName":"null","charts":["Test","Test2","Test3","Test4","Test5","Blank"]}]
    }

    @Test
    void getChartDataForName() {
    }

    @Test
    void uploadNewData() {
    }

    @Test
    void createChart() {
    }

    @Test
    void deleteChart() {
    }

    @Test
    void getAllGroups() {
    }
}
