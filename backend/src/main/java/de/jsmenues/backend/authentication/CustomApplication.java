package de.jsmenues.backend.authentication;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

public class CustomApplication extends ResourceConfig 
{
	public CustomApplication() 
	{
		packages("de.jsmeues.backend");
		register(AuthenticationFilter.class);
		register(RolesAllowedDynamicFeature.class);
	}
}
