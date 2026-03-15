package com.app.unit;

import com.app.model.IMeasurable;

public enum LengthUnit implements IMeasurable {

    FEET(1.0),
    INCHES(1.0 / 12.0),
    YARDS(3.0),
    CENTIMETERS(1.0 / 30.48),
    METERS(3.28084);

    private final double conversionFactor;

    LengthUnit(double conversionFactor) {
        this.conversionFactor = conversionFactor;
    }

    @Override
    public double convertToBaseUnit(double value) {
        return value * conversionFactor;
    }

    @Override
    public double convertFromBaseUnit(double baseValue) {
        return baseValue / conversionFactor;
    }

    @Override
    public String getUnitName() {
        return this.name();
    }

    // UC15
    @Override
    public String getMeasurementType() {
        return "LengthUnit";
    }

    // UC15
    @Override
    public IMeasurable getUnitByName(String name) {
        return LengthUnit.valueOf(name.toUpperCase());
    }

    public double getConversionFactor() {
        return conversionFactor;
    }
}
