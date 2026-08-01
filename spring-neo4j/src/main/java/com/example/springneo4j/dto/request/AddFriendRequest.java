package com.example.springneo4j.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AddFriendRequest(
        @NotBlank
        String userId1,
        @NotBlank
        String userId2
) {
}
