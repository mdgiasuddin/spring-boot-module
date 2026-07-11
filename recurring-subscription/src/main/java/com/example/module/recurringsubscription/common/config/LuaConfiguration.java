package com.example.module.recurringsubscription.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.io.IOException;

@Configuration
public class LuaConfiguration {

    @Bean
    public RedisScript<Long> tokenBucketScript() throws IOException {
        ResourceScriptSource source = new ResourceScriptSource(new ClassPathResource("lua/token_bucket.lua"));
        return RedisScript.of(source.getScriptAsString(), Long.class);
    }

}
