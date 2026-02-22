package com.app.quantitymeasurement;

import java.util.Objects;

public class Length {

    private static final double ROUNDING_FACTOR = 100.0;

    private final double value;
    private final LengthUnit unit;

    public Length(double value, LengthUnit unit) {
        validateValue(value);
        validateUnit(unit);
        this.value = value;
        this.unit = unit;
    }

    // Convert to another unit
    public Length convertTo(LengthUnit targetUnit) {
        validateUnit(targetUnit);

        double baseValue = this.unit.convertToBaseUnit(this.value);
        double converted = targetUnit.convertFromBaseUnit(baseValue);

        return new Length(round(converted), targetUnit);
    }

    public static double convert(double value, LengthUnit source, LengthUnit target) {

        validateValue(value);
        validateUnit(source);
        validateUnit(target);
        if (source == target) {
            return value;
        }

        return value * (source.getConversionFactor() / target.getConversionFactor());
    }

    // Addition (default target = this.unit)
    public Length add(Length other) {
        validateOperand(other);

        double base1 = this.unit.convertToBaseUnit(this.value);
        double base2 = other.unit.convertToBaseUnit(other.value);

        double baseSum = base1 + base2;
        double result = this.unit.convertFromBaseUnit(baseSum);

        return new Length(round(result), this.unit);
    }

    // Addition with explicit target unit
    public static Length add(Length l1, Length l2, LengthUnit targetUnit) {
        validateOperand(l1);
        validateOperand(l2);
        validateUnit(targetUnit);

        double base1 = l1.unit.convertToBaseUnit(l1.value);
        double base2 = l2.unit.convertToBaseUnit(l2.value);

        double baseSum = base1 + base2;
        double result = targetUnit.convertFromBaseUnit(baseSum);

        return new Length(round(result), targetUnit);
    }

    // Equality
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Length)) return false;

        Length other = (Length) obj;

        double base1 = this.unit.convertToBaseUnit(this.value);
        double base2 = other.unit.convertToBaseUnit(other.value);

        return Double.compare(base1, base2) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(unit.convertToBaseUnit(value));
    }

    @Override
    public String toString() {
        return "Quantity(" + value + ", " + unit + ")";
    }

    private static void validateUnit(LengthUnit unit) {
        if (unit == null)
            throw new IllegalArgumentException("Unit cannot be null");
    }

    private static void validateValue(double value) {
        if (!Double.isFinite(value))
            throw new IllegalArgumentException("Value must be finite");
    }

    private static void validateOperand(Length length) {
        if (length == null)
            throw new IllegalArgumentException("Length cannot be null");
    }

    public static double round(double value) {
        return Math.round(value * ROUNDING_FACTOR) / ROUNDING_FACTOR;
    }
}