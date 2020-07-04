package de.jsmenues.backend.authentication;

import java.io.IOException;

import javax.annotation.security.PermitAll;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/authentication")
public class AuthenticationController {
    
    @PermitAll
    @POST
    @Path("/login")
    @Produces(MediaType.TEXT_PLAIN)
    public Response login(String password) throws IOException {
        TokenGenerator tokeGenerator = new TokenGenerator();
        String token = tokeGenerator.generateMapToken(password);

        return Response.ok(token).build();
    }

    @PermitAll
    @POST
    @Path("/isvalid")
    @Produces(MediaType.TEXT_PLAIN)
    public Response isValid(String token) throws IOException {
        boolean isValid = AuthenticationTokens.getInstance().isValid(token);
        String stringIsValid = String.valueOf(isValid);
        return Response.ok(stringIsValid).build();
    }
}
