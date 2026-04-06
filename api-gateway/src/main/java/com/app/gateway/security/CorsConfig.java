package com.app.gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * CORS Configuration for API Gateway
 * Handles Cross-Origin Resource Sharing for all incoming requests
 */
@Configuration
public class CorsConfig {

    /**
     * Configure CORS for the reactive gateway
     * Allows requests from:
     * - Frontend deployment: https://quantitymeasurementapp-jivh.onrender.com
     * - Auth server: https://auth-server-sks7.onrender.com
     * - Local development: http://localhost:4200, http://localhost:8080
     * - Vercel deployment (legacy): https://quantity-measurement-app-frontend-eta.vercel.app
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();

        // Set allowed origins
        corsConfig.setAllowedOrigins(Arrays.asList(
            "https://quantitymeasurementapp-jivh.onrender.com",
            "https://auth-server-sks7.onrender.com",
            "https://api-gateway-g587.onrender.com",
            "https://quantity-measurement-app-frontend-eta.vercel.app",
            "http://localhost:4200",
            "http://localhost:8080",
            "http://127.0.0.1:4200",
            "http://127.0.0.1:8080"
        ));

        // Set allowed HTTP methods
        corsConfig.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"
        ));

        // Set allowed headers
        corsConfig.setAllowedHeaders(Arrays.asList(
            "Content-Type",
            "Authorization",
            "X-Requested-With",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers",
            "X-CSRF-Token"
        ));

        // Allow credentials
        corsConfig.setAllowCredentials(true);

        // Set max age for preflight cache
        corsConfig.setMaxAge(3600L);

        // Set exposed headers for client access
        corsConfig.setExposedHeaders(Arrays.asList(
            "Content-Type",
            "Authorization",
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials"
        ));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}

