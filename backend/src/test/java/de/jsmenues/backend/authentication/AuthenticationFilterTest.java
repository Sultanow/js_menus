package de.jsmenues.backend.authentication;

import de.jsmenues.backend.BackendApplication;
import de.jsmenues.backend.authentication.AuthenticationFilter.Authorizer;
import de.jsmenues.backend.authentication.AuthenticationFilter.User;
import de.jsmenues.redis.repository.ConfigurationRepository;
import de.jsmenues.redis.repository.IConfigurationRepository;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Application;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class AuthenticationFilterTest extends JerseyTest {
    private final ConfigurationRepository repo = mock(ConfigurationRepository.class);
    private final AuthenticationTokens tokens = mock(AuthenticationTokens.class);

    @Override
    public Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);

        ResourceConfig rc = new ResourceConfig(BackendApplication.class);
        rc.register(MultiPartFeature.class);
        rc.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(repo).to(IConfigurationRepository.class);
                bind(tokens).to(AuthenticationTokens.class);
            }
        });
        return rc;
    }

    /**
     * Test if user is allowed to access
     */
    @Test
    public void isUserAllowedTest() {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(repo, tokens);

        when(repo.getVal("password")).thenReturn("1234");

        Set<String> rolesSet = new HashSet<>();
        rolesSet.add("ADMIN");
        assertTrue(authenticationFilter.isUserAllowed(
                "admin", "1234", rolesSet));

        verify(tokens, never()).getTokens();
        verify(repo, times(1)).getVal("password");
    }

    @Test
    public void testAuthorizerHasFieldsSet() {
        String username = "admin";
        String role = "RolesAllowed";
        User user = new User(username, role);
        Authorizer authorizer = new Authorizer(user);
        assertEquals("admin", authorizer.getUserPrincipal().getName());
        assertEquals("BASIC", authorizer.getAuthenticationScheme());
        assertTrue(authorizer.isSecure());
        assertTrue(authorizer.isUserInRole(role));
    }
}
