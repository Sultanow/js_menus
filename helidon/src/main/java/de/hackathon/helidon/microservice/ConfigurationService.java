package de.hackathon.helidon.microservice;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Path("/")
@RequestScoped
public class ConfigurationService {

	private static Map<String, String> CONFIGURATION = Stream
			.of(new String[][] { { "key1", "val1" }, { "key2", "val2" }, })
			.collect(Collectors.toMap(data -> data[0], data -> data[1]));

	@Inject
	@Named("configuration")
	private JedisPool configurationPool;

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

	@GET
	@Path("/redis/configuration")
	public String readConfigurations() {
		// just for testing redis connection
		// todo change to something useful
		try(Jedis jedis = configurationPool.getResource()){
			String test = jedis.get("testkey");
			if(test == null) {
				jedis.append("testkey", "testvalue");
				test = jedis.get("testkey");
			}
			
			return test;
		}
	}
}