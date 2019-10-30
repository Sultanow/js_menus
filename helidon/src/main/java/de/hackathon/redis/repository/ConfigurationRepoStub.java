package de.hackathon.redis.repository;

import de.hackathon.redis.data.Batch;

import javax.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Configuration Access to Redis stub
 */
@ApplicationScoped
public class ConfigurationRepoStub {

    private static Map<String, String> CONFIGURATION = Stream
            .of(new String[][]{{"key1", "val1"}, {"key2", "val2"}, {"a220", "42"}})
            .collect(Collectors.toMap(data -> data[0], data -> data[1]));

    public void storeConfigurationIfAbsent(Batch batch) {
        CONFIGURATION.putIfAbsent(batch.getBatchId(), batch.getDuration());
    }

    public Batch retrieveConfiguration(String k) {
        return new Batch(k, CONFIGURATION.get(k));
    }

    public void deleteBatch(String batchId) {
        CONFIGURATION.remove(batchId);
    }
}
