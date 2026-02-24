package com.app.quantitymeasurement;

import java.util.Objects;


public final class Quantity<U extends IMeasurable> {

    private static final double ROUND_FACTOR = 100.0;

    private final double value;
    private final U unit;

    // Validates that unit is non-null and value is a finite number
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

    // Rounds a value to 2 decimal places for consistent output precision
    private static double round(double value) {
        return Math.round(value * ROUND_FACTOR) / ROUND_FACTOR;
    }

    // Prevents cross-category operations like subtracting kilograms from feet.
    private void validateOperand(Quantity<?> other, String operationName) {
        if (other == null) {
            throw new IllegalArgumentException("Cannot " + operationName + " null quantity");
        }
        if (this.unit.getClass() != other.unit.getClass()) {
            throw new IllegalArgumentException(
                    "Cannot " + operationName + " quantities of different categories: "
                    + this.unit.getClass().getSimpleName() + " and "
                    + other.unit.getClass().getSimpleName());
        }
    }

    // Converts this quantity to the given target unit.
    // Returns the same instance if target unit is already the current unit.
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

    // Adds another quantity to this one; result is expressed in this quantity's unit.
    public Quantity<U> add(Quantity<U> other) {
        if (other == null) {
            throw new IllegalArgumentException("Cannot add null quantity");
        }
        double base1 = this.unit.convertToBaseUnit(this.value);
        double base2 = other.unit.convertToBaseUnit(other.value);
        double resultValue = round(this.unit.convertFromBaseUnit(base1 + base2));
        return new Quantity<>(resultValue, this.unit);
    }

    // Adds another quantity to this one; result is expressed in the specified target unit.
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

    // Static addition: adds two quantities and returns result in the specified target unit.
    public static <U extends IMeasurable> Quantity<U> add(Quantity<U> q1,
                                                           Quantity<U> q2,
                                                           U targetUnit) {
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


    public Quantity<U> subtract(Quantity<U> other) {
        validateOperand(other, "subtract");
        double base1 = this.unit.convertToBaseUnit(this.value);
        double base2 = other.unit.convertToBaseUnit(other.value);
        double resultValue = round(this.unit.convertFromBaseUnit(base1 - base2));
        return new Quantity<>(resultValue, this.unit);
    }


    public Quantity<U> subtract(Quantity<U> other, U targetUnit) {
        validateOperand(other, "subtract");
        if (targetUnit == null) {
            throw new IllegalArgumentException("Target unit cannot be null");
        }
        double base1 = this.unit.convertToBaseUnit(this.value);
        double base2 = other.unit.convertToBaseUnit(other.value);
        double resultValue = round(targetUnit.convertFromBaseUnit(base1 - base2));
        return new Quantity<>(resultValue, targetUnit);
    }

    public double divide(Quantity<U> other) {
        validateOperand(other, "divide");
        double base1 = this.unit.convertToBaseUnit(this.value);
        double base2 = other.unit.convertToBaseUnit(other.value);
        if (base2 == 0.0) {
            throw new ArithmeticException("Division by zero: divisor quantity has a base value of zero");
        }
        return base1 / base2;
    }

    // Cross-category comparisons 
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Quantity<?> other = (Quantity<?>) obj;

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