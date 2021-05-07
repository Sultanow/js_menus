package de.jsmenues.backend.authentication;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

/**
 * Helper class to generate login tokens.
 */
public class TokenGenerator {
    private static final String ADMIN_USER = "admin";
    
    /**
     * Generates a token for the admin user. This does not check whether the admin
     * user is logged in correctly!
     * 
     * @param pass The password used for the login attempt.
     * 
     * @return a valid token
     */
    public String generateToken(String pass) {
        UUID uuid = UUID.randomUUID();
        String token = ADMIN_USER + ":" + pass + ":" + uuid;
        byte[] byteArray = token.getBytes(StandardCharsets.UTF_8);
        token = new String(Base64.getEncoder().encode(byteArray));
        return token;
    }
}
