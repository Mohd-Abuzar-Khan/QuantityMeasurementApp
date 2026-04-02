package com.quantity.app.dto;




import com.quantity.app.unit.IMeasurable;
import com.quantity.app.unit.LengthUnit;
import com.quantity.app.unit.TemperatureUnit;
import com.quantity.app.unit.VolumeUnit;
import com.quantity.app.unit.WeightUnit;

import jakarta.validation.constraints.*;
import lombok.*;

// ─────────────────────────────────────────────────────────────────────────────
// QuantityDTO  —  single operand: value + unit + type
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Represents one quantity: a numeric value, its unit, and the measurement type.
 *
 * Example JSON:
 * { "value": 100.0, "unit": "METERS", "measurementType": "LengthUnit" }
 */
@Data
@NoArgsConstructor
public class QuantityDTO {

    @NotNull(message = "Value must not be null")
    private Double value;

    @NotEmpty(message = "Unit must not be empty")
    private String unit;

    @NotEmpty(message = "Measurement type must not be empty")
    @Pattern(
        regexp = "LengthUnit|WeightUnit|VolumeUnit|TemperatureUnit",
        message = "measurementType must be one of: LengthUnit, WeightUnit, VolumeUnit, TemperatureUnit"
    )
    private String measurementType;

    public QuantityDTO(Double value, String unit, String measurementType) {
        this.value           = value;
        this.unit            = unit;
        this.measurementType = measurementType;
    }

    /**
     * Resolves the IMeasurable enum constant for this DTO.
     * Throws IllegalArgumentException if unit name is invalid for the type.
     */
    public IMeasurable resolveIMeasurable() {
        String u = unit.toUpperCase();
        switch (measurementType) {
            case "LengthUnit":      return LengthUnit.valueOf(u);
            case "WeightUnit":      return WeightUnit.valueOf(u);
            case "VolumeUnit":      return VolumeUnit.valueOf(u);
            case "TemperatureUnit": return TemperatureUnit.valueOf(u);
            default: throw new IllegalArgumentException("Unknown measurement type: " + measurementType);
        }
    }

    /** Cross-field validation: unit must be valid for the declared type. */
    @AssertTrue(message = "Unit must be valid for the specified measurementType")
    public boolean isUnitValidForMeasurementType() {
        if (unit == null || measurementType == null) return true;
        try { resolveIMeasurable(); return true; } catch (Exception e) { return false; }
    }

    @Override
    public String toString() { return value + " " + unit + " [" + measurementType + "]"; }
}
