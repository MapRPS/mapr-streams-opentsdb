package com.mapr.streams.opentsdb;

import org.apache.commons.lang.StringUtils;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.LogManager;

/**
 * Created by chufe on 16/06/16.
 */
@SpringBootApplication
public class StreamsOpenTSDBApplication implements CommandLineRunner {
    private static String configFile;

    @Autowired
    private StreamsService streamsService;

    @Override
    public void run(String... args) throws Exception {
        streamsService.executeStream();
    }

    @Bean
    public StreamsConfig opcConfig() throws FileNotFoundException {
        Yaml yaml = new Yaml();
        StreamsConfig opcConfig = yaml.loadAs(new FileInputStream(new File(configFile)), StreamsConfig.class);
        writeDefaultsToItemIfEmpty(opcConfig);
        return opcConfig;
    }

    private void writeDefaultsToItemIfEmpty(StreamsConfig streamsConfig) {
        for (StreamConfig stream : streamsConfig.getStreams()) {
            if(StringUtils.isBlank(stream.getLineFormat())) {
                stream.setLineFormat(streamsConfig.getLineFormat());
            }
            if(StringUtils.isBlank(stream.getTimeFormat())) {
                stream.setTimeFormat(streamsConfig.getTimeFormat());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.install();
        if (args.length != 1) {
            System.out.println("Usage: mapr-streams-opentsdb <config file>");
            return;
        }
        configFile = args[0];
        SpringApplication.run(StreamsOpenTSDBApplication.class, args);
        Thread.currentThread().join();
    }
}
