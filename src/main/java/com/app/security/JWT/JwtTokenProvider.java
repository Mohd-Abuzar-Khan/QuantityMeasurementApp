package com.app.security.JWT;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Utility component for generating, parsing, and validating JWT tokens.
 *
 * <h3>Configuration (application.properties):</h3>
 * <pre>
 *   app.jwt.secret        – HMAC-SHA256 signing key (minimum 32 characters).
 *                           In production, supply via environment variable:
 *                           app.jwt.secret=${JWT_SECRET}
 *   app.jwt.expiration-ms – Token lifetime in milliseconds (default: 3 600 000 = 1 hour).
 * </pre>
 *
 * <h3>Token structure:</h3>
 * <ul>
 *   <li>Subject  – the authenticated username.</li>
 *   <li>IssuedAt – timestamp of generation.</li>
 *   <li>Expiry   – {@code issuedAt + jwtExpirationMs}.</li>
 *   <li>Signed with HMAC-SHA256 ({@code HS256}).</li>
 * </ul>
 *
 * <p><b>Deduplication note:</b> The original codebase contained two identical
 * copies of this class.  The duplicate has been removed; this is the single
 * canonical version.
 */
@Slf4j
@Component
public class JwtTokenProvider {

    /** HMAC-SHA256 signing secret, injected from application properties. */
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    /** Token lifetime in milliseconds (default 1 hour). */
    @Value("${app.jwt.expiration-ms:3600000}")
    private long jwtExpirationMs;

    // ── Token generation ──────────────────────────────────────────────────────

    /**
     * Generates a JWT for an already-authenticated principal.
     *
     * @param authentication The successful authentication object from Spring Security.
     * @return A signed, compact JWT string.
     */
    public String generateToken(Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        return buildToken(username);
    }

    /**
     * Generates a JWT directly from a username string.
     * Used for OAuth2 flows and post-registration immediate login.
     *
     * @param username The subject to embed in the token.
     * @return A signed, compact JWT string.
     */
    public String generateToken(String username) {
        return buildToken(username);
    }

    // ── Token parsing ─────────────────────────────────────────────────────────

    /**
     * Extracts the username (subject) from a valid token.
     *
     * @param token A JWT string; must be valid (call {@link #validateToken} first).
     * @return The username embedded in the token's subject claim.
     */
    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Validates a JWT string.
     *
     * <p>Returns {@code false} – instead of throwing – for any token problem so
     * that the JWT filter can silently reject bad tokens without breaking the
     * filter chain.
     *
     * @param token The JWT string to validate.
     * @return {@code true} if the token is well-formed, signed correctly, and not expired.
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("JWT algorithm unsupported: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("JWT malformed: {}", e.getMessage());
        } catch (SecurityException e) {
            log.warn("JWT signature invalid: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string empty: {}", e.getMessage());
        }
        return false;
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /**
     * Constructs and signs the JWT payload.
     */
    private String buildToken(String subject) {
        Date now    = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(subject)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey())
                .compact();
    }

    /**
     * Parses and verifies the token, returning the claims payload.
     * Throws a JJWT exception on any problem (expired, malformed, invalid signature).
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Derives the HMAC-SHA256 {@link SecretKey} from the configured secret string.
     * Called per operation; the key is lightweight to reconstruct.
     */
    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
}
