package de.jsmenues.backend.zabbixapi;

import de.jsmenues.redis.data.Configuration;
import de.jsmenues.redis.repository.ConfigurationRepository;
import io.github.cgi.zabbix.api.DefaultZabbixApi;
import io.github.cgi.zabbix.api.Request;
import io.github.cgi.zabbix.api.RequestBuilder;
import io.github.cgi.zabbix.api.ZabbixApi;
import org.codehaus.jackson.JsonNode;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
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
    
    //Map<String, String[]> filter = new HashMap<>();
    //{"jsonrpc":"2.0","method":"apiinfo.version","id":1,"auth":null,"params":{}}
    //filter.put("host", new String[] { "dev1" });
    //Request getRequest = RequestBuilder.newBuilder().method("host.get")
    //.paramEntry("filter", filter).build();
    //JsonNode getResponse = zabbixApi.call(getRequest);
    //return getResponse.toString();

}
