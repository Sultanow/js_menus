package de.jsmenues.backend.authentication;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

/**
 * Authentication Resource
 * 
 */
// I have no idea whether this class ever did anything so it stays for now until we know it
// doesn't break anything.
@Deprecated
public class AuthenticationResource extends ResourceConfig {
	public AuthenticationResource() 
	{
		register(AuthenticationFilter.class);
		register(RolesAllowedDynamicFeature.class);
		register(AuthenticationTokens.class);
	}
}
