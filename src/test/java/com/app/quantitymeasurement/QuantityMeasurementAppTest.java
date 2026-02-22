package com.app.quantitymeasurement;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class QuantityMeasurementAppTest {


    // UC1 : Basic Equality Tests
    @Test
    void sameFeetAreEqual() {
        Length l1 = new Length(5.0, LengthUnit.FEET);
        Length l2 = new Length(5.0, LengthUnit.FEET);
        assertEquals(l1, l2);
    }

    @Test
    void differentFeetNotEqual() {
        Length l1 = new Length(5.0, LengthUnit.FEET);
        Length l2 = new Length(6.0, LengthUnit.FEET);
        assertNotEquals(l1, l2);
    }

    @Test
    void equalsReturnsFalseForNull() {
        Length length = new Length(2.0, LengthUnit.FEET);
        assertNotEquals(length, null);
    }

    @Test
    void referenceEqualitySameObject() {
        Length length = new Length(2.0, LengthUnit.FEET);
        assertEquals(length, length);
    }

    // UC2 : Cross Unit Equality
    @Test
    void twelveInchesEqualsOneFoot() {
        Length inches = new Length(12.0, LengthUnit.INCHES);
        Length foot = new Length(1.0, LengthUnit.FEET);
        assertEquals(foot, inches);
    }

    @Test
    void thirtySixInchesEqualsOneYard() {
        Length inches = new Length(36.0, LengthUnit.INCHES);
        Length yard = new Length(1.0, LengthUnit.YARDS);
        assertEquals(yard, inches);
    }

    @Test
    void thirtyPoint48CmEqualsOneFoot() {
        Length cm = new Length(30.48, LengthUnit.CENTIMETERS);
        Length foot = new Length(1.0, LengthUnit.FEET);
        assertEquals(foot, cm);
    }

    @Test
    void yardNotEqualToInches() {
        Length yard = new Length(1.0, LengthUnit.YARDS);
        Length inches = new Length(12.0, LengthUnit.INCHES);
        assertNotEquals(yard, inches);
    }

    // UC3 : Equality Contract
    @Test
    void reflexiveSymmetricAndTransitiveProperty() {
        Length a = new Length(2.0, LengthUnit.YARDS);
        Length b = new Length(6.0, LengthUnit.FEET);
        Length c = new Length(72.0, LengthUnit.INCHES);

        assertTrue(b.equals(a));
        assertTrue(b.equals(c));
        assertTrue(a.equals(c));
    }

    @Test
    void crossUnitEqualityDemonstrateMethod() {
        Length l1 = new Length(1.0, LengthUnit.FEET);
        Length l2 = new Length(12.0, LengthUnit.INCHES);
        assertTrue(QuantityMeasurementApp.demonstrateLengthEquality(l1, l2));
    }

    // UC5 : Conversion Tests
    @Test
    void convertFeetToInches() {
        Length feet = new Length(3.0, LengthUnit.FEET);
        Length inches = feet.convertTo(LengthUnit.INCHES);
        assertEquals(new Length(36.0, LengthUnit.INCHES), inches);
    }

    @Test
    void convertYardsToInchesUsingOverloadedMethod() {
        Length yards = new Length(2.0, LengthUnit.YARDS);
        Length inches = yards.convertTo(LengthUnit.INCHES);
        assertEquals(new Length(6.0, LengthUnit.INCHES), inches);
    }

    @Test
    void convertCentimeterToFeet() {
        Length cm = new Length(30.48, LengthUnit.CENTIMETERS);
        Length feet = cm.convertTo(LengthUnit.FEET);
        assertEquals(new Length(1.0, LengthUnit.FEET), feet);
    }

    // UC6 : Addition Tests
    @Test
    void addFeetToFeet() {
        Length l1 = new Length(2.0, LengthUnit.FEET);
        Length l2 = new Length(3.0, LengthUnit.FEET);
        Length result = l1.add(l2);
        assertEquals(new Length(5.0, LengthUnit.FEET), result);
    }

    @Test
    void addFeetAndInches() {
        Length l1 = new Length(1.0, LengthUnit.FEET);
        Length l2 = new Length(6.0, LengthUnit.INCHES);
        Length result = l1.add(l2);
        assertEquals(new Length(1.5, LengthUnit.FEET), result);
    }

    @Test
    void addYardAndFeet() {
        Length l1 = new Length(1.0, LengthUnit.YARDS);
        Length l2 = new Length(3.0, LengthUnit.FEET);
        Length result = l1.add(l2);
        assertEquals(new Length(2.0, LengthUnit.YARDS), result);
    }

    @Test
    void addCentimeterAndInches() {
        Length l1 = new Length(2.54, LengthUnit.CENTIMETERS);
        Length l2 = new Length(1.0, LengthUnit.INCHES);
        Length result = l1.add(l2);
        assertEquals(new Length(33.02, LengthUnit.CENTIMETERS), result);
    }

    @Test
    void addResultInUnitOfFirstOperand() {
        Length l1 = new Length(12.0, LengthUnit.INCHES);
        Length l2 = new Length(1.0, LengthUnit.FEET);
        Length result = l1.add(l2);
        assertEquals(new Length(24.0, LengthUnit.INCHES), result);
    }

    @Test
    void addNullLengthShouldThrowException() {
        Length l1 = new Length(1.0, LengthUnit.FEET);
        assertThrows(IllegalArgumentException.class, () -> {
            l1.add(null);
        });
    }

    @Test
    void addRoundingCheck() {
        Length l1 = new Length(1.333, LengthUnit.FEET);
        Length l2 = new Length(2.222, LengthUnit.FEET);
        Length result = l1.add(l2);
        assertEquals(new Length(3.56, LengthUnit.FEET), result);
    }

    // UC7 : Addition With Target Unit
    @Test
    void addFeetAndInchesWithTargetUnitInches() {
        Length l1 = new Length(1.0, LengthUnit.FEET);
        Length l2 = new Length(12.0, LengthUnit.INCHES);
        Length result = Length.add(l1, l2, LengthUnit.INCHES);
        assertEquals(new Length(24.0, LengthUnit.INCHES), result);
    }
}