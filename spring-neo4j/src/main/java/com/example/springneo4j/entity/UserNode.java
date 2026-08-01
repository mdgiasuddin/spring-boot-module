package com.example.springneo4j.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.OUTGOING;

@Node("User")
@NoArgsConstructor
@Getter
@Setter
public class UserNode {

    @Id
    private String userId;
    private String username;

    @Relationship(type = "FRIEND_WITH", direction = OUTGOING)
    private Set<UserNode> friends = new HashSet<>();

    @Relationship(type = "LIKES", direction = OUTGOING)
    private Set<PostNode> likedPosts = new HashSet<>();

    public UserNode(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public void addFriend(UserNode user) {
        this.friends.add(user);
    }

    public void likePost(PostNode post) {
        this.likedPosts.add(post);
    }
}