package com.example.module.springboottest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record SampleRequest(
        long id,
        String name,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate dob
) {
}
