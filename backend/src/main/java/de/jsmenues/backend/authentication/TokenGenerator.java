package de.jsmenues.backend.authentication;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import java.util.Base64;
import java.util.UUID;

import de.jsmenues.redis.repository.ConfigurationRepository;
public class TokenGenerator {
    private final String admin = "admin";
    private final Charset charset = StandardCharsets.UTF_8;
    String token = "";
    
    /**
     * Generate an individual token 
     * 
     * @param pass passing from fronted
     * 
     * @return a valid token
     */
    public String generateMapToken(String pass) {
        
        String currentPassword = ConfigurationRepository.getRepo().getVal("password");
        
        if (pass.equals(currentPassword)) {  
            UUID uuid = UUID.randomUUID();
            token = admin + ":" + pass + ":" + uuid;
            byte[] byteArrray = token.getBytes(charset);
            token = new String(Base64.getEncoder().encode(byteArrray));
            AuthenticationTokens.getInstance().addToken(token);        
        }
        return token;
    }
}
