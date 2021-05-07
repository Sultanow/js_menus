package de.jsmenues.backend.authentication;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/authentication")
public class AuthenticationController {

    /**
     * Generate a valid token for each correct login
     * 
     * @param password passing from frontend
     * 
     * @return a valid token
     */
    @PermitAll
    @POST
    @Path("/login")
    @Produces(MediaType.TEXT_PLAIN)
    public Response login(String password) {
        TokenGenerator tokenGenerator = new TokenGenerator();
        String token = tokenGenerator.generateMapToken(password);
        return Response.ok(token).build();
    }

    /**
     * verify if token is valid
     * 
     * @param token passing from frontend
     * 
     * @return token  is valid "true" or "false"
     */
    @PermitAll
    @POST
    @Path("/isvalid")
    @Produces(MediaType.TEXT_PLAIN)
    public Response isValid(String token) {
        boolean isValid = AuthenticationTokens.getInstance().isValid(token);
        String stringIsValid = String.valueOf(isValid);
        return Response.ok(stringIsValid).build();
    }

    /**
     * used to change password
     * 
     * @param changePassword json data content old and new password, passing from frontend
     * @return password is changed "true" or "false"
     */
    @RolesAllowed("ADMIN")
    @POST
    @Path("/changePassword")
    @Produces(MediaType.TEXT_PLAIN)
    public Response changePassword(ChangePassword changePassword) {
        boolean isChanged = Password.changeRootPassword(changePassword.getOldPassword(),
                changePassword.getNewPassword());
        String stringIsChanged = String.valueOf(isChanged);
        return Response.ok(stringIsChanged).build();
    }
}
