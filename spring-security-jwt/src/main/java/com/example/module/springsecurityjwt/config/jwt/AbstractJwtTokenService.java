package com.example.module.springsecurityjwt.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;

import java.security.Key;
import java.util.Date;

@Slf4j
public abstract class AbstractJwtTokenService implements JwtTokenService {

    @Override
    public String[] extractUsernameAndRole(String token) {
        Claims claims = extractAllClaims(token);
        return new String[]{
                claims.getSubject(),
                claims.get("role", String.class)
        };
    }

    @Override
    public boolean validate(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("Token has expired", e);
            throw new IllegalArgumentException("Token has expired");
        } catch (UnsupportedOperationException e) {
            log.error("Token is not signed", e);
            throw new IllegalArgumentException("Token is not signed");
        } catch (MalformedJwtException e) {
            log.error("Token is malformed", e);
            throw new IllegalArgumentException("Token is malformed");
        } catch (SignatureException | SecurityException e) {
            log.error("Invalid JWT Signature", e);
            throw new IllegalArgumentException("Invalid JWT Signature");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty", e);
            throw new IllegalArgumentException("JWT claims string is empty");
        }
    }

    protected Claims extractAllClaims(String token) {
        JwtParserBuilder parserBuilder = Jwts.parser();
        Key verificationKey = getVerificationKey();
        if (verificationKey instanceof java.security.PublicKey) {
            parserBuilder.verifyWith((java.security.PublicKey) verificationKey);
        } else if (verificationKey instanceof javax.crypto.SecretKey) {
            parserBuilder.verifyWith((javax.crypto.SecretKey) verificationKey);
        }
        return parserBuilder
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    protected String buildToken(String username, String role, Key signingKey) {
        final Date now = new Date();
        final Date expiration = new Date(System.currentTimeMillis() + 15 * 60 * 1000);

        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiration)
                .issuer("stock-saas-app")
                .signWith(signingKey)
                .compact();
    }

    protected abstract Key getVerificationKey();
}
