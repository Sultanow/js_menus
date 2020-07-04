package de.jsmenues.backend.authentication;

import java.util.LinkedHashMap;
import java.util.Map;

public class AuthenticationTokens {

    // static variable single_instance of type AuthenticationTokens
    private static AuthenticationTokens single_instance = null;
    public static long VALID_PERIOD = 1000 * 60 * 60 * 12;
    private Map<String, Long> tokens;

    public Map<String, Long> getTokens() {
        return tokens;
    }

    public void setTokens(Map<String, Long> tokens) {
        this.tokens = tokens;
    }

    private AuthenticationTokens() {
        tokens = new LinkedHashMap<String, Long>();
    }

    // static method to create instance of AuthenticationTokens class
    public static AuthenticationTokens getInstance() {
        if (single_instance == null)
            single_instance = new AuthenticationTokens();

        return single_instance;
    }

    public void addToken(String token) {
        long timestamp = System.currentTimeMillis();
        tokens.put(token, timestamp);

    }

    // if token is valid
    public boolean isValid(String token) {

        if (tokens.isEmpty()) {
            return false;
        }
        if (tokens.containsKey(token)) {
            long timestamp = tokens.get(token);
            long unixTime = System.currentTimeMillis();
            // tokens are 12 hour valid
            if (unixTime < timestamp + VALID_PERIOD) {
                return true;

            } else {
                return false;
            }
        } else {
            return false;
        }

    }

    public void deleteOldTokens() {
        long unixTime = System.currentTimeMillis() / 1000L;
        for (Map.Entry<String, Long> token : tokens.entrySet()) {
            if (token.getValue() < unixTime + VALID_PERIOD) {
                tokens.remove(token.getKey());
            }
        }
    }
}
