package com.app.entity;

import com.app.unit.IMeasurable;

import java.util.Objects;

public class QuantityModel<U extends IMeasurable> {

    private static final double ROUND_FACTOR = 100.0;

    private final double value;
    private final U      unit;

    public QuantityModel(double value, U unit) {
        if (unit == null)
            throw new IllegalArgumentException("Unit cannot be null");
        if (Double.isNaN(value) || Double.isInfinite(value))
            throw new IllegalArgumentException("Invalid numeric value: " + value);
        this.value = value;
        this.unit  = unit;
    }

    public double getValue() { return value; }
    public U      getUnit()  { return unit;  }

    private static double round(double value) {
        return Math.round(value * ROUND_FACTOR) / ROUND_FACTOR;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        QuantityModel<?> other = (QuantityModel<?>) obj;
        if (!this.unit.getMeasurementType().equals(other.unit.getMeasurementType())) return false;
        double base1 = round(this.unit.convertToBaseUnit(this.value));
        double base2 = round(other.unit.convertToBaseUnit(other.value));
        return Double.compare(base1, base2) == 0;
    }

    @Override
    public int hashCode() {
        double baseValue = round(unit.convertToBaseUnit(value));
        return Objects.hash(unit.getMeasurementType(), baseValue);
    }

    @Override
    public String toString() {
        return String.format("Quantity(%.2f, %s)", value, unit.getUnitName());
    }
}
