package com.example.module.mvctest.repository;

import com.example.module.mvctest.entity.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class PersonRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PersonRepository personRepository;

    @Test
    void shouldSaveAndFindProductByStatus() {
        Person person1 = new Person();
        person1.setName("Person-1");
        person1.setDob(LocalDate.of(2010, 10, 1));

        Person person2 = new Person();
        person2.setName("Person-2");
        person2.setDob(LocalDate.of(2009, 5, 10));

        entityManager.persistAndFlush(person1);
        entityManager.persistAndFlush(person2);

        List<Person> people = personRepository.findAll();
        assertThat(people).hasSize(2);
    }
}
