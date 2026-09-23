package com.example.module.springboottest.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LogTraceService {

    public void testLog() {
        log.info("Testing log trace id...");
    }
}
