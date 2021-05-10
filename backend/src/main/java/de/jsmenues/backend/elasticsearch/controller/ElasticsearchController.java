package de.jsmenues.backend.elasticsearch.controller;

import de.jsmenues.backend.elasticsearch.dao.ElasticsearchDao;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/elasticsearch")
public class ElasticsearchController {
    private final ElasticsearchDao dao;

    @Inject
    public ElasticsearchController(ElasticsearchDao dao) {
        this.dao = dao;
    }

    /**
     * Get indices names from elasticsearch
     *
     * @param patternIndexName index pattern example * or index name
     * @return names of indices
     */
    @PermitAll
    @GET
    @Path("/indexname/{patternindexname}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIndexNames(@PathParam("patternindexname") String patternIndexName) {
        String[] result = dao.getIndexName(patternIndexName);
        return Response.ok(result).build();
    }

    /**
     * Get all history index names
     *
     * @return names of historyindices
     */
    @PermitAll
    @GET
    @Path("/historyIndexNames")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHistoryIndexNames() {
        String[] result = dao.getIndexName("history-*");
        return Response.ok(result).build();
    }

    /**
     * Get cluster health
     */
    @PermitAll
    @GET
    @Path("/indexhealth")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClusterHealth()  {
        String result = dao.getClusterHealth();
        return Response.ok(result).build();
    }

    /**
     * Delete index by name in elasticsearch
     *
     * @param indexName The index to delete.
     * @return Whether the deletion succeeded.
     */
    @PermitAll
    @DELETE
    @Path("/{indexname}")
    public Response deleteIndexByDate(@PathParam("indexname") String indexName) {
        boolean result = dao.deleteIndexByName(indexName);
        return Response.ok("deleted: " + result).build();
    }
}
