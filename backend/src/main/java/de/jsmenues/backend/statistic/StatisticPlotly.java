package de.jsmenues.backend.statistic;

import java.util.List;
import java.util.Objects;

class StatisticPlotly {
    private String updateTime;
    private String title;
    private List<Object> traces;
    private Object layout;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Object> getTraces() {
        return traces;
    }

    public void setTraces(List<Object> traces) {
        this.traces = traces;
    }

    public Object getLayout() {
        return layout;
    }

    public void setLayout(Object layout) {
        this.layout = layout;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "StatisticPlotly{" +
                "updateTime='" + updateTime + '\'' +
                ", title='" + title + '\'' +
                ", traces=" + traces +
                ", layout=" + layout +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatisticPlotly that = (StatisticPlotly) o;
        return Objects.equals(updateTime, that.updateTime) &&
                Objects.equals(title, that.title) &&
                Objects.equals(traces, that.traces) &&
                Objects.equals(layout, that.layout);
    }

    @Override
    public int hashCode() {
        return Objects.hash(updateTime, title, traces, layout);
    }
}
