package com.app.quantitymeasurement;

public enum LengthUnit {

    INCHES(1.0),                  
    FEET(12.0),                   
    YARDS(36.0),                  
    CENTIMETERS(1.0 / 2.54);      

    private final double conversionFactorToInches;

    LengthUnit(double conversionFactorToInches) {
        this.conversionFactorToInches = conversionFactorToInches;
    }

    public double getConversionFactor() {
        return conversionFactorToInches;
    }
    public double convertToBaseUnit(double value) {
        validate(value);
        return value * conversionFactorToInches;
    }
    public double convertFromBaseUnit(double baseValue) {
        validate(baseValue);
        return baseValue / conversionFactorToInches;
    }

    private static void validate(double value) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Value must be finite");
        }
    }
}