package com.app.gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Security Configuration for API Gateway
 * Adds security headers to all responses while allowing CORS
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
     *
     * Note: CORS preflight requests (OPTIONS) are handled separately by Spring
     */
    @Bean
    public WebFilter securityHeadersWebFilter() {
        return new WebFilter() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
                // Handle CORS preflight requests (OPTIONS)
                if (HttpMethod.OPTIONS.equals(exchange.getRequest().getMethod())) {
                    exchange.getResponse().getHeaders().add("Access-Control-Allow-Origin", exchange.getRequest().getHeaders().getOrigin());
                    exchange.getResponse().getHeaders().add("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS,PATCH");
                    exchange.getResponse().getHeaders().add("Access-Control-Allow-Headers", exchange.getRequest().getHeaders().getFirst("Access-Control-Request-Headers") != null ?
                        exchange.getRequest().getHeaders().getFirst("Access-Control-Request-Headers") : "*");
                    exchange.getResponse().getHeaders().add("Access-Control-Allow-Credentials", "true");
                    exchange.getResponse().getHeaders().add("Access-Control-Max-Age", "3600");
                    return exchange.getResponse().setComplete();
                }

                // Add security headers to all responses
                exchange.getResponse().getHeaders().add("Referrer-Policy", "strict-origin-when-cross-origin");
                exchange.getResponse().getHeaders().add("X-Content-Type-Options", "nosniff");
                exchange.getResponse().getHeaders().add("X-Frame-Options", "DENY");
                exchange.getResponse().getHeaders().add("X-XSS-Protection", "1; mode=block");
                exchange.getResponse().getHeaders().add("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
                exchange.getResponse().getHeaders().add("Cache-Control", "no-cache, no-store, must-revalidate");

                // Note: Removed overly restrictive CSP for API responses
                // APIs don't typically need CSP as they return JSON, not HTML

                return chain.filter(exchange);
            }
        };
    }
}

