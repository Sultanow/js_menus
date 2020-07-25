package de.jsmenues.backend.authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.jsmenues.backend.authentication.AuthenticationFilter.Authorizer;
import de.jsmenues.backend.authentication.AuthenticationFilter.User;
import de.jsmenues.redis.data.Configuration;
import de.jsmenues.redis.repository.ConfigurationRepository;

public class AuthenticationFilterTest extends JerseyTest {

    ConfigurationRepository repo = mock(ConfigurationRepository.class, RETURNS_DEEP_STUBS);

    @Override
    public Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);

        ResourceConfig rc = new ResourceConfig(AuthenticationFilter.class);
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
     * Test if user is allwoed to access
     */
    @Test
    public void isUserAllowedTest() {

        AuthenticationFilter authenticationFilter = new AuthenticationFilter();

        String username = "admin";
        when(repo.get("password")).thenReturn(new Configuration("password", "1234"));
        String pass = ConfigurationRepository.getRepo().get("password").getValue();

        Set<String> rolesSet = new HashSet<String>();
        rolesSet.add("ADMIN");
        assertTrue(authenticationFilter.isUserAllowed(username, pass, rolesSet));
    }

    /**
     * Test Autroiazation
     */
    @Test
    public void AutorizerClass() {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter();
        AuthenticationResource authenticationResource = new AuthenticationResource();
        authenticationResource.getInstances();
        String username = "admin";
        String role = "RolesAllowed";
        User user = authenticationFilter.new User(username, role);
        Authorizer authorizer = authenticationFilter.new Authorizer(user);
        assertEquals("admin", authorizer.getUserPrincipal().getName());
        assertEquals("BASIC", authorizer.getAuthenticationScheme());
        assertTrue(authorizer.isSecure());
        assertTrue(authorizer.isUserInRole(role));
    }
}
