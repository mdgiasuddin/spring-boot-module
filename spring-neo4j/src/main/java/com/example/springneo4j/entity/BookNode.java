package com.example.springneo4j.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Book")
@Getter
@Setter
public class BookNode {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String author;
}
