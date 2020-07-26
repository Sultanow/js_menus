package de.jsmenues.backend.elasticsearch.controller;

import java.io.IOException;
import javax.annotation.security.PermitAll;

import javax.ws.rs.DELETE;
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
    @POST
    @Path("/indexname")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIndicesName(@QueryParam("patternindexname") String patternIndexName) throws IOException {

        String[] result = ElasticsearchDao.getIdexName(patternIndexName);
        return Response.ok(result).build();
    }

    /**
     * Get cluster health
<<<<<<< HEAD
     * @param indexName
=======
     *
>>>>>>> 86580d5fe7e41ab365dbf0070865896f051afdf1
     * @return index status
     */
    @PermitAll
    @POST
    @Path("/indexhealth")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClusterhealth(@QueryParam("indexname") String indexName) throws IOException {

        String result = ElasticsearchDao.getClusterHealth(indexName);
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