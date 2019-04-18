package com.eresearch.repositorer.application.configuration;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class EventBusConfiguration {

    @Bean
    public EventBus batchExtractorEventBus() {

        ThreadPoolExecutor batchExtractorThreadPool = new ThreadPoolExecutor(
                20, 80,
                5000L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(300, true),
                new ThreadFactoryBuilder().setNameFormat("batch-extractor-thread-%d").build(),
                new ThreadPoolExecutor.CallerRunsPolicy());

        return new AsyncEventBus("batchExtractorBus", batchExtractorThreadPool);
    }

}
