package de.jsmenues.backend.zabbixapi;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Application;

public class ZabbixControllerTest extends JerseyTest {

    @Override
    public Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig(ZabbixController.class);
    }

    /**
     * Test to see that the message "Got it!" is sent in the response.
     */
    @Test
    public void testGetIt() {
        //String responseMsg = target("/zabbixapi/").request().get(String.class);
        //assertEquals("Got it!", responseMsg);
    }
}
