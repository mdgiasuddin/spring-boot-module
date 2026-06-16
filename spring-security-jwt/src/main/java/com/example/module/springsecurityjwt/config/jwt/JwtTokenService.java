package com.example.module.springsecurityjwt.config.jwt;

public interface JwtTokenService {
    String generateToken(String username, String role);

    String[] extractUsernameAndRole(String token);

    boolean validate(String token);
}
