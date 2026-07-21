package com.example.module.springboottest.util;

import lombok.NoArgsConstructor;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class SignatureUtil {

    private static final Base64.Encoder base64UrlEncoder = Base64.getUrlEncoder();
    private static final Base64.Decoder base64UrlDecoder = Base64.getUrlDecoder();
    private static final String algorithm = "HmacSHA256";

    public static String sign(String securityKey, String payload) {
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

    public static boolean verify(String securityKey, String signature, String payload) {
        String computedSignature = sign(securityKey, payload);
        return MessageDigest.isEqual(
                computedSignature.getBytes(UTF_8),
                signature.getBytes(UTF_8)
        );
    }
}
