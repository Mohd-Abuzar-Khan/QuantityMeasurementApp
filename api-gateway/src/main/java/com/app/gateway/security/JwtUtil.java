package com.app.gateway.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

/**
 * JWT utility used by the API Gateway to validate tokens
 * before routing requests to downstream microservices.
 *
 * The secret must match the one configured in auth-service.
 * Secret is expected as a Base64-encoded string (from application.properties jwt.secret).
 */
@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * Validates the JWT token.
     * Returns true if valid; false for expired, malformed, or invalid signatures.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("Gateway: JWT expired — {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("Gateway: JWT unsupported — {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("Gateway: JWT malformed — {}", e.getMessage());
        } catch (SecurityException e) {
            log.warn("Gateway: JWT signature invalid — {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("Gateway: JWT empty — {}", e.getMessage());
        }
        return false;
    }

    /**
     * Extracts the username (subject) from a valid JWT.
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    private SecretKey signingKey() {
        // Changed: match auth-service which uses raw UTF-8 bytes
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
}
