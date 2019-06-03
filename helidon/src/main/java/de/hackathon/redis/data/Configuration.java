package de.hackathon.redis.data;

public class Configuration {
    private final String key;
    private final String value;
 
    public Configuration(String key) {
        this.key = key;
        this.value = null;
    }
    
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

	@Override
	public String toString() {
		return "Configuration [key=" + key + ", value=" + value + "]";
	}	
	
}
