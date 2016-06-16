package com.mapr.streams.opentsdb;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.opentsdb.client.ExpectResponse;
import org.opentsdb.client.HttpClient;
import org.opentsdb.client.HttpClientImpl;
import org.opentsdb.client.builder.Metric;
import org.opentsdb.client.builder.MetricBuilder;
import org.opentsdb.client.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chufe on 16/06/16.
 */
@Service
public class StreamsService implements InitializingBean {
    private static List<String> itemNames = Arrays.asList("VALUE", "ITEM_ID", "TIME", "QUALITY", "ERROR");
    private static final Logger logger = LoggerFactory.getLogger(StreamsService.class);


    @Autowired
    private StreamsConfig streamsConfig;
    private Map<String, FormatStructure> formatStructure;

    public void executeStream() {
        Properties props = new Properties();
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        for (Map.Entry<String, String> entry : streamsConfig.getKafka().entrySet()) {
            props.put(entry.getKey(), entry.getValue());
        }
        HttpClient client = new HttpClientImpl(streamsConfig.getOpenTsdbUrl());
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
//        consumer.subscribe(Arrays.asList("a/user/mapr/opc:User1"));
        consumer.assign(getPartitions());
        while (true) {
            ConsumerRecords<String, String> msgs = consumer.poll(streamsConfig.getPollIntervalInMs());
            Iterator<ConsumerRecord<String, String>> iterator = msgs.iterator();
            while (iterator.hasNext()) {
                MetricBuilder builder = MetricBuilder.getInstance();
                ConsumerRecord<String, String> record = iterator.next();
                String topic = record.topic();
                FormatStructure formatStructure = this.formatStructure.get(topic);
                Matcher m = formatStructure.getLinePattern().matcher(record.value());
                if(!m.find()) {
                    logger.error("Could not parse: " + topic + " = " + record.value());
                }
                String time = m.group(formatStructure.getTimeIdx());
                long timeMillis = getTimeMillis(formatStructure, time);
                if(StringUtils.isNotBlank(formatStructure.getMetricNameValue())) {
                    String value = m.group(formatStructure.getValueIdx());
                    Metric metric = builder.addMetric(formatStructure.getMetricNameValue());
                    if(value.contains(".")) {
                        metric.setDataPoint(timeMillis, Double.valueOf(value));
                    } else {
                        metric.setDataPoint(timeMillis, Long.valueOf(value));
                    }
                    Map<String, String> tags = formatStructure.getTags();
                    if(tags != null) {
                        for (Map.Entry<String, String> entry : tags.entrySet()) {
                            metric.addTag(entry.getKey(), entry.getValue());
                        }
                    }
                }
                if(StringUtils.isNotBlank(formatStructure.getMetricNameError())) {
                    String error = m.group(formatStructure.getErrorIdx());
                    Metric metric = builder.addMetric(formatStructure.getMetricNameError());
                    if(error.contains(".")) {
                        metric.setDataPoint(timeMillis, Double.valueOf(error));
                    } else {
                        metric.setDataPoint(timeMillis, Long.valueOf(error));
                    }
                    Map<String, String> tags = formatStructure.getTags();
                    if(tags != null) {
                        for (Map.Entry<String, String> entry : tags.entrySet()) {
                            metric.addTag(entry.getKey(), entry.getValue());
                        }
                    }
                }
                if(StringUtils.isNotBlank(formatStructure.getMetricNameQuality())) {
                    String quality = m.group(formatStructure.getQualityIdx());
                    Metric metric = builder.addMetric(formatStructure.getMetricNameQuality());
                    if(quality.contains(".")) {
                        metric = metric.setDataPoint(timeMillis, Double.valueOf(quality));
                    } else {
                        metric = metric.setDataPoint(timeMillis, Long.valueOf(quality));
                    }
                    Map<String, String> tags = formatStructure.getTags();
                    if(tags != null) {
                        for (Map.Entry<String, String> entry : tags.entrySet()) {
                            metric = metric.addTag(entry.getKey(), entry.getValue());
                        }
                    }
                }
                if(builder.getMetrics().size() > 0) {
                    try {
                        Response response = client.pushMetrics(builder, ExpectResponse.SUMMARY);
                        if (!response.isSuccess()) {
                            logger.error("An error occurred: " + response);
                        }
                    } catch (IOException e) {
                        logger.error("An error occured: ", e);
                    }
                }
            }
        }
    }

    private long getTimeMillis(FormatStructure formatStructure, String time) {
        String timeFormat = formatStructure.getTimeFormat();
        if(timeFormat.equals("millis")) {
            return Long.valueOf(time);
        }
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
        try {
            return sdf.parse(time).getTime();
        } catch (ParseException e) {
            throw new UnhandledException(e);
        }
    }

    private List<TopicPartition> getPartitions() {
        List<TopicPartition> converted = new ArrayList<>();
        for (String topic : formatStructure.keySet()) {
            converted.add(new TopicPartition(topic, 0)); // TODO support more than one partition
        }
        return converted;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        formatStructure = new HashMap<>();
        for (StreamConfig streamConfig : streamsConfig.getStreams()) {
            String lineFormatPattern = getRegexPattern(streamConfig.getLineFormat());
            Pattern r = Pattern.compile(lineFormatPattern);
            List<ItemOffset> itemOffsets = findOffsets(streamConfig.getLineFormat());
            int timeIdx = getIndex(itemOffsets, "TIME");
            if(timeIdx == -1) {
                throw new IllegalArgumentException("{TIME} must be part of the pattern.");
            }
            int valueIdx = getIndex(itemOffsets, "VALUE");
            int itemIdIdx = getIndex(itemOffsets, "ITEM_ID");
            int qualityIdx = getIndex(itemOffsets, "QUALITY");
            int errorIdx = getIndex(itemOffsets, "ERROR");
            FormatStructure fs = new FormatStructure();
            fs.setTimeFormat(streamConfig.getTimeFormat());
            fs.setErrorIdx(errorIdx);
            fs.setItemIdIdx(itemIdIdx);
            fs.setLinePattern(r);
            fs.setMetricNameError(streamConfig.getMetricNameError());
            fs.setMetricNameQuality(streamConfig.getMetricNameQuality());
            fs.setMetricNameValue(streamConfig.getMetricNameValue());
            fs.setQualityIdx(qualityIdx);
            fs.setTimeIdx(timeIdx);
            fs.setValueIdx(valueIdx);
            fs.setTags(streamConfig.getTags());
            if(formatStructure.containsKey(streamConfig.getTopic())) {
                throw new IllegalArgumentException("A topic can be configured only once per config file!");
            }
            formatStructure.put(streamConfig.getTopic(), fs);
        }
    }

    private List<ItemOffset> findOffsets(String lineFormat) {
        List<ItemOffset> itemOffsets = new ArrayList<>();
        for (String itemName : itemNames) {
            int offset = lineFormat.indexOf('{' + itemName + '}');
            if(offset != -1) {
                itemOffsets.add(new ItemOffset(itemName, offset));
            }
        }
        itemOffsets.sort((o1, o2) -> o1.getOffset().compareTo(o2.getOffset()));
        return itemOffsets;
    }

    private String getRegexPattern(String lineFormat) {
        String lineFormatPattern = lineFormat;
        for (String itemName : itemNames) {
            lineFormatPattern = lineFormatPattern.replace("{" + itemName + "}", "(.*)");
        }
        return lineFormatPattern;
    }

    private static int getIndex(List<ItemOffset> itemOffsets, String name) {
        int i = 1;
        for (ItemOffset itemOffset : itemOffsets) {
            if(itemOffset.getName().equals(name)) {
                return i;
            }
            i++;
        }
        return -1;
    }
}
