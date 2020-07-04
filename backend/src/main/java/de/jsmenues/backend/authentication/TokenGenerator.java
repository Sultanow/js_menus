package de.jsmenues.backend.authentication;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import java.util.Base64;
import java.util.UUID;

public class TokenGenerator {
    private final String admin = "admin";
    private final Charset charset = StandardCharsets.UTF_8;
    private final static String password = "1234";
    String token = "";
    //generate an individual token
    public String generateMapToken(String pass) {
        if (pass.equals(getPassword())) {
            UUID uuid = UUID.randomUUID();
            token = admin + ":" + pass + ":" + uuid;
            byte[] byteArrray = token.getBytes(charset);
            token = new String(Base64.getEncoder().encode(byteArrray));
            AuthenticationTokens.getInstance().addToken(token);
        }
        return token;
    }

    public static String getPassword() {
        return password;
    }
}
