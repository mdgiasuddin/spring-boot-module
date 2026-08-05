package com.example.module.mvctest.service;

import com.example.module.mvctest.dto.PersonResponse;
import com.example.module.mvctest.entity.Person;
import com.example.module.mvctest.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestService {

    private final PersonService personService;
    private final PersonRepository personRepository;

    public List<PersonResponse> getPersonList() {
        return personService.getPersonList();
    }

    public PersonResponse getPersonById(int id) {
        return personService.getPersonById(id);
    }

    public Page<Person> getAllPerson(int page) {
        return personRepository.findAll(PageRequest.of(page, 5));
    }

}
