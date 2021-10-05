package de.jsmenues.backend.authentication;

import de.jsmenues.redis.repository.ConfigurationRepository;
import de.jsmenues.redis.repository.IConfigurationRepository;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/authentication")
public class AuthenticationController {
    private final AuthenticationTokens authenticationTokens;
    private final IConfigurationRepository configurationRepository;

    @Inject
    public AuthenticationController(AuthenticationTokens authenticationTokens,
                                    IConfigurationRepository configurationRepository) {
        this.authenticationTokens = authenticationTokens;
        this.configurationRepository = configurationRepository;
    }

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
        String token = tokenGenerator.generateToken(password);
        // Remember the generated token for subsequent logins.
        authenticationTokens.addToken(token);
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
        boolean tokenIsValid = authenticationTokens.isValid(token);
        return Response.ok(tokenIsValid).build();
    }

    /**
     * Changes the password of the admin user.
     * 
     * @param changePassword json data content old and new password, passing from frontend
     * @return True if the supplied old password matched the actual password and as such the
     * change was successful, false otherwise.
     */
    @RolesAllowed("ADMIN")
    @POST
    @Path("/changePassword")
    @Produces(MediaType.TEXT_PLAIN)
    public Response changePassword(ChangePassword changePassword) {
        // This code is a bit of business logic, but extracting this to another class
        // is probably not worth the boilerplate.
        boolean oldPasswordMatchedActualPassword;

        String currentPassword = configurationRepository.getVal("password");
        if (currentPassword.equals(changePassword.getOldPassword())) {
            configurationRepository.save(
                    "password", changePassword.getNewPassword());
            oldPasswordMatchedActualPassword = true;
        }
        else {
            oldPasswordMatchedActualPassword = false;
        }
        return Response.ok(oldPasswordMatchedActualPassword).build();
    }
}
