package com.app.quantitymeasurement;

import java.util.Objects;

public class Inches {

    private final double value;

    public Inches(double value) {
        validate(value);
        this.value = value;
    }

    private void validate(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException("Invalid Inches value");
        }
    }

    public double getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;

        if (obj == null || getClass() != obj.getClass()) return false;

        Inches other = (Inches) obj;

        return Double.compare(this.value, other.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}