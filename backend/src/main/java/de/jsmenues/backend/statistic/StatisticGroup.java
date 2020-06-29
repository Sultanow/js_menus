package de.jsmenues.backend.statistic;

import java.util.HashMap;
import java.util.Map;

public class StatisticGroup {
    public String groupName;
    public Map<String, StatisticChart> charts;

    public StatisticGroup() {
        groupName = "";
        charts = new HashMap<>();
    }
}
