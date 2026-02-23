package com.app.quantitymeasurement;

public enum LengthUnit {

    FEET(12.0),      
    INCHES(1.0),
    YARDS(36.0),
    CENTIMETERS(0.393700787);     

    private final double conversionFactor;

    LengthUnit(double conversionFactor) {
        this.conversionFactor = conversionFactor;
    }

    public double getConversionFactor() {
        return conversionFactor;
    }
}