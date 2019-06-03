package de.hackathon.redis.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import de.hackathon.redis.data.Configuration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Configuration Access to Redis
 *
 */
@ApplicationScoped
public class ConfigurationRepository {

	
	@Inject
	@Named("configuration")
	private JedisPool configurationPool;
	
	
	/**
	 * Save a config in redis
	 * @param config - The config
	 */
	public void save(Configuration config) {
		try(Jedis jedis = configurationPool.getResource()){
			if(jedis.get(config.getKey()) != null){
				// config key already exists
				return;
			}
			
			jedis.append(config.getKey(), config.getValue());
		}
	}
	
	/**
	 * Get configuration entry by key
	 * @param key
	 * @return null, if the given key has no value
	 * @return Configuration, if a value was found
	 */
	public Configuration get(String key) {
		try(Jedis jedis = configurationPool.getResource()){
			String value = jedis.get(key);
			if(value == null) {
				return null;
			}
			return new Configuration(key, value);
		}
	}
	
	public List<Configuration> getAll(List<String> keys){
		try(Jedis jedis = configurationPool.getResource()){
			List<String> values = jedis.mget(keys.toArray(new String[0]));
			
			if(values == null) {
				return Collections.emptyList();
			}
			
			List<Configuration> configurations = new ArrayList<>();
			for(int i=0; i<keys.size(); i++) {
				configurations.add(new Configuration(keys.get(i), values.get(i)));
			}
			
			return configurations;
		}
	}
	
	
}
