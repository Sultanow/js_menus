package de.jsmenues.backend.statistic;

import java.util.ArrayList;

public class StatisticItem {
    public String groupName;
    public ArrayList<String> charts;

    public StatisticItem() {
        groupName = "";
        charts = new ArrayList<String>();
    }
}
