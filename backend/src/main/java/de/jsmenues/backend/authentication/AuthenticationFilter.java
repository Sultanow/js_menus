package de.jsmenues.backend.authentication;

import java.lang.reflect.Method;
import java.security.Principal;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import javax.annotation.Priority;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import de.jsmenues.redis.repository.ConfigurationRepository;

import javax.ws.rs.Priorities;


/**
 * This filter verify the access permissions for a user based on username and
 * passowrd provided in request
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
	javax.inject.Provider<UriInfo> uriInfo;

	@Context
	private ResourceInfo resourceInfo;
	private static final String AUTHORIZATION_PROPERTY = "Authorization";
	private static final String AUTHENTICATION_SCHEME = "Basic";

	@Override
	public void filter(ContainerRequestContext requestContext) {
		Method method = resourceInfo.getResourceMethod();

		//all access
		if (!method.isAnnotationPresent(PermitAll.class)) {
			//all deny
			if (method.isAnnotationPresent(DenyAll.class)) {
				requestContext.abortWith(
						Response.status(Response.Status.FORBIDDEN).entity("Access blocked for all users !!").build());
				return;
			}

			final MultivaluedMap<String, String> headers = requestContext.getHeaders();
			final List<String> authorization = headers.get(AUTHORIZATION_PROPERTY);
			if (authorization == null || authorization.isEmpty()) {
				requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
						.entity("You cannot access this resource1").build());
				return;
			}
			final String encodedUserPassword = authorization.get(0).replaceFirst(AUTHENTICATION_SCHEME + " ", "");
			String usernameAndPassword = new String(Base64.getDecoder().decode(encodedUserPassword.getBytes()));

			final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
			final String username = tokenizer.nextToken();
			final String password = tokenizer.nextToken();
			
			//is user valid?
			if (method.isAnnotationPresent(RolesAllowed.class)) {
				RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
				Set<String> rolesSet = new HashSet<String>(Arrays.asList(rolesAnnotation.value()));

				if (!isUserAllowed(username, password, rolesSet)) {
					requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
							.entity("You cannot access this resource2").build());
					return;
				}
			}
		}
		
		User user=new User("admin","ADMIN");
		requestContext.setSecurityContext(new Authorizer(user));
	}

	public boolean isUserAllowed(final String username, final String password, final Set<String> rolesSet) {
		boolean isAllowed = false;
		String pass = ConfigurationRepository.getRepo().getPassword();
		if (username.equals("admin") && password.equals(pass)){
			String userRole = "ADMIN";
			if (rolesSet.contains(userRole)) {
				isAllowed = true;
			}
		}
		return isAllowed;
	}
	
	public class Authorizer implements SecurityContext {

		private User user;
		private Principal principal;

		public Authorizer(final User user) {
			this.user = user;
			this.principal = new Principal() {

				public String getName() {
					return user.username;
				}
			};
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

	public class User {
		public String username;
		public String role;

		public User(String username, String role) {
			this.username = username;
			this.role = role;
		}
	}
}