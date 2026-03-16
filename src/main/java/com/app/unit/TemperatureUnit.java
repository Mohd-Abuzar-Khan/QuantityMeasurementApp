package com.app.unit;

public enum TemperatureUnit implements IMeasurable {

    CELSIUS {
        @Override public double convertToBaseUnit(double v)  { return v + 273.15; }
        @Override public double convertFromBaseUnit(double b){ return b - 273.15; }
        @Override public String getUnitName()                { return "CELSIUS"; }
        @Override public boolean supportsAddition()          { return false; }
        @Override public boolean supportsSubtraction()       { return false; }
        @Override public boolean supportsDivision()          { return false; }
        @Override public boolean supportsMultiplication()    { return false; }
    },
    FAHRENHEIT {
        @Override public double convertToBaseUnit(double v)  { return (v - 32) * 5.0 / 9.0 + 273.15; }
        @Override public double convertFromBaseUnit(double b){ return (b - 273.15) * 9.0 / 5.0 + 32; }
        @Override public String getUnitName()                { return "FAHRENHEIT"; }
        @Override public boolean supportsAddition()          { return false; }
        @Override public boolean supportsSubtraction()       { return false; }
        @Override public boolean supportsDivision()          { return false; }
        @Override public boolean supportsMultiplication()    { return false; }
    },
    KELVIN {
        @Override public double convertToBaseUnit(double v)  { return v; }
        @Override public double convertFromBaseUnit(double b){ return b; }
        @Override public String getUnitName()                { return "KELVIN"; }
        @Override public boolean supportsAddition()          { return false; }
        @Override public boolean supportsSubtraction()       { return false; }
        @Override public boolean supportsDivision()          { return false; }
        @Override public boolean supportsMultiplication()    { return false; }
    };

    @Override public String getMeasurementType()            { return "TemperatureUnit"; }
    @Override public IMeasurable getUnitByName(String name) { return TemperatureUnit.valueOf(name.toUpperCase()); }
}
