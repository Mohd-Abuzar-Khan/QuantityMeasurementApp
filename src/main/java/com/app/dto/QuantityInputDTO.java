package com.app.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * REST request body for all quantity measurement operations.
 * Wraps the two operand DTOs so a single JSON object can carry both inputs.
 *
 * <pre>
 * {
 *   "thisQuantityDTO": { "value": 1.0, "unit": "FEET",   "measurementType": "LengthUnit" },
 *   "thatQuantityDTO": { "value": 12.0,"unit": "INCHES", "measurementType": "LengthUnit" }
 * }
 * </pre>
 */
@Data
@NoArgsConstructor
public class QuantityInputDTO {

    @Valid
    @NotNull(message = "thisQuantityDTO must not be null")
    private QuantityDTO thisQuantityDTO;

    @Valid
    @NotNull(message = "thatQuantityDTO must not be null")
    private QuantityDTO thatQuantityDTO;
}