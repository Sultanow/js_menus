package de.jsmenues.backend;

import org.glassfish.jersey.server.ResourceConfig;

/**
 * Main application class for this application. Does nothing but register our own
 * dependency injection logic.
 */
public class BackendApplication extends ResourceConfig {
    public BackendApplication() {
        register(new BackendApplicationBinder());
        packages(true, "de.jsmenues");
    }
}
