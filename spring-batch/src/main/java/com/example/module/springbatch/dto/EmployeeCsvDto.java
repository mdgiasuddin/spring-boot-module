package com.example.module.springbatch.dto;

import java.math.BigDecimal;

public record EmployeeCsvDto(
        String name,
        String department,
        String email,
        BigDecimal salary,
        String dob
) {
}
