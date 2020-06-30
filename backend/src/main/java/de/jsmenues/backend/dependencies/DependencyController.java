package de.jsmenues.backend.dependencies;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("dependencies")
public class DependencyController {

	/**
	 * Method handling HTTP GET requests. The returned object will be sent to the
	 * client as "text/plain" media type.
	 *
	 * @return String that will be returned as a text/plain response.
	 */
	@RolesAllowed("ADMIN")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getIt() {
		return Response.ok("[" + "{ \"id\": \"1\", " + "\"title\": \"Antrag bei Geburt\", "
				+ "\"description\": \"Dependency chart for the product Antrag bei Geburt\","
				+ "\"chartContent\": \"antragBeiGeburt\"" + "}" + "]").build();
	}
}
