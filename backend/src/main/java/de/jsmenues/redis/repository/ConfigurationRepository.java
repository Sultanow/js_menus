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
public class ConfigurationRepository implements IConfigurationRepository{
    private static IConfigurationRepository instance;
    private final JedisPool configurationPool;

    private ConfigurationRepository() {

        configurationPool = new JedisPool(new JedisPoolConfig(), "redis-service", 6379, 60000, "password");
        checkMinimalConfiguration();

    }


    public static IConfigurationRepository getRepo() {
        if (instance == null) {
            instance = new ConfigurationRepository();
        }
        return instance;
    }

    /**
     * Save a config in redis
     *
     * @param config - The config
     * @Deprecated: The class Configuration will removed in future. Use save(String key, String val).
     */
    @Override
    @Deprecated
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
     * @param key Key for the saving point in Redis.
     * @return Configuration, if a value was found
     * @Deprecated: The class Configuration will removed in future. Use getVal(String key).
     */
    @Override
    @Deprecated
    public Configuration get(String key) {
        try (Jedis jedis = configurationPool.getResource()) {
            String value = jedis.get(key);
            if (value == null) {
                return new Configuration(key, "");
            }
            return new Configuration(key, value);
        }
    }

    @Override
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

    /**
     * The new version for saving data to redis
     * @param key key with which the specified value is to be associated
     * @param val value to be associated with the specified key
     */
    @Override
    public void save(String key, String val) {
        try (Jedis jedis = configurationPool.getResource()) {
            if (jedis.get(key) != null) {
                jedis.set(key, val);
                return;
            }

            jedis.append(key, val);
        }
    }

    /**
     * Get a value for a specific key
     * @param key Name for the search parameter
     * @return The Value for a key or an empty string if not exists
     */
    @Override
    public String getVal(String key) {
        try (Jedis jedis = configurationPool.getResource()) {
            String value = jedis.get(key);
            if (value == null) {
                return "";
            }
            return value;
        }
    }

}
