package com.quantity.app.unit;

/**
 * Length units — base unit is FEET.
 *
 * All values are stored/compared in FEET internally.
 * Example: 1 METER = 3.28084 FEET
 */
public enum LengthUnit implements IMeasurable {

    FEET        (1.0),
    INCHES      (1.0 / 12.0),
    YARDS       (3.0),
    CENTIMETERS (1.0 / 30.48),
    METERS      (3.28084),
    KILOMETERS  (3280.84),
    MILES       (5280.0);

    private final double conversionFactor;  // how many FEET = 1 of this unit

    LengthUnit(double factor) { this.conversionFactor = factor; }

    @Override public double convertToBaseUnit(double value)  { return value * conversionFactor; }
    @Override public double convertFromBaseUnit(double base) { return base  / conversionFactor; }
    @Override public String getUnitName()                    { return this.name(); }
    @Override public String getMeasurementType()             { return "LengthUnit"; }
    @Override public IMeasurable getUnitByName(String name)  { return LengthUnit.valueOf(name.toUpperCase()); }
}
