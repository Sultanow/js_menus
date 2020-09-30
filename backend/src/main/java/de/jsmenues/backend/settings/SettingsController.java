package de.jsmenues.backend.settings;

import com.google.gson.Gson;
import de.jsmenues.backend.zabbixapi.ZabbixUser;
import de.jsmenues.redis.data.Configuration;
import de.jsmenues.redis.repository.ConfigurationRepository;
import de.jsmenues.redis.repository.IConfigurationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Path("/settings")
public class SettingsController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsController.class);

    private static ArrayList<String> allConfigurationItems = new ArrayList<>(Arrays.asList("configuration.zabbix.User",
            "configuration.zabbix.Password", "configuration.zabbix.URL", "configuration.zabbix.filterGroup",
            "configuration.zabbix.items", "configuration.frontend.title", "configuration.frontend.logo",
            "configuration.dummy.statuswarning", "configuration.servercompare.config", "configuration.activeitems", 
            "configuration.delete.history.data"));

    @RolesAllowed("ADMIN")
    @GET
    @Path("/getZabbixConfig")
    @Produces(MediaType.TEXT_PLAIN)
    public String getConfigOfZabbix() {
        ZabbixUser user = new ZabbixUser();
        return user.toString();
    }

    @RolesAllowed("ADMIN")
    @GET
    @Path("/setZabbixConfig")
    public Response setZabbixConfig(@DefaultValue("") @QueryParam("zabbixUser") String user,
            @DefaultValue("") @QueryParam("zabbixPassword") String pass,
            @DefaultValue("") @QueryParam("zabbixURL") String url) {
        ZabbixUser zabbixUser = new ZabbixUser(user, pass, url);
        zabbixUser.saveUser();

        return Response.ok().build();
    }

    @PermitAll
    @GET
    @Path("/title")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getTitle() {
        String siteTitle = ConfigurationRepository.getRepo().get("configuration.frontend.title").getValue();

        return Response.ok(siteTitle).build();
    }

    @RolesAllowed("ADMIN")
    @POST
    @Path("/title")
    public Response setTitle(String title) {
        ConfigurationRepository.getRepo().save(new Configuration("configuration.frontend.title", title));
        return Response.ok().build();
    }

    @PermitAll
    @GET
    @Path("/logo")
    public Response getLogoSVG() {
        String siteLogo = ConfigurationRepository.getRepo().get("configuration.frontend.logo").getValue();
        return Response.ok(siteLogo).build();
    }

    @RolesAllowed("ADMIN")
    @POST
    @Path("/logo")
    public Response setLogo(String logo) {
        ConfigurationRepository.getRepo().save(new Configuration("configuration.frontend.logo", logo));
        return Response.ok().build();
    }

    @RolesAllowed("ADMIN")
    @GET
    @Path("/getAllConfig")
    public Response getAllConfig() {
        List<Configuration> items = ConfigurationRepository.getRepo().getAll(allConfigurationItems);
        Gson jsonItems = new Gson();
        String returnItems = jsonItems.toJson(items);
        return Response.ok(returnItems, MediaType.APPLICATION_JSON).build();
    }

    @RolesAllowed("ADMIN")
    @POST
    @Path("/setConfig")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setConfig(SettingItem[] config) {
        LOGGER.info("Size of config: " + config.length);
        IConfigurationRepository repo = ConfigurationRepository.getRepo();
        for (SettingItem settingItem : config) {
            LOGGER.error(settingItem.toString());
            repo.save(new Configuration(settingItem.getKey(), settingItem.getValue()));
        }
        return Response.ok().build();
    }

    @PermitAll
    @GET
    @Path("/dummyStatusWarnings")
    public Response getDummyStatusWarnings() {
        String value = ConfigurationRepository.getRepo().get("configuration.dummy.statuswarning").getValue();
        return Response.ok(value).build();
    }

    @PermitAll
    @GET
    @Path("/servercompareconfig")
    public Response getServerCompareConfig() {
        String value = ConfigurationRepository.getRepo().get("configuration.servercompare.config").getValue();
        return Response.ok(value).build();
    }

    @PermitAll
    @GET
    @Path("/version")
    public Response getVersion() {

        final Properties properties = new Properties();
        String version = "no version";
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream("project.properties"));
            version = properties.getProperty("version");
        } catch (IOException e) {
        }
        return Response.ok(version).build();
    }

    @PermitAll
    @GET
    @Path("/activeItems")
    public Response getActiveItems() {
        String value = ConfigurationRepository.getRepo().get("configuration.activeitems").getValue();
        return Response.ok(value).build();
    }
}
