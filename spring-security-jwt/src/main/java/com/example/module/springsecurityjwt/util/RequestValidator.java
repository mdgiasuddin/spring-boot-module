package com.example.module.springsecurityjwt.util;

import com.example.module.springsecurityjwt.dto.SecuredRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.SECONDS;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestValidator {

    @Value("${api-security.key}")
    private String securityKey;

    public boolean validateRequest(SecuredRequest request) {
        Instant now = Instant.now();
        Instant requestTimestamp = Instant.ofEpochMilli(request.timeStamp());
        if (SECONDS.between(requestTimestamp, now) > 120) {
            log.error("Invalid timestamp");
            return false;
        }

        String modifiedPayload = String.format("%s|%d|%s", request.requestId(), request.timeStamp(), request.payload());
        return SignatureUtil.verify(securityKey, request.signature(), modifiedPayload);
    }

}
