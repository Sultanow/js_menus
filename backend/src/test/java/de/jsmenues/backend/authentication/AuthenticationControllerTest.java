package de.jsmenues.backend.authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.jsmenues.redis.data.Configuration;
import de.jsmenues.redis.repository.ConfigurationRepository;

public class AuthenticationControllerTest extends JerseyTest {

    ConfigurationRepository repo = mock(ConfigurationRepository.class, RETURNS_DEEP_STUBS);

    @Override
    public Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);

        ResourceConfig rc = new ResourceConfig(AuthenticationController.class);
        rc.register(MultiPartFeature.class);
        return rc;
    }

    /**
     * Redis mock
     */
    private void setRedisMock(ConfigurationRepository mock) {
        try {
            Field instance = ConfigurationRepository.class.getDeclaredField("instance");
            instance.setAccessible(true);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();
        setRedisMock(repo);
    }

    @AfterEach
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        Field instance = ConfigurationRepository.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);

    }

    /**
     * Test "login" and "isvalid" endpoint
     */
    @Test
    public void loginTest() {

        when(repo.get("password")).thenReturn(new Configuration("password", "1234"));
        String pass = ConfigurationRepository.getRepo().get("password").getValue();

        Response loginResponse = target("/authentication/login").request()
                .post(Entity.entity(pass, MediaType.TEXT_PLAIN));
        String token = loginResponse.readEntity(String.class);

        Response isValidResponse = target("/authentication/isvalid").request()
                .post(Entity.entity(token, MediaType.TEXT_PLAIN));
        boolean isValid = Boolean.parseBoolean(isValidResponse.readEntity(String.class));
        assertTrue(isValid);

    }

    /**
     * Test "changePassword" endpoint
     */
    @Test
    public void changePasswordTest() {
        when(repo.get("password")).thenReturn(new Configuration("password", "1234"));
        String oldPass = ConfigurationRepository.getRepo().get("password").getValue();
        String newPass = "1111";

        ChangePassword changePassword = new ChangePassword(oldPass, newPass);

        Response response = target("/authentication/changePassword").request()
                .post(Entity.entity(changePassword, MediaType.APPLICATION_JSON));
        boolean isChanged = Boolean.parseBoolean(response.readEntity(String.class));
        assertTrue(isChanged);
    }
}
