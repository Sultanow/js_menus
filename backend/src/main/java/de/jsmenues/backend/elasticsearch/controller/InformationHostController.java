package de.jsmenues.backend.elasticsearch.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.security.PermitAll;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jsmenues.backend.elasticsearch.dao.InformationHostDao;
import de.jsmenues.backend.zabbixservice.ZabbixService;

@Path("/elasticsearch/hostInformation")
public class InformationHostController {
    static ObjectMapper objectMapper = new ObjectMapper();
    private static Logger LOGGER = LoggerFactory.getLogger(InformationHostController.class);

    /**
     * insert all host information from zabbix to elasticsearch
     *
     * @return list of host information
     */
    @PermitAll
    @PUT
    public Response insertAllHostInformation() throws IOException, ParseException {

        ZabbixService zabbixService = new ZabbixService();
        List<Map<String, List<Object>>> result = zabbixService.getHostInfos();
        InformationHostDao.insertAllHostInformation(result);
        return Response.ok(result).build();
    }

    /**
     * Get all host information from elasticsearch
     *
     * @return list of host information
     */
    @PermitAll
    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllHostInformation() throws IOException {

        List<Map<String, Object>> result = InformationHostDao.getAllHostInformation();

        return Response.ok(result).build();
    }

    /**
     * Get host information from elasticsearch
     *
     * @param hostNames list of hostName
     * @return list of host information
     */
    @PermitAll
    @GET
    @Path("/{hostname}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response gethostinformationByListOfNames(@PathParam("hostname") List<String> hostNames) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        try {
            for (String hostName : hostNames) {
                result = InformationHostDao.getHostInformationByHostName(hostName);

            }
            return Response.ok(result).build();
        } catch (Exception e) {
            return Response.ok(e.getMessage()).build();
        }
    }

    /**
     * Get all keys from elasticsearch
     *
     * @return list of keys
     */
    @PermitAll
    @GET
    @Path("/all/keys")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllKeys() throws IOException {
        List<String> result = InformationHostDao.getAllKeys();
        return Response.ok(result).build();
    }

    /**
     * Get last value's an item by key and hostname
     *
     * @param hostName
     * @param itemKey
     * @return last value
     */
    @PermitAll
    @GET
    @Path("/lastValue/{hostname}/{itemkey}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getLastValue(@PathParam("hostname") String hostName, @PathParam("itemkey") String itemKey)
            throws IOException {
        String result = InformationHostDao.getLastValuByKey(hostName, itemKey);
        return Response.ok(result).build();
    }

    /**
     * Delete host information by host id
     *
     * @param hostId
     * @return response about delete request
     */
    @PermitAll
    @DELETE
    @Path("/{hostid}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteHostById(@PathParam("hostid") String hostId) throws IOException {

        String result = InformationHostDao.deleteHostInformationById(hostId);
        return Response.ok(result).build();
    }

}
