package com.app.quantitymeasurement;

public enum WeightUnit {

    GRAM(1.0),
    KILOGRAM(1000.0),
    POUND(453.592);

    private final double conversionFactorToGram;

    WeightUnit(double conversionFactorToGram) {
        this.conversionFactorToGram = conversionFactorToGram;
    }

    public double getConversionFactor() {
        return conversionFactorToGram;
    }

    public double convertToBaseUnit(double value) {
        validate(value);
        return value * conversionFactorToGram;
    }
    public double convertFromBaseUnit(double baseValue) {
        validate(baseValue);
        return baseValue / conversionFactorToGram;
    }

    private static void validate(double value) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Value must be finite");
        }
    }
}