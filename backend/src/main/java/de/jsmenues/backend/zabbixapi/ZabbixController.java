package de.jsmenues.backend.zabbixapi;

import de.jsmenues.redis.repository.ConfigurationRepository;
import io.github.cgi.zabbix.api.DefaultZabbixApi;
import io.github.cgi.zabbix.api.Request;
import io.github.cgi.zabbix.api.RequestBuilder;
import io.github.cgi.zabbix.api.ZabbixApi;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Path("zabbixapi")
public class ZabbixController {
    private static Logger LOGGER = LoggerFactory.getLogger(ZabbixController.class);

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
	@PermitAll
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getTestConnection() {
        ZabbixUser user = new ZabbixUser();
        ZabbixApi zabbixApi = new DefaultZabbixApi(user.getZabbixUrl());
        zabbixApi.init();
        boolean login = zabbixApi.login(user.getUsername(), user.getPassword());
        return zabbixApi.apiVersion();
    }
	@RolesAllowed("ADMIN")
    @GET
    @Path("/getAllHosts")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getAllHosts() {
        String responseText = "";
        ZabbixApi api = zabbixLogin();
        if (api == null) {
            return Response.status(505, "ZabbixLogin not successful").build();
        }
        List<String> hostOutputParams = new LinkedList<>();
        hostOutputParams.add("hostid");
        hostOutputParams.add("host");
        String filterGroup = ConfigurationRepository.getRepo().get("configuration.zabbix.filterGroup").getValue();
        if (filterGroup.isEmpty()) {


            Request hostRequest = RequestBuilder.newBuilder()
                    .method("host.get")
                    .paramEntry("output", hostOutputParams.toArray())
                    .build();
            JsonNode getResponse = api.call(hostRequest, true);
            JsonNode result = getResponse.path("result");
            return Response.ok(responseText + result.toString()).build();
        } else {
            Map<String, String[]> filter = new HashMap<>();
            filter.put("name", new String[]{filterGroup});
            List<String> outputParams = new LinkedList<>();
            outputParams.add("name");
            outputParams.add("hosts");
            Map<String, String[]> query = new HashMap<>();
            //query.put("output", hostOutputParams.toArray());
            Request hostgroupRequest = RequestBuilder.newBuilder()
                    .method("hostgroup.get")
                    .paramEntry("selectHosts", hostOutputParams.toArray())
                    .paramEntry("filter", filter)
                    .paramEntry("output", outputParams)
                    .build();
            JsonNode getResponse = api.call(hostgroupRequest, true);
            JsonNode result = getResponse.path("result");
            if (result.isArray()) {
                for (final JsonNode objNode : result) {
                    JsonNode node = objNode.get("hosts");
                    result = node;
                }
            }

            return Response.ok(responseText + result.toString()).build();
        }
    }
	@RolesAllowed("ADMIN")
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
