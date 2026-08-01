package com.example.springneo4j.service;

import com.example.springneo4j.dto.response.UserResponse;
import com.example.springneo4j.entity.PostNode;
import com.example.springneo4j.entity.UserNode;
import com.example.springneo4j.repository.PostRepository;
import com.example.springneo4j.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SocialGraphService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public void makeFriends(String userId1, String userId2) {
        UserNode user1 = userRepository.findById(userId1)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId1));
        UserNode user2 = userRepository.findById(userId2)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId2));

        // Link both ways in Java memory
        user1.addFriend(user2);
        user2.addFriend(user1);

        // Save BOTH in a single repository batch call
        userRepository.saveAll(List.of(user1, user2));
    }

    @Transactional
    public void likePost(String userId, String postId) {
        UserNode user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        PostNode post = postRepository.findByPostId(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));

        user.likePost(post);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getFriendRecommendations(String userId) {
        return userRepository.findFriendRecommendations(userId)
                .stream()
                .map(UserResponse::new)
                .toList();
    }
}
