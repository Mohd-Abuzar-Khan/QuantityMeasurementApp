package com.app.gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

/**
 * Security Configuration for API Gateway
 * Adds security headers to all responses
 */
@Configuration
public class SecurityHeadersConfig {

    /**
     * Add security headers to all HTTP responses
     * Includes:
     * - Referrer-Policy: strict-origin-when-cross-origin
     * - X-Content-Type-Options: nosniff
     * - X-Frame-Options: DENY
     * - X-XSS-Protection: 1; mode=block
     * - Strict-Transport-Security: max-age=31536000; includeSubDomains
     */
    @Bean
    public WebFilter securityHeadersWebFilter() {
        return (exchange, chain) -> {
            exchange.getResponse().getHeaders().add("Referrer-Policy", "strict-origin-when-cross-origin");
            exchange.getResponse().getHeaders().add("X-Content-Type-Options", "nosniff");
            exchange.getResponse().getHeaders().add("X-Frame-Options", "DENY");
            exchange.getResponse().getHeaders().add("X-XSS-Protection", "1; mode=block");
            exchange.getResponse().getHeaders().add("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
            exchange.getResponse().getHeaders().add("Content-Security-Policy", "default-src 'self'");
            
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                // Headers are already added above
            }));
        };
    }
}

