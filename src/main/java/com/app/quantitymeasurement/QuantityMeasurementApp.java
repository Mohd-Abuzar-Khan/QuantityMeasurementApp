package com.app.quantitymeasurement;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.app") 
@OpenAPIDefinition(
    info = @Info(
        title       = "Quantity Measurement API",
        version     = "1.0",
        description = "REST API for unit-aware quantity measurement operations (UC17)"
    )
)
@EnableJpaRepositories(basePackages = "com.app.repository")
@EntityScan(basePackages = "com.app.model")
public class QuantityMeasurementApp {

    public static void main(String[] args) {
        SpringApplication.run(QuantityMeasurementApp.class, args);
    }
}
