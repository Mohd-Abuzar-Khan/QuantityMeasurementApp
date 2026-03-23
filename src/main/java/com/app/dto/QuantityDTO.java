package com.app.dto;

import com.app.unit.IMeasurable;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing a single quantity (value + unit).
 * Used as the leaf input in {@link QuantityInputDTO} for REST requests.
 * Carries its own validation constraints so Spring can reject bad input
 * before the request reaches the service layer.
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
        message = "Measurement type must be one of: LengthUnit, WeightUnit, VolumeUnit, TemperatureUnit"
    )
    private String measurementType;

    public QuantityDTO(Double value, String unit, String measurementType) {
        this.value           = value;
        this.unit            = unit;
        this.measurementType = measurementType;
    }

    /**
     * Cross-field validation: the unit name must be valid for the declared
     * measurement type.  Annotated with {@code @AssertTrue} so Bean Validation
     * calls it automatically.
     */
    @AssertTrue(message = "Unit must be valid for the specified measurement type")
    public boolean isUnitValidForMeasurementType() {
        if (unit == null || measurementType == null) return true; // covered by @NotEmpty
        try {
            resolveIMeasurable();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public IMeasurable resolveIMeasurable() {
        String u = unit.toUpperCase();
        switch (measurementType) {
            case "LengthUnit":      return com.app.unit.LengthUnit.valueOf(u);
            case "WeightUnit":      return com.app.unit.WeightUnit.valueOf(u);
            case "VolumeUnit":      return com.app.unit.VolumeUnit.valueOf(u);
            case "TemperatureUnit": return com.app.unit.TemperatureUnit.valueOf(u);
            default: throw new IllegalArgumentException("Unknown measurement type: " + measurementType);
        }
    }

    @Override
    public String toString() {
        return value + " " + unit + " [" + measurementType + "]";
    }
}