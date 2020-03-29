package de.jsmenues.backend.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jsmenues.backend.zabbixAPI.ZabbixUser;
import de.jsmenues.redis.data.Configuration;
import de.jsmenues.redis.repository.ConfigurationRepository;


@Path("/settings")
public class SettingsController {
    private static final Logger logger = LoggerFactory.getLogger(SettingsController.class);

             private static ArrayList<String> allConfigurationItems = new ArrayList<>(Arrays.asList(
                     "configuration.zabbix.User",
                     "configuration.zabbix.Password",
                     "configuration.zabbix.URL",
                     "configuration.zabbix.filterGroup",
                     "configuration.zabbix.items",
                     "configuration.frontend.title",
                     "configuration.frontend.logo",
                     "configuration.dummy.statuswarning",
                     "configuration.servercompare.config"));

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
             @Produces(MediaType.TEXT_PLAIN)
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
                             return Response.ok(returnItems, MediaType.APPLICATION_JSON).build();
             }

             @POST
             @Path("/setConfig")
             @Consumes(MediaType.APPLICATION_JSON)
             public Response setConfig(SettingItem[] config) {
                             logger.info("Size of config: " + config.length);
                             ConfigurationRepository repo = ConfigurationRepository.getRepo();
                             for (SettingItem settingItem : config) {
                                             logger.error(settingItem.toString());
                                             repo.save(new Configuration(settingItem.getKey(), settingItem.getValue()));
                             }
                             return Response.ok().build();
             }

             @GET
             @Path("/dummyStatusWarnings")
             public Response getDummyStatusWarnings() {
                     String value = ConfigurationRepository.getRepo().get("configuration.dummy.statuswarning").getValue();
                     return Response.ok(value).build();
             }

             @GET
             @Path("/servercompareconfig")
             public Response getServerCompareConfig() {
                     String value = ConfigurationRepository.getRepo().get("configuration.servercompare.config").getValue();
                     return Response.ok(value).build();
             }
}
