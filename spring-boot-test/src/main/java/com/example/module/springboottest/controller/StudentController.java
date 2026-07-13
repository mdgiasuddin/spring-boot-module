package com.example.module.springboottest.controller;

import com.example.module.springboottest.apigroup.Create;
import com.example.module.springboottest.apigroup.Update;
import com.example.module.springboottest.dto.StudentRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/students")
public class StudentController {

    @PostMapping
    public void createStudent(@RequestBody @Validated(Create.class) StudentRequest request) {
        log.info("Create Student: {}", request);
    }

    @PutMapping
    public void updateStudent(@RequestBody @Validated(Update.class) StudentRequest request) {
        log.info("Update Student: {}", request);
    }
}
