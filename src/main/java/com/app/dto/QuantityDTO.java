package com.app.dto;

import com.app.model.IMeasurable;


public class QuantityDTO {

    private final double value;
    private final IMeasureableUnit unit;


    public QuantityDTO(double value, IMeasureableUnit unit) {
        this.value = value;
        this.unit  = unit;
    }

    public double getValue() {
        return value;
    }

    public IMeasureableUnit getUnit() {
        return unit;
    }

    @Override
    public String toString() {
        return value + " " + (unit != null ? unit.getUnitName() : "null");
    }

    public enum LengthUnit implements IMeasureableUnit {
        FEET, INCHES, YARDS, CENTIMETERS, METERS;

        @Override
        public String getUnitName() {
            return this.name();
        }

        @Override
        public String getMeasurementType() {
            return this.getClass().getSimpleName();
        }

        @Override
        public IMeasurable getMeasurableUnit() {
            switch (this) {
                case FEET:        return com.app.quantitymeasurement.LengthUnit.FEET;
                case INCHES:      return com.app.quantitymeasurement.LengthUnit.INCHES;
                case YARDS:       return com.app.quantitymeasurement.LengthUnit.YARDS;
                case CENTIMETERS: return com.app.quantitymeasurement.LengthUnit.CENTIMETERS;
                default: throw new IllegalStateException("Unknown LengthUnit: " + this);
            }
        }
    }

    public enum WeightUnit implements IMeasureableUnit {
        GRAMS, KILOGRAMS, POUNDS, OUNCES;

        @Override
        public String getUnitName() {
            return this.name();
        }

        @Override
        public String getMeasurementType() {
            return this.getClass().getSimpleName();
        }

        @Override
        public IMeasurable getMeasurableUnit() {
            switch (this) {
                case GRAMS:     return com.app.quantitymeasurement.WeightUnit.GRAM;
                case KILOGRAMS: return com.app.quantitymeasurement.WeightUnit.KILOGRAM;
                case POUNDS:    return com.app.quantitymeasurement.WeightUnit.POUND;
                default: throw new IllegalStateException("Unknown WeightUnit: " + this);
            }
        }
    }

    public enum VolumeUnit implements IMeasureableUnit {
        LITERS, MILLILITERS, GALLONS;

        @Override
        public String getUnitName() {
            return this.name();
        }

        @Override
        public String getMeasurementType() {
            return this.getClass().getSimpleName();
        }

        @Override
        public IMeasurable getMeasurableUnit() {
            switch (this) {
                case LITERS:      return com.app.quantitymeasurement.VolumeUnit.LITRE;
                case MILLILITERS: return com.app.quantitymeasurement.VolumeUnit.MILLILITRE;
                case GALLONS:     return com.app.quantitymeasurement.VolumeUnit.GALLON;
                default: throw new IllegalStateException("Unknown VolumeUnit: " + this);
            }
        }
    }

    public enum TemperatureUnit implements IMeasureableUnit {
        CELSIUS, FAHRENHEIT, KELVIN;

        @Override
        public String getUnitName() {
            return this.name();
        }

        @Override
        public String getMeasurementType() {
            return this.getClass().getSimpleName();
        }

        @Override
        public IMeasurable getMeasurableUnit() {
            switch (this) {
                case CELSIUS:    return com.app.quantitymeasurement.TemperatureUnit.CELSIUS;
                case FAHRENHEIT: return com.app.quantitymeasurement.TemperatureUnit.FAHRENHEIT;
                case KELVIN:     return com.app.quantitymeasurement.TemperatureUnit.KELVIN;
                default: throw new IllegalStateException("Unknown TemperatureUnit: " + this);
            }
        }
    }
}