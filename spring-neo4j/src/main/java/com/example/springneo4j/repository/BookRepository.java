package com.example.springneo4j.repository;

import com.example.springneo4j.entity.Book;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface BookRepository extends Neo4jRepository<Book, Long> {
}
