package de.jsmenues.backend.statistic;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.jsmenues.redis.data.Configuration;
import de.jsmenues.redis.repository.ConfigurationRepository;
import org.glassfish.hk2.utilities.reflection.Logger;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Path("/statistic")
public class StatisticController {
    @GET
    @Path("/allChartNames")
    public Response getAllChartNames() {
        String chartnames = ConfigurationRepository.getRepo().get("statistic.allChartNames").getValue();
        return Response.ok(chartnames).build();
    }

    @GET
    @Path("/chartData")
    public Response getChartDataForName(
            @DefaultValue("") @QueryParam("chart") String chartName
    ) {
        if (chartName.isEmpty()) {
            return Response.ok("No data").build();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("statistic.chart.").append(chartName).append(".data");
        String chartData = ConfigurationRepository.getRepo().get(sb.toString()).getValue();
        Logger.getLogger().warning(chartData);
        Logger.getLogger().warning(sb.toString());
        return Response.ok(chartData).build();
    }

    @POST
    @Path("/updateData")
    public Response uploadNewData(
            @FormDataParam("chart") String chartName,
            @FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition fileMetaData
    ) throws IOException {
        Logger.getLogger().warning("/updateData");
        // First save the File to the tmp
        saveFileToTmpFolder(fileInputStream, fileMetaData);
        // TODO find all charts in Group to update with dataSource

        // Get script name for chartName
        StringBuilder sb_Script = new StringBuilder();
        sb_Script.append("statistic.chart.").append(chartName).append(".script");
        String scriptName = ConfigurationRepository.getRepo().get(sb_Script.toString()).getValue();
        String url = "http://python-nginx-service:80/updateData";
        Response response = sendFileDataToPythonService(url, fileMetaData.getFileName(), "script", scriptName);

        StringBuilder sbData = new StringBuilder();
        sbData.append("statistic.chart.").append(chartName).append(".data");
        if (response.getStatus() == 200) {
            String responseText = response.readEntity(String.class);
            ConfigurationRepository.getRepo().save(new Configuration(sbData.toString(), responseText));
            Logger.getLogger().warning(responseText);
            Logger.getLogger().warning(sb_Script.toString());
            Logger.getLogger().warning(ConfigurationRepository.getRepo().get(sbData.toString()).getValue());
            return Response.ok(responseText).build();
        } else {
            Logger.getLogger().warning(response.toString());
            return Response.status(401, "Could not get Response from service").build();
        }
    }

    @POST
    @Path("/createChart")
    public Response createChart(
            @FormDataParam("chartName") String chartName,
            @FormDataParam("groupName") String groupName,
            @FormDataParam("description") String description,
            @FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition fileMetaData
    ) {
        saveFileToTmpFolder(fileInputStream, fileMetaData);
        // Save chartname and scriptname to redis database
        String url = "http://python-nginx-service:80/createChart";
        Response response = sendFileDataToPythonService(url, fileMetaData.getFileName(), "", "");

        if (response.getStatus() == 200) { // Successfull placed script on Server.
            List<StatisticItem> groupsAndCharts = getGroupsAndCharts();
            if(groupsAndCharts == null)
                groupsAndCharts = new ArrayList<>();
            if (groupName.isEmpty()) groupName = "noGroup";
            boolean added = false;
            for (StatisticItem chart : groupsAndCharts) {
                if (chart.groupName.equals(groupName)) {
                    if (!chart.charts.contains(chartName)) {
                        chart.charts.add(chartName);
                    }
                    added = true;
                }
            }
            if (!added) { // The GroupName not exists, so add it and the chart.
                StatisticItem item = new StatisticItem();
                item.groupName = groupName;
                item.charts.add(chartName);
                groupsAndCharts.add(item);
            }
            // TODO Group Name usage / Description
            String responseText = response.readEntity(String.class);
            Logger.getLogger().warning(responseText);
            Gson gson = new Gson();
            ConfigurationRepository.getRepo().save(new Configuration("statistic.allChartNames", gson.toJson(groupsAndCharts)));
            StringBuilder sb = new StringBuilder();
            sb.append("statistic.chart.").append(chartName).append(".script");
            ConfigurationRepository.getRepo().save(new Configuration(sb.toString(), fileMetaData.getFileName()));
            Logger.getLogger().warning(sb.toString());
            Logger.getLogger().warning(ConfigurationRepository.getRepo().get(sb.toString()).getValue());
            return Response.ok(responseText).build();
        } else
            return Response.status(401, "Could not get Response from service").build();
    }

    @DELETE
    @Path("/deleteChart")
    public Response deleteChart(
            @DefaultValue("") @QueryParam("chart") String chartName
    ) {
        List<StatisticItem> groupsAndCharts = getGroupsAndCharts();
        StatisticItem deleteItem = null;
        for (StatisticItem item : groupsAndCharts) {
            if (item.charts.contains(chartName)) {
                item.charts.remove(chartName);
                if (item.charts.size() == 0)
                    deleteItem = item;
            }
        }
        if(deleteItem != null) {
            groupsAndCharts.remove(deleteItem);
        }
        Gson gson = new Gson();

        ConfigurationRepository.getRepo().save(new Configuration("statistic.allChartNames", gson.toJson(groupsAndCharts)));
        String itemBase = "statistic.chart." + chartName;
        ConfigurationRepository.getRepo().save(new Configuration(itemBase + ".data", ""));
        ConfigurationRepository.getRepo().save(new Configuration(itemBase + ".script", ""));

        return Response.ok().build();
    }

    private ArrayList<StatisticItem> getGroupsAndCharts() {
        String chartNames = ConfigurationRepository.getRepo().get("statistic.allChartNames").getValue();
        Gson gson = new Gson();
        ArrayList<StatisticItem> groupsAndCharts = gson.fromJson(chartNames, new TypeToken<ArrayList<StatisticItem>>() {
        }.getType());
        return groupsAndCharts;
    }

    @GET
    @Path("/groups")
    public Response getAllGroups() {
        Gson gson = new Gson();
        ArrayList<StatisticItem> statisticItems = getGroupsAndCharts();
        ArrayList<String> groupList = new ArrayList<>();
        if (statisticItems != null && statisticItems.size() != 0)
            for (StatisticItem item : statisticItems) {
                if (!item.groupName.equals("noGroup")) {
                    groupList.add(item.groupName);
                }
            }
        return Response.ok(gson.toJson(groupList)).build();
    }


    private void saveFileToTmpFolder(InputStream fileInputStream, FormDataContentDisposition fileMetaData) {
        final String UPLOAD_PATH = "/tmp/";
        try {
            int read = 0;
            byte[] bytes = new byte[1024];

            OutputStream out = new FileOutputStream(new File(UPLOAD_PATH + fileMetaData.getFileName()));
            while ((read = fileInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            throw new WebApplicationException("Error while uploading file. Please try again !!");
        }
    }

    private Response sendFileDataToPythonService(String url, String fileName, String fieldName, String fieldValue) {
        try {
            final Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
            final FileDataBodyPart filePart = new FileDataBodyPart("file", new File("/tmp/" + fileName));
            FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
            final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.field(fieldName, fieldValue).bodyPart(filePart);

            final WebTarget target = client.target(url);
            final Response response = target.request().post(Entity.entity(multipart, multipart.getMediaType()));
            formDataMultiPart.close();
            multipart.close();

            return response;
        } catch (Exception e) {
            Logger.getLogger().warning("Exception: " + e.getMessage());
            return Response.status(401).build();
        }
    }
}
