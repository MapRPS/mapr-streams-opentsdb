package com.mapr.streams.opentsdb;

import java.util.List;
import java.util.Map;

/**
 * Created by chufe on 10/06/16.
 */
public class StreamsConfig {
    private String openTsdbUrl;
    private Integer pollIntervalInMs;
    private Map<String, String> kafka;
    private String timeFormat;
    private String lineFormat;
    private List<StreamConfig> streams;

    public String getOpenTsdbUrl() {
        return openTsdbUrl;
    }

    public void setOpenTsdbUrl(String openTsdbUrl) {
        this.openTsdbUrl = openTsdbUrl;
    }

    public Integer getPollIntervalInMs() {
        return pollIntervalInMs;
    }

    public void setPollIntervalInMs(Integer pollIntervalInMs) {
        this.pollIntervalInMs = pollIntervalInMs;
    }

    public Map<String, String> getKafka() {
        return kafka;
    }

    public void setKafka(Map<String, String> kafka) {
        this.kafka = kafka;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public String getLineFormat() {
        return lineFormat;
    }

    public void setLineFormat(String lineFormat) {
        this.lineFormat = lineFormat;
    }

    public List<StreamConfig> getStreams() {
        return streams;
    }

    public void setStreams(List<StreamConfig> streams) {
        this.streams = streams;
    }


}
