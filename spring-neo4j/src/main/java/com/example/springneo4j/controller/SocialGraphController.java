package com.example.springneo4j.controller;

import com.example.springneo4j.dto.request.AddFriendRequest;
import com.example.springneo4j.dto.response.UserResponse;
import com.example.springneo4j.service.SocialGraphService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/social-graph")
@RequiredArgsConstructor
public class SocialGraphController {
    private final SocialGraphService socialGraphService;

    @PostMapping("/friends")
    public void makeFriends(@Valid @RequestBody AddFriendRequest request) {
        socialGraphService.makeFriends(request.userId1(), request.userId2());
    }

    @GetMapping("/friends/recommendations/{userId}")
    public List<UserResponse> getFriendRecommendations(@PathVariable String userId) {
        return socialGraphService.getFriendRecommendations(userId);
    }

}
