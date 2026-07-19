package com.example.module.springboottest.controller;

import com.example.module.springboottest.dto.Person;
import com.example.module.springboottest.util.NumberWordConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final NumberWordConverter numberWordConverter;

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
        return numberWordConverter.convertToWords(number);
    }
}
