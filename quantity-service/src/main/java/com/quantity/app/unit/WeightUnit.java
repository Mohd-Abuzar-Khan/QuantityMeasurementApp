package com.quantity.app.unit;

/**
 * Weight units — base unit is GRAMS.
 */
public enum WeightUnit implements IMeasurable {

    GRAMS       (1.0),
    KILOGRAMS   (1000.0),
    MILLIGRAMS  (0.001),
    POUNDS      (453.592),
    OUNCES      (28.3495),
    TONNES      (1_000_000.0);

    private final double conversionFactor;  

    WeightUnit(double factor) { this.conversionFactor = factor; }

    @Override public double convertToBaseUnit(double value)  { return value * conversionFactor; }
    @Override public double convertFromBaseUnit(double base) { return base  / conversionFactor; }
    @Override public String getUnitName()                    { return this.name(); }
    @Override public String getMeasurementType()             { return "WeightUnit"; }
    @Override public IMeasurable getUnitByName(String name)  { return WeightUnit.valueOf(name.toUpperCase()); }
}
