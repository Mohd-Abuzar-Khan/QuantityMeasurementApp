package com.app.controller;

import com.app.dto.QuantityDTO;
import com.app.dto.QuantityInputDTO;
import com.app.dto.QuantityMeasurementDTO;
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

/**
 * REST controller for all quantity-measurement operations.
 *
 * <p>Base path: {@code /api/v1/quantities}
 *
 * <h3>POST endpoints (operations)</h3>
 * <ul>
 *   <li>POST /compare  – Compare two quantities for equality (same physical value).</li>
 *   <li>POST /convert  – Convert thisQuantity to the unit of thatQuantity.</li>
 *   <li>POST /add      – Add two quantities; result expressed in thisQuantity's unit.</li>
 *   <li>POST /subtract – Subtract thatQuantity from thisQuantity.</li>
 *   <li>POST /divide   – Divide thisQuantity by thatQuantity; returns dimensionless ratio.</li>
 * </ul>
 *
 * <h3>GET endpoints (history/analytics)</h3>
 * <ul>
 *   <li>GET /history/operation/{operation}     – All records for a given operation type.</li>
 *   <li>GET /history/type/{measurementType}    – All records for a given measurement type.</li>
 *   <li>GET /count/{operation}                 – Count of successful operations by type.</li>
 *   <li>GET /history/errored                   – All records where isError = true.</li>
 * </ul>
 *
 * <p>Each operation delegates entirely to {@link IQuantityMeasurementService};
 * this controller is intentionally thin (no business logic).
 * Domain exceptions propagate to {@link com.app.exception.GlobalExceptionHandler}.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/quantities")
@RequiredArgsConstructor
@Tag(name = "Quantity Measurements", description = "REST API for unit-aware quantity measurement operations")
public class QuantityMeasurementController {

    private final IQuantityMeasurementService service;

    // ── Mutation / operation endpoints ────────────────────────────────────────

    /**
     * Compares two quantities for physical equality after unit conversion.
     *
     * @param input Payload containing {@code thisQuantityDTO} and {@code thatQuantityDTO}.
     * @return DTO with {@code resultString} = "true" or "false".
     */
    @PostMapping("/compare")
    @Operation(
        summary     = "Compare two quantities",
        description = "Returns a result DTO with resultString='true' or 'false'."
    )
    public ResponseEntity<QuantityMeasurementDTO> compareQuantities(
            @Valid @RequestBody QuantityInputDTO input) {

        log.info("POST /compare  input={}", input);
        QuantityMeasurementDTO result =
                service.compare(input.getThisQuantityDTO(), input.getThatQuantityDTO());
        return ResponseEntity.ok(result);
    }

    /**
     * Converts thisQuantity to the unit specified by thatQuantityDTO.
     *
     * @param input Payload where {@code thatQuantityDTO.unit} is the target unit.
     * @return DTO with the converted value and target unit.
     */
    @PostMapping("/convert")
    @Operation(
        summary     = "Convert a quantity to another unit",
        description = "Converts 'thisQuantityDTO' to the unit specified by 'thatQuantityDTO'."
    )
    public ResponseEntity<QuantityMeasurementDTO> convertQuantity(
            @Valid @RequestBody QuantityInputDTO input) {

        log.info("POST /convert  input={}", input);
        QuantityMeasurementDTO result =
                service.convert(input.getThisQuantityDTO(), input.getThatQuantityDTO());
        return ResponseEntity.ok(result);
    }

    /**
     * Adds two quantities; the result is expressed in thisQuantity's unit.
     *
     * <p>A synthetic target DTO is constructed with value=0 so the service
     * knows which unit to express the result in without the client having to
     * specify it explicitly.
     */
    @PostMapping("/add")
    @Operation(
        summary     = "Add two quantities",
        description = "Result expressed in the unit of 'thisQuantityDTO'."
    )
    public ResponseEntity<QuantityMeasurementDTO> addQuantities(
            @Valid @RequestBody QuantityInputDTO input) {

        log.info("POST /add  input={}", input);

        // Build a zero-valued target in thisQuantity's unit to direct where the result lands
        QuantityDTO target = new QuantityDTO(
                0.0,
                input.getThisQuantityDTO().getUnit(),
                input.getThisQuantityDTO().getMeasurementType());

        return ResponseEntity.ok(
                service.add(input.getThisQuantityDTO(), input.getThatQuantityDTO(), target));
    }

    /**
     * Subtracts thatQuantity from thisQuantity; result in thisQuantity's unit.
     */
    @PostMapping("/subtract")
    @Operation(
        summary     = "Subtract two quantities",
        description = "Result expressed in the unit of 'thisQuantityDTO'."
    )
    public ResponseEntity<QuantityMeasurementDTO> subtractQuantities(
            @Valid @RequestBody QuantityInputDTO input) {

        log.info("POST /subtract  input={}", input);

        QuantityDTO target = new QuantityDTO(
                0.0,
                input.getThisQuantityDTO().getUnit(),
                input.getThisQuantityDTO().getMeasurementType());

        return ResponseEntity.ok(
                service.subtract(input.getThisQuantityDTO(), input.getThatQuantityDTO(), target));
    }

    /**
     * Divides thisQuantity by thatQuantity; returns a dimensionless ratio.
     */
    @PostMapping("/divide")
    @Operation(
        summary     = "Divide two quantities",
        description = "Returns a dimensionless ratio in resultValue."
    )
    public ResponseEntity<QuantityMeasurementDTO> divideQuantities(
            @Valid @RequestBody QuantityInputDTO input) {

        log.info("POST /divide  input={}", input);
        return ResponseEntity.ok(
                service.divide(input.getThisQuantityDTO(), input.getThatQuantityDTO()));
    }

    // ── Query / analytics endpoints ───────────────────────────────────────────

    /**
     * Returns all persisted measurement records for a given operation name.
     *
     * @param operation One of ADD, SUBTRACT, DIVIDE, COMPARE, CONVERT (case-insensitive).
     */
    @GetMapping("/history/operation/{operation}")
    @Operation(
        summary     = "Get operation history by type",
        description = "Returns all persisted records for operations like ADD, COMPARE, CONVERT."
    )
    public ResponseEntity<List<QuantityMeasurementDTO>> getOperationHistory(
            @Parameter(description = "Operation name, e.g. ADD, COMPARE, CONVERT")
            @PathVariable String operation) {

        log.info("GET /history/operation/{}", operation);
        return ResponseEntity.ok(service.getHistoryByOperation(operation));
    }

    /**
     * Returns all records whose first operand belongs to the given measurement type.
     *
     * @param measurementType e.g. LengthUnit, WeightUnit, VolumeUnit, TemperatureUnit.
     */
    @GetMapping("/history/type/{measurementType}")
    @Operation(
        summary     = "Get history by measurement type",
        description = "Returns all records whose first operand belongs to the given type."
    )
    public ResponseEntity<List<QuantityMeasurementDTO>> getHistoryByType(
            @Parameter(description = "Measurement type, e.g. LengthUnit, WeightUnit")
            @PathVariable String measurementType) {

        log.info("GET /history/type/{}", measurementType);
        return ResponseEntity.ok(service.getHistoryByMeasurementType(measurementType));
    }

    /**
     * Returns the count of successful (non-error) operations of the given type.
     */
    @GetMapping("/count/{operation}")
    @Operation(summary = "Count successful operations by type")
    public ResponseEntity<Long> getOperationCount(@PathVariable String operation) {

        log.info("GET /count/{}", operation);
        return ResponseEntity.ok(service.getOperationCount(operation));
    }

    /**
     * Returns all records where {@code isError = true}.
     */
    @GetMapping("/history/errored")
    @Operation(
        summary     = "Get all error records",
        description = "Returns all records where isError = true."
    )
    public ResponseEntity<List<QuantityMeasurementDTO>> getErrorHistory() {

        log.info("GET /history/errored");
        return ResponseEntity.ok(service.getErrorHistory());
    }
}
