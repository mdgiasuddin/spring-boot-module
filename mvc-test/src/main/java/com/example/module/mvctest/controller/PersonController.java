package com.example.module.mvctest.controller;

import com.example.module.mvctest.dto.PersonRequest;
import com.example.module.mvctest.service.PersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/people")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @PostMapping
    public void createPerson(@Valid @RequestBody PersonRequest personRequest) {
        personService.createPerson(personRequest);
    }
}
