package com.example.module.mvctest.repository;


import com.example.module.mvctest.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Integer> {
}
