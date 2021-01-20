package de.jsmenues.backend.elasticsearch.controller;

import de.jsmenues.backend.elasticsearch.dao.BatchDao;

import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.index.IndexResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/elasticsearch/batches")
public class BatchController {

    private static Logger LOGGER = LoggerFactory.getLogger(BatchController.class);

    /**
     * Add batch to elasticsearch
     *
     * @return list of batches
     */
    @RolesAllowed("ADMIN")
    @POST
    // no empty paths to avoid useless warning @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertBatch(Map<String, Object> batch) throws IOException { 
        IndexResponse result  = BatchDao.insertBatch(batch);
        return Response.ok(result).build();
    }


    /**
     * Get all batches from elasticsearch
     *
     * @return list of batches
     */
    @PermitAll
    @GET
    // no empty paths to avoid useless warning @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllBatches() throws IOException {
        List<Map<String, Object>> result = BatchDao.getAllBatches();
        return Response.ok(result).build();
    }


    /**
     * Get batch by id from elasticsearch
     *
     * @param batchId
     * @return response about GET request
     */
    @PermitAll
    @GET
    @Path("/{batchid}")
    public Response getBatchByID(@PathParam("batchid") String batchId) throws IOException {
        String result = BatchDao.getBatchByID(batchId);
        return Response.ok(result).build();
    }

    /**
     * Update batch by id from elasticsearch
     *
     * @param batchId
     * @return response about PUT request
     */
    @RolesAllowed("ADMIN")
    @PUT
    @Path("/{batchid}")
    public Response updateBatchByID(@PathParam("batchid") String batchId, Object batch) throws IOException {
    	
    	ObjectMapper oMapper = new ObjectMapper();
    	Map<String, Object> batchMap = oMapper.convertValue(batch, Map.class);
        String result = BatchDao.updateBatchById(batchId, batchMap);
        return Response.ok(result).build();
    }

    /**
     * Delete batch by id from elasticsearch
     *
     * @param batchId
     * @return response about DELETE request
     */
    @RolesAllowed("ADMIN")
    @DELETE
    @Path("/{batchid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteBatchByID(@PathParam("batchid") String batchId) throws IOException {
        String result = BatchDao.deleteBatchById(batchId);
        JsonObject json = new JsonObject();
        json.addProperty("deleteResponse", result);
        return Response.ok(json.toString(), MediaType.APPLICATION_JSON).build();
    }

}
