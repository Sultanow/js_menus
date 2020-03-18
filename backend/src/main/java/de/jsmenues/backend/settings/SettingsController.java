package de.jsmenues.backend.settings;

import com.google.gson.Gson;
import de.jsmenues.backend.zabbixapi.ZabbixUser;
import de.jsmenues.redis.data.Configuration;
import de.jsmenues.redis.repository.ConfigurationRepository;

import javax.annotation.PostConstruct;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Path("/settings")
public class SettingsController {
    private ArrayList<String> allConfigurationItems = new ArrayList<>(Arrays.asList(
            "configuration.zabbix.User",
            "configuration.zabbix.Password",
            "configuration.zabbix.URL",
            "configuration.frontend.title",
            "configuration.frontend.logo"));



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
            @DefaultValue("") @QueryParam("zabbixPassword") String pass,
            @DefaultValue("") @QueryParam("zabbixURL") String url) {
        ZabbixUser zabbixUser = new ZabbixUser(user, pass, url);
        zabbixUser.saveUser();

        return Response.ok().build();
    }

    @GET
    @Path("/title")
    public Response getTitle() {
        String siteTitle = ConfigurationRepository.getRepo().get("configuration.frontend.title").getValue();

        return Response.ok(siteTitle).build();
    }

    @POST
    @Path("/title")
    public Response setTitle(String title) {
        ConfigurationRepository.getRepo().save(new Configuration("configuration.frontend.title", title));
        return Response.ok().build();
    }

    @GET
    @Path("/logo")
    public Response getLogoSVG() {
        String siteLogo = ConfigurationRepository.getRepo().get("configuration.frontend.logo").getValue();
        return Response.ok(siteLogo).build();
    }

    @POST
    @Path("/logo")
    public Response setLogo(String logo) {
        ConfigurationRepository.getRepo().save(new Configuration("configuration.frontend.logo", logo));
        return Response.ok().build();
    }

    @GET
    @Path("/getAllConfig")
    public Response getAllConfig() {
        List<Configuration> items = ConfigurationRepository.getRepo().getAll(allConfigurationItems);
        Gson jsonItems = new Gson();
        String returnItems = jsonItems.toJson(items);
        return Response.ok(returnItems,MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Path("/setConfig")
    public Response setConfig(String config) {
        Gson gson = new Gson();
        //gson.fromJson(config, Configuration.class);
        return Response.ok().build();
    }
}
