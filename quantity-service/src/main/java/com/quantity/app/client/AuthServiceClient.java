package com.quantity.app.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * OpenFeign Client — measurement-service → auth-service
 *
 * This interface demonstrates interservice communication.
 * Spring generates the full HTTP client at runtime — you write zero HTTP code.
 *
 * Usage:
 *   The name "auth-service" must match spring.application.name in auth-service.
 *   Feign asks Eureka for auth-service's current address, then calls it.
 *
 * Fallback:
 *   If auth-service is unreachable, AuthServiceClientFallback is used
 *   so measurement operations still work (graceful degradation).
 *
 * Example call flow:
 *   measurement-service calls authServiceClient.getUserByUsername("john")
 *   → Feign looks up auth-service in Eureka
 *   → Feign calls GET http://192.168.x.x:8082/api/v1/auth/users/john
 *   → auth-service responds with user details
 */
@FeignClient(
    name     = "auth-service",
    fallback = AuthServiceClientFallback.class
)
public interface AuthServiceClient {

    /**
     * Example endpoint — fetch basic user info from auth-service.
     * Add more methods here as your integration grows.
     */
    @GetMapping("/api/v1/auth/users/{username}")
    AuthUserDTO getUserByUsername(@PathVariable("username") String username);
}
