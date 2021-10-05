package de.jsmenues.redis.repository;

import de.jsmenues.redis.data.Configuration;

import java.util.List;
import java.util.Map;

public interface IConfigurationRepository {

    /**
     * @param config - The config
     * @deprecated The class Configuration will be removed in future. Please use save(String key, String val).
     */
    @Deprecated
    void save(Configuration config);
    /**
     * @param key Key for the saving point in Redis.
     * @return Configuration, if a value was found
     * @deprecated The class Configuration will be removed in future. Please use save(String key, String val).
     */
    @Deprecated
    Configuration get(String key);
    List<Configuration> getAll(List<String> keys);

    void save(String key, String val);
    String getVal(String key);
    Map<String, String> getAllByPattern(String pattern);
    void delete(String key);
}
