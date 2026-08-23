package com.example.module.springredis.config;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;

public class RedisJsonSerializer<T> implements RedisSerializer<T> {

    private final ObjectMapper objectMapper;
    private final JavaType javaType;

    public RedisJsonSerializer(ObjectMapper objectMapper, JavaType javaType) {
        this.objectMapper = objectMapper;
        this.javaType = javaType;
    }

    @Override
    public @Nonnull byte[] serialize(@Nullable T value) throws SerializationException {
        if (value == null)
            return new byte[0];

        return objectMapper.writeValueAsBytes(value);
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0)
            return null;

        return objectMapper.readValue(bytes, javaType);
    }
}
