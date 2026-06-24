package com.example.module.mvctest.service;

import com.example.module.mvctest.dto.Person;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class TestServiceTest {

    private final TestService testService = new TestService();

    @Test
    void getPersonList_shouldReturnList() {
        List<Person> people = testService.getPersonList();
        assertNotNull(people);
        assertEquals(10, people.size());
    }

    @Test
    void getPersonById_shouldReturnPersonForValidId() {
        Person person = testService.getPersonById(1);
        assertNotNull(person);
        assertEquals(1, person.getId());
        assertEquals("John", person.getName());
    }

    @Test
    void getPersonById_shouldReturnNullForInvalidId() {
        Person person = testService.getPersonById(999);
        assertNull(person);
    }
}
