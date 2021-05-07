package de.jsmenues.backend.authentication;

import de.jsmenues.redis.repository.ConfigurationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.*;

/**
 * This filter verify the access permissions for a user based on username and
 * Password provided in request
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
    private static final String AUTHORIZATION_PROPERTY = "Authorization";
    private static final String AUTHENTICATION_SCHEME = "Basic";
    public static final String USER_ADMIN = "admin";
    public static final String USER_ROLE_ADMIN = "ADMIN";

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFilter.class);

    @Context
    private ResourceInfo resourceInfo;

    /**
     * Filter for the annotation over the Methods
     */
    @Override
    public void filter(ContainerRequestContext requestContext) {
        Method method = resourceInfo.getResourceMethod();

        // All users have access
        if (!method.isAnnotationPresent(PermitAll.class)) {
            // all users deny
            if (method.isAnnotationPresent(DenyAll.class)) {
                requestContext.abortWith(
                        Response.status(Response.Status.FORBIDDEN)
                                .entity("Access blocked for all users !!")
                                .build());
                return;
            }

            final MultivaluedMap<String, String> headers = requestContext.getHeaders();
            final List<String> authorization = headers.get(AUTHORIZATION_PROPERTY);
            if (authorization == null || authorization.isEmpty()) {
                requestContext.abortWith(Response
                        .status(Response.Status.UNAUTHORIZED)
                        .entity("You cannot access this resource")
                        .build());
                return;
            }
            final String encodedUserPassword = authorization.get(0)
                    .replaceFirst(AUTHENTICATION_SCHEME + " ", "");

            if (!AuthenticationTokens.getInstance().isValid(encodedUserPassword)) {
                requestContext
                        .abortWith(Response.status(Response.Status.UNAUTHORIZED)
                                .entity("user is not valid")
                                .build());
            }
            String usernameAndPassword = new String(Base64.getDecoder().decode(encodedUserPassword.getBytes()));

            final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
            try {
                final String username = tokenizer.nextToken();
                final String password = tokenizer.nextToken();

                // is user valid?
                if (method.isAnnotationPresent(RolesAllowed.class)) {
                    RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
                    Set<String> rolesSet = new HashSet<>(Arrays.asList(rolesAnnotation.value()));

                    if (!isUserAllowed(username, password, rolesSet)) {
                        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                                .entity("You cannot access this resource").build());
                        return;
                    }
                }
            } catch (NoSuchElementException e) {
                // TODO: possibly leaking passwords into logs is a bad idea.
                LOGGER.error("No Token in username/password, usernameAndPassword:" + usernameAndPassword);
                LOGGER.error(e.getMessage());
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                        .entity("You cannot access this resource, username/password not found").build());
            }
        }
        User user = new User(USER_ADMIN, "ADMIN");
        requestContext.setSecurityContext(new Authorizer(user));
    }

    /**
     * Verify users access
     *
     * @param username The username.
     * @param password The password.
     * @param rolesSet what permissions does the user have?
     * @return is user allowed true or false
     */
    public boolean isUserAllowed(final String username, final String password, final Set<String> rolesSet) {
        String pass = ConfigurationRepository.getRepo().getVal("password");
        return username.equals(USER_ADMIN)
                && password.equals(pass)
                && rolesSet.contains(USER_ROLE_ADMIN);
    }

    /**
     * set user as Authorizer
     */
    public static class Authorizer implements SecurityContext {

        private final User user;
        private final Principal principal;

        public Authorizer(final User user) {
            this.user = user;
            this.principal = () -> user.username;
        }

        public Principal getUserPrincipal() {
            return this.principal;
        }

        public boolean isUserInRole(String role) {
            return (role.equals(user.role));
        }

        public boolean isSecure() {
            return true;
        }

        public String getAuthenticationScheme() {
            return SecurityContext.BASIC_AUTH;
        }
    }

    public static class User {
        public String username;
        public String role;

        public User(String username, String role) {
            this.username = username;
            this.role = role;
        }
    }
}
