package com.app.quantitymeasurement;

import java.util.Objects;

// Generic Length class applying DRY principle
public class Length {


    private static final double EPSILON = 1e-6;

    private final double value;
    private final LengthUnit unit;

    public Length(double value, LengthUnit unit) {
        if (unit == null) {
            throw new IllegalArgumentException("Unit cannot be null");
        }
        this.value = value;
        this.unit = unit;
    }

    private double convertToBaseUnit() {
        return this.value * unit.getConversionFactor();
    }

    public boolean compare(Length other) {
        if (other == null) {
            return false;
        }
        return Double.compare(
                this.convertToBaseUnit(),
                other.convertToBaseUnit()
        ) == 0;
    }

    @Override
    public boolean equals(Object obj) {
    
        if (this == obj)
            return true;
    
        if (!(obj instanceof Length other))
            return false;
    
        return Math.abs(
                this.convertToBaseUnit() - other.convertToBaseUnit()
        ) < EPSILON;
    }

    @Override
    public int hashCode() {
        return Objects.hash(convertToBaseUnit());
    }
    
    @Override
    public String toString() {
        return "Quantity(" + value + ", " + unit + ")";
    }
}