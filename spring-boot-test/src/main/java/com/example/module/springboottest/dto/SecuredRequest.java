package com.example.module.springboottest.dto;

public record SecuredRequest(
        String requestId,
        Long timeStamp,
        String signature,
        String payload
) {
}
