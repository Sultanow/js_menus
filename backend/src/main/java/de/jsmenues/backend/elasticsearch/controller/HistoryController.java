package de.jsmenues.backend.elasticsearch.controller;

import de.jsmenues.backend.elasticsearch.dao.HistoryDao;
import de.jsmenues.backend.zabbixservice.ZabbixService;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Path("/elasticsearch/history")
public class HistoryController {

    private final ZabbixService zabbixService;

    private final HistoryDao historyDao;

    @Inject
    public HistoryController(ZabbixService zabbixService, HistoryDao historyDao) {
        this.zabbixService = zabbixService;
        this.historyDao = historyDao;
    }

    /**
     * Insert all history records from zabbix to elasticsearch
     * 
     * @return list of history records
     */
    @PermitAll
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response insertHistory() {
        List<Map<String, Object>> result = zabbixService.getHistory();
        historyDao.insertHistory(result);
        return Response.ok(result).build();
    }

    /**
     * Get history records by index from elasticsearch
     *
     * @param patternIndexName index pattern for selected indices or index name
     *
     * @return list of history records
     */
    @PermitAll
    @POST
    @Path("/{patternindexname}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHistoryRecordsByIndex(@PathParam("patternindexname") String patternIndexName)
            throws IOException {
        List<Map<String, Object>> result = historyDao.getHistoryRecordsByIndex(patternIndexName);
        return Response.ok(result).build();
    }

    /**
     * Get history records of an index between tow dates from elasticsearch
     *
     * @param startTime the first date
     * @param endTime the second date
     * @param indexName The index to search in
     *
     * @return list of history records between tow selected Dates
     */
    @PermitAll
    @GET
    @Path("/{unixtime1}/{unixtime2}/{indexname}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHistoryBetweenDates(@PathParam("unixtime1") long startTime,
                                           @PathParam("unixtime2") long endTime,
                                           @PathParam("indexname") String indexName)  {
        try {
            List<Map<String, Object>> result = historyDao.getHistoryRecordsBetweenDates(startTime, endTime,
                    indexName);
            return Response.ok(result).build();
        } catch (Exception e) {
            return Response.ok(e.getMessage()).build();
        }
    }
}
