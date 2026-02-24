package com.app.quantitymeasurement;


public enum VolumeUnit implements IMeasurable {

    LITRE(1.0),
    MILLILITRE(0.001),

    GALLON(3.78541);

    private final double conversionFactor;

    VolumeUnit(double conversionFactor) {
        this.conversionFactor = conversionFactor;
    }

    // Returns the conversion factor relative to the base unit (litre)
    @Override
    public double getConversionFactor() {
        return conversionFactor;
    }

    // Converts a value in this unit to the base unit (litre)
    @Override
    public double convertToBaseUnit(double value) {
        return value * conversionFactor;
    }

    // Converts a value from the base unit (litre) to this unit
    @Override
    public double convertFromBaseUnit(double baseValue) {
        return baseValue / conversionFactor;
    }

    // Returns the readable name of this unit
    @Override
    public String getUnitName() {
        return this.name();
    }
}