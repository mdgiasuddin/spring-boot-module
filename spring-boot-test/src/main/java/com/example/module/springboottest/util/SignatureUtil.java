package com.example.module.springboottest.util;

import lombok.NoArgsConstructor;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class SignatureUtil {

    private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder BASE_URL_DECODER = Base64.getUrlDecoder();
    private static final String ALGORITHM = "HmacSHA256";

    public static String sign(String securityKey, String payload) {
        byte[] decodedKey = BASE_URL_DECODER.decode(securityKey);
        SecretKeySpec keySpec = new SecretKeySpec(decodedKey, ALGORITHM);

        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(keySpec);
            byte[] bytes = mac.doFinal(payload.getBytes(UTF_8));

            return BASE64_URL_ENCODER.encodeToString(bytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
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
