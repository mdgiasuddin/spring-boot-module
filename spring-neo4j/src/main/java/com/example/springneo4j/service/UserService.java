package com.example.springneo4j.service;

import com.example.springneo4j.dto.response.UserResponse;
import com.example.springneo4j.entity.UserNode;
import com.example.springneo4j.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    //    @PostConstruct
    public void createUsers() {
        List<UserNode> users = List.of(
                new UserNode(UUID.randomUUID().toString(), "giash"),
                new UserNode(UUID.randomUUID().toString(), "sobuj"),
                new UserNode(UUID.randomUUID().toString(), "rony"),
                new UserNode(UUID.randomUUID().toString(), "biplob"),
                new UserNode(UUID.randomUUID().toString(), "jonaed"),
                new UserNode(UUID.randomUUID().toString(), "emran"),
                new UserNode(UUID.randomUUID().toString(), "raju")
        );

        userRepository.saveAll(users);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::new)
                .toList();
    }
}
