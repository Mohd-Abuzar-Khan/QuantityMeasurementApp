package com.app.quantitymeasurement;

import java.util.Objects;


public  class Feet {

    private final double value; // Immutable field

    public Feet(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {

        // Step 1: Same reference check (Reflexive)
        if (this == obj) {
            return true;
        }

        // Step 2: Null and type check
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        // Step 3: Safe casting
        Feet other = (Feet) obj;

        // Step 4: Proper double comparison
        return Double.compare(this.value, other.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}

