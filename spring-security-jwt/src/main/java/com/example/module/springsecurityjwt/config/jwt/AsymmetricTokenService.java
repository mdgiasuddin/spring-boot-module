package com.example.module.springsecurityjwt.config.jwt;

import jakarta.annotation.Nonnull;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
 * keyPairGen.initialize(2048);
 * KeyPair keyPair = keyPairGen.generateKeyPair();
 * String publicKeyStr = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
 * String privateKeyStr = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class AsymmetricTokenService extends AbstractJwtTokenService {

    @Value("${jwt.private-key-path}")
    private String privateKeyPath;
    @Value("${jwt.public-key-path}")
    private String publicKeyPath;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    @PostConstruct
    public void init() {
        try {
            this.privateKey = loadPrivateKey(privateKeyPath);
            this.publicKey = loadPublicKey(publicKeyPath);

            log.info("Private & Public key loaded successfully");
        } catch (final Exception e) {
            log.error("Error loading private key", e);
            throw new RuntimeException("Error loading private key", e);
        }
    }

    @Override
    public String generateToken(@Nonnull final String username, @Nonnull final String role) {
        return buildToken(username, role, privateKey);
    }

    @Override
    protected java.security.Key getVerificationKey() {
        return publicKey;
    }


    private PrivateKey loadPrivateKey(final String privateKeyPath) throws Exception {
        try (final InputStream is = this.getClass().getClassLoader().getResourceAsStream(privateKeyPath)) {
            if (is == null) {
                throw new RuntimeException("Private key not found");
            }

            final String key = new String(is.readAllBytes());
            final String privateKeyPEM = key.replaceAll("\\s", "");

            final byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
            final PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
            return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
        }
    }

    private PublicKey loadPublicKey(final String publicKeyPath) throws Exception {
        try (final InputStream is = this.getClass().getClassLoader().getResourceAsStream(publicKeyPath)) {
            if (is == null) {
                throw new RuntimeException("Public key not found");
            }

            final String key = new String(is.readAllBytes());
            final String publicKeyPEM = key.replaceAll("\\s", "");

            final byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);
            final X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
            return KeyFactory.getInstance("RSA").generatePublic(keySpec);
        }
    }
}