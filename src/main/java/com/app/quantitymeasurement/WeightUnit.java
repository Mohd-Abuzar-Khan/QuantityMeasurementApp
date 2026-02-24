package com.app.quantitymeasurement;

public enum WeightUnit implements IMeasurable {

    KILOGRAM(1.0),
    GRAM(0.001),
    TONNE(1000.0),
    POUND(0.453592);

    private final double toKilogramFactor;

    WeightUnit(double toKilogramFactor) {
        this.toKilogramFactor = toKilogramFactor;
    }

    @Override
    public double convertToBaseUnit(double value) {
        return value * toKilogramFactor;
    }

    @Override
    public double convertFromBaseUnit(double baseValue) {
        return baseValue / toKilogramFactor;
    }

    @Override
    public String getUnitName() {
        return this.name();
    }

    
    public double getConversionFactor() {
        return toKilogramFactor;
    }
}