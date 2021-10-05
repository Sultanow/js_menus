package de.jsmenues.backend.elasticsearch.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.jsmenues.backend.elasticsearch.dao.HostsDao;
import de.jsmenues.backend.zabbixservice.ZabbixService;

@Path("/elasticsearch/hosts")
public class HostController {
    private final ZabbixService zabbixService;

    private final HostsDao dao;

    @Inject
    public HostController(ZabbixService zabbixService, HostsDao dao) {
        this.zabbixService = zabbixService;
        this.dao = dao;
    }

    /**
     * insert all hosts from zabbix to elasticsearch
     *
     * @return list of hosts
     */
    @PermitAll
    @PUT
    public Response InsertAllHosts() throws IOException {
        List<Map<String, Object>> result = zabbixService.getAllHosts();
        dao.insertAllHosts(result);
        return Response.ok().build();
    }

    /**
     * Get all hosts from elasticsearch
     *
     * @return list of hosts
     */
    @PermitAll
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllHosts() throws IOException {
        List<Map<String, Object>> result = dao.getAllHosts();
        return Response.ok(result).build();
    }

    /**
     * Get all host names
     *
     * @return list of hsotnames
     */
    @PermitAll
    @GET
    @Path("/hostnames")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllHostName() {

        List<String> result = dao.getAllHostName();
        return Response.ok(result).build();
    }

    /**
     * Delete host by id from elasticsearch
     *
     * @param hostId
     * @return response about delete request
     */
    @PermitAll
    @DELETE
    @Path("/{hostid}")
    public Response deleteHostByID(@PathParam("hostid") String hostId) throws IOException {
        String result1 = dao.deleteHostById(hostId);
        String result2 = dao.deleteHostInfoById(hostId);
        return Response.ok(result1 + "\n" + result2).build();
    }

}
