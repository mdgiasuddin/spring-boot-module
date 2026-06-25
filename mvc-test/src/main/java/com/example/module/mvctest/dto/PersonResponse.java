package com.example.module.mvctest.dto;

import com.example.module.mvctest.entity.Person;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PersonResponse {
    private int id;
    private String name;
    private LocalDate dob;

    public PersonResponse(Person person) {
        this.id = person.getId();
        this.name = person.getName();
        this.dob = person.getDob();
    }
}
