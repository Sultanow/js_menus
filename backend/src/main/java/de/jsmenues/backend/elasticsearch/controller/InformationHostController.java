package de.jsmenues.backend.elasticsearch.controller;

import de.jsmenues.backend.elasticsearch.dao.InformationHostDao;
import de.jsmenues.backend.zabbixservice.ZabbixService;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Path("/elasticsearch/hostInformation")
public class InformationHostController {
    private final ZabbixService zabbixService;

    private final InformationHostDao dao;

    @Inject
    public InformationHostController(ZabbixService zabbixService, InformationHostDao dao) {
        this.zabbixService = zabbixService;
        this.dao = dao;
    }

    /**
     * insert all host information from zabbix to elasticsearch
     *
     * @return list of host information
     */
    @PermitAll
    @PUT
    public Response insertAllHostInformation() {
        List<Map<String, List<Object>>> result = zabbixService.getHostInfos();
        dao.insertAllHostInformation(result);
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
    public Response getAllHostInformation() {
        List<Map<String, Object>> result = dao.getAllHostInformation();
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
    public Response getHostInformationByListOfNames(@PathParam("hostname") List<String> hostNames) {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            for (String hostName : hostNames) {
                result = dao.getHostInformationByHostName(hostName);
            }
            return Response.ok(result).build();
        } catch (Exception e) {
            // TODO: an exception should not be 200 OK.
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
    public Response getAllKeys() {
        List<String> result = dao.getAllKeys();
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
    public Response getLastValue(@PathParam("hostname") String hostName,
                                 @PathParam("itemkey") String itemKey)
            throws IOException {
        String result = dao.getLastValueByKey(hostName, itemKey);
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
        String result = dao.deleteHostInformationById(hostId);
        return Response.ok(result).build();
    }

}
