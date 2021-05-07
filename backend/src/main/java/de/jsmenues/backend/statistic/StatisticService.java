package de.jsmenues.backend.statistic;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.jsmenues.redis.data.Configuration;
import de.jsmenues.redis.repository.ConfigurationRepository;
import de.jsmenues.redis.repository.IConfigurationRepository;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.*;

/**
 * Statistic Service for communication with the python service and local caching in the redis db
 */
@Singleton
class StatisticService {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticService.class);
    // Use the traefik container directly cause the DNS Server is still Docker.
    // TODO make the Server configurable
    private static final String UPDATE_URL = "http://traefik-service:81/update";
    private static final String CREATE_URL = "http://traefik-service:81/save";

    private static final String LOCAL_UPLOAD_PATH = "/tmp/";

    private final Gson gson = new Gson();

    private final HttpClient httpClient;

    private final IConfigurationRepository configurationRepository;

    @Inject
    StatisticService(HttpClient httpClient, IConfigurationRepository configurationRepository) {
        this.httpClient = httpClient;
        this.configurationRepository = configurationRepository;
    }

    /**
     * Get all chart names.
     *
     * @return the chart and group information as json
     */
    public String getAllChartNames() {
        return gson.toJson(this.getGroupsAndCharts());
    }

    /**
     * Update Data for all charts in the group of the updated chartName.
     * Send the request to the python-service
     * TODO Send requests in parallel.
     *
     * @param chartName       Name of the chart to update
     * @param fileInputStream File data as stream
     * @param fileMetaData    File meta data containing the filename
     * @return Success information
     */
    public boolean updateData(String chartName, InputStream fileInputStream, FormDataContentDisposition fileMetaData) {
        // First save the File to the tmp
        if (chartName == null || chartName.isEmpty() || fileInputStream == null || fileMetaData == null)
            return false;
        LOGGER.warn("Update Data for " + chartName);
        String filename = saveFileToTmpFolder(fileInputStream, fileMetaData);
        List<StatisticGroup> groups = this.getGroupsAndCharts();
        for (StatisticGroup group : groups) {
            if (group.charts.containsKey(chartName)) {
                boolean result = true;
                if ("noGroup".equals(group.groupName)) {
                    result = updateSingleChart(chartName, group.charts.get(chartName), filename);
                } else {
                    for (Map.Entry<String, StatisticChart> chart : group.charts.entrySet()) {
                        result = updateSingleChart(chart.getKey(), chart.getValue(), filename);
                        if (!result)
                            return result;
                    }
                }
                return result;
            }
        }
        return false;
    }

    private boolean updateSingleChart(String chartName, StatisticChart statisticChart, String filename) {
        String scriptname = statisticChart.getScriptName();
        LOGGER.warn("ScriptName: " + scriptname);
        if (null == scriptname || scriptname.isEmpty() || "null".equals(scriptname)) {
            scriptname = updateScriptNameLocation(chartName);
        }
        Response response = httpClient.sendFileDataToPythonService(UPDATE_URL, filename, "script", scriptname);
        if (response.getStatus() == 200) {
            String responseText = response.readEntity(String.class);
            LOGGER.warn("Response Text: " + responseText);
            if (responseText.isEmpty())
                return false;

            StatisticInterface statisticInterface = gson.fromJson(responseText, StatisticInterface.class);

            if (statisticInterface.timeseries) {
                // TODO better converting
                this.saveDataForChart(chartName, "title", statisticInterface.title);
                this.saveDataForChart(chartName, "layout", gson.toJson(statisticInterface.layout));
                this.saveDataForChart(chartName, "updateTime", statisticInterface.updateTime);
                this.saveChartOptions(chartName, statisticInterface.options);
                String json = gson.toJson(statisticInterface.traces);
                List<StatisticTimeTrace> timeTrace = gson.fromJson(json, new TypeToken<ArrayList<StatisticTimeTrace>>() {
                }.getType());
                LOGGER.warn(timeTrace.toString());
                String savedTimes = this.getDataForChart(chartName, "savedTimes");
                Set<String> savedTraceTimes = gson.fromJson(savedTimes, new TypeToken<Set<String>>() {
                }.getType());
                if (savedTraceTimes == null) {
                    savedTraceTimes = new HashSet<>();
                }
                for (StatisticTimeTrace trace : timeTrace) {
                    this.saveChartData(chartName, trace.getTime(), gson.toJson(trace.getTimetraces()));
                    savedTraceTimes.add(trace.getTime());
                }
                this.saveDataForChart(chartName, "savedTimes", gson.toJson(savedTraceTimes));
            } else {
                this.saveChartData(chartName, "", responseText);
            }
            this.updateChartMeta(chartName, statisticInterface.timeseries, statisticInterface.multiple, statisticInterface.accuracy);
            return true;
        } else {
            LOGGER.warn("Status is not 200: " + response);
            return false;
        }
    }

    private String getDataForChart(String chartName, String part) {
        return ConfigurationRepository.getRepo().getVal("statistic.chart." + chartName + "." + part);
    }

    private void saveDataForChart(String chartName, String part, String savedTraceTimes) {
        ConfigurationRepository.getRepo().save(new Configuration("statistic.chart." + chartName + "." + part, savedTraceTimes));
    }


    /**
     * Get a list of all group names
     *
     * @return List of group names.
     */
    public String getAllGroupNames() {
        List<StatisticGroup> statisticGroups = getGroupsAndCharts();
        ArrayList<String> groupList = new ArrayList<>();
        if (statisticGroups != null && !statisticGroups.isEmpty()) {
            for (StatisticGroup item : statisticGroups) {
                if (!item.groupName.equals("noGroup") && !groupList.contains(item.groupName)) {
                    groupList.add(item.groupName);
                }
            }
        }
        return gson.toJson(groupList);
    }

    public void deleteChart(String chartName) {
        List<StatisticGroup> groupsAndCharts = getGroupsAndCharts();
        if (groupsAndCharts != null) {
            StatisticGroup deleteItem = null;
            for (StatisticGroup item : groupsAndCharts) {
                if (item.charts.containsKey(chartName)) {
                    item.charts.remove(chartName);
                    if (item.charts.size() == 0)
                        deleteItem = item;
                }
            }
            if (deleteItem != null) {
                groupsAndCharts.remove(deleteItem);
            }
            this.saveGroupsAndCharts(groupsAndCharts);

            this.saveScriptName(chartName, "");
            this.saveChartData(chartName, "", "");

            // TODO delete timeseries data
        }
    }

    /**
     * Create a chart. For this upload the script to the python service.
     *
     * @param chartName       Name of the new chart
     * @param groupName       Name of the Group
     * @param description     A description of the chart
     * @param fileInputStream Script file as input stream
     * @param fileMetaData    Script file meta data
     * @return The success status as Response
     */
    public Response createChart(String chartName, String groupName, String description, InputStream fileInputStream, FormDataContentDisposition fileMetaData) {
        if (null != chartName && !chartName.isEmpty() && !existChart(chartName)) {
            String filename = saveFileToTmpFolder(fileInputStream, fileMetaData);
            // Save chartname and scriptname to redis database
            Response response = httpClient.sendFileDataToPythonService(CREATE_URL, filename, "", "");

            if (response.getStatus() == 200) { // Successfull placed script on Server.
                List<StatisticGroup> groupsAndCharts = getGroupsAndCharts();
                if (null == groupName || groupName.isEmpty()) groupName = "noGroup";
                boolean added = false;
                StatisticChart chart = new StatisticChart();
                chart.setScriptName(fileMetaData.getFileName());
                chart.setDescription(description);
                chart.setDbName(chartName.replaceAll(" ", "").trim());
                for (StatisticGroup group : groupsAndCharts) {
                    if (group.groupName.equals(groupName) && !group.charts.containsKey(chartName)) {
                        group.charts.put(chartName, chart);
                        added = true;
                    }
                }
                if (!added) { // The GroupName not exists, so add it and the chart.
                    StatisticGroup group = new StatisticGroup();
                    group.groupName = groupName;
                    group.charts.put(chartName, chart);
                    groupsAndCharts.add(group);
                }
                String responseText = response.readEntity(String.class);
                LOGGER.debug(responseText);
                this.saveGroupsAndCharts(groupsAndCharts);
                return Response.ok(responseText).build();
            }
        } else {
            return Response.status(401, "Chartname is empty or already exists").build();
        }
        return Response.status(401, "Could not get Response from service or chartname is empty").build();
    }

    /**
     * Get the chart Data for a chart name.
     *
     * @param chartName Name of the chart (also with blanks) to get data from
     * @param date      Trace requested for the date
     * @param update    If true only an update is sent.
     * @return The data for the frontend to show a chart.
     */
    public String getChartDataForName(String chartName, Map<String, String> date, boolean update) {
        List<StatisticGroup> groups = getGroupsAndCharts();
        StatisticChart chart = null;
        String chartData;
        for (StatisticGroup group : groups) {
            if (group.charts.containsKey(chartName)) {
                chart = group.charts.get(chartName);
                break;
            }
        }
        if (chart != null) {
            if (chart.isTimeseries()) {
                chartData = getTimeSeriesChartData(chartName, chart, date, update);
            } else {
                if (chart.getDbName().isEmpty()) {
                    chartData = this.getChartData(chartName, "");
                } else {
                    chartData = this.getChartData(chart.getDbName(), "");
                }
            }
        } else {
            return "";
        }
        LOGGER.warn(chartData);
        return chartData;
    }

    /**
     * Get all the timeseries dates where data is available.
     *
     * @param chartName name of the chart looking for the dates
     * @return list of the saved dates for a chart.
     */
    public String getTimeseriesDates(String chartName) {
        return getDataForChart(chartName, "savedTimes");
    }

    /**
     * Get the timeseries charts for a startDate.
     *
     * @param chartName the name of the chart looking for data
     * @param chart     the chart with information about accuracy and timeseries.
     * @param dates     If no startDate is given then the newest one is selected.
     * @param update    If true only the trace data will returned.
     * @return JSON encoded String of the statistic data
     */
    private String getTimeSeriesChartData(String chartName, StatisticChart chart, Map<String, String> dates, boolean update) throws IllegalArgumentException {
        StatisticPlotly statisticPlotly = new StatisticPlotly();
        if (!update) {
            statisticPlotly.setTitle(getDataForChart(chartName, "title"));
            statisticPlotly.setUpdateTime(getDataForChart(chartName, "updateTime"));
            statisticPlotly.setLayout(gson.fromJson(getDataForChart(chartName, "layout"), Object.class));
        }
        String json = getDataForChart(chartName, "savedTimes");
        Set<String> savedTimeCharts = gson.fromJson(json, new TypeToken<Set<String>>() {
        }.getType());
        DateTimeFormatter formatter = getDateTimeFormat(chart.getAccuracy());
        List<LocalDate> localDates = getListOfLocalDatesFromString(savedTimeCharts, formatter);
        List<Object> traces = new ArrayList<>();
        if (chart.isMultiple()) {
            Map<String, String> options = this.getChartOptions(chartName);
            int indexStart = 0;
            int indexEnd = 0;
            int prevStart;
            int prevEnd;
            int nextStart;
            int nextEnd;
            int range;
            if (dates.containsKey("start") && dates.containsKey("end")) {
                indexStart = localDates.indexOf(LocalDate.parse(dates.get("start"), formatter));
                indexEnd = localDates.indexOf(LocalDate.parse(dates.get("end"), formatter));
                if (indexStart > indexEnd || indexEnd == -1 || indexStart == -1) {
                    throw new IllegalArgumentException();
                }
                statisticPlotly.setStartDate(dates.get("start"));
                statisticPlotly.setEndDate(dates.get("end"));
                range = indexEnd - indexStart;
            } else {
                range = options.containsKey("displayDefault") ? Integer.parseInt(options.get("displayDefault")) : 5;
                if (localDates.size() > 0) {
                    indexStart = localDates.size() - range;
                    indexEnd = localDates.size() - 1;
                    statisticPlotly.setEndDate(localDates.get(indexEnd).format(formatter));
                    if (indexStart <= 0) {
                        indexStart = 0;
                    }
                    statisticPlotly.setStartDate(localDates.get(indexStart).format(formatter));
                }
            }
            // Get the Start and End date for the Prev trace.
            if (indexStart > 0) {
                prevEnd = indexStart - 1;
                prevStart = prevEnd - range;
                if (prevStart < 0)
                    prevStart = 0;
                statisticPlotly.setPrevDate(localDates.get(prevStart).format(formatter));
                statisticPlotly.setPrevEndDate(localDates.get(prevEnd).format(formatter));
                statisticPlotly.setPrevTrace(getTraceDataForRange(chartName, formatter, localDates, prevStart, prevEnd));
            }

            // Get the Start and End date for the Next trace.
            // Compare with less than, cause we want to check if there are more traces available.
            if (indexEnd < localDates.size() - 1) {
                nextStart = indexEnd + 1;
                nextEnd = nextStart + range;
                if (nextEnd > localDates.size() - 1) {
                    nextEnd = localDates.size() - 1;
                }
                statisticPlotly.setNextDate(localDates.get(nextStart).format(formatter));
                statisticPlotly.setNextEndDate(localDates.get(nextEnd).format(formatter));
                statisticPlotly.setNextTrace(getTraceDataForRange(chartName, formatter, localDates, nextStart, nextEnd));
            }
            traces = getTraceDataForRange(chartName, formatter, localDates, indexStart, indexEnd);

        } else {
            String startDate = dates.getOrDefault("start", "");
            if (!startDate.isEmpty() && savedTimeCharts.contains(startDate)) {
                // Get ChartData by startDate
                LocalDate localDate = LocalDate.parse(startDate, formatter);
                int index = localDates.indexOf(localDate);
                if (index != -1) {
                    statisticPlotly.setStartDate(localDates.get(index).format(formatter));
                    if (index != 0) { // Get prev
                        statisticPlotly.setPrevDate(localDates.get(index - 1).format(formatter));
                    } else {
                        statisticPlotly.setPrevDate("");
                    }
                    if (index != localDates.size() - 1) {
                        statisticPlotly.setNextDate(localDates.get(index + 1).format(formatter));
                    } else {
                        statisticPlotly.setNextDate("");
                    }
                }
            } else {
                // If not in list, use the start startDate.
                statisticPlotly.setStartDate(getLastDayInSet(localDates, formatter, 0));
                statisticPlotly.setPrevDate(getLastDayInSet(localDates, formatter, 1));
                statisticPlotly.setNextDate("");
            }

            if (!statisticPlotly.getStartDate().isEmpty()) {
                String data = getChartData(chartName, statisticPlotly.getStartDate());
                traces.addAll(gson.fromJson(data, new TypeToken<List<Object>>() {
                }.getType()));
            }
            if (!statisticPlotly.getPrevDate().isEmpty()) {
                String prevData = getChartData(chartName, statisticPlotly.getPrevDate());
                statisticPlotly.setPrevTrace(gson.fromJson(prevData, new TypeToken<List<Object>>() {
                }.getType()));
            }
            if (!statisticPlotly.getNextDate().isEmpty()) {
                String prevData = getChartData(chartName, statisticPlotly.getNextDate());
                statisticPlotly.setNextTrace(gson.fromJson(prevData, new TypeToken<List<Object>>() {
                }.getType()));
            }

        }
        statisticPlotly.setTraces(traces);
        return gson.toJson(statisticPlotly);
    }

    /**
     * Get Trace Data for a chart for a specific time
     *
     * @param chartName  to get Data for
     * @param formatter  for formatting the date to the correct string format
     * @param localDates list of all dates with available data
     * @param indexStart start index for the list
     * @param indexEnd   end index for the list
     * @return A list of traces containing the date traces for the indexStart to indexEnd
     */
    private List<Object> getTraceDataForRange(String chartName, DateTimeFormatter formatter, List<LocalDate> localDates, int indexStart, int indexEnd) {
        List<Object> traces = new ArrayList<>();
        if (localDates.size() > 0) {
            for (int i = indexStart; i <= indexEnd; ++i) {
                String data = getChartData(chartName, localDates.get(i).format(formatter));
                traces.addAll(gson.fromJson(data, new TypeToken<List<Object>>() {
                }.getType()));
            }
        }
        return traces;
    }

    /**
     * Get the Options for a chart
     *
     * @param chartName Chart to get Options for
     * @return Map of Options
     */
    private Map<String, String> getChartOptions(String chartName) {
        String opt = this.getDataForChart(chartName, "options");
        Map<String, String> options = gson.fromJson(opt, new TypeToken<HashMap<String, String>>() {
        }.getType());
        if (options == null)
            options = new HashMap<>();
        return options;
    }

    /**
     * Save the Options
     */
    private void saveChartOptions(String chartName, List<Object> options) {
        Map<String, String> chartOptions = new HashMap<>();
        for (Object o : options) {
            String json = gson.toJson(o);
            chartOptions.putAll(gson.fromJson(json, new TypeToken<HashMap<String, String>>() {
            }.getType()));
        }
        this.saveDataForChart(chartName, "options", gson.toJson(chartOptions));
    }

    /**
     * Get the formatter for parsing dates from strings by the accuracy of the chart
     *
     * @param accuracy the accuracy for a chart
     * @return a formatter to parse dates
     */
    private DateTimeFormatter getDateTimeFormat(String accuracy) {
        DateTimeFormatter formatter;
        switch (accuracy) {
            case "month":
                formatter = new DateTimeFormatterBuilder()
                        .appendPattern("yyyy-MM")
                        .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
                        .toFormatter();
                break;
            case "year":
                formatter = new DateTimeFormatterBuilder()
                        .appendPattern("yyyy")
                        .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
                        .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
                        .toFormatter();
                break;
            case "week":
                formatter = DateTimeFormatter.ofPattern("yyyy-ww");
                break;
            default:
                formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                break;
        }
        return formatter;
    }

    /**
     * Get a list of local dates from a string list
     *
     * @param savedTimeCharts The String list
     * @param formatter       Formatter in which format the string list is
     * @return list of sorted localdates with the newest time at the end.
     */
    private List<LocalDate> getListOfLocalDatesFromString(Set<String> savedTimeCharts, DateTimeFormatter formatter) {
        List<LocalDate> localDates = new ArrayList<>();
        if (savedTimeCharts != null) {
            savedTimeCharts.forEach(time -> localDates.add(LocalDate.parse(time, formatter)));
        }
        localDates.sort(LocalDate::compareTo);
        return localDates;
    }

    /**
     * Get the last day of the list. If daysBeforeLastDay is 0.
     *
     * @param localDates        List of all Dates for TimeCharts
     * @param formatter         Formats the date in the correct format
     * @param daysBeforeLastDay Given number is used to select number days before last day
     * @return String with the last day (or before)
     */
    private String getLastDayInSet(List<LocalDate> localDates, DateTimeFormatter formatter, int daysBeforeLastDay) {
        if (daysBeforeLastDay == 0 || daysBeforeLastDay > localDates.size()) {
            LocalDate date = localDates.stream().max(LocalDate::compareTo).get();
            return date.format(formatter);
        } else {
            return localDates.get(localDates.size() - 1 - daysBeforeLastDay).format(formatter);
        }
    }

    /**
     * Save the file to the local /tmp directory.
     *
     * @param fileInputStream File as input stream.
     * @param fileMetaData    File meta Date
     * @return filename and path of the saved file.
     */
    private String saveFileToTmpFolder(InputStream fileInputStream, FormDataContentDisposition fileMetaData) {
        String filename = LOCAL_UPLOAD_PATH + fileMetaData.getFileName();
        try (OutputStream out = new FileOutputStream(filename)) {
            int read;
            byte[] bytes = new byte[1024];
            while ((read = fileInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
        } catch (IOException e) {
            throw new WebApplicationException("Error while uploading file. Please try again !!");
        }
        return filename;
    }


    /**
     * Access the repo and get the data for the allChartNames key
     *
     * @return a list of all groups with charts
     */
    private List<StatisticGroup> getGroupsAndCharts() {
        String groupNames = ConfigurationRepository.getRepo().getVal("statistic.allChartNames");
        if (groupNames.isEmpty())
            return new ArrayList<>();
        List<StatisticGroup> groups;
        try {
            groups = gson.fromJson(groupNames, new TypeToken<ArrayList<StatisticGroup>>() {
            }.getType());
        } catch (Exception e) {
            LOGGER.info("Can't convert JSON directly in StatisticGroup List -> Update Groups to the new format");
            // We need to convert the JSON to the new Format, to prevent delete of all data and newly creation.
            groups = convertGroupsToStatisticGroupObjectAndSave(groupNames);
        }
        return groups;
    }


    /**
     * This helper method converts the JSON get from the DB to the new Format.
     * This is required in order to not delete all chart data.
     *
     * @param groupNames The JSON encoded String from the database
     * @return List of StatisticGroup Items converted to the new Format
     */
    private List<StatisticGroup> convertGroupsToStatisticGroupObjectAndSave(String groupNames) {
        ArrayList<StatisticGroup> groups = new ArrayList<>();
        // Do the magic converting:
        if (!groupNames.isEmpty()) {
            List<StatisticGroupOld> oldGroups = gson.fromJson(groupNames, new TypeToken<ArrayList<StatisticGroupOld>>() {
            }.getType());
            for (StatisticGroupOld group : oldGroups) {
                StatisticGroup newGroup = new StatisticGroup();
                newGroup.groupName = group.groupName;
                for (String chartname : group.charts) {
                    StatisticChart chart = new StatisticChart();
                    chart.setScriptName(this.getScriptName(chartname));
                    newGroup.charts.put(chartname, chart);
                }
                groups.add(newGroup);
            }
            // Save the new format to Database.
            saveGroupsAndCharts(groups);
        }
        return groups;
    }


    /**
     * Save the group information to redis.
     *
     * @param groupsAndCharts List of all Groups containing the charts.
     */
    private void saveGroupsAndCharts(List<StatisticGroup> groupsAndCharts) {
        ConfigurationRepository.getRepo().save(new Configuration("statistic.allChartNames", gson.toJson(groupsAndCharts)));
    }

    /**
     * Save the script name in the db.
     * This is deprecated cause in the new version the Script Name is saved in the StatisticChart Object.
     *
     * @param chartName Name of the chart for saving the scriptname
     * @param value     New value of the scriptname
     */
    @Deprecated
    private void saveScriptName(String chartName, String value) {
        if (!chartName.isEmpty()) {
            ConfigurationRepository.getRepo().save(new Configuration("statistic.chart." + chartName + ".script", value));
        }
    }


    /**
     * Get the script name from the db.
     * This is deprecated, cause the script name is set in the StatisticChart Object.
     *
     * @param chartName Name of the chart for getting the scriptname
     * @return Name of the script.
     */
    @Deprecated
    private String getScriptName(String chartName) {
        if (!chartName.isEmpty()) {
            return ConfigurationRepository.getRepo().getVal("statistic.chart." + chartName + ".script");
        }
        return "";
    }

    private void saveChartData(String chartName, String time, String value) {
        if (!chartName.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("statistic.chart.").append(chartName).append(".data");
            if (time != null && !time.isEmpty()) {
                sb.append(".").append(time);
            }
            ConfigurationRepository.getRepo().save(new Configuration(sb.toString(), value));
        }
    }

    private String getChartData(String chartname, String time) {
        if (!chartname.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("statistic.chart.").append(chartname).append(".data");
            if (time != null && !time.isEmpty()) {
                sb.append(".").append(time);
            }
            return ConfigurationRepository.getRepo().getVal(sb.toString());
        }
        return "";
    }


    /**
     * Update the Chart Meta data in the DB.
     *
     * @param chartName  Name of the Chart to update
     * @param timeseries value is used to indicate if the new features can be used.
     * @param multiple   value is used to get info if multiple items can be shown
     * @param accuracy   the given value indicates the
     */
    private void updateChartMeta(String chartName, boolean timeseries, boolean multiple, String accuracy) {
        List<StatisticGroup> groups = this.getGroupsAndCharts();
        for (StatisticGroup group : groups) {
            if (group.charts.containsKey(chartName)) {
                StatisticChart chart = group.charts.get(chartName);
                chart.setMultiple(multiple);
                chart.setTimeseries(timeseries);
                if (validAccuracyParameter(accuracy)) {
                    chart.setAccuracy(accuracy);
                } else {
                    chart.setAccuracy("none");
                }
                this.saveGroupsAndCharts(groups);
            }
        }
    }

    /**
     * Check if the parameter accuracy is correct.
     *
     * @param accuracy given by the python script.
     * @return true if the accuracy is one of the allowed values
     */
    private boolean validAccuracyParameter(String accuracy) {
        return accuracy.matches("none|year|month|day|week");
    }

    /**
     * Update the scriptname location if not already done.
     *
     * @param chartname Name of the chart to update scriptname
     * @return The scriptname for the chart or empty string if not found chartname.
     */
    private String updateScriptNameLocation(String chartname) {
        String scriptname = this.getScriptName(chartname);
        List<StatisticGroup> groups = getGroupsAndCharts();
        for (StatisticGroup group : groups) {
            if (group.charts.containsKey(chartname)) {
                group.charts.get(chartname).setScriptName(scriptname);
                this.saveGroupsAndCharts(groups);
                this.saveScriptName(chartname, "");
                return scriptname;
            }
        }
        return "";
    }

    /**
     * Check if a given chartName exists.
     *
     * @param chartName Name to search for
     * @return True if the chart exist
     */
    private boolean existChart(String chartName) {
        List<StatisticGroup> groups = this.getGroupsAndCharts();
        for (StatisticGroup group : groups) {
            if (group.charts.containsKey(chartName))
                return true;
        }
        return false;
    }
}
