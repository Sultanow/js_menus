package de.jsmenues.backend.elasticsearch.saveitem;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.elasticsearch.action.index.IndexResponse;
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
    @GET
    @Path("/insertExpectedValues")
    @Produces(MediaType.TEXT_PLAIN)
    public Response insertExpectedValues(@QueryParam("hostname") String hostName, @QueryParam("key") String key,
            @QueryParam("expectedvalue") String expectedValue) throws IOException {
        String resulte = "";
        try {
            resulte = ExpectedValues.insertExpectedValues(hostName, key, expectedValue);
            if (resulte != null) {
                return Response.ok(resulte.toString()).build();
            }
            return Response.ok("no update").build();
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
    @Path("/getExpectedValues")
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
    @Path("/getExpectedValueByHostnameAndKey")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getExpectedValueByHostnameAndKey(@QueryParam("hostname") String hostName,
            @QueryParam("key") String key) {              
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
    @GET
    @Path("/insertHistoryExpectedValue")
    @Produces(MediaType.TEXT_PLAIN)

    public Response insertHistoryExpectedValue(@QueryParam("hostname") String hostName, @QueryParam("key") String key,
            @QueryParam("expectedvalue") String expectedValue) throws IOException {
        IndexResponse resulte = null;
        try {
            resulte = ExpectedValues.inserHistoryExpectedValue(hostName, key, expectedValue);
            if (resulte != null) {
                return Response.ok(resulte.toString()).build();
            }
            return Response.ok("history data is not inserted").build();
        } catch (Exception e) {
            return Response.ok(e.getMessage()).build();
        }
    }
}
