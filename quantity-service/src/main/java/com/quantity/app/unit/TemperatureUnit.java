package com.quantity.app.unit;

/**
 * Temperature units.
 *
 * Unlike other units, temperature conversion is NOT linear (y = mx),
 * it requires an offset: °C to °F = (C × 9/5) + 32
 *
 * The base unit is CELSIUS.
 * All conversions go through Celsius as the intermediate.
 *
 * IMPORTANT: Temperature does NOT support addition/subtraction —
 * adding 20°C + 30°C does not equal 50°C in any meaningful physical sense.
 * The validateOperationSupport() method enforces this.
 */
public enum TemperatureUnit implements IMeasurable {

    CELSIUS {
        @Override public double convertToBaseUnit(double v)   { return v; }
        @Override public double convertFromBaseUnit(double b) { return b; }
    },
    FAHRENHEIT {
        @Override public double convertToBaseUnit(double v)   { return (v - 32) * 5.0 / 9.0; }
        @Override public double convertFromBaseUnit(double b) { return (b * 9.0 / 5.0) + 32; }
    },
    KELVIN {
        @Override public double convertToBaseUnit(double v)   { return v - 273.15; }
        @Override public double convertFromBaseUnit(double b) { return b + 273.15; }
    };

    @Override public String getUnitName()                   { return this.name(); }
    @Override public String getMeasurementType()            { return "TemperatureUnit"; }
    @Override public IMeasurable getUnitByName(String name) { return TemperatureUnit.valueOf(name.toUpperCase()); }

    /**
     * Temperature does not support arithmetic (addition, subtraction, division).
     * Only COMPARE and CONVERT are valid operations.
     */
    @Override
    public void validateOperationSupport(String operation) {
        throw new UnsupportedOperationException(
            "TemperatureUnit does not support " + operation +
            ". Only COMPARE and CONVERT are valid for temperature.");
    }
}
