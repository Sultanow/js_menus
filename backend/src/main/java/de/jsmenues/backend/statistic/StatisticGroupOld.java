package de.jsmenues.backend.statistic;

import java.util.ArrayList;
import java.util.List;

/**
 * This Class is only for converting from the old interface to the new one.
 * Do not use this class for generating new charts!
 * @Deprecated: use StatisticGroup
 */
@Deprecated
class StatisticGroupOld {
    public String groupName;
    public List<String> charts;

    public StatisticGroupOld() {
        groupName = "";
        charts = new ArrayList<>();
    }
}
