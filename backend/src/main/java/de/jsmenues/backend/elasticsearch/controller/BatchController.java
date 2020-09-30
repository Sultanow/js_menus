package de.jsmenues.backend.elasticsearch.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.security.PermitAll;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jsmenues.backend.elasticsearch.dao.BatchDao;
import de.jsmenues.backend.zabbixservice.ZabbixService;

@Path("/elasticsearch/batches")
public class BatchController {

    private static Logger LOGGER = LoggerFactory.getLogger(BatchController.class);

    /**
     * insert all batches from zabbix to elasticsearch
     *
     * @return list of batches
     */
    @PermitAll
    @PUT
    @Path("/")
    public Response InsertAllBatcs(batches) throws IOException {
        BatchDao.insertAllBatches(batches);
        return Response.ok(batches).build();
    }

    /**
     * Get all batches from elasticsearch
     *
     * @return list of batches
     */
    @PermitAll
    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllBatches() throws IOException {

        List<Map<String, Object>> result = BatchDao.getAllBatches();
        return Response.ok(result).build();
    }

    /**
     * Delete batch by id from elasticsearch
     *
     * @param batchId
     * @return response about delete request
     */
    @PermitAll
    @DELETE
    @Path("/{batchid}")
    public Response deleteHostByID(@PathParam("batchid") String batchId) throws IOException {
        String result1 = BatchDao.deleteHostById(batchId);
        return Response.ok(result1).build();
    }

}
