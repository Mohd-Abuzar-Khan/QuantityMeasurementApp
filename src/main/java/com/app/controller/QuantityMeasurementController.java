package com.app.controller;

import com.app.dto.QuantityDTO;
import com.app.dto.QuantityInputDTO;
import com.app.dto.QuantityMeasurementDTO;
import com.app.exception.QuantityMeasurementException;
import com.app.service.IQuantityMeasurementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/quantities")
@RequiredArgsConstructor
@Tag(name = "Quantity Measurements", description = "REST API for quantity measurement operations")
public class QuantityMeasurementController {

    private final IQuantityMeasurementService service;

    // ── POST endpoints ────────────────────────────────────────────────────

    @PostMapping("/compare")
    @Operation(summary = "Compare two quantities",
               description = "Returns a result DTO with resultString='true'/'false'.")
    public ResponseEntity<QuantityMeasurementDTO> compareQuantities( @Valid @RequestBody QuantityInputDTO input) {
        log.info("POST /compare  {}", input);
        QuantityMeasurementDTO result =
            service.compare(input.getThisQuantityDTO(), input.getThatQuantityDTO());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/convert")
    @Operation(summary = "Convert a quantity to another unit",
               description = "Converts 'thisQuantityDTO' to the unit specified by 'thatQuantityDTO'.")
    public ResponseEntity<QuantityMeasurementDTO> convertQuantity(
            @Valid @RequestBody QuantityInputDTO input) {
        log.info("POST /convert  {}", input);
        QuantityMeasurementDTO result =
            service.convert(input.getThisQuantityDTO(), input.getThatQuantityDTO());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/add")
    @Operation(summary = "Add two quantities",
               description = "Result expressed in the unit of 'thisQuantityDTO'.")
    public ResponseEntity<QuantityMeasurementDTO> addQuantities(
            @Valid @RequestBody QuantityInputDTO input) {
        log.info("POST /add  {}", input);
        QuantityDTO target = new QuantityDTO(
            0.0,
            input.getThisQuantityDTO().getUnit(),
            input.getThisQuantityDTO().getMeasurementType());
        QuantityMeasurementDTO result =
            service.add(input.getThisQuantityDTO(), input.getThatQuantityDTO(), target);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/subtract")
    @Operation(summary = "Subtract two quantities",
               description = "Result expressed in the unit of 'thisQuantityDTO'.")
    public ResponseEntity<QuantityMeasurementDTO> subtractQuantities(
            @Valid @RequestBody QuantityInputDTO input) {
        log.info("POST /subtract  {}", input);
        QuantityDTO target = new QuantityDTO(
            0.0,
            input.getThisQuantityDTO().getUnit(),
            input.getThisQuantityDTO().getMeasurementType());
        QuantityMeasurementDTO result =
            service.subtract(input.getThisQuantityDTO(), input.getThatQuantityDTO(), target);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/divide")
    @Operation(summary = "Divide two quantities",
               description = "Returns a dimensionless ratio in resultValue.")
    public ResponseEntity<QuantityMeasurementDTO> divideQuantities(
            @Valid @RequestBody QuantityInputDTO input) {
        log.info("POST /divide  {}", input);
        QuantityMeasurementDTO result =
            service.divide(input.getThisQuantityDTO(), input.getThatQuantityDTO());
        return ResponseEntity.ok(result);
    }

    // ── GET endpoints (history / analytics) ──────────────────────────────

    @GetMapping("/history/operation/{operation}")
    @Operation(summary = "Get operation history by operation type",
               description = "Returns all persisted records for operations like ADD, COMPARE, CONVERT, etc.")
    public ResponseEntity<List<QuantityMeasurementDTO>> getOperationHistory(
            @Parameter(description = "Operation name, e.g. ADD, COMPARE, CONVERT")
            @PathVariable String operation) {
        log.info("GET /history/operation/{}", operation);
        return ResponseEntity.ok(service.getHistoryByOperation(operation));
    }

    @GetMapping("/history/type/{measurementType}")
    @Operation(summary = "Get history by measurement type",
               description = "Returns all records whose first operand belongs to the given type.")
    public ResponseEntity<List<QuantityMeasurementDTO>> getHistoryByType(
            @Parameter(description = "Measurement type, e.g. LengthUnit, WeightUnit")
            @PathVariable String measurementType) {
        log.info("GET /history/type/{}", measurementType);
        return ResponseEntity.ok(service.getHistoryByMeasurementType(measurementType));
    }

    @GetMapping("/count/{operation}")
    @Operation(summary = "Count successful operations by type")
    public ResponseEntity<Long> getOperationCount(
            @PathVariable String operation) {
        log.info("GET /count/{}", operation);
        return ResponseEntity.ok(service.getOperationCount(operation));
    }

    @GetMapping("/history/errored")
    @Operation(summary = "Get all error records",
               description = "Returns all records where isError = true.")
    public ResponseEntity<List<QuantityMeasurementDTO>> getErrorHistory() {
        log.info("GET /history/errored");
        return ResponseEntity.ok(service.getErrorHistory());
    }
}