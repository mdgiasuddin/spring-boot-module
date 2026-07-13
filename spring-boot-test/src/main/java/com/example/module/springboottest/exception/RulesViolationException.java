package com.example.module.springboottest.exception;

import lombok.Getter;

@Getter
public class RulesViolationException extends RuntimeException {
    private final String code;

    public RulesViolationException(String code, String message) {
        super(message);
        this.code = code;
    }
}
