package com.example.module.mvctest.controller;

import com.example.module.mvctest.dto.Person;
import com.example.module.mvctest.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicApiController {

    private final TestService testService;

    @GetMapping("/people")
    public List<Person> getPersonList() {
        return testService.getPersonList();
    }

    @GetMapping("/people/{id}")
    public Person getPersonById(@PathVariable int id) {
        return testService.getPersonById(id);
    }
}
