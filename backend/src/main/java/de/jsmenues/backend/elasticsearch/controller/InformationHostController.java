package de.jsmenues.backend.elasticsearch.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

import de.jsmenues.backend.elasticsearch.dao.InformationHostDao;
import de.jsmenues.backend.zabbixservice.ZabbixService;

@Path("/elasticsearch")
public class InformationHostController {

    private static Logger LOGGER = LoggerFactory.getLogger(InformationHostController.class);

    /**
     * insert all host information from zabbix to elasticsearch
     *
     * @return list of host information
     */
    @PermitAll
    @POST
    @Path("/inserthostinformation")
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
    @Path("/getallhostinformation")
    public Response getAllHostInformation() throws IOException {

        List<Map<String, List<Object>>> result = InformationHostDao.getAllHostInformation();
        return Response.ok(result).build();
    }

    /**
     * Get  host information from elasticsearch
     *
     * @param hostNames list of hostName
     * @return list of host information
     */
    @PermitAll
    @GET
    @Path("/getHostInformationByListOfNames")
    @Produces(MediaType.APPLICATION_JSON)
    public Response gethostinformationByListOfNames(@QueryParam("hostname") List<String> hostNames) {
        List<Map<String, List<Object>>> result = new ArrayList<Map<String, List<Object>>>();
        try {
            for (String hostName : hostNames) {
                Map<String, List<Object>> map = InformationHostDao.getHostInformationByHostName(hostName);
                result.add(map);
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
    @Path("/getAllKeys")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllKeys() throws IOException {
        List<String> result = InformationHostDao.getAllKeys();
        return Response.ok(result).build();
    }

    /**
     * Get last value's an item by key and hostname
     *
     * @param hostName
     * @param key
     * @return last value
     */
    @PermitAll
    @GET
    @Path("/getLastValue")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getLastValue(@QueryParam("hostname") String hostName, @QueryParam("itemkey") String itemKey)
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
    @Path("/deletehostinformation")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteHostById(@QueryParam("hostid") String hostId) throws IOException {

        String result = InformationHostDao.deleteHostInformationById(hostId);
        return Response.ok(result).build();
    }

}
