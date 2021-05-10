package de.jsmenues.backend;

import de.jsmenues.backend.authentication.AuthenticationTokens;
import de.jsmenues.backend.elasticsearch.ElasticsearchConnector;
import de.jsmenues.backend.elasticsearch.dao.*;
import de.jsmenues.backend.elasticsearch.expectedvalue.ExpectedValues;
import de.jsmenues.backend.zabbixservice.ZabbixService;
import de.jsmenues.redis.repository.ConfigurationRepository;
import de.jsmenues.redis.repository.IConfigurationRepository;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

/**
 * Binder that manages the dependencies to inject in our application.
 */
public class BackendApplicationBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(AuthenticationTokens.class)
                .to(AuthenticationTokens.class);
        // Provide the implementation when searching for the interface.
        bind(ConfigurationRepository.class)
                .to(IConfigurationRepository.class);
        bind(ZabbixService.class).to(ZabbixService.class);
        bind(ElasticsearchConnector.class).to(ElasticsearchConnector.class);
        bind(ElasticsearchDao.class).to(ElasticsearchDao.class);
        bind(HistoryDao.class).to(HistoryDao.class);
        bind(InformationHostDao.class).to(InformationHostDao.class);
        bind(HostsDao.class).to(HostsDao.class);
        bind(SnapshotDao.class).to(SnapshotDao.class);
        bind(ExpectedValues.class).to(ExpectedValues.class);
    }
}
