package de.jsmenues.backend.authentication;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Singleton
public class AuthenticationTokens {
    public static final long VALIDITY_PERIOD = TimeUnit.HOURS.toSeconds(12);

    private static AuthenticationTokens instance = null;

    /**
     * Maps each token to their expiry time in UNIX time.
     */
    private final Map<String, Long> tokens = new HashMap<>();

    /**
     * static method to create instance of AuthenticationTokens class
     *
     * @return The singleton instance.
     */
    @Deprecated
    public static AuthenticationTokens getInstance() {
        if (instance == null) {
            instance = new AuthenticationTokens();
        }
        return instance;
    }

    public Map<String, Long> getTokens() {
        return tokens;
    }

    /**
     * Adds a new token and sets it to expire in {@link #VALIDITY_PERIOD} hours.
     * 
     * @param token The token to add.
     */
    public void addToken(String token) {
        long currentUnixTime = System.currentTimeMillis() / 1000L;
        tokens.put(token, currentUnixTime + VALIDITY_PERIOD);
    }

    /**
     * Checks validity of a given token.
     * 
     * @param token The token to check.
     * 
     * @return True if the token has not yet expired, false otherwise.
     */
    public boolean isValid(String token) {
        if (tokens.containsKey(token)) {
            long expirationUnixTime = tokens.get(token);
            long currentUnixTime = System.currentTimeMillis() / 1000L;
            return currentUnixTime <= expirationUnixTime;
        }
        return false;
    }

    /**
     * Deletes all tokens that have expired.
     * 
     * @return True if any tokens have been deleted, false otherwise.
     */
    public boolean deleteOldTokens() {
        boolean anyDeleted = false;
        long currentUnixTime = System.currentTimeMillis() / 1000L;
        for (Map.Entry<String, Long> entry : tokens.entrySet()) {
            if (currentUnixTime > entry.getValue()) {
                tokens.remove(entry.getKey());
                anyDeleted = true;
            }
        }
        return anyDeleted;
    }
}
