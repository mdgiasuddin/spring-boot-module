package com.example.module.springsecurityjwt.controller;

import com.example.module.springsecurityjwt.config.jwt.AsymmetricTokenService;
import com.example.module.springsecurityjwt.dto.JwtValidateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test/jwt")
@RequiredArgsConstructor
public class JwtController {

    private final AsymmetricTokenService jwtTokenServiced;

    @PostMapping("/generate")
    public String generateJwt() {
        return jwtTokenServiced.generateToken("giash@gmail.com", "ADMIN");
    }

    @PostMapping("/validate")
    public boolean validateJwt(@Valid @RequestBody JwtValidateRequest request) {
        return jwtTokenServiced.validate(request.getToken());
    }
}
