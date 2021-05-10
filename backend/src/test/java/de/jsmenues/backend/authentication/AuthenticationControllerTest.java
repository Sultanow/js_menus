package de.jsmenues.backend.authentication;

import de.jsmenues.backend.BackendApplication;
import de.jsmenues.redis.repository.ConfigurationRepository;
import de.jsmenues.redis.repository.IConfigurationRepository;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthenticationControllerTest extends JerseyTest {
    private final ConfigurationRepository repo = mock(ConfigurationRepository.class);
    private final AuthenticationTokens tokens = mock(AuthenticationTokens.class);

    @Override
    public Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);

        ResourceConfig rc = new BackendApplication();
        rc.register(new AbstractBinder() {
            @Override
            protected void configure() {
                // Use a higher rank to override the definitions in BackendApplication with our mocks.
                bind(repo).to(IConfigurationRepository.class).ranked(2);
                bind(tokens).to(AuthenticationTokens.class).ranked(2);
            }
        });
        return rc;
    }

    /**
     * Test "login" and "isvalid" endpoint
     */
    @Test
    public void loginTest() {
        when(tokens.isValid(any())).thenReturn(true);

        Response loginResponse = target("/authentication/login")
                .request()
                .post(Entity.entity("somepassword", MediaType.TEXT_PLAIN));
        String token = loginResponse.readEntity(String.class);
        assertFalse(token.isEmpty());

        Response response = target("/authentication/isvalid")
                .request()
                .post(Entity.entity(token, MediaType.TEXT_PLAIN));
        boolean isValid = response.readEntity(Boolean.class);

        assertEquals(response.getStatus(),200);
        assertTrue(isValid);
    }

    /**
     * Test "changePassword" endpoint
     */
    @Test
    public void changePasswordTest() {
        when(repo.getVal("password")).thenReturn("somepass");
        when(tokens.isValid(any())).then(invocationOnMock -> {
            System.out.println("isValid() called!");
            return true;
        });

        String oldPass = repo.getVal("password");
        String newPass = "1111";
        String validBase64Authorization = "Basic " + new String(
                Base64.getEncoder().encode("admin:somepass"
                        .getBytes(StandardCharsets.UTF_8)),
                StandardCharsets.UTF_8
        );

        ChangePassword changePassword = new ChangePassword(oldPass, newPass);

        Response response = target("/authentication/changePassword")
                .request()
                .header("Authorization", validBase64Authorization)
                .post(Entity.entity(changePassword, MediaType.APPLICATION_JSON));
        Assertions.assertEquals(response.getStatus(), 200);

        boolean isChanged = response.readEntity(Boolean.class);
        assertTrue(isChanged);
    }
}
