package de.jsmenues.backend.zabbixapi;

import de.jsmenues.redis.data.Configuration;
import de.jsmenues.redis.repository.ConfigurationRepository;
import io.github.cgi.zabbix.api.DefaultZabbixApi;
import io.github.cgi.zabbix.api.Request;
import io.github.cgi.zabbix.api.RequestBuilder;
import io.github.cgi.zabbix.api.ZabbixApi;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

@Path("zabbixapi")
public class ZabbixController {
    private static Logger LOGGER = LoggerFactory.getLogger(ZabbixController.class);

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

        List<String> outputParams = new LinkedList<>();
        outputParams.add("hostid");
        outputParams.add("host");

        String filterGroup = ConfigurationRepository.getRepo().get("configuration.zabbix.filterGroup").getValue();
        Map<String, String[]> filter = new HashMap<>();
        filter.put("hostgroup", new String[] { filterGroup });
        Request hostRequest = RequestBuilder.newBuilder()
                .method("host.get")
                .paramEntry("output", outputParams.toArray())
                .paramEntry("filter", filter)
                .build();
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
        String[] output = {"name", "hostid", "host"};
        String[] selectItems = {"itemid", "key_", "name", "prevvalue", "lastvalue", "lastclock", "description"};
        Request informationRequest = RequestBuilder.newBuilder()
            .method("host.get")
            .paramEntry("filter", filter)
            .paramEntry("output", output)
            .paramEntry("selectItems", selectItems)
            .paramEntry("selectHosts", "extend")
            .paramEntry("selectGroups", "extend")
            .build();
        JsonNode getResponse = api.call(informationRequest);
        JsonNode result = getResponse.path("result");
        removeNotNeededInformationFromResult(result);
        return Response.ok(result.toString()).build();
    }

    private void removeNotNeededInformationFromResult(JsonNode information) {
        String item = ConfigurationRepository.getRepo().get("configuration.zabbix.items").getValue();
        String[] items = item.split(",");
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
