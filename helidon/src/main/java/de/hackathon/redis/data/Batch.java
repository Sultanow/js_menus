package de.hackathon.redis.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Batch {

    @XmlElement public String batchId;
    @XmlElement public String duration;

    public Batch(String batchId, String duration) {
        this.batchId = batchId;
        this.duration = duration;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getBatchId() {
        return batchId;
    }

    public String getDuration() {
        return duration;
    }
}
