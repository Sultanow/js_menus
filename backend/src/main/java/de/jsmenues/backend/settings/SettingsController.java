package de.jsmenues.backend.settings;

import de.jsmenues.backend.zabbixapi.ZabbixUser;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/settings")
public class SettingsController {
    @GET
    @Path("/getConfig")
    @Produces(MediaType.TEXT_PLAIN)
    public String getConfigOfZabbix() {
        ZabbixUser user = new ZabbixUser();
        return user.toString();
    }

    @GET
    @Path("/setConfig")
    public Response setZabbixConfig(
            @DefaultValue("") @QueryParam("zabbixUser") String user,
            @DefaultValue("") @QueryParam("zabbixPass") String pass,
            @DefaultValue("") @QueryParam("zabbixURL") String url) {
        ZabbixUser zabbixUser = new ZabbixUser(user, pass, url);
        zabbixUser.saveUser();

        return Response.ok().build();
    }
}
