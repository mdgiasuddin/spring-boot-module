package com.example.springneo4j.dto.response;

import com.example.springneo4j.entity.UserNode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {
    private String userId;
    private String username;

    public UserResponse(UserNode user) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
    }
}
