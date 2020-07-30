package de.jsmenues.redis.repository;

import de.jsmenues.redis.data.Configuration;

import java.util.List;

public interface IConfigurationRepository {


    void save(Configuration config);
    Configuration get(String key);
    List<Configuration> getAll(List<String> keys);

    void save(String key, String val);
    String getVal(String key);

}
