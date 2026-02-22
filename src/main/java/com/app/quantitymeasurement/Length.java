package com.app.quantitymeasurement;

import java.util.Objects;

// Generic Length class applying DRY principle
public class Length {

    private final double value;
    private final LengthUnit unit;

    public Length(double value, LengthUnit unit) {
        validateValue(value);
        validateUnit(unit);
        this.value = value;
        this.unit = unit;
    }




    // Conversion Methods
    public static double convert(double value, LengthUnit source, LengthUnit target) {

        validateValue(value);
        validateUnit(source);
        validateUnit(target);

        if (source == target) {
            return value;
        }

        return value * (source.getConversionFactor() / target.getConversionFactor());
    }

    public Length convertTo(LengthUnit targetUnit) {
        double convertedValue = convert(this.value, this.unit, targetUnit);
        return new Length(convertedValue, targetUnit);
    }



    // Equality and Comparison Methods
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

        if (obj == null || getClass() != obj.getClass())
            return false;

        Length other = (Length) obj;

        return Double.compare(
                this.convertToBaseUnit(),
                other.convertToBaseUnit()
        ) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(convertToBaseUnit());
    }
    
    @Override
    public String toString() {
        return "Quantity(" + value + ", " + unit + ")";
    }


    // Validation Methods for Lengths 
    private static void validateUnit(LengthUnit unit) {
        if (unit == null) {
            throw new IllegalArgumentException("Unit cannot be null");
        }
    }

    private static void validateValue(double value) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Value must be finite");
        }
    }
}