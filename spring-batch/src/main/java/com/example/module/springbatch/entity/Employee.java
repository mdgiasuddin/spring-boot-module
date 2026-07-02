package com.example.module.springbatch.entity;

import com.example.module.springbatch.dto.EmployeeCsvDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table
@NoArgsConstructor
@Getter
@Setter
public class Employee {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String department;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private BigDecimal salary;

    @Column(nullable = false)
    private LocalDate dob;

    public Employee(EmployeeCsvDto dto) {
        this.name = dto.name();
        this.department = dto.department();
        this.email = dto.email();
        this.salary = dto.salary();
        this.dob = LocalDate.parse(dto.dob());
    }
}
