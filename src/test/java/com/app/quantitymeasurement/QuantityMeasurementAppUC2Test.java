package com.app.quantitymeasurement;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class QuantityMeasurementAppUC2Test {

    // =========================
    // FEET TESTS
    // =========================

    @Test
    void testFeetEquality_SameValue() {
        assertTrue(QuantityMeasurementApp.compareFeet(1.0, 1.0));
    }

    @Test
    void testFeetEquality_DifferentValue() {
        assertFalse(QuantityMeasurementApp.compareFeet(1.0, 2.0));
    }

    @Test
    void testFeetEquality_NullComparison() {
        Feet f = new Feet(1.0);
        assertFalse(f.equals(null));
    }

    @Test
    void testFeetEquality_SameReference() {
        Feet f = new Feet(1.0);
        assertTrue(f.equals(f));
    }

    @Test
    void testFeetEquality_InvalidInput() {
        assertThrows(IllegalArgumentException.class,
                () -> new Feet(Double.NaN));
    }

    // =========================
    // INCHES TESTS
    // =========================

    @Test
    void testInchesEquality_SameValue() {
        assertTrue(QuantityMeasurementApp.compareInches(5.0, 5.0));
    }

    @Test
    void testInchesEquality_DifferentValue() {
        assertFalse(QuantityMeasurementApp.compareInches(5.0, 10.0));
    }

    @Test
    void testInchesEquality_NullComparison() {
        Inches i = new Inches(2.0);
        assertFalse(i.equals(null));
    }

    @Test
    void testInchesEquality_SameReference() {
        Inches i = new Inches(2.0);
        assertTrue(i.equals(i));
    }

    @Test
    void testInchesEquality_InvalidInput() {
        assertThrows(IllegalArgumentException.class,
                () -> new Inches(Double.POSITIVE_INFINITY));
    }

    // =========================
    // TYPE SAFETY TEST
    // =========================

    @Test
    void testFeetNotEqualToInches() {
        Feet f = new Feet(1.0);
        Inches i = new Inches(1.0);
        assertFalse(f.equals(i));
    }
}