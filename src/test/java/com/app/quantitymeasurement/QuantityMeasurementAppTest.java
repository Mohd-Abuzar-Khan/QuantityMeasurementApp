package com.app.quantitymeasurement;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuantityMeasurementAppTest {


    // UC1 - UC4 EQUALITY TESTING
    @Nested
    class LengthEqualityTests {

        @Test
        void shouldBeEqual_whenSameValueAndUnit() {
            assertEquals(length(5, LengthUnit.FEET),length(5, LengthUnit.FEET));
        }

        @Test
        void shouldNotBeEqual_whenDifferentValue() {
            assertNotEquals(length(5, LengthUnit.FEET),length(6, LengthUnit.FEET));
        }

        @Test
        void shouldBeEqual_whenCrossUnitEquivalent() {
            assertEquals(length(1, LengthUnit.FEET), length(12, LengthUnit.INCHES));
        }

        @Test
        void shouldReturnFalse_whenComparedWithNull() {
            assertNotEquals(length(2, LengthUnit.FEET), null);
        }

        @Test
        void shouldFollowEqualityContract() {
            Length a = length(2, LengthUnit.YARDS);
            Length b = length(6, LengthUnit.FEET);
            Length c = length(72, LengthUnit.INCHES);

            assertTrue(a.equals(b) && b.equals(c) && a.equals(c));
        }
    }


    // UC5 : UNIT CONVERSION
    @Nested
    class LengthConversionTests {

        @Test
        void shouldConvertFeetToInches() {
            Length result = length(3, LengthUnit.FEET).convertTo(LengthUnit.INCHES);
            assertEquals(length(36, LengthUnit.INCHES), result);
        }

        @Test
        void shouldConvertCmToFeet() {
            Length result = length(30.48, LengthUnit.CENTIMETERS).convertTo(LengthUnit.FEET);
            assertEquals(length(1, LengthUnit.FEET), result);
        }
    }


    // UC6 : Additions on Unit Tests
    @Nested
    class LengthAdditionTests {

        @Test
        void shouldAddSameUnit() {
            Length result = length(2, LengthUnit.FEET).add(length(3, LengthUnit.FEET));
            assertEquals(length(5, LengthUnit.FEET), result);
        }

        @Test
        void shouldAddCrossUnit() {
            Length result = length(1, LengthUnit.FEET).add(length(6, LengthUnit.INCHES));
            assertEquals(length(1.5, LengthUnit.FEET), result);
        }

        @Test
        void shouldThrowException_whenAddingNull() {
            assertThrows(IllegalArgumentException.class, () -> length(1, LengthUnit.FEET).add(null));
        }
    }

    //----------------------WEIGHT TESTS ---------------------------
   
    // Unit 9 : Weight Class Test Cases
    @Nested
    class WeightEqualityTests {

        @Test
        void shouldBeEqual_whenKgEqualsGram() {
            assertEquals(weight(1, WeightUnit.KILOGRAM), weight(1000, WeightUnit.GRAM));
        }

        @Test
        void shouldNotBeEqual_whenDifferentValue() {
            assertNotEquals(weight(1, WeightUnit.KILOGRAM), weight(2, WeightUnit.KILOGRAM));
        }

        @Test
        void shouldReturnFalse_whenComparedWithDifferentType() {
            assertFalse(weight(1, WeightUnit.KILOGRAM).equals(length(1, LengthUnit.FEET)));
        }
    }

    @Nested
    class WeightConversionTests {

        @Test
        void shouldConvertKgToPound() {
            QuantityWeight result = weight(1, WeightUnit.KILOGRAM).convertTo(WeightUnit.POUND);
            assertEquals(2.20462, result.getValue(), 1e-4);
        }

        @Test
        void shouldConvertPoundToKg() {
            QuantityWeight result = weight(2.20462, WeightUnit.POUND).convertTo(WeightUnit.KILOGRAM);
            assertEquals(1, result.getValue(), 1e-4);
        }
    }

    @Nested
    class WeightAdditionTests {

        @Test
        void shouldAddKgAndGram() {
            QuantityWeight result = weight(1, WeightUnit.KILOGRAM) .add(weight(1000, WeightUnit.GRAM));
            assertEquals(2, result.getValue());
        }

        @Test
        void shouldAddWithExplicitTargetUnit() {
            QuantityWeight result = QuantityWeight.add( weight(1, WeightUnit.KILOGRAM), weight(1000, WeightUnit.GRAM), WeightUnit.GRAM);
            assertEquals(2000, result.getValue());
        }
    }



    private Length length(double value, LengthUnit unit) {
        return new Length(value, unit);
    }

    private QuantityWeight weight(double value, WeightUnit unit) {
        return new QuantityWeight(value, unit);
    }
    
}