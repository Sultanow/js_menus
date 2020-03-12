package de.jsmenues.backend.zabbixapi;

import de.jsmenues.redis.data.Configuration;
import de.jsmenues.redis.repository.ConfigurationRepository;
import io.github.cgi.zabbix.api.DefaultZabbixApi;
import io.github.cgi.zabbix.api.Request;
import io.github.cgi.zabbix.api.RequestBuilder;
import io.github.cgi.zabbix.api.ZabbixApi;
import org.codehaus.jackson.JsonNode;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Path("zabbixapi")
public class ZabbixController {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getTestConnection() {
        ZabbixUser user = new ZabbixUser();
        ZabbixApi zabbixApi = new DefaultZabbixApi(user.getZabbixUrl());
        zabbixApi.init();
        boolean login = zabbixApi.login(user.getUsername(), user.getPassword());
        return zabbixApi.apiVersion();
    }



    @GET
    @Path("/getAllHosts")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getAllHosts() {
        String responseText = "";
        ZabbixApi api = zabbixLogin();
        if (api == null) {
            return Response.status(505, "ZabbixLogin not successful").build();
        }

        List<String> a = new LinkedList<>();
        a.add("hostid");
        a.add("host");
        Request hostRequest = RequestBuilder.newBuilder().method("host.get").paramEntry("output", a.toArray()).build();
        JsonNode getResponse = api.call(hostRequest, true);
        JsonNode result = getResponse.path("result");
        return Response.ok(responseText + result.toString()).build();
    }

    @GET
    @Path("/getInformationForHosts")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHostInfos(@QueryParam("host") List<String> hosts) {
        ZabbixApi api = zabbixLogin();
        if (api == null) {
            return Response.status(505, "ZabbixLogin not successful").build();
        }
        Map<String, String[]> filter = new HashMap<>();
        filter.put("host", hosts.toArray(new String[0]));
        Request informationRequest = RequestBuilder.newBuilder()
            .method("host.get")
            .paramEntry("filter", filter)
            .paramEntry("output", "extend")
            .paramEntry("selectItems", "extend")
            .paramEntry("selectHosts", "extend")
            .paramEntry("selectGroups", "extend")
            .build();
        JsonNode getResponse = api.call(informationRequest);
        JsonNode result = getResponse.path("result");
        return Response.ok(result.toString()).build();
    }

    private ZabbixApi zabbixLogin() {
        ZabbixUser user = new ZabbixUser();
        ZabbixApi zabbixApi = new DefaultZabbixApi(user.getZabbixUrl());
        zabbixApi.init();
        boolean loginsuccess = zabbixApi.login(user.getUsername(), user.getPassword());

        if (!loginsuccess) {
            zabbixApi = null;
        }
        return zabbixApi;

    }

}
