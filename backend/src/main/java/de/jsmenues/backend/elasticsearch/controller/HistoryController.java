package de.jsmenues.backend.elasticsearch.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jsmenues.backend.elasticsearch.dao.HistoryDao;
import de.jsmenues.backend.zabbixservice.ZabbixService;

@Path("/elasticsearch/history")
public class HistoryController {

    private static Logger LOGGER = LoggerFactory.getLogger(HistoryController.class);

    /**
     * Insert all history records from zabbix to elasticsearch
     * 
     * @return list of history records
     */
    @PermitAll
    @GET
    @Path("/")
    @Produces(MediaType.TEXT_PLAIN)
    public Response insertHistory() throws IOException, ParseException {

        ZabbixService zabbixService = new ZabbixService();
        List<Map<String, Object>> result = zabbixService.getHistory();
        HistoryDao.insertHistory(result);
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

        List<Map<String, Object>> result = HistoryDao.getHistoryRecordsByIndex(patternIndexName);
        return Response.ok(result).build();
    }

    /**
     * Get history records of an index between tow dates from elasticsearch
     *
     * @param unixDate1 the first date
     * @param unixDate2 the second date
     * @param indexname
     *
     * @return list of history records between tow selected Dates
     */
    @PermitAll
    @GET
    @Path("/{unixtime1}/{unixtime2}/{indexname}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHistorytowdatum1(@PathParam("unixtime1") String unixTime1,
            @PathParam("unixtime2") String unixTime2, @PathParam("indexname") String indexName)  {
        try {
            List<Map<String, Object>> result = HistoryDao.getHistoryRecordBetweenTowDatesByIndexName(unixTime1, unixTime2,
                    indexName);
            return Response.ok(result).build();
        } catch (Exception e) {
            return Response.ok(e.getMessage()).build();
        }
    }
}
