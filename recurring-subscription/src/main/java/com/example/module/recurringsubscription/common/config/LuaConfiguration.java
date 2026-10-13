package com.example.module.recurringsubscription.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.List;

@Configuration
public class LuaConfiguration {

    @SuppressWarnings("unchecked")
    @Bean(name = "globalRateLimitScript")
    public RedisScript<List<Long>> globalRateLimitScript() {
        return RedisScript.of(
                new ClassPathResource("lua/global_rate_limit_script.lua"),
                (Class<List<Long>>) (Class<?>) List.class
        );
    }

    @SuppressWarnings("unchecked")
    @Bean(name = "merchantRateLimitScript")
    public RedisScript<List<Long>> merchantRateLimitScript() {
        return RedisScript.of(
                new ClassPathResource("lua/merchant_rate_limit_script.lua"),
                (Class<List<Long>>) (Class<?>) List.class
        );
    }

}
