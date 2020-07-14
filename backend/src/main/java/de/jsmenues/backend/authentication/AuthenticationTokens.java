package de.jsmenues.backend.authentication;

import java.util.LinkedHashMap;
import java.util.Map;

public class AuthenticationTokens {

    private static AuthenticationTokens single_instance = null;
    public static long VALID_PERIOD = 60 * 60 * 12 ;
    private Map<String, Long> tokens;

    public Map<String, Long> getTokens() {
        return tokens;
    }

    /**
     * create tokens map to save all tokens
     */
    private AuthenticationTokens() {
        tokens = new LinkedHashMap<String, Long>();
    }

    /**
     * static method to create instance of AuthenticationTokens class
     * 
     * @param single_instance create singleton instance of AuthenticationTokens class
     *                        
     */
    public static AuthenticationTokens getInstance() {
        if (single_instance == null)
            single_instance = new AuthenticationTokens();

        return single_instance;
    }

    /**
     * Add new token to tokens map
     *
     */
    public Map<String, Long> addToken(String token) {
        long timestamp = System.currentTimeMillis()/ 1000L;
        tokens.put(token, timestamp);
        return tokens;
    }

    /**
     * verify if a token has been valid yet
     */
    public boolean isValid(String token) {

        if (tokens.isEmpty()) {
            return false;
        }
        if (tokens.containsKey(token)) {
            long timestamp = tokens.get(token);
            long unixTime = System.currentTimeMillis()/ 1000L;
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

    /**
     * Delete old tokens after 12 hours
     * 
     */
    public String deleteOldTokens() {
        String status="";
        long unixTime = System.currentTimeMillis() / 1000L;
        for (Map.Entry<String, Long> token : tokens.entrySet()) {
            if (unixTime > token.getValue() + VALID_PERIOD) {
                tokens.remove(token.getKey());
                status+= "is deleted";
            }
        }
        return status;
    }
}
