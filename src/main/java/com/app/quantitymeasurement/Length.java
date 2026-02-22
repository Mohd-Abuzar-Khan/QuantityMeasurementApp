package com.app.quantitymeasurement;

import java.util.Objects;

// Generic Length class applying DRY principle
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


    private double convertFromBaseToTargetUnit(double lengthInBase, LengthUnit targetUnit) {
        
        validateUnit(targetUnit);
        double result = lengthInBase / targetUnit.getConversionFactor();
        return round(result);

    }




    // Method to Add two unit of different unit
    public Length add(Length thatLength) {

        if (thatLength == null) {
            throw new IllegalArgumentException("Length to add cannot be null");
        }

        double base1 = this.convertToBaseUnit();
        double base2 = thatLength.convertToBaseUnit();
        double sumInBase = base1 + base2;
        double result = convertFromBaseToTargetUnit(sumInBase, this.unit);
        
        return new Length(result, this.unit);
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


    // Utiltiy for Rounding
    private static double round(double value) {
        return Math.round(value * ROUNDING_FACTOR) / ROUNDING_FACTOR;
    }
}