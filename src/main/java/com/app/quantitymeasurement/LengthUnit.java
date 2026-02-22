package com.app.quantitymeasurement;

public enum LengthUnit {

    FEET(1.0),                 
    INCHES(1.0 / 12.0),
    YARDS(3.0),
    CENTIMETERS(1.0 / 30.48);

    private final double conversionFactorToFeet;

    LengthUnit(double conversionFactorToFeet) {
        this.conversionFactorToFeet = conversionFactorToFeet;
    }

    public double getConversionFactor() {
        return conversionFactorToFeet;
    }

    // Single Risponsiblity Principle for Easy Integration of new class
    public double convertToBaseUnit(double value) {
        validate(value);
        return value * conversionFactorToFeet;
    }
    public double convertFromBaseUnit(double baseValue) {
        validate(baseValue);
        return baseValue / conversionFactorToFeet;
    }
    private static void validate(double value) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Value must be finite");
        }
    }
}