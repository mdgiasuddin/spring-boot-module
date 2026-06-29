package com.example.module.springboottest.controller;

import com.example.module.springboottest.dto.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/test")
public class TestController {
    @GetMapping
    public String test() {
        log.info("test");
        return "test";
    }

    @GetMapping("/person")
    public Person getPerson() {
        return new Person(
                "Giash Uddin",
                25,
                "giash.uddin@gmail.com",
                "01711111111",
                "Dhaka, Bangladesh"
        );
    }
}
