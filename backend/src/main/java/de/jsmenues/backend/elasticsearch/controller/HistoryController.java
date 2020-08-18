package de.jsmenues.backend.elasticsearch.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jsmenues.backend.elasticsearch.dao.HistoryDao;
import de.jsmenues.backend.zabbixservice.ZabbixService;

@Path("/elasticsearch")
public class HistoryController {

    private static Logger LOGGER = LoggerFactory.getLogger(HistoryController.class);

    /**
     * Insert all history records from zabbix to elasticsearch
     * 
     * @return list of history records
     */
    @PermitAll
    @POST
    @Path("/inserthistory")
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
    @Path("/gethistorybyindex")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHistoryRecordsByIndex(@QueryParam("patternindexname") String patternIndexName)
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
    @Path("/gethistorybetweentowdatum")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHistorytowdatum1(@QueryParam("unixtime1") String unixTime1,
            @QueryParam("unixtime2") String unixTime2, @QueryParam("indexname") String indexName)  {
        try {
            List<Map<String, Object>> result = HistoryDao.getHistoryRecordBetweenTowDatesByIndexName(unixTime1, unixTime2,
                    indexName);
            return Response.ok(result).build();
        } catch (Exception e) {
            return Response.ok(e.getMessage()).build();
        }
    }
}
