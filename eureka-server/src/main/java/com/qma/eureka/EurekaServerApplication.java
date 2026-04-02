package com.qma.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Eureka Service Registry
 *
 * This is the "phone book" of our microservices system.
 * All other services (auth-service, measurement-service, api-gateway)
 * register themselves here on startup so they can discover each other.
 *
 * Access the Eureka Dashboard at: http://localhost:8761
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
