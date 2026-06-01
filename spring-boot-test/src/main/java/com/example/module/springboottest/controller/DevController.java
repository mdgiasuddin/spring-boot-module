package com.example.module.springboottest.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("dev2")
@Slf4j
@RestController
@RequestMapping("/api/dev")
public class DevController {

    @RequestMapping("/test")
    public String test() {
        log.info("dev profile test");
        return "dev profile test";
    }
}
