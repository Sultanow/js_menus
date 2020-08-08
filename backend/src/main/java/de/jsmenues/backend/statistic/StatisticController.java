package de.jsmenues.backend.statistic;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Path("/statistic")
public class StatisticController {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticController.class);

    private StatisticService statisticService;

    @Inject
    public StatisticController(StatisticService service) {
        this.statisticService = service;
    }

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
     * Request the chartData for a specific chart, date or get only an update one for a date
     *
     * @param chartName Name for the requested chart
     * @param startDate Start Date for the timeseries information
     * @param endDate   End Date for the timeseries information
     * @param update    If "true" then only the trace information will send back to the caller.
     * @return The chart data locally cached.
     */
    @PermitAll
    @GET
    @Path("/chartData")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChartDataForName(
            @QueryParam("chart") String chartName,
            @QueryParam("startdate") String startDate,
            @QueryParam("enddate") String endDate,
            @DefaultValue("false") @QueryParam("update") String update
    ) {
        if (null == chartName || chartName.isEmpty()) {
            return Response.ok("No data").build();
        }
        Map<String, String> dates = new HashMap<>();
        if (null != startDate && !startDate.isEmpty()) {
            dates.put("start", startDate);
        }
        if (null != endDate && !endDate.isEmpty()) {
            dates.put("end", endDate);
        }
        Response response;
        if( dates.size() == 1 && dates.containsKey("end")) {
            // Only Enddate is set without start date. So send response 401
            response = Response.status(400, "Missing User input Startdate for given Enddate").build();
        } else {
            try {
                String chartData = statisticService.getChartDataForName(chartName, dates, "true".equals(update));
                response = Response.ok(chartData).build();
            } catch (IllegalArgumentException e) {
                response = Response.status(400, "Start- and/or End-Date not valid").build();
            }
        }
        return response;
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
    @PermitAll
    @POST
    @Path("/updateData")
    public Response uploadNewData(
            @FormDataParam("chart") String chartName,
            @FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition fileMetaData
    ) {
        LOGGER.debug("Enter [POST] '/updateData'");
        boolean result = statisticService.updateData(chartName, fileInputStream, fileMetaData);
        Response response;
        if (!result) {
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
     * TODO: Charts should be created only if the user is authenticated! For this
     * the password service have to be implemented.
     *
     * @param chartName       new chartname
     * @param groupName       group for the chart
     * @param description     description of the chart
     * @param fileInputStream python file stream
     * @param fileMetaData    python file metadata
     * @return response if the create was successful.
     */
    @PermitAll
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
        Response response;
        if (chartName == null || chartName.isEmpty() || fileInputStream == null) {
            response = Response.status(400, "Missing user input data").build();
        } else {
            response = statisticService.createChart(chartName, groupName, description, fileInputStream, fileMetaData);
        }
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
    @PermitAll
    @DELETE
    @Path("/deleteChart")
    public Response deleteChart(
            @DefaultValue("") @QueryParam("chart") String chartName
    ) {
        LOGGER.debug("Enter [DELETE] '/deleteChart'");
        if (!chartName.isEmpty()) {
            statisticService.deleteChart(chartName);
        }
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

    /**
     * All Statistic dates available for a timeseries chart
     *
     * @param chartName Name for the chart
     * @return list of all dates data is available
     */
    @PermitAll
    @GET
    @Path("/timeseriesDates")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTimeseriesDates(
            @QueryParam("chart") String chartName
    ) {
        LOGGER.debug("Enter [GET] '/timeseriesDates'");
        if (null == chartName || chartName.isEmpty()) {
            return Response.ok("").build();
        }
        String timeseriesDates = statisticService.getTimeseriesDates(chartName);
        LOGGER.debug("Leave [GET] '/timeseriesDates'");
        return Response.ok(timeseriesDates).build();
    }
}
