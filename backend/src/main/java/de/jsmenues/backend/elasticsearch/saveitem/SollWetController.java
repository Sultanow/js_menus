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
public class SollWetController {

    private static Logger LOGGER = LoggerFactory.getLogger(SollWetController.class);

    /**
     * insert sollwerte from frontend
     * 
     * @param hostName
     * @param key
     * @param sollValue
     * 
     * @return response about stored data
     */
    @PermitAll
    @GET
    @Path("/insertSollWerte")
    @Produces(MediaType.TEXT_PLAIN)
    public Response insertSollWerte(@QueryParam("hostname") String hostName, @QueryParam("key") String key,
            @QueryParam("sollvalue") String sollValue) throws IOException {
        String resulte = "";
        try {
            resulte = SollWerte.insertSollWerte(hostName, key, sollValue);
            if (resulte != null) {
                return Response.ok(resulte.toString()).build();
            }
            return Response.ok("no update").build();
        } catch (Exception e) {
            return Response.ok(e.getMessage()).build();
        }
    }

    /**
     * Get sollwerte from elasticsearch
     *
     * @return list of soll werte
     */
    @PermitAll
    @GET
    @Path("/getSollWerte")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSollWerte() {
        try {
            List<Map<String, Object>> result = SollWerte.getSollWerte();
            return Response.ok(result).build();
        } catch (Exception e) {
            return Response.ok("soll werte are not exist").build();
        }
    }

    /**
     * Get a soll value from elasticsearch by host name and key
     * 
     * @param hostName
     * @param key
     * 
     * @return soll value
     */
    @PermitAll
    @GET
    @Path("/getSollWValueByHostnameAndKey")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getSollWValueByHostnameAndKey(@QueryParam("hostname") String hostName,
            @QueryParam("key") String key) {              
        try {
            String result = SollWerte.getSollValueByHostnameAndKey(hostName, key);
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
     * insert history sollwerte from frontend
     * 
     * @param hostName
     * @param key
     * @param sollValue
     * 
     * @return response about stored data
     */
    @PermitAll
    @GET
    @Path("/insertHistorySollWert")
    @Produces(MediaType.TEXT_PLAIN)

    public Response insertHistorySollWert(@QueryParam("hostname") String hostName, @QueryParam("key") String key,
            @QueryParam("sollvalue") String sollValue) throws IOException {
        IndexResponse resulte = null;
        try {
            resulte = SollWerte.inserHistorySollWert(hostName, key, sollValue);
            if (resulte != null) {
                return Response.ok(resulte.toString()).build();
            }
            return Response.ok("history data is not inserted").build();
        } catch (Exception e) {
            return Response.ok(e.getMessage()).build();
        }
    }
}
