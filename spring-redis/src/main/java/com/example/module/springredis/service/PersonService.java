package com.example.module.springredis.service;

import com.example.module.springredis.dto.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static com.example.module.springredis.config.RedisCacheConfig.PEOPLE_CACHE;
import static com.example.module.springredis.config.RedisCacheConfig.PERSON_BY_ID_CACHE;

@Slf4j
@Service
public class PersonService {

    @Cacheable(cacheNames = PEOPLE_CACHE)
    public List<Person> findAll() {
        log.info("Fetching all people");

        return getAllPeople();
    }

    @Cacheable(cacheNames = PERSON_BY_ID_CACHE, key = "#id")
    public Person findById(int id) {
        log.info("Fetching person with id {}", id);

        return getAllPeople().stream()
                .filter(person -> person.id() == id)
                .findFirst()
                .orElse(null);
    }

    private List<Person> getAllPeople() {
        return Arrays.asList(
                new Person(1, "John", 30),
                new Person(2, "Jane", 25),
                new Person(3, "Alice", 28),
                new Person(4, "Bob", 35),
                new Person(5, "Charlie", 22)
        );
    }
}
