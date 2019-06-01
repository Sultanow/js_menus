package de.hackathon.helidon.microservice;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.springframework.beans.factory.annotation.Autowired;

import de.hackathon.redis.data.Configuration;
import de.hackathon.redis.repository.ConfigurationRepository;

public class ConfigurationService {

	@Autowired
    private ConfigurationRepository configurationRepository;
	
	private static Map<String, String> CONFIGURATION = Stream.of(new String[][] { { "key1", "val1" }, { "key2", "val2" }, })
			.collect(Collectors.toMap(data -> data[0], data -> data[1]));

	@GET
	@Path("/static/configuration")
	public Map<String, String> getConfiguration() {
		return CONFIGURATION;
	}
	
	@GET
	@Path("/redis/configuration")
    public Iterable<Configuration> readConfigurations(){
        Iterable<Configuration> configurations = configurationRepository.findAll();
        return configurations;
    }
}
