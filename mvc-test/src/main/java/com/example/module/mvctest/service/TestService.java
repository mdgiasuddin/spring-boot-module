package com.example.module.mvctest.service;

import com.example.module.mvctest.dto.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class TestService {

    private final Random random = new SecureRandom();

    public List<Person> getPersonList() {
        return Arrays.asList(
                new Person(1, "John", LocalDate.now().minusMonths(random.nextInt(100, 3000))),
                new Person(2, "Jane", LocalDate.now().minusMonths(random.nextInt(100, 300))),
                new Person(3, "Bob", LocalDate.now().minusMonths(random.nextInt(100, 300))),
                new Person(4, "Alice", LocalDate.now().minusMonths(random.nextInt(100, 300))),
                new Person(5, "Mike", LocalDate.now().minusMonths(random.nextInt(100, 300))),
                new Person(6, "Emily", LocalDate.now().minusMonths(random.nextInt(100, 300))),
                new Person(7, "Sarah", LocalDate.now().minusMonths(random.nextInt(100, 300))),
                new Person(8, "David", LocalDate.now().minusMonths(random.nextInt(100, 300))),
                new Person(9, "Emma", LocalDate.now().minusMonths(random.nextInt(100, 300))),
                new Person(100, "Olivia", LocalDate.now().minusMonths(random.nextInt(100, 300)))
        );
    }

    public Person getPersonById(int id) {
        return getPersonList().stream()
                .filter(person -> person.getId() == id)
                .findFirst()
                .orElse(null);
    }

}
