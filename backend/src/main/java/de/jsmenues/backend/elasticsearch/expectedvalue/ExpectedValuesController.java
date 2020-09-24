package de.jsmenues.backend.elasticsearch.expectedvalue;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/elasticsearch")
public class ExpectedValuesController {

    private static Logger LOGGER = LoggerFactory.getLogger(ExpectedValuesController.class);

    /**
     * insert expected values from frontend
     * 
     * @param hostName
     * @param key
     * @param expectedValue
     * 
     * @return response about stored data
     */
    @PermitAll
    @PUT
    @Path("/expectedValues/{hostname}/{key}/{expectedvalue}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response insertExpectedValues(@PathParam("hostname") String hostName, @PathParam("key") String key,
            @PathParam("expectedvalue") String expectedValue) throws IOException {

        try {
            ExpectedValues.insertExpectedValues(hostName, key, expectedValue);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.ok(e.getMessage()).build();
        }
    }

    /**
     * Get expected Values from elasticsearch
     *
     * @return list of expectedValue
     */
    @PermitAll
    @GET
    @Path("/expectedValues")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getExpectedValues() {
        try {
            List<Map<String, Object>> result = ExpectedValues.getExpectedValues();
            return Response.ok(result).build();
        } catch (Exception e) {
            return Response.ok("Expected value  are not exist").build();
        }
    }

    /**
     * Get a expected value from elasticsearch by host name and key
     * 
     * @param hostName
     * @param key
     * 
     * @return expected value
     */
    @PermitAll
    @GET
    @Path("/expectedValue/{hostname}/{key}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getExpectedValueByHostnameAndKey(@PathParam("hostname") String hostName,
            @PathParam("key") String key) {              
        try {
            String result = ExpectedValues.getExpectedValueByHostnameAndKey(hostName, key);
            if(result!= null){
                return Response.ok(result).build();
            }else{
                return Response.ok("").build();
            }         
        } catch (Exception e) {
            return Response.ok("").build();
        }
    }

    /**
     * insert history expected Value from frontend
     * 
     * @param hostName
     * @param key
     * @param expectedValue
     * 
     * @return response about stored data
     */
    @PermitAll
    @PUT
    @Path("/historyExpectedValue/{hostname}/{key}/{expectedvalue}")
    @Produces(MediaType.TEXT_PLAIN)

    public Response insertHistoryExpectedValue(@PathParam("hostname") String hostName, @PathParam("key") String key,
            @PathParam("expectedvalue") String expectedValue) throws IOException {

        try {
            ExpectedValues.inserHistoryExpectedValue(hostName, key, expectedValue);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.ok(e.getMessage()).build();
        }
    }
}
