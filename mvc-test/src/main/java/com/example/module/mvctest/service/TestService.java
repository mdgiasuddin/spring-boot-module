package com.example.module.mvctest.service;

import com.example.module.mvctest.dto.Person;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestService {

    private final DataProviderService dataProviderService;

    public List<Person> getPersonList() {
        return dataProviderService.getPersonList();
    }

    public Person getPersonById(int id) {
        return dataProviderService.getPersonById(id);
    }

}
