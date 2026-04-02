package com.quantity.app.unit;

/**
 * Contract for all unit enums (LengthUnit, WeightUnit, VolumeUnit, TemperatureUnit).
 *
 * Every unit can:
 *   - Convert a value TO its "base unit" (e.g. FEET for length)
 *   - Convert FROM the base unit back to this unit
 *   - Report its name and measurement type
 *
 * This enables arithmetic: convert both operands to base → operate → convert back.
 */
public interface IMeasurable {

    /** Converts a value in this unit to the shared base unit. */
    double convertToBaseUnit(double value);

    /** Converts a value from the base unit back to this unit. */
    double convertFromBaseUnit(double base);

    /** Returns the canonical name of this unit (enum name). */
    String getUnitName();

    /** Returns the measurement type name, e.g. "LengthUnit". */
    String getMeasurementType();

    /** Looks up a unit constant by name within the same enum. */
    IMeasurable getUnitByName(String name);

    /**
     * Validates whether the given arithmetic operation is supported.
     * Temperature is non-additive; override this in TemperatureUnit to reject arithmetic.
     */
    default void validateOperationSupport(String operation) {
        // Most units support all operations — override in TemperatureUnit
    }
}
