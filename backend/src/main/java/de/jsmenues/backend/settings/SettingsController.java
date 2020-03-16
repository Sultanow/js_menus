package de.jsmenues.backend.settings;

import de.jsmenues.backend.zabbixapi.ZabbixUser;
import de.jsmenues.redis.data.Configuration;
import de.jsmenues.redis.repository.ConfigurationRepository;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/settings")
public class SettingsController {
    @GET
    @Path("/getZabbixConfig")
    @Produces(MediaType.TEXT_PLAIN)
    public String getConfigOfZabbix() {
        ZabbixUser user = new ZabbixUser();
        return user.toString();
    }

    @GET
    @Path("/setZabbixConfig")
    public Response setZabbixConfig(
            @DefaultValue("") @QueryParam("zabbixUser") String user,
            @DefaultValue("") @QueryParam("zabbixPass") String pass,
            @DefaultValue("") @QueryParam("zabbixURL") String url) {
        ZabbixUser zabbixUser = new ZabbixUser(user, pass, url);
        zabbixUser.saveUser();

        return Response.ok().build();
    }

    @GET
    @Path("/title")
    public Response getTitle() {
        String siteTitle = ConfigurationRepository.getRepo().get("configuration.title").getValue();

        return Response.ok(siteTitle).build();
    }

    @POST
    @Path("/title")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response setTitle(String title) {
        ConfigurationRepository.getRepo().save(new Configuration("configuration.title", title));
        return Response.ok().build();
    }
}
