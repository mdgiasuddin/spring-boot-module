package com.example.module.springredis.config;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.metrics.CommandLatencyRecorder;
import io.lettuce.core.resource.ClientResources;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.data.redis.autoconfigure.ClientResourcesBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class BloomFilterConfig {

    @Value("${spring.data.redis.uri}")
    private String redisUri;

    /**
     * Disables Lettuce's command-latency metrics on the Spring Data Redis connection factory
     * (used by the {@code @Cacheable} caches and ShedLock). Nothing in this app ever reads those
     * metrics, and {@code DefaultCommandLatencyCollector}'s CommandLatencyId -> histogram map is
     * never evicted.
     */
    @Bean
    public ClientResourcesBuilderCustomizer disableLettuceCommandLatencyMetrics() {
        return builder -> builder.commandLatencyRecorder(CommandLatencyRecorder.disabled());
    }

    // disabled: nothing in this app reads these metrics, and the collector's
    // CommandLatencyId -> histogram map is never evicted, so every reconnect
    @Bean(destroyMethod = "shutdown")
    public ClientResources bloomClientResources() {
        return ClientResources.builder()
                .commandLatencyRecorder(CommandLatencyRecorder.disabled())
                .build();
    }

    @Bean(destroyMethod = "shutdown")
    public RedisClient redisClient(ClientResources bloomClientResources) {
        RedisClient client = RedisClient.create(bloomClientResources, redisUri);
        client.setOptions(ClientOptions.builder()
                .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
                .requestQueueSize(10_000)
                .timeoutOptions(TimeoutOptions.enabled(Duration.ofSeconds(5)))
                .build());
        return client;
    }

    @Bean(destroyMethod = "close")
    public StatefulRedisConnection<String, String> redisConnection(RedisClient redisClient) {
        return redisClient.connect();
    }

    @Bean
    public RedisCommands<String, String> redisCommands(StatefulRedisConnection<String, String> connection) {
        return connection.sync();
    }
}
