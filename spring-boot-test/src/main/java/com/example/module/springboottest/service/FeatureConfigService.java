package com.example.module.springboottest.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Random;

@Slf4j
@Service
public class FeatureConfigService {
    private final Random random = new SecureRandom();

    public boolean isMaskingEnabled(String configKey) {
        boolean enabled = random.nextBoolean();
        log.info("Masking for config key: {}, enabled: {}", configKey, enabled);
        return enabled;
    }
}
