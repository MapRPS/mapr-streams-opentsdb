package com.mapr.streams.opentsdb;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chufe on 16/06/16.
 */
public class FormatStructure {
    private int timeIdx;
    private int valueIdx;
    private int itemIdIdx;
    private int qualityIdx;
    private int errorIdx;
    private String timeFormat;
    private Pattern linePattern;
    private String metricNameValue;
    private String metricNameQuality;
    private String metricNameError;
    private Map<String, String> tags;

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public int getTimeIdx() {
        return timeIdx;
    }

    public void setTimeIdx(int timeIdx) {
        this.timeIdx = timeIdx;
    }

    public int getValueIdx() {
        return valueIdx;
    }

    public void setValueIdx(int valueIdx) {
        this.valueIdx = valueIdx;
    }

    public int getItemIdIdx() {
        return itemIdIdx;
    }

    public void setItemIdIdx(int itemIdIdx) {
        this.itemIdIdx = itemIdIdx;
    }

    public int getQualityIdx() {
        return qualityIdx;
    }

    public void setQualityIdx(int qualityIdx) {
        this.qualityIdx = qualityIdx;
    }

    public int getErrorIdx() {
        return errorIdx;
    }

    public void setErrorIdx(int errorIdx) {
        this.errorIdx = errorIdx;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public Pattern getLinePattern() {
        return linePattern;
    }

    public void setLinePattern(Pattern linePattern) {
        this.linePattern = linePattern;
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
