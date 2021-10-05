package de.jsmenues.backend.statistic;

import java.util.ArrayList;
import java.util.Objects;

public class StatisticInterface {
    public String title;
    public ArrayList<Object> traces;
    public Object layout;
    public String updateTime;
    public boolean timeseries;
    public String accuracy;
    public boolean multiple;
    public ArrayList<Object> options;

    public StatisticInterface() {
        title = "";
        traces = new ArrayList<>();
        layout = "";
        updateTime = "";
        timeseries = false;
        accuracy = "none";
        multiple = false;
        options = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "StatisticInterface{" +
                "title='" + title + '\'' +
                ", traces=" + traces +
                ", layout='" + layout + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", timeseries=" + timeseries +
                ", accuracy='" + accuracy + '\'' +
                ", multiple=" + multiple +
                ", options=" + options +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatisticInterface that = (StatisticInterface) o;
        return timeseries == that.timeseries &&
                multiple == that.multiple &&
                Objects.equals(title, that.title) &&
                Objects.equals(traces, that.traces) &&
                Objects.equals(layout, that.layout) &&
                Objects.equals(updateTime, that.updateTime) &&
                Objects.equals(accuracy, that.accuracy) &&
                Objects.equals(options, that.options);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, traces, layout, updateTime, timeseries, accuracy, multiple, options);
    }
}
