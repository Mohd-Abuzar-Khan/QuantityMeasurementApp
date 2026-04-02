package com.app.gateway.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

/**
 * JWT Gateway Filter - Security Layer
 *
 * This filter is executed for EVERY request coming through the gateway.
 * It acts as a security checkpoint:
 * 1. Checks if the request is to a public path (like login, Swagger docs)
 * 2. If public, allows it through
 * 3. If protected, checks for a JWT token
 * 4. Validates the JWT token using the shared secret
 * 5. Only allows the request if the token is valid
 * 6. Forwards the authenticated username downstream via X-Auth-User header
 *
 * Example flow:
 * - Request: POST /api/v1/quantities/add (with Authorization header)
 *   → Filter extracts token
 *   → Filter validates token
 *   → If valid, request continues to the service with X-Auth-User header
 *   → If invalid, request is rejected with 401 UNAUTHORIZED (JSON body)
 */
@Slf4j
@Component
public class JwtGatewayFilter implements GlobalFilter, Ordered {

    private static final String BEARER_PREFIX = "Bearer ";

    // Public path prefixes — requests starting with these bypass the JWT check
    private static final List<String> PUBLIC_PREFIXES = List.of(
            "/api/v1/auth/",
            "/oauth2/",
            "/login/oauth2/",
            "/swagger-ui/",
            "/v3/api-docs/",
            "/webjars/",
            "/actuator/"
    );

    // Exact paths that do not require authentication
    private static final Set<String> PUBLIC_EXACT_PATHS = Set.of("/");

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        log.debug("Gateway filter: {} {}", request.getMethod(), path);

        // ── Step 1: Is this a public path? ────────────────────────────────
        if (isPublic(path)) {
            log.debug("Public path — skipping JWT check: {}", path);
            return chain.filter(exchange);
        }

        // ── Step 2: Extract Bearer token ──────────────────────────────────
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            log.warn("Missing or malformed Authorization header for path: {}", path);
            return unauthorized(exchange, "Missing or malformed Authorization header");
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        // ── Step 3: Validate JWT ───────────────────────────────────────────
        if (!jwtUtil.validateToken(token)) {
            log.warn("Invalid JWT for path: {}", path);
            return unauthorized(exchange, "Invalid or expired JWT token");
        }

        // ── Step 4: Forward with username header ──────────────────────────
        String username = jwtUtil.getUsernameFromToken(token);
        log.debug("JWT valid for user '{}' — forwarding to {}", username, path);

        ServerHttpRequest mutatedRequest = exchange.getRequest()
                .mutate()
                .header("X-Auth-User", username)   // Downstream services read this
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    @Override
    public int getOrder() {
        return -100;  // Runs very early — before all other filters (security first)
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    /**
     * Check if a path is public (doesn't require JWT).
     * Matches exact paths (e.g. "/") or prefix paths (e.g. "/api/v1/auth/").
     */
    private boolean isPublic(String path) {
        return PUBLIC_EXACT_PATHS.contains(path)
                || PUBLIC_PREFIXES.stream().anyMatch(path::startsWith);
    }

    /**
     * Returns a 401 Unauthorized response with a JSON error body.
     */
    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = String.format(
            "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"%s\"}", message);

        var buffer = response.bufferFactory().wrap(body.getBytes());
        return response.writeWith(Mono.just(buffer));
    }
}
