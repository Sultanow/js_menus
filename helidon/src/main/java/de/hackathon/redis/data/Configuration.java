package de.hackathon.redis.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
 
@RedisHash("Configuration")
public class Configuration {
    @Id private final String key;
    private final String value;
 
    public Configuration(String key, String value) {
        this.key = key;
        this.value = value;
    }

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
}
