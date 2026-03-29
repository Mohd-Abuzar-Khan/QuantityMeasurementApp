package com.app.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Centralised exception handling for all REST controllers.
 *
 * Three handler methods cover the main failure modes:
 * <ol>
 *   <li>{@code handleValidationException} – Bean Validation constraint violations
 *       (invalid unit names, null values, wrong measurement type, etc.).</li>
 *   <li>{@code handleQuantityException} – Known domain errors from the service layer.</li>
 *   <li>{@code handleGlobalException} – Catch-all for unexpected runtime errors.</li>
 * </ol>
 *
 * Every response follows the same JSON structure:
 * <pre>
 * {
 *   "timestamp": "...",
 *   "status":    400,
 *   "error":     "Quantity Measurement Error",
 *   "message":   "...",
 *   "path":      "/api/v1/quantities/..."
 * }
 * </pre>
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // ── Bean Validation failures (@Valid on @RequestBody) ─────────────────

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {

        // Collect the first failing field's message (or all, joined)
        String message = ex.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .findFirst()
            .orElse("Validation failed");

        log.warn("Validation error: {}", message);
        return buildResponse(HttpStatus.BAD_REQUEST, "Quantity Measurement Error",
                             message, describeRequest(request));
    }

    // ── Domain exceptions from the service layer ──────────────────────────

    @ExceptionHandler(QuantityMeasurementException.class)
    public ResponseEntity<Map<String, Object>> handleQuantityException(
            QuantityMeasurementException ex, WebRequest request) {

        log.warn("Quantity measurement error: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "Quantity Measurement Error",
                             ex.getMessage(), describeRequest(request));
    }

    // ── Catch-all ─────────────────────────────────────────────────────────

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(
            Exception ex, WebRequest request) {

        log.error("Unexpected error", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                             ex.getMessage(), describeRequest(request));
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private ResponseEntity<Map<String, Object>> buildResponse(
            HttpStatus status, String error, String message, String path) {

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status",    status.value());
        body.put("error",     error);
        body.put("message",   message);
        body.put("path",      path);
        return ResponseEntity.status(status).body(body);
    }

    private String describeRequest(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}