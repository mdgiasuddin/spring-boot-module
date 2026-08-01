package com.example.springneo4j.repository;

import com.example.springneo4j.entity.PostNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;

public interface PostRepository extends Neo4jRepository<PostNode, String> {
    Optional<PostNode> findByPostId(String postId);
}