package com.example.module.mvctest.service;

import com.example.module.mvctest.dto.Person;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TestServiceTest {

    @Mock
    private DataProviderService dataProviderService;

    @InjectMocks
    private TestService testService;

    @Test
    void getPersonList_shouldReturnList() {
        Person p1 = new Person(1, "John", LocalDate.of(1990, 1, 1));
        Person p2 = new Person(2, "Jane", LocalDate.of(1995, 10, 20));
        given(dataProviderService.getPersonList()).willReturn(Arrays.asList(p1, p2));

        List<Person> people = testService.getPersonList();
        assertNotNull(people);
        assertEquals(2, people.size());
        assertEquals("John", people.getFirst().getName());
    }

    @Test
    void getPersonById_shouldReturnPersonForValidId() {
        Person p1 = new Person(1, "John", LocalDate.of(1990, 1, 1));
        given(dataProviderService.getPersonById(anyInt())).willReturn(p1);

        Person person = testService.getPersonById(1);
        assertNotNull(person);
        assertEquals("John", person.getName());
    }
}
