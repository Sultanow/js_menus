package de.jsmenues.backend.batches;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("batches")
public class BatchesController {

	/**
	 * Method handling HTTP GET requests. The returned object will be sent to the
	 * client as "text/plain" media type.
	 *
	 * @return String that will be returned as a text/plain response.
	 */
    @PermitAll
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getIt() {

		return "[{ id: 'a220', duration: 6 },{ id: 'a610', duration: 28 },{ id: 'a620', duration: 496 },{ id: 'a999', duration: 8128 },];";

	}
}
