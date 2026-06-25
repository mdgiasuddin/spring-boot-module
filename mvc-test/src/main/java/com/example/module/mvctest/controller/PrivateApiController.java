package com.example.module.mvctest.controller;

import com.example.module.mvctest.dto.PersonResponse;
import com.example.module.mvctest.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/private")
@RequiredArgsConstructor
public class PrivateApiController {

    private final TestService testService;

    @GetMapping("/people")
    public List<PersonResponse> getPersonList() {
        return testService.getPersonList();
    }

    @GetMapping("/people/{id}")
    public PersonResponse getPersonById(@PathVariable int id) {
        return testService.getPersonById(id);
    }
}
