package de.jsmenues.backend.authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;
import java.util.Timer;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.jsmenues.redis.data.Configuration;
import de.jsmenues.redis.repository.ConfigurationRepository;

class AuthenticationTokensTest extends JerseyTest {

    ConfigurationRepository repo = mock(ConfigurationRepository.class, RETURNS_DEEP_STUBS);

    static Map<String, Long> tokenMAp;
    static String token1 = "";
    static String token2 = "";
    static String token3 = "";
    static Map<String, Long> tokens;

    @Override
    public Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig(AuthenticationTokens.class);
    }

    /**
     * Redis mock
     */
    void setRedisMock(ConfigurationRepository mock) {
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
     * Test to verify if a token exist in map.
     * 
     */
    @Test
    void isTokenExistInMap() {
        Map<String, Long> tokenMAp;
        TokenGenerator tokenGenerator = new TokenGenerator();
        when(repo.getVal("password")).thenReturn("1234");
        String pass = ConfigurationRepository.getRepo().getVal("password");
        token1 = tokenGenerator.generateMapToken(pass);
        tokenMAp = AuthenticationTokens.getInstance().getTokens();
        for (String key : tokenMAp.keySet()) {
            token2 = key;
        }
        assertEquals(token1, token2);
    }

    /**
     * Test to verify if a token is valid and deleted after 12 hours
     */
    @Test
    void isTokenValid() {
        Map<String, Long> tokenMAp1;

        TokenGenerator tokenGenerator = new TokenGenerator();

        when(repo.getVal("password")).thenReturn("1234");
        token1 = tokenGenerator.generateMapToken("1234");
        token2 = "YWRtaW46MTIzNDphZGZmZmUxMC00ZTg5LTQ1ZDAtOTkwZC01ZDg1Mjk5MjliYjI=";
        when(repo.getVal("password")).thenReturn("1235");
        token3 = tokenGenerator.generateMapToken("1235");
        tokenMAp1 = AuthenticationTokens.getInstance().getTokens();
        long timestamp1 = tokenMAp1.get(token3);
        long timestampAfter12Hour1 = timestamp1 - AuthenticationTokens.VALID_PERIOD - 10;
        tokenMAp1.replace(token3, timestamp1, timestampAfter12Hour1);

        boolean isValid1 = AuthenticationTokens.getInstance().isValid(token1);
        boolean isValid2 = AuthenticationTokens.getInstance().isValid(token2);
        boolean isValid3 = AuthenticationTokens.getInstance().isValid(token3);
        String result = AuthenticationTokens.getInstance().deleteOldTokens();
        tokenMAp1.clear();
        assertTrue(isValid1);
        assertFalse(isValid2);
        assertFalse(isValid3);
        assertEquals("is deleted", result);
        assertFalse(AuthenticationTokens.getInstance().isValid(token3));
    }

    /**
     * Test to verify if a password is changed.
     */
    @Test
    void changePasswordTest() {
        Password password = new Password();
        String currentPassword = "1234";
        String oldPassword = "1234";
        String newPassword = "1111";
        when(repo.getVal("password")).thenReturn(currentPassword);
        boolean isChanged = password.changeRootPassword(oldPassword, newPassword);
        assertTrue(isChanged);
    }

    /**
     * Test set password to Redis
     */
    @Test
    void setRootPassword() {
        String rootPassword = "1234";
        when(repo.getVal("password")).thenReturn("1234");
        boolean isSaved = Password.setRootPassword(rootPassword);
        assertTrue(isSaved);
    }

    /**
     * Test timer to delete old tokens
     */
    @Test
    void timerToDeleteOldTokensTest() {
        TimerToDeleteOldTokens timerToDeleteOldTokens = new TimerToDeleteOldTokens();
        assertDoesNotThrow(() -> timerToDeleteOldTokens.start());

    }
}
