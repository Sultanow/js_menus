package de.jsmenues.backend.authentication;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

/**
 * Authentication Resource
 * 
 */
public class AuthenticationResource extends ResourceConfig {
	public AuthenticationResource() 
	{
		register(AuthenticationFilter.class);
		register(RolesAllowedDynamicFeature.class);
	}
}
