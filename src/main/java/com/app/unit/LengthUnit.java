package com.app.unit;

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

    @Override public double convertToBaseUnit(double value)  { return value * conversionFactor; }
    @Override public double convertFromBaseUnit(double base) { return base  / conversionFactor; }
    @Override public String getUnitName()                    { return this.name(); }
    @Override public String getMeasurementType()             { return "LengthUnit"; }
    @Override public IMeasurable getUnitByName(String name)  { return LengthUnit.valueOf(name.toUpperCase()); }

    public double getConversionFactor() { return conversionFactor; }
}