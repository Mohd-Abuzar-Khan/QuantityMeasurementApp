package com.app.gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * Security Configuration for API Gateway
 * Adds security headers to all responses while allowing CORS
 */
@Configuration
public class SecurityHeadersConfig {

    // Allowed origins for CORS
    private static final List<String> ALLOWED_ORIGINS = Arrays.asList(
        "https://quantitymeasurementapp-jivh.onrender.com",
        "https://auth-server-sks7.onrender.com",
        "https://api-gateway-g587.onrender.com",
        "https://quantity-measurement-app-frontend-eta.vercel.app",
        "http://localhost:4200",
        "http://localhost:8080",
        "http://127.0.0.1:4200",
        "http://127.0.0.1:8080"
    );

    /**
     * Add security headers to all HTTP responses
     * Includes:
     * - Referrer-Policy: strict-origin-when-cross-origin
     * - X-Content-Type-Options: nosniff
     * - X-Frame-Options: SAMEORIGIN
     * - X-XSS-Protection: 1; mode=block
     * - Strict-Transport-Security: max-age=31536000; includeSubDomains
     *
     * CORS is now handled by CorsWebFilter bean, but this filter adds extra security headers
     */
    @Bean
    public WebFilter securityHeadersWebFilter() {
        return new WebFilter() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
                // Add security headers to all responses
                exchange.getResponse().getHeaders().add("Referrer-Policy", "strict-origin-when-cross-origin");
                exchange.getResponse().getHeaders().add("X-Content-Type-Options", "nosniff");
                exchange.getResponse().getHeaders().add("X-Frame-Options", "SAMEORIGIN");
                exchange.getResponse().getHeaders().add("X-XSS-Protection", "1; mode=block");
                exchange.getResponse().getHeaders().add("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
                exchange.getResponse().getHeaders().add("Cache-Control", "no-cache, no-store, must-revalidate");


                return chain.filter(exchange);
            }
        };
    }
}

