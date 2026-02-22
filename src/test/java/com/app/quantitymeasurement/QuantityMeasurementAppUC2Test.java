package com.app.quantitymeasurement;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class QuantityMeasurementAppTest {

	// Feet Testing for UC1
    @Test
    void feet_SameValue() {
        assertTrue(QuantityMeasurementApp.compareFeet(1.0, 1.0));
    }

    @Test
    void feet_DifferentValue() {
        assertFalse(QuantityMeasurementApp.compareFeet(1.0, 2.0));
    }

    @Test
    void feet_NullComparison() {
        Feet f = new Feet(1.0);
        assertFalse(f.equals(null));
    }

    @Test
    void feet_SameReference() {
        Feet f = new Feet(1.0);
        assertTrue(f.equals(f));
    }


    
    // Inches Testing for UC2
    @Test
    void Inches_SameValue() {
        assertTrue(QuantityMeasurementApp.compareInches(5.0, 5.0));
    }

    @Test
    void Inches_DifferentValue() {
        assertFalse(QuantityMeasurementApp.compareInches(5.0, 10.0));
    }

    @Test
    void Inches_NullComparison() {
        Inches i = new Inches(2.0);
        assertFalse(i.equals(null));
    }

    @Test
    void Inches_SameReference() {
        Inches i = new Inches(2.0);
        assertTrue(i.equals(i));
    }



    // Type Safety Testing
    @Test
    void testFeetNotEqualToInches() {
        Feet f = new Feet(1.0);
        Inches i = new Inches(1.0);
        assertFalse(f.equals(i));
    }
}