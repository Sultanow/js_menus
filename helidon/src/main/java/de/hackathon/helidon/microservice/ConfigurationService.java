package de.hackathon.helidon.microservice;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigurationService {

	private static Map<String, String> map = Stream.of(new String[][] { { "Hello", "World" }, { "John", "Doe" }, })
			.collect(Collectors.toMap(data -> data[0], data -> data[1]));

	@GET
	@Path("/configuration")
	public Map<String, String> getConfiguration() {
		return "Hello World!";
	}

}
