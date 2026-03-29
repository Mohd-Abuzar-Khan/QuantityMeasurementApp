package com.app.unit;

public enum WeightUnit implements IMeasurable {

    KILOGRAM(1.0),
    GRAM(0.001),
    TONNE(1000.0),
    POUND(0.453592),
    OUNCE(0.0283495);

    private final double toKilogramFactor;

    WeightUnit(double toKilogramFactor) {
        this.toKilogramFactor = toKilogramFactor;
    }

    @Override public double convertToBaseUnit(double value)    { return value * toKilogramFactor; }
    @Override public double convertFromBaseUnit(double base)   { return base  / toKilogramFactor; }
    @Override public String getUnitName()                      { return this.name(); }
    @Override public String getMeasurementType()               { return "WeightUnit"; }
    @Override public IMeasurable getUnitByName(String name)    { return WeightUnit.valueOf(name.toUpperCase()); }

    public double getConversionFactor() { return toKilogramFactor; }
}
