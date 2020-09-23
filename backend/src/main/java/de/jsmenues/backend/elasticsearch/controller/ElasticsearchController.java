package de.jsmenues.backend.elasticsearch.controller;

import java.io.IOException;
import javax.annotation.security.PermitAll;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jsmenues.backend.elasticsearch.dao.ElasticsearchDao;

@Path("/elasticsearch")
public class ElasticsearchController {

    private static Logger LOGGER = LoggerFactory.getLogger(ElasticsearchController.class);

    /**
     * Get indices names from elasticsearch
     *
     * @param patternIndexName index pattern example * or index name
     * @return names of indices
     */
    @PermitAll
    @GET
    @Path("/indexname")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIndexNames(@QueryParam("patternindexname") String patternIndexName) throws IOException {

        String[] result = ElasticsearchDao.getIdexName(patternIndexName);
        return Response.ok(result).build();
    }

    /**
     * Get all history index names
     *
     * @return names of historyindices
     */
    @PermitAll
    @GET
    @Path("/allHistoryIndexName")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHistoryIndexNames() throws IOException {

        String[] result = ElasticsearchDao.getIdexName("history-*");
        return Response.ok(result).build();
    }

    /**
     * Get cluster health
     * 
     * @param indexName
     */
    @PermitAll
    @GET
    @Path("/indexhealth")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClusterhealth() throws IOException {

        String result = ElasticsearchDao.getClusterHealth();
        return Response.ok(result).build();
    }

    /**
     * Delete index by name in elasticsearch
     *
     * @param indexName
     * @return response about delete true or false
     */
    @PermitAll
    @DELETE
    @Path("/deleteindexbyname")
    public Response deleteIndexByDate(@QueryParam("indexname") String indexName) throws IOException {
        boolean result = false;
        try {
            result = ElasticsearchDao.deleteIndexByName(indexName);
        } catch (Exception e) {
            LOGGER.error(indexName + "is not exist");
        }
        return Response.ok("deleted: " + result).build();
    }
}
