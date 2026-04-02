package com.app.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * API Gateway Application - Single Entry Point
 *
 * This gateway acts as a single entry point for all client requests.
 * Instead of calling individual services directly, clients call the gateway.
 * The gateway then routes requests to the appropriate service.
 *
 * Key responsibilities:
 * 1. Receives all incoming requests from clients
 * 2. Validates JWT tokens (security)
 * 3. Routes requests to the correct service using service names
 * 4. Handles load balancing through Eureka
 *
 * Routes configured:
 * - /api/v1/auth/** → routes to auth-service
 * - /api/v1/quantities/** → routes to qma-service
 */
@SpringBootApplication
public class ApiGatewayApplication {

    // Main entry point - starts the Spring Boot application
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}

