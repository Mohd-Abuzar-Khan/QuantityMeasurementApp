package com.quantity.app.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration for Quantity Service.
 *
 * Provides custom OpenAPI documentation setup including:
 * - API metadata (title, description, version)
 * - Contact and license information
 * - JWT Bearer token security scheme
 * - Unit conversion and measurement operations documentation
 */
@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Quantity Measurement - Quantity Service API")
                .version("1.0.0")
                .description("REST API for unit-aware quantity measurement operations including comparison, conversion, addition, subtraction, and division of physical quantities.")
                .contact(new Contact()
                    .name("QMA Team")
                    .url("https://quantitymeasurementapp-jivh.onrender.com"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            .components(new Components()
                .addSecuritySchemes("Bearer Authentication", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("JWT token for API authentication")))
            .addSecurityItem(new SecurityRequirement()
                .addList("Bearer Authentication"));
    }
}

