package de.hackathon.helidon.microservice;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

public class ConfigurationService {

	private static Map<String, String> CONFIGURATION = Stream.of(new String[][] { { "key1", "val1" }, { "key2", "val2" }, })
			.collect(Collectors.toMap(data -> data[0], data -> data[1]));

	@GET
	@Path("/configuration")
	public Map<String, String> getConfiguration() {
		return CONFIGURATION;
	}

}
