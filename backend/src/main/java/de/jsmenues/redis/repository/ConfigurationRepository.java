package de.jsmenues.redis.repository;

import de.jsmenues.redis.data.Configuration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Configuration Access to Redis
 */
public class ConfigurationRepository {
    private static ConfigurationRepository instance;
    private JedisPool configurationPool;

    private ConfigurationRepository() {
        configurationPool = new JedisPool(new JedisPoolConfig(), "redis-service", 6379, 60000, "password");
        checkMinimalConfiguration();
    }

    private void checkMinimalConfiguration() {
        saveConfigItemIfNotExist("zabbixURL");
        saveConfigItemIfNotExist("zabbixUser");
        saveConfigItemIfNotExist("zabbixPass");
    }

    private void saveConfigItemIfNotExist(String item) {
        try (Jedis jedis = configurationPool.getResource()) {
            if (jedis.get(item) == null)
                save(new Configuration(item, ""));
        }
    }

    public static ConfigurationRepository getRepo() {
        if (instance == null) {
            instance = new ConfigurationRepository();
        }
        return instance;
    }

    /**
     * Save a config in redis
     *
     * @param config - The config
     */
    public void save(Configuration config) {
        try (Jedis jedis = configurationPool.getResource()) {
            if (jedis.get(config.getKey()) != null) {
                jedis.set(config.getKey(), config.getValue());
                return;
            }

            jedis.append(config.getKey(), config.getValue());
        }
    }

    /**
     * Get configuration entry by key
     *
     * @param key
     * @return Configuration, if a value was found
     */
    public Configuration get(String key) {
        try (Jedis jedis = configurationPool.getResource()) {
            String value = jedis.get(key);
            if (value == null) {
                return new Configuration(key, "");
            }
            return new Configuration(key, value);
        }
    }

    public List<Configuration> getAll(List<String> keys) {
        List<Configuration> configurations = new ArrayList<>();
        try (Jedis jedis = configurationPool.getResource()) {
            List<String> values = jedis.mget(keys.toArray(new String[0]));

            if (values == null) {
                return Collections.emptyList();
            }

            for (int i = 0; i < keys.size(); i++) {
                configurations.add(new Configuration(keys.get(i), values.get(i)));
            }
        }
        return configurations;
    }

}
