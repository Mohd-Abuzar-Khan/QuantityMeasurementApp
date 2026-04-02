package com.quantity.app.unit;

/**
 * Volume units — base unit is LITERS.
 */
public enum VolumeUnit implements IMeasurable {

    LITERS      (1.0),
    MILLILITERS (0.001),
    GALLONS     (3.78541),
    PINTS       (0.473176),
    CUPS        (0.236588),
    TABLESPOONS (0.0147868),
    TEASPOONS   (0.00492892);

    private final double conversionFactor;  // how many LITERS = 1 of this unit

    VolumeUnit(double factor) { this.conversionFactor = factor; }

    @Override public double convertToBaseUnit(double value)  { return value * conversionFactor; }
    @Override public double convertFromBaseUnit(double base) { return base  / conversionFactor; }
    @Override public String getUnitName()                    { return this.name(); }
    @Override public String getMeasurementType()             { return "VolumeUnit"; }
    @Override public IMeasurable getUnitByName(String name)  { return VolumeUnit.valueOf(name.toUpperCase()); }
}
