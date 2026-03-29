package com.app.unit;

public enum VolumeUnit implements IMeasurable {

    LITRE(1.0),
    MILLILITRE(0.001),
    GALLON(3.78541);

    private final double conversionFactor;

    VolumeUnit(double conversionFactor) {
        this.conversionFactor = conversionFactor;
    }

    @Override public double convertToBaseUnit(double value)    { return value * conversionFactor; }
    @Override public double convertFromBaseUnit(double base)   { return base  / conversionFactor; }
    @Override public String getUnitName()                      { return this.name(); }
    @Override public String getMeasurementType()               { return "VolumeUnit"; }
    @Override public IMeasurable getUnitByName(String name)    { return VolumeUnit.valueOf(name.toUpperCase()); }

    public double getConversionFactor() { return conversionFactor; }
}
