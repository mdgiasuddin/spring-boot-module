package com.example.module.springboottest.controller;

import com.example.module.springboottest.dto.Person;
import com.example.module.springboottest.util.NumberWordConverter;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
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

    @GetMapping("/number-to-words")
    public String getNumber(@RequestParam long number) {
        return NumberWordConverter.convertToWords(number);
    }

    @PostConstruct
    public void print() {
        log.info(UUID.randomUUID().toString());
    }
}
