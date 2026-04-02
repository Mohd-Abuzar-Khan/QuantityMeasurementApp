package com.auth.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Auth Microservice — Port 8082
 *
 * Responsibilities:
 *   - User registration (POST /api/v1/auth/register)
 *   - User login      (POST /api/v1/auth/login)
 *   - JWT issuance
 *   - Google OAuth2 login
 *   - Owns its own H2 database (auth_db) — "database per service" pattern
 *
 * This service registers itself with the Eureka server on startup
 * so the API Gateway can discover it as "auth-service".
 */
@SpringBootApplication
@EnableDiscoveryClient
public class AuthServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
