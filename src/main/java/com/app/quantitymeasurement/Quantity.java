package com.app.quantitymeasurement;

import java.util.Objects;

public final class Quantity<U extends IMeasurable> {

    private static final double ROUND_FACTOR = 100.0; 

    private final double value;
    private final U unit;

    // Constructor with validation
    public Quantity(double value, U unit) {
        if (unit == null) {
            throw new IllegalArgumentException("Unit cannot be null");
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

    public U getUnit() {
        return unit;
    }

    // Rounds a value to 2 decimal places
    private static double round(double value) {
        return Math.round(value * ROUND_FACTOR) / ROUND_FACTOR;
    }

    // Conversion
    public Quantity<U> convertTo(U targetUnit) {
        if (targetUnit == null) {
            throw new IllegalArgumentException("Target unit cannot be null");
        }
        if (this.unit == targetUnit) {
            return this;
        }
        double baseValue = unit.convertToBaseUnit(value);
        double convertedValue = round(targetUnit.convertFromBaseUnit(baseValue));
        return new Quantity<>(convertedValue, targetUnit);
    }

    // Addition (implicit target unit — result in caller's unit)
    public Quantity<U> add(Quantity<U> other) {
        if (other == null) {
            throw new IllegalArgumentException("Cannot add null quantity");
        }
        double base1 = this.unit.convertToBaseUnit(this.value);
        double base2 = other.unit.convertToBaseUnit(other.value);
        double resultValue = round(this.unit.convertFromBaseUnit(base1 + base2));
        return new Quantity<>(resultValue, this.unit);
    }

    // Addition (explicit target unit)
    public Quantity<U> add(Quantity<U> other, U targetUnit) {
        if (other == null) {
            throw new IllegalArgumentException("Cannot add null quantity");
        }
        if (targetUnit == null) {
            throw new IllegalArgumentException("Target unit cannot be null");
        }
        double base1 = this.unit.convertToBaseUnit(this.value);
        double base2 = other.unit.convertToBaseUnit(other.value);
        double resultValue = round(targetUnit.convertFromBaseUnit(base1 + base2));
        return new Quantity<>(resultValue, targetUnit);
    }

    // Static addition with explicit target unit
    public static <U extends IMeasurable> Quantity<U> add(Quantity<U> q1, Quantity<U> q2, U targetUnit) {
        if (q1 == null || q2 == null) {
            throw new IllegalArgumentException("Quantities cannot be null");
        }
        if (targetUnit == null) {
            throw new IllegalArgumentException("Target unit cannot be null");
        }
        double base1 = q1.unit.convertToBaseUnit(q1.value);
        double base2 = q2.unit.convertToBaseUnit(q2.value);
        double resultValue = round(targetUnit.convertFromBaseUnit(base1 + base2));
        return new Quantity<>(resultValue, targetUnit);
    }

    // Equality — category-safe (cross-category returns false)
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Quantity<?> other = (Quantity<?>) obj;

        // Cross-category prevention: units must be of the same class
        if (this.unit.getClass() != other.unit.getClass()) return false;

        double base1 = round(this.unit.convertToBaseUnit(this.value));
        double base2 = round(other.unit.convertToBaseUnit(other.value));

        return Double.compare(base1, base2) == 0;
    }

    @Override
    public int hashCode() {
        double baseValue = round(unit.convertToBaseUnit(value));
        return Objects.hash(unit.getClass().getName(), baseValue);
    }

    @Override
    public String toString() {
        return String.format("Quantity(%.2f, %s)", value, unit.getUnitName());
    }
}