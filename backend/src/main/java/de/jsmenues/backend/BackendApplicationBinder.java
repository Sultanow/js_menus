package de.jsmenues.backend;

import de.jsmenues.backend.authentication.AuthenticationFilter;
import de.jsmenues.backend.authentication.AuthenticationTokens;
import de.jsmenues.redis.repository.ConfigurationRepository;
import de.jsmenues.redis.repository.IConfigurationRepository;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

/**
 * Binder that manages the dependencies to inject in our application.
 */
public class BackendApplicationBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(AuthenticationTokens.class)
                .to(AuthenticationTokens.class);
        bind(ConfigurationRepository.class)
                .to(IConfigurationRepository.class);
    }
}
