package com.example.module.recurringsubscription.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.List;

@Configuration
public class LuaConfiguration {

    @Bean
    @SuppressWarnings("unchecked")
    public RedisScript<List<Long>> tokenBucketScript() {
        return RedisScript.of(
                new ClassPathResource("lua/token_bucket.lua"),
                (Class<List<Long>>) (Class<?>) List.class
        );
    }

}
