package com.quantity.app.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Fallback implementation for AuthServiceClient.
 *
 * If auth-service is unreachable or returns an error, Feign calls this instead.
 * Measurement operations CONTINUE to work — we just can't enrich with user data.
 *
 * This is the "Resilience" principle:
 *   "If user-service crashes, conversions still work."
 *
 * To enable circuit-breaker fallback, add Resilience4j dependency and set:
 *   feign.circuitbreaker.enabled=true  in application.yml
 */
@Slf4j
@Component
public class AuthServiceClientFallback implements AuthServiceClient {

    @Override
    public AuthUserDTO getUserByUsername(String username) {
        log.warn("auth-service unavailable — returning empty user for '{}'", username);
        // Return a safe empty object instead of throwing
        return new AuthUserDTO(null, username, null);
    }
}
