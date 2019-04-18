package com.eresearch.repositorer.application.configuration;

import com.eresearch.repositorer.deserializer.InstantDeserializer;
import com.eresearch.repositorer.deserializer.LocalDateDeserializer;
import com.eresearch.repositorer.deserializer.LocalTimeDeserializer;
import com.eresearch.repositorer.serializer.InstantSerializer;
import com.eresearch.repositorer.serializer.LocalDateSerializer;
import com.eresearch.repositorer.serializer.LocalTimeSerializer;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.handler.ssl.SslContextBuilder;
import net.jodah.failsafe.RetryPolicy;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLException;
import java.time.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


@Configuration
public class AppConfiguration {

    @Bean
    @Qualifier("basicRetryPolicyForConnectors")
    public RetryPolicy basicRetryPolicyForConnectors() {
        return new RetryPolicy()
                .retryOn(RestClientException.class)
                .withMaxRetries(10)
                .withDelay(8, TimeUnit.SECONDS)
                .withJitter(7, TimeUnit.SECONDS);
    }

    @Bean(destroyMethod = "shutdownNow")
    @Qualifier("authorExtractionExecutor")
    public ExecutorService authorExtractionExecutor() {
        return new ThreadPoolExecutor(
                20, 80,
                5000L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(300, true),
                new ThreadFactoryBuilder().setNameFormat("author-extraction-thread-%d").build(),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @Bean(destroyMethod = "shutdownNow")
    @Qualifier("authorEntriesMatchingExecutor")
    public ExecutorService authorEntriesMatchingExecutor() {
        return new ThreadPoolExecutor(
                20, 80,
                5000L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(300, true),
                new ThreadFactoryBuilder().setNameFormat("author-entries-matching-thread-%d").build(),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @Bean(destroyMethod = "shutdownNow")
    @Qualifier("dblpEntriesProcessorsExecutor")
    public ExecutorService dblpEntriesProcessorsExecutor() {
        return new ThreadPoolExecutor(
                20, 80,
                5000L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(300, true),
                new ThreadFactoryBuilder().setNameFormat("dblp-entries-processing-thread-%d").build(),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        JavaTimeModule javaTimeModule = new JavaTimeModule();

        javaTimeModule.addSerializer(Instant.class, new InstantSerializer());
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer());
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer());

        javaTimeModule.addDeserializer(Instant.class, new InstantDeserializer());
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer());
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer());

        objectMapper.registerModule(javaTimeModule);

        objectMapper.findAndRegisterModules();
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true); //for elsevier api.

        return objectMapper;
    }

    @Bean
    @Qualifier("repositorerRestTemplate")
    public RestTemplate restTemplate() throws SSLException {
        Netty4ClientHttpRequestFactory nettyFactory = new Netty4ClientHttpRequestFactory();
        nettyFactory.setSslContext(SslContextBuilder.forClient().build());

        return new RestTemplate(nettyFactory);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Value("${service.zone.id}")
    private ZoneId zoneId;

    @Bean
    public Clock clock() {
        return Clock.system(zoneId);
    }
}
