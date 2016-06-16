package com.mapr.streams.opentsdb;

import java.util.Map;

/**
 * Created by chufe on 16/06/16.
 */
public class StreamConfig {
    private String topic;
    private String timeFormat;
    private String lineFormat;
    private String metricNameValue;
    private String metricNameQuality;
    private String metricNameError;
    private Map<String, String> tags;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
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

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public String getMetricNameValue() {
        return metricNameValue;
    }

    public void setMetricNameValue(String metricNameValue) {
        this.metricNameValue = metricNameValue;
    }

    public String getMetricNameQuality() {
        return metricNameQuality;
    }

    public void setMetricNameQuality(String metricNameQuality) {
        this.metricNameQuality = metricNameQuality;
    }

    public String getMetricNameError() {
        return metricNameError;
    }

    public void setMetricNameError(String metricNameError) {
        this.metricNameError = metricNameError;
    }
}
