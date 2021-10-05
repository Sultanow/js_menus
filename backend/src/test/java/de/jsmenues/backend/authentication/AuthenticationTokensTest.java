package de.jsmenues.backend.authentication;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Application;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationTokensTest extends JerseyTest {
    @Override
    public Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig(AuthenticationTokens.class);
    }

    /**
     * Checks whether tokens are successfully marked as invalid after expiring.
     */
    @Test
    public void isTokenValidReturnsCorrectValues() {
        AuthenticationTokens tokens = new AuthenticationTokens();

        String validToken = "validToken";
        String expiredToken = "expiredToken";

        Map<String, Long> tokenMap = tokens.getTokens();
        // Add a token that expires in 12 hours from now and as such is valid.
        tokenMap.put(validToken,
                System.currentTimeMillis() / 1000L + AuthenticationTokens.VALIDITY_PERIOD);
        // Add a token that expired 1000 seconds ago.
        tokenMap.put(expiredToken, System.currentTimeMillis() / 1000L - 1000L);

        // Make sure that there's no other tokens in the map somehow.
        assertEquals(tokenMap.size(), 2);
        assertTrue(tokens.isValid(validToken));
        assertFalse(tokens.isValid(expiredToken));
    }

    @Test
    public void isTokenValidReturnsCorrectValueForNonExistingToken() {
        AuthenticationTokens tokens = new AuthenticationTokens();

        String nonExistentToken = "nonExistentToken";

        // There should be no tokens if we don't add anything.
        assertEquals(tokens.getTokens().size(), 0);
        // Tokens not in the map should not be valid.
        assertFalse(tokens.isValid(nonExistentToken));
    }

    @Test
    public void deleteTokensRemovesExpiredTokens() {
        AuthenticationTokens tokens = new AuthenticationTokens();

        String validToken = "validToken";
        String expiredToken = "expiredToken";

        Map<String, Long> tokenMap = tokens.getTokens();
        // Add a token that expires in 12 hours from now and as such is valid.
        tokenMap.put(validToken,
                System.currentTimeMillis() / 1000L + AuthenticationTokens.VALIDITY_PERIOD);
        // Add a token that expired 1000 seconds ago.
        tokenMap.put(expiredToken, System.currentTimeMillis() / 1000L - 1000L);

        assertEquals(tokenMap.size(), 2);

        // The call should delete at least one token.
        assertTrue(tokens.deleteOldTokens());
        assertTrue(tokenMap.containsKey(validToken));
        assertFalse(tokenMap.containsKey(expiredToken));
    }

    /**
     * Test timer to delete old tokens
     */
    @Test
    void timerToDeleteOldTokensTest() {
        TimerToDeleteOldTokens timerToDeleteOldTokens = new TimerToDeleteOldTokens();
        assertDoesNotThrow(timerToDeleteOldTokens::start);
    }
}
