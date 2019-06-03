package de.hackathon.helidon.microservice;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;

import de.hackathon.redis.data.Configuration;
import de.hackathon.redis.repository.ConfigurationRepository;

@Path("/")
@RequestScoped
public class ConfigurationService {

	private static Map<String, String> CONFIGURATION = Stream
			.of(new String[][] { { "key1", "val1" }, { "key2", "val2" }, })
			.collect(Collectors.toMap(data -> data[0], data -> data[1]));

	@Inject
	private ConfigurationRepository configRepo;

	@GET
	@Path("/static/hello")
	@Produces(MediaType.TEXT_PLAIN)
	public String getHello() {
		return "Hello World!"; // working
	}

	@GET
	@Path("/static/configuration")
	@Produces(MediaType.APPLICATION_JSON)
	public String getConfiguration() {
		Gson gson = new Gson();
		return gson.toJson(CONFIGURATION);
	}

	/**
	 * 
	 * Get Configurations by key
	 *  
	 * e.g. Query for configuration with key "test1" and "test2" 
	 * => http://localhost:8080/redis/configuration/key?key=test1&key=test2
	 * 
	 * @param keys
	 * @return configurations
	 */
	@GET
	@Path("/redis/configuration/key")
	@Produces(MediaType.APPLICATION_JSON)
	public String readConfigurationsByKey(@QueryParam("key") List<String> keys) {
		List<Configuration> configurations = configRepo.getAll(keys);
		
		Gson gson = new Gson();
		return gson.toJson(configurations);
	}
	
	/**
	 * Add new Configuration to redis
	 * e.g. http://localhost:8080/redis/configuration/add/testkey/testvalue
	 * => new Config with testkey and testvalue added.
	 * @param key
	 * @param value
	 */
	@GET
	@Path("/redis/configuration/add/{key}/{value}")
	@Produces(MediaType.TEXT_PLAIN)
	public String addConfiguration(@PathParam("key") String key, @PathParam("value") String value) {
		configRepo.save(new Configuration(key, value));
		// todo exception handling
		return "Config( " + key + "," + value + ") added.";
	}
}
