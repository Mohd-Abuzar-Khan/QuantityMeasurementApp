package com.app.quantitymeasurement;

import java.util.Objects;

public final class QuantityWeight {

    private static final double EPSILON = 1e-6;

    private final double value;
    private final WeightUnit unit;

    // Constructor with validation
    public QuantityWeight(double value, WeightUnit unit) {

        if (unit == null) {
            throw new IllegalArgumentException("Weight unit cannot be null");
        }

        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException("Invalid numeric value");
        }

        this.value = value;
        this.unit = unit;
    }

    public double getValue() {
        return value;
    }

    public WeightUnit getUnit() {
        return unit;
    }

    // Conversion
    public QuantityWeight convertTo(WeightUnit targetUnit) {

        if (targetUnit == null) {
            throw new IllegalArgumentException("Target unit cannot be null");
        }

        if (this.unit == targetUnit) {
            return this;
        }

        double baseValue = unit.convertToBaseUnit(value);
        double convertedValue = targetUnit.convertFromBaseUnit(baseValue);

        return new QuantityWeight(convertedValue, targetUnit);
    }

    // Addition (Implicit Target Unit)
    public QuantityWeight add(QuantityWeight other) {

        if (other == null) {
            throw new IllegalArgumentException("Cannot add null weight");
        }

        double base1 = this.unit.convertToBaseUnit(this.value);
        double base2 = other.unit.convertToBaseUnit(other.value);

        double sumInBase = base1 + base2;

        double resultValue = this.unit.convertFromBaseUnit(sumInBase);

        return new QuantityWeight(resultValue, this.unit);
    }

    // Addition (Explicit Target Unit)
    public static QuantityWeight add(QuantityWeight w1,
                                     QuantityWeight w2,
                                     WeightUnit targetUnit) {

        if (w1 == null || w2 == null) {
            throw new IllegalArgumentException("Weights cannot be null");
        }

        if (targetUnit == null) {
            throw new IllegalArgumentException("Target unit cannot be null");
        }

        double base1 = w1.unit.convertToBaseUnit(w1.value);
        double base2 = w2.unit.convertToBaseUnit(w2.value);

        double sumInBase = base1 + base2;

        double resultValue = targetUnit.convertFromBaseUnit(sumInBase);

        return new QuantityWeight(resultValue, targetUnit);
    }

    // Equality (Category Safe)
    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;

        if (obj == null || getClass() != obj.getClass()) return false;

        QuantityWeight other = (QuantityWeight) obj;

        double base1 = this.unit.convertToBaseUnit(this.value);
        double base2 = other.unit.convertToBaseUnit(other.value);

        return Math.abs(base1 - base2) < EPSILON;
    }

    @Override
    public int hashCode() {

        double baseValue = unit.convertToBaseUnit(value);
        long normalized = Math.round(baseValue / EPSILON);

        return Objects.hash(normalized);
    }

    @Override
    public String toString() {
        return String.format("Quantity(%.6f, %s)", value, unit);
    }
}
