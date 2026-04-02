package com.quantity.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Measurement Microservice — Port 8081
 *
 * Responsibilities:
 *   - All quantity operations: compare, convert, add, subtract, divide
 *   - Supported types: LengthUnit, WeightUnit, VolumeUnit, TemperatureUnit
 *   - Persists operation history to its own H2 database (measurement_db)
 *   - Receives authenticated requests via the API Gateway
 *   - Reads the X-Auth-User header (injected by gateway) for user context
 *
 * Service Discovery:
 *   - Registers with Eureka as "measurement-service"
 *   - Uses OpenFeign to call auth-service (if needed) via Eureka
 * * Startup order:
 *   1. eureka-server  (port 8761)
 *   2. auth-service   (port 8082)
 *   3. measurement-service (port 8081)  ← this service
 *   4. api-gateway    (port 8080)
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class MeasurementServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MeasurementServiceApplication.class, args);
    }
}
