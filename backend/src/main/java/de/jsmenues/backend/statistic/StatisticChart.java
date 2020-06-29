package de.jsmenues.backend.statistic;

import java.util.Objects;

class StatisticChart {
    private String accuracy;
    private boolean timeseries;
    private boolean multiple;
    private String scriptName;
    private String description;
    private String dbName;

    public StatisticChart() {
        this.accuracy = "none";
        this.timeseries = false;
        this.multiple = false;
        this.scriptName = "";
        this.description = "";
        this.dbName = "";
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isTimeseries() {
        return timeseries;
    }

    public void setTimeseries(boolean timeseries) {
        this.timeseries = timeseries;
    }

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public boolean isMultiple() {
        return multiple;
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    @Override
    public String toString() {
        return "StatisticChart{" +
                "accuracy='" + accuracy + '\'' +
                ", timeseries=" + timeseries +
                ", multiple=" + multiple +
                ", scriptName='" + scriptName + '\'' +
                ", description='" + description + '\'' +
                ", dbName='" + dbName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatisticChart that = (StatisticChart) o;
        return timeseries == that.timeseries &&
                multiple == that.multiple &&
                Objects.equals(accuracy, that.accuracy) &&
                Objects.equals(scriptName, that.scriptName) &&
                Objects.equals(description, that.description) &&
                Objects.equals(dbName, that.dbName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accuracy, timeseries, multiple, scriptName, description, dbName);
    }

}
