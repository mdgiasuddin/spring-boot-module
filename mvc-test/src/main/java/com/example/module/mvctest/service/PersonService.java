package com.example.module.mvctest.service;

import com.example.module.mvctest.dto.PersonRequest;
import com.example.module.mvctest.dto.PersonResponse;
import com.example.module.mvctest.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    public void createPerson(PersonRequest personRequest) {
        log.info("Creating person with request: {}", personRequest);
    }

    public List<PersonResponse> getPersonList() {
        return personRepository.findAll()
                .stream()
                .map(PersonResponse::new)
                .toList();
    }

    public PersonResponse getPersonById(int id) {
        return personRepository.findById(id)
                .map(PersonResponse::new)
                .orElse(null);
    }
}
