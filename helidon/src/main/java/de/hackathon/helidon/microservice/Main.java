package de.hackathon.helidon.microservice;

import io.helidon.microprofile.server.Server;

import java.io.IOException;

/**
 * The application main class.
 */
public final class Main {

    /**
     * Cannot be instantiated.
     */
    private Main() { }

    /**
     * Application main entry point.
     * @param args command line arguments
     * @throws IOException if there are problems reading logging properties
     */
    public static void main(final String[] args) throws IOException {
        Server.create().start();
    }
}
