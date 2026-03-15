package com.app.dto;

import com.app.model.IMeasurable;

/**
 * Data Transfer Object for quantity measurement input/output.
 * Self-contained: carries its own IMeasureableUnit hierarchy and
 * maps to the domain IMeasurable enums for business logic.
 */
public class QuantityDTO {

    private final double          value;
    private final IMeasureableUnit unit;

    public QuantityDTO(double value, IMeasureableUnit unit) {
        this.value = value;
        this.unit  = unit;
    }

    public double          getValue() { return value; }
    public IMeasureableUnit getUnit()  { return unit;  }

    @Override
    public String toString() {
        return value + " " + (unit != null ? unit.getUnitName() : "null");
    }

    // ── Length ────────────────────────────────────────────────────────────────
    public enum LengthUnit implements IMeasureableUnit {
        FEET, INCHES, YARDS, CENTIMETERS, METERS;

        @Override public String getUnitName()       { return this.name(); }
        @Override public String getMeasurementType(){ return "LengthUnit"; }

        @Override
        public IMeasurable getMeasurableUnit() {
            switch (this) {
                case FEET:        return com.app.unit.LengthUnit.FEET;
                case INCHES:      return com.app.unit.LengthUnit.INCHES;
                case YARDS:       return com.app.unit.LengthUnit.YARDS;
                case CENTIMETERS: return com.app.unit.LengthUnit.CENTIMETERS;
                case METERS:      return com.app.unit.LengthUnit.METERS;
                default: throw new IllegalStateException("Unknown LengthUnit: " + this);
            }
        }
    }

    // ── Weight ────────────────────────────────────────────────────────────────
    public enum WeightUnit implements IMeasureableUnit {
        GRAMS, KILOGRAMS, POUNDS, OUNCES, TONNES;

        @Override public String getUnitName()       { return this.name(); }
        @Override public String getMeasurementType(){ return "WeightUnit"; }

        @Override
        public IMeasurable getMeasurableUnit() {
            switch (this) {
                case GRAMS:     return com.app.unit.WeightUnit.GRAM;
                case KILOGRAMS: return com.app.unit.WeightUnit.KILOGRAM;
                case POUNDS:    return com.app.unit.WeightUnit.POUND;
                case OUNCES:    return com.app.unit.WeightUnit.OUNCE;
                case TONNES:    return com.app.unit.WeightUnit.TONNE;
                default: throw new IllegalStateException("Unknown WeightUnit: " + this);
            }
        }
    }

    // ── Volume ────────────────────────────────────────────────────────────────
    public enum VolumeUnit implements IMeasureableUnit {
        LITERS, MILLILITERS, GALLONS;

        @Override public String getUnitName()       { return this.name(); }
        @Override public String getMeasurementType(){ return "VolumeUnit"; }

        @Override
        public IMeasurable getMeasurableUnit() {
            switch (this) {
                case LITERS:      return com.app.unit.VolumeUnit.LITRE;
                case MILLILITERS: return com.app.unit.VolumeUnit.MILLILITRE;
                case GALLONS:     return com.app.unit.VolumeUnit.GALLON;
                default: throw new IllegalStateException("Unknown VolumeUnit: " + this);
            }
        }
    }

    // ── Temperature ───────────────────────────────────────────────────────────
    public enum TemperatureUnit implements IMeasureableUnit {
        CELSIUS, FAHRENHEIT, KELVIN;

        @Override public String getUnitName()       { return this.name(); }
        @Override public String getMeasurementType(){ return "TemperatureUnit"; }

        @Override
        public IMeasurable getMeasurableUnit() {
            switch (this) {
                case CELSIUS:    return com.app.unit.TemperatureUnit.CELSIUS;
                case FAHRENHEIT: return com.app.unit.TemperatureUnit.FAHRENHEIT;
                case KELVIN:     return com.app.unit.TemperatureUnit.KELVIN;
                default: throw new IllegalStateException("Unknown TemperatureUnit: " + this);
            }
        }
    }
}
