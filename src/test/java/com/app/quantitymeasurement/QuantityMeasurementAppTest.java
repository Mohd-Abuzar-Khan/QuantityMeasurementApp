package com.app.quantitymeasurement;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class QuantityMeasurementAppTest {


    // UC1: Feet to Feet
    @Test
    void FeetToFeet_SameValue() {
        Length l1 = new Length(1.0, LengthUnit.FEET);
        Length l2 = new Length(1.0, LengthUnit.FEET);

        assertEquals(l1, l2);
    }

    @Test
    void FeetToInch_EquivalentValue() {
        Length l1 = new Length(1.0, LengthUnit.FEET);
        Length l2 = new Length(12.0, LengthUnit.INCHES);

        assertEquals(l1, l2);
    }

    @Test
    void InchToInch_SameValue() {
        Length l1 = new Length(5.0, LengthUnit.INCHES);
        Length l2 = new Length(5.0, LengthUnit.INCHES);

        assertEquals(l1, l2);
    }

    @Test
    void FeetToFeet_DifferentValue() {
        Length l1 = new Length(1.0, LengthUnit.FEET);
        Length l2 = new Length(2.0, LengthUnit.FEET);

        assertNotEquals(l1, l2);
    }


    // UC2: Inch to Inch
    @Test
    void InchToFeet_EquivalentValue() {
        Length l1 = new Length(12.0, LengthUnit.INCHES);
        Length l2 = new Length(1.0, LengthUnit.FEET);

        assertEquals(l1, l2);
    }

    @Test
    void InchToInch_DifferentValue() {
        Length l1 = new Length(10.0, LengthUnit.INCHES);
        Length l2 = new Length(5.0, LengthUnit.INCHES);

        assertNotEquals(l1, l2);
    }

    // UC3: Generic Equality and Comparison
    @Test
    void SameReference() {
        Length l1 = new Length(3.0, LengthUnit.FEET);

        assertEquals(l1, l1);
    }

    @Test
    void NullComparison() {
        Length l1 = new Length(3.0, LengthUnit.FEET);

        assertNotEquals(l1, null);
    }

    @Test
    void NullUnit() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Length(1.0, null);
        });
    }

    @Test
    void ConsistentResult() {
        Length l1 = new Length(1.0, LengthUnit.FEET);
        Length l2 = new Length(12.0, LengthUnit.INCHES);

        assertTrue(l1.equals(l2));
        assertTrue(l1.equals(l2));
    }


    // UC4: Yard and Centimeter Tests

    @Test
    void Yard_SameValue() {
        Length l1 = new Length(1.0, LengthUnit.YARDS);
        Length l2 = new Length(1.0, LengthUnit.YARDS);

        assertTrue(l1.equals(l2));
    }

    @Test
    void Yard_DifferentValue() {
        Length l1 = new Length(1.0, LengthUnit.YARDS);
        Length l2 = new Length(2.0, LengthUnit.YARDS);

        assertFalse(l1.equals(l2));
    }


    @Test
    void YardToFeet_NonEquivalentValue() {
        Length l1 = new Length(1.0, LengthUnit.YARDS);
        Length l2 = new Length(2.0, LengthUnit.FEET);

        assertFalse(l1.equals(l2));
    }


    // UC4 : Centimeter Tests
    @Test
    void CentimeterToCentimeter_SameValue() {
        Length l1 = new Length(2.0, LengthUnit.CENTIMETERS);
        Length l2 = new Length(2.0, LengthUnit.CENTIMETERS);

        assertTrue(l1.equals(l2));
    }


    @Test
    void CentimeterToFeet_NonEquivalentValue() {
        Length l1 = new Length(1.0, LengthUnit.CENTIMETERS);
        Length l2 = new Length(1.0, LengthUnit.FEET);

        assertFalse(l1.equals(l2));
    }


    @Test
    void Transitive() {
        Length yard = new Length(2.0, LengthUnit.YARDS);
        Length feet = new Length(6.0, LengthUnit.FEET);
        Length inches = new Length(72.0, LengthUnit.INCHES);

        assertTrue(yard.equals(feet));
        assertTrue(feet.equals(inches));
        assertTrue(yard.equals(inches));
    }




    // UC5: Conversion Tests
    @Test
    void convert_FeetToInches() {
        double result = Length.convert( 1.0, LengthUnit.FEET, LengthUnit.INCHES );
        assertEquals(12.0, result);
    }

    @Test
    void convert_YardsToFeet() {
        double result = Length.convert( 3.0, LengthUnit.YARDS, LengthUnit.FEET );
        assertEquals(9.0, result);
    }

    @Test
    void convert_CentimeterToInches() {
        double result = Length.convert( 2.54, LengthUnit.CENTIMETERS, LengthUnit.INCHES);
        assertEquals(1.0, result);
    }

    @Test
    void convert_RoundTrip() {
        double value = 5.0;
        double converted = Length.convert( value, LengthUnit.FEET, LengthUnit.INCHES );
        double back = Length.convert( converted, LengthUnit.INCHES, LengthUnit.FEET );
        assertEquals(value, back);
    }

    @Test
    void convert_InvalidUnit_Throws() {
        assertThrows(IllegalArgumentException.class,
                () -> Length.convert(1.0, null, LengthUnit.FEET));
    }




    // UC6 : Addition Testing
    @Test
    void add_FeetToFeet() {
        Length l1 = new Length(2.0, LengthUnit.FEET);
        Length l2 = new Length(3.0, LengthUnit.FEET);
        Length result = l1.add(l2);
        assertEquals(new Length(5.0, LengthUnit.FEET), result);
    }

    @Test
    void add_FeetAndInches() {
        Length l1 = new Length(1.0, LengthUnit.FEET);
        Length l2 = new Length(6.0, LengthUnit.INCHES);
        Length result = l1.add(l2);
        assertEquals(new Length(1.5, LengthUnit.FEET), result);
    }

    @Test
    void add_YardAndFeet() {
        Length l1 = new Length(1.0, LengthUnit.YARDS);
        Length l2 = new Length(3.0, LengthUnit.FEET);
        Length result = l1.add(l2);
        assertEquals(new Length(2.0, LengthUnit.YARDS), result);
    }

    @Test
    void add_CentimeterAndInches() {
        Length l1 = new Length(2.54, LengthUnit.CENTIMETERS); // 1 inch
        Length l2 = new Length(1.0, LengthUnit.INCHES);
        Length result = l1.add(l2);
        assertEquals(new Length(5.08, LengthUnit.CENTIMETERS), result);
    }

    @Test
    void add_ResultInUnitOfFirstOperand() {
        Length l1 = new Length(12.0, LengthUnit.INCHES);
        Length l2 = new Length(1.0, LengthUnit.FEET);
        Length result = l1.add(l2);
        assertEquals(new Length(24.0, LengthUnit.INCHES), result);
    }

    @Test
    void add_NullLength_ShouldThrowException() {
        Length l1 = new Length(1.0, LengthUnit.FEET);
        assertThrows(IllegalArgumentException.class, () -> {
            l1.add(null);
        });
    }

    @Test
    void add_RoundingCheck() {
        Length l1 = new Length(1.333, LengthUnit.FEET);
        Length l2 = new Length(2.222, LengthUnit.FEET);
        Length result = l1.add(l2);
        assertEquals(new Length(3.56, LengthUnit.FEET), result);
    }


    
}