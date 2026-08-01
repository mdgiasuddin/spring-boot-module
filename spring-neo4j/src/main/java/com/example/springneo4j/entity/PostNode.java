package com.example.springneo4j.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Post")
@NoArgsConstructor
@Getter
@Setter
public class PostNode {

    @Id
    private String postId;
    private String content;

    public PostNode(String postId, String content) {
        this.postId = postId;
        this.content = content;
    }
}