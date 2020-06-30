package de.jsmenues.backend.statistic;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.InputStream;


@Path("/statistic")
public class StatisticController {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticController.class);

    private static StatisticService statisticService = new StatisticService();

    /**
     * Returns all the chart names and the groups.
     *
     * @return Chart names and groups.
     */
    @PermitAll
    @GET
    @Path("/allChartNames")
    public Response getAllChartNames() {
        LOGGER.warn("Try get ChartNames");
        String chartNames = statisticService.getAllChartNames();
        return Response.ok(chartNames).build();
    }

    /**
     * Request the chartData for a specific chart
     *
     * @param chartName Name for the requested chart
     * @return The chart data locally cached.
     */
    @PermitAll
    @GET
    @Path("/chartData")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChartDataForName(
            @DefaultValue("") @QueryParam("chart") String chartName
    ) {
        if (chartName.isEmpty()) {
            return Response.ok("No data").build();
        }
        String chartData = statisticService.getChartDataForName(chartName);
        return Response.ok(chartData).build();
    }

    /**
     * Update chart data. The data file is sent to the python service.
     * TODO Check why MediaType making problems which end in 404 Errors...
     * 
     * @param chartName       Name of the chart to update
     * @param fileInputStream File data as stream
     * @param fileMetaData    File meta data containing the filename
     * @return Success information
     */
    @RolesAllowed("ADMIN")
    @POST
    @Path("/updateData")
    //@Consumes(MediaType.MULTIPART_FORM_DATA)
    //@Produces(MediaType.APPLICATION_JSON)
    public Response uploadNewData(
            @FormDataParam("chart") String chartName,
            @FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition fileMetaData
    ) {
        LOGGER.debug("Enter [POST] '/updateData'");
        boolean result = statisticService.updateData(chartName, fileInputStream, fileMetaData);
        Response response;
        if(!result) {
            response = Response.status(401, "Error on update").build();
        } else {
            response = Response.ok().build();
        }
        LOGGER.debug("Leave [POST] '/updateData'");
        return response;
    }

    /**
     * Call this to create a new chart.
     * <p>
     * TODO: Charts should be created only if the user is authenticated! For this the password service have to be implemented.
     *
     * @param chartName       new chartname
     * @param groupName       group for the chart
     * @param description     description of the chart
     * @param fileInputStream python file stream
     * @param fileMetaData    python file metadata
     * @return response if the create was successful.
     */
    @RolesAllowed("ADMIN")
    @POST
    @Path("/createChart")
    public Response createChart(
            @FormDataParam("chartName") String chartName,
            @FormDataParam("groupName") String groupName,
            @FormDataParam("description") String description,
            @FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition fileMetaData
    ) {
        LOGGER.debug("Enter [POST] '/createChart'");
        Response response = statisticService.createChart(chartName, groupName, description, fileInputStream, fileMetaData);
        LOGGER.debug("Leave [POST] '/createChart'");
        return response;
    }

    /**
     * This can be called to delete a chart from the Service.
     * <p>
     * TODO: Charts should be deleted only if the user is authenticated! For this the password service have to be implemented.
     *
     * @param chartName Chartname to delete from the repository
     * @return boolean as JSON which shows the success of the delete
     */
    @RolesAllowed("ADMIN")
    @DELETE
    @Path("/deleteChart")
    public Response deleteChart(
            @DefaultValue("") @QueryParam("chart") String chartName
    ) {
        LOGGER.debug("Enter [DELETE] '/deleteChart'");
        statisticService.deleteChart(chartName);
        LOGGER.debug("Leave [DELETE] '/deleteChart'");
        return Response.ok().build();
    }

    /**
     * All Statistic groups will be returned.
     *
     * @return The groupList JSON encoded string.
     */
	@PermitAll
    @GET
    @Path("/groups")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllGroups() {
        LOGGER.debug("Enter [GET] '/groups'");
        String groupNames = statisticService.getAllGroupNames();
        LOGGER.debug("Leave [GET] '/groups'");
        return Response.ok(groupNames).build();
    }
}
