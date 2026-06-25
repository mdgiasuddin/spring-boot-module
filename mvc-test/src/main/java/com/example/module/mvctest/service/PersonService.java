package com.example.module.mvctest.service;

import com.example.module.mvctest.dto.PersonResponse;
import com.example.module.mvctest.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

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
