package de.jsmenues.backend.elasticsearch.expectedvalue;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/elasticsearch/expectedValues")
public class ExpectedValuesController {
    private final ExpectedValues expectedValues;

    @Inject
    public ExpectedValuesController(ExpectedValues expectedValues) {
        this.expectedValues = expectedValues;
    }

    /**
     * insert expected values from frontend
     *
     * @param hostName
     * @param key
     * @param expectedValue
     * @return response about stored data
     */
    @PermitAll
    @PUT
    @Path("/{hostname}/{key}/{expectedvalue}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response insertExpectedValues(@PathParam("hostname") String hostName,
                                         @PathParam("key") String key,
                                         @PathParam("expectedvalue") String expectedValue) throws Exception {
        expectedValues.insertExpectedValues(hostName, key, expectedValue);
        return Response.ok().build();
    }

    /**
     * Get expected Values from elasticsearch
     *
     * @return list of expectedValue
     */
    @PermitAll
    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getExpectedValues() throws Exception {
        List<Map<String, Object>> result = expectedValues.getExpectedValues();
        return Response.ok(result).build();
    }

    /**
     * Get a expected value from elasticsearch by host name and key
     *
     * @param hostName
     * @param key
     * @return expected value
     */
    @PermitAll
    @GET
    @Path("/{hostname}/{key}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getExpectedValueByHostnameAndKey(@PathParam("hostname") String hostName,
                                                     @PathParam("key") String key) {
        String result = expectedValues.getExpectedValueByHostnameAndKey(hostName, key);
        return Response.ok(result).build();
    }

}
