
package com.example.module.springsecurityjwt.config.jwt;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.io.InputStream;

/**
 * Generate Secret Key:
 * byte[] bytes = new byte[64];
 * Random random = new SecureRandom();
 * random.nextBytes(bytes);
 * String.valueOf(Hex.encode(bytes));
 */

@Slf4j
@Component
public class SymmetricTokenService extends AbstractJwtTokenService {

    @Value("${jwt.signing-key-path}")
    private String signingKeyPath;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        try {
            this.secretKey = loadSigningKey(signingKeyPath);

            log.info("Private & Public key loaded successfully");
        } catch (final Exception e) {
            log.error("Error loading private key", e);
            throw new RuntimeException("Error loading private key", e);
        }
    }

    @Override
    public String generateToken(String username, String role) {
        return buildToken(username, role, secretKey);
    }

    @Override
    protected java.security.Key getVerificationKey() {
        return secretKey;
    }


    private SecretKey loadSigningKey(final String privateKeyPath) throws Exception {
        try (final InputStream is = this.getClass().getClassLoader().getResourceAsStream(privateKeyPath)) {
            if (is == null) {
                throw new RuntimeException("Signing key not found");
            }

            final String key = new String(is.readAllBytes());
            byte[] keyBytes = Decoders.BASE64.decode(key.replaceAll("\\s", ""));
            return Keys.hmacShaKeyFor(keyBytes);
        }
    }

}