package de.jsmenues.backend.statistic;

import java.util.List;
import java.util.Objects;

class StatisticTimeTrace {
    private List<Object> timetraces;
    private String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<Object> getTimetraces() {
        return timetraces;
    }

    public void setTimetraces(List<Object> timetraces) {
        this.timetraces = timetraces;
    }

    @Override
    public String toString() {
        return "StatisticTimeTrace{" +
                "timetraces=" + timetraces +
                ", time=" + time +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatisticTimeTrace that = (StatisticTimeTrace) o;
        return Objects.equals(timetraces, that.timetraces) &&
                Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timetraces, time);
    }

}
