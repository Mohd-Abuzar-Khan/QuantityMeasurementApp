package com.app.quantitymeasurement;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class QuantityMeasurementAppUC3Test {

    @Test
    void testEquality_FeetToFeet_SameValue() {
        Length l1 = new Length(1.0, LengthUnit.FEET);
        Length l2 = new Length(1.0, LengthUnit.FEET);

        assertEquals(l1, l2);
    }

    @Test
    void testEquality_InchToInch_SameValue() {
        Length l1 = new Length(5.0, LengthUnit.INCHES);
        Length l2 = new Length(5.0, LengthUnit.INCHES);

        assertEquals(l1, l2);
    }

    @Test
    void testEquality_FeetToInch_EquivalentValue() {
        Length l1 = new Length(1.0, LengthUnit.FEET);
        Length l2 = new Length(12.0, LengthUnit.INCHES);

        assertEquals(l1, l2);
    }

    @Test
    void testEquality_InchToFeet_EquivalentValue() {
        Length l1 = new Length(12.0, LengthUnit.INCHES);
        Length l2 = new Length(1.0, LengthUnit.FEET);

        assertEquals(l1, l2);
    }

    @Test
    void testEquality_FeetToFeet_DifferentValue() {
        Length l1 = new Length(1.0, LengthUnit.FEET);
        Length l2 = new Length(2.0, LengthUnit.FEET);

        assertNotEquals(l1, l2);
    }

    @Test
    void testEquality_InchToInch_DifferentValue() {
        Length l1 = new Length(10.0, LengthUnit.INCHES);
        Length l2 = new Length(5.0, LengthUnit.INCHES);

        assertNotEquals(l1, l2);
    }

    @Test
    void testEquality_SameReference() {
        Length l1 = new Length(3.0, LengthUnit.FEET);

        assertEquals(l1, l1);
    }

    @Test
    void testEquality_NullComparison() {
        Length l1 = new Length(3.0, LengthUnit.FEET);

        assertNotEquals(l1, null);
    }

    @Test
    void testEquality_NullUnit() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Length(1.0, null);
        });
    }

    @Test
    void testEquality_ConsistentResult() {
        Length l1 = new Length(1.0, LengthUnit.FEET);
        Length l2 = new Length(12.0, LengthUnit.INCHES);

        assertTrue(l1.equals(l2));
        assertTrue(l1.equals(l2)); // multiple calls must return same result
    }

    @Test
    void testEquality_Transitive() {
        Length a = new Length(1.0, LengthUnit.FEET);
        Length b = new Length(12.0, LengthUnit.INCHES);
        Length c = new Length(1.0, LengthUnit.FEET);

        assertEquals(a, b);
        assertEquals(b, c);
        assertEquals(a, c);
    }
}