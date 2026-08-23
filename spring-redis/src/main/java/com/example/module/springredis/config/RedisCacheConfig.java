package com.example.module.springredis.config;

import com.example.module.springredis.dto.Person;
import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.List;

@EnableCaching
@Configuration
public class RedisCacheConfig {

    public static final String PEOPLE_CACHE = "cache.people";
    public static final String PERSON_BY_ID_CACHE = "cache.person_by_id";

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer(ObjectMapper objectMapper) {

        return builder -> builder
                .withCacheConfiguration(PEOPLE_CACHE,
                        listCache(objectMapper, Person.class, Duration.ofMinutes(5))
                )
                .withCacheConfiguration(PERSON_BY_ID_CACHE,
                        singleCache(objectMapper, Person.class, Duration.ofMinutes(3))
                );
    }

    private RedisCacheConfiguration singleCache(ObjectMapper objectMapper, Class<?> type, Duration ttl) {
        JavaType javaType = objectMapper.getTypeFactory().constructType(type);

        RedisJsonSerializer<?> serializer =
                new RedisJsonSerializer<>(objectMapper, javaType);

        return baseConfig(ttl)
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));
    }

    private RedisCacheConfiguration listCache(ObjectMapper objectMapper, Class<?> elementType, Duration ttl) {
        JavaType javaType = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, elementType);

        RedisJsonSerializer<?> serializer =
                new RedisJsonSerializer<>(objectMapper, javaType);

        return baseConfig(ttl)
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));
    }

    private RedisCacheConfiguration baseConfig(Duration ttl) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(ttl)
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string())
                );
    }

}
