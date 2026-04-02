package com.quantity.app.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * REST request body wrapping two quantity operands.
 *
 * Example JSON:
 * {
 *   "thisQuantityDTO": { "value": 1.0, "unit": "FEET",   "measurementType": "LengthUnit" },
 *   "thatQuantityDTO": { "value": 12.0,"unit": "INCHES", "measurementType": "LengthUnit" }
 * }
 */
@Data
@NoArgsConstructor
public class QuantityInputDTO {

    @Valid @NotNull(message = "thisQuantityDTO must not be null")
    private QuantityDTO thisQuantityDTO;

    @Valid @NotNull(message = "thatQuantityDTO must not be null")
    private QuantityDTO thatQuantityDTO;
}
