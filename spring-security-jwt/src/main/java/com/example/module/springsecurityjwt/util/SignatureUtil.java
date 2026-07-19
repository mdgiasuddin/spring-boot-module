package com.example.module.springsecurityjwt.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class SignatureUtil {

    private final Base64.Encoder base64UrlEncoder = Base64.getUrlEncoder();
    private final Base64.Decoder base64UrlDecoder = Base64.getUrlDecoder();
    private static final String algorithm = "HmacSHA256";

    @Value("${api-security.key}")
    private String securityKey;

    public String sign(String payload) {
        byte[] decodedKey = base64UrlDecoder.decode(securityKey);
        SecretKeySpec keySpec = new SecretKeySpec(decodedKey, algorithm);

        try {
            Mac mac = Mac.getInstance(algorithm);
            mac.init(keySpec);
            byte[] bytes = mac.doFinal(payload.getBytes(UTF_8));

            return base64UrlEncoder.encodeToString(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean verify(String signature, String payload) {
        String computedSignature = sign(payload);
        return MessageDigest.isEqual(
                computedSignature.getBytes(UTF_8),
                signature.getBytes(UTF_8)
        );
    }
}
