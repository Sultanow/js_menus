package de.jsmenues.redis.repository;

import de.jsmenues.redis.data.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Mock of the ConfigurationRepository to allow simple Junit testing and testing if
 * the values in the DB are correct.
 */
public class ConfigurationRepositoryMock implements IConfigurationRepository {
    private final Map<String, String> repoMap;

    public ConfigurationRepositoryMock() {
        repoMap = new HashMap<>();
    }

    @Override
    public void save(Configuration config) {
        repoMap.put(config.getKey(), config.getValue());
    }

    @Override
    public Configuration get(String key) {
        if (repoMap.containsKey(key)) {
            return new Configuration(key, repoMap.get(key));
        }
        return new Configuration(key, "");
    }

    @Override
    public List<Configuration> getAll(List<String> keys) {
        // TODO
        return null;
    }

    @Override
    public void save(String key, String val) {
        repoMap.put(key, val);
    }

    @Override
    public String getVal(String key) {
        if (repoMap.containsKey(key)) {
            return repoMap.get(key);
        }
        return "";
    }

    @Override
    public Map<String, String> getAllByPattern(String pattern) {
        return repoMap
                .entrySet()
                .stream()
                .filter(e -> e.getKey().contains(pattern))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public void delete(String key) {
        repoMap.remove(key);
    }
}
