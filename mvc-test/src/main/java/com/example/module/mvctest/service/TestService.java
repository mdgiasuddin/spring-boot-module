package com.example.module.mvctest.service;

import com.example.module.mvctest.dto.PersonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestService {

    private final PersonService personService;

    public List<PersonResponse> getPersonList() {
        return personService.getPersonList();
    }

    public PersonResponse getPersonById(int id) {
        return personService.getPersonById(id);
    }

}
