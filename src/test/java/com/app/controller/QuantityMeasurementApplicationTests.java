package com.app.controller;

import com.app.dto.QuantityDTO;
import com.app.dto.QuantityInputDTO;
import com.app.dto.QuantityMeasurementDTO;
import com.app.quantitymeasurement.QuantityMeasurementApp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(                                          // ← replace @WebMvcTest
	    classes = QuantityMeasurementApp.class        // ← point to your main class
	)
@AutoConfigureMockMvc                                     // ← sets up MockMvc
@ContextConfiguration(
	    classes = QuantityMeasurementApp.class        // ← explicit context
)
class QuantityMeasurementApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/v1/quantities";
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private QuantityInputDTO input(double v1, String u1, String m1,
                                   double v2, String u2, String m2) {
        QuantityInputDTO dto = new QuantityInputDTO();
        dto.setThisQuantityDTO(new QuantityDTO(v1, u1, m1));
        dto.setThatQuantityDTO(new QuantityDTO(v2, u2, m2));
        return dto;
    }

    private ResponseEntity<QuantityMeasurementDTO> post(String endpoint, QuantityInputDTO body) {
        return restTemplate.postForEntity(baseUrl + endpoint, body, QuantityMeasurementDTO.class);
    }

    // ── Compare ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Compare: 1 FEET == 12 INCHES → true")
    void compare_feetEqualsInches() {
        ResponseEntity<QuantityMeasurementDTO> resp =
            post("/compare", input(1.0, "FEET", "LengthUnit", 12.0, "INCHES", "LengthUnit"));

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().getResultString()).isEqualTo("true");
        assertThat(resp.getBody().isError()).isFalse();
    }

    @Test
    @DisplayName("Compare: 1 YARDS == 3 FEET → true")
    void compare_yardsEqualsFeet() {
        ResponseEntity<QuantityMeasurementDTO> resp =
            post("/compare", input(1.0, "YARDS", "LengthUnit", 3.0, "FEET", "LengthUnit"));

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody().getResultString()).isEqualTo("true");
    }

    @Test
    @DisplayName("Compare: 1 KILOGRAM == 1000 GRAM → true")
    void compare_kilogramEqualsGrams() {
        ResponseEntity<QuantityMeasurementDTO> resp =
            post("/compare", input(1.0, "KILOGRAM", "WeightUnit", 1000.0, "GRAM", "WeightUnit"));

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody().getResultString()).isEqualTo("true");
    }

    @Test
    @DisplayName("Compare: 0 CELSIUS == 32 FAHRENHEIT → true")
    void compare_celsiusEqualsFahrenheit() {
        ResponseEntity<QuantityMeasurementDTO> resp =
            post("/compare", input(0.0, "CELSIUS", "TemperatureUnit",
                                  32.0, "FAHRENHEIT", "TemperatureUnit"));

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody().getResultString()).isEqualTo("true");
    }

    // ── Convert ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Convert: 1 FEET → 12 INCHES")
    void convert_feetToInches() {
        ResponseEntity<QuantityMeasurementDTO> resp =
            post("/convert", input(1.0, "FEET", "LengthUnit", 0.0, "INCHES", "LengthUnit"));

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody().getResultValue()).isEqualTo(12.0);
        assertThat(resp.getBody().getResultUnit()).isEqualTo("INCHES");
    }

    @Test
    @DisplayName("Convert: 100 CELSIUS → 212 FAHRENHEIT")
    void convert_celsiusToFahrenheit() {
        ResponseEntity<QuantityMeasurementDTO> resp =
            post("/convert", input(100.0, "CELSIUS", "TemperatureUnit",
                                  0.0, "FAHRENHEIT", "TemperatureUnit"));

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody().getResultValue()).isEqualTo(212.0);
    }

    @Test
    @DisplayName("Convert: 1 GALLON → 3.79 LITRE (approx)")
    void convert_gallonToLitre() {
        ResponseEntity<QuantityMeasurementDTO> resp =
            post("/convert", input(1.0, "GALLON", "VolumeUnit", 0.0, "LITRE", "VolumeUnit"));

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody().getResultValue()).isGreaterThan(3.78);
        assertThat(resp.getBody().getResultValue()).isLessThan(3.80);
    }

    // ── Add ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Add: 1 FEET + 12 INCHES = 2 FEET")
    void add_feetAndInches() {
        ResponseEntity<QuantityMeasurementDTO> resp =
            post("/add", input(1.0, "FEET", "LengthUnit", 12.0, "INCHES", "LengthUnit"));

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody().getResultValue()).isEqualTo(2.0);
        assertThat(resp.getBody().getResultUnit()).isEqualTo("FEET");
    }

    @Test
    @DisplayName("Add: 1 KILOGRAM + 1000 GRAM = 2 KILOGRAM")
    void add_kilogramAndGrams() {
        ResponseEntity<QuantityMeasurementDTO> resp =
            post("/add", input(1.0, "KILOGRAM", "WeightUnit", 1000.0, "GRAM", "WeightUnit"));

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody().getResultValue()).isEqualTo(2.0);
    }

    @Test
    @DisplayName("Add: 1 LITRE + 1000 MILLILITRE = 2 LITRE")
    void add_litreAndMillilitres() {
        ResponseEntity<QuantityMeasurementDTO> resp =
            post("/add", input(1.0, "LITRE", "VolumeUnit", 1000.0, "MILLILITRE", "VolumeUnit"));

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody().getResultValue()).isEqualTo(2.0);
    }

    @Test
    @DisplayName("Add: Temperature addition returns 400")
    void add_temperature_returns400() {
        ResponseEntity<Map> resp = restTemplate.postForEntity(
            baseUrl + "/add",
            input(100.0, "CELSIUS", "TemperatureUnit", 50.0, "CELSIUS", "TemperatureUnit"),
            Map.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resp.getBody().get("message").toString())
            .containsIgnoringCase("does not support addition");
    }

    // ── Subtract ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("Subtract: 10 FEET - 6 INCHES = 9.5 FEET")
    void subtract_feetAndInches() {
        ResponseEntity<QuantityMeasurementDTO> resp =
            post("/subtract", input(10.0, "FEET", "LengthUnit", 6.0, "INCHES", "LengthUnit"));

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody().getResultValue()).isEqualTo(9.5);
    }

    @Test
    @DisplayName("Subtract: 10 KILOGRAM - 5000 GRAM = 5 KILOGRAM")
    void subtract_kilogramAndGrams() {
        ResponseEntity<QuantityMeasurementDTO> resp =
            post("/subtract", input(10.0, "KILOGRAM", "WeightUnit", 5000.0, "GRAM", "WeightUnit"));

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody().getResultValue()).isEqualTo(5.0);
    }

    // ── Divide ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Divide: 10 FEET / 2 FEET = 5.0 ratio")
    void divide_feetByFeet() {
        ResponseEntity<QuantityMeasurementDTO> resp =
            post("/divide", input(10.0, "FEET", "LengthUnit", 2.0, "FEET", "LengthUnit"));

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody().getResultValue()).isEqualTo(5.0);
        assertThat(resp.getBody().getResultUnit()).isEqualTo("RATIO");
    }

    @Test
    @DisplayName("Divide: 24 INCHES / 2 FEET = 1.0 ratio (same in base)")
    void divide_inchesAndFeet() {
        ResponseEntity<QuantityMeasurementDTO> resp =
            post("/divide", input(24.0, "INCHES", "LengthUnit", 2.0, "FEET", "LengthUnit"));

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody().getResultValue()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Divide: by zero returns error response")
    void divide_byZero() {
        ResponseEntity<Map> resp = restTemplate.postForEntity(
            baseUrl + "/divide",
            input(10.0, "FEET", "LengthUnit", 0.0, "INCHES", "LengthUnit"),
            Map.class);

        assertThat(resp.getStatusCode().is4xxClientError()
                || resp.getStatusCode().is5xxServerError()).isTrue();
    }

    // ── History endpoints ──────────────────────────────────────────────────

    @Test
    @DisplayName("GET /history/operation/compare – returns saved records")
    void getHistoryByOperation() {
        // Seed data
        post("/compare", input(1.0, "FEET", "LengthUnit", 12.0, "INCHES", "LengthUnit"));

        ResponseEntity<QuantityMeasurementDTO[]> resp = restTemplate.getForEntity(
            baseUrl + "/history/operation/compare", QuantityMeasurementDTO[].class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).hasSizeGreaterThanOrEqualTo(1);
        assertThat(resp.getBody()[0].getOperation()).isEqualTo("compare");
    }

    @Test
    @DisplayName("GET /history/type/LengthUnit – returns only length records")
    void getHistoryByMeasurementType() {
        post("/add",     input(1.0,  "FEET",     "LengthUnit", 12.0, "INCHES", "LengthUnit"));
        post("/compare", input(1.0,  "KILOGRAM", "WeightUnit", 1000.0, "GRAM", "WeightUnit"));

        ResponseEntity<QuantityMeasurementDTO[]> resp = restTemplate.getForEntity(
            baseUrl + "/history/type/LengthUnit", QuantityMeasurementDTO[].class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).allMatch(
            r -> "LengthUnit".equals(r.getThisMeasurementType()));
    }

    @Test
    @DisplayName("GET /count/compare – returns count of successful compares")
    void getOperationCount() {
        post("/compare", input(1.0, "FEET", "LengthUnit", 1.0,  "FEET",   "LengthUnit"));
        post("/compare", input(1.0, "YARD", "LengthUnit", 3.0,  "FEET",   "LengthUnit"));

        ResponseEntity<Long> resp = restTemplate.getForEntity(
            baseUrl + "/count/compare", Long.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isGreaterThanOrEqualTo(1L);
    }

    @Test
    @DisplayName("GET /history/errored – returns error records only")
    void getErrorHistory() {
        // Trigger an error: incompatible types
        restTemplate.postForEntity(
            baseUrl + "/add",
            input(1.0, "FEET", "LengthUnit", 1.0, "KILOGRAM", "WeightUnit"),
            Map.class);

        ResponseEntity<QuantityMeasurementDTO[]> resp = restTemplate.getForEntity(
            baseUrl + "/history/errored", QuantityMeasurementDTO[].class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isNotEmpty();
        assertThat(resp.getBody()[0].isError()).isTrue();
    }

    // ── Application context ────────────────────────────────────────────────

    @Test
    @DisplayName("Application context loads successfully")
    void contextLoads() {
        // If the context fails to load, this test will fail automatically.
        assertThat(restTemplate).isNotNull();
    }
}