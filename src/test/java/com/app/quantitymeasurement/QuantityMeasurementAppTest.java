package com.app.quantitymeasurement;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuantityTest {


    private <U extends IMeasurable> Quantity<U> quantity(double value, U unit) {
        return new Quantity<>(value, unit);
    }

    private Quantity<LengthUnit> length(double value, LengthUnit unit) {
        return new Quantity<>(value, unit);
    }

    private Quantity<WeightUnit> weight(double value, WeightUnit unit) {
        return new Quantity<>(value, unit);
    }

    //UC1–UC4 : Length Equality

    @Nested
    class LengthEqualityTests {

        @Test
        void shouldBeEqual_whenSameValueAndUnit() {
            assertEquals(length(5, LengthUnit.FEET),
                         length(5, LengthUnit.FEET));
        }

        @Test
        void shouldNotBeEqual_whenDifferentValue() {
            assertNotEquals(length(5, LengthUnit.FEET),
                            length(6, LengthUnit.FEET));
        }

        @Test
        void shouldBeEqual_whenCrossUnitEquivalent_FeetAndInches() {
            assertEquals(length(1, LengthUnit.FEET),
                         length(12, LengthUnit.INCHES));
        }

        @Test
        void shouldBeEqual_whenCrossUnitEquivalent_YardsAndFeet() {
            assertEquals(length(1, LengthUnit.YARDS),
                         length(3, LengthUnit.FEET));
        }

        @Test
        void shouldBeEqual_whenCrossUnitEquivalent_CentimetersAndInches() {
            assertEquals(length(1, LengthUnit.CENTIMETERS),
                         length(0.393701, LengthUnit.INCHES));
        }

        @Test
        void shouldReturnFalse_whenComparedWithNull() {
            assertNotEquals(length(2, LengthUnit.FEET), null);
        }

        @Test
        void shouldFollowEqualityContract_transitivity() {
            Quantity<LengthUnit> a = length(1, LengthUnit.YARDS);
            Quantity<LengthUnit> b = length(3, LengthUnit.FEET);
            Quantity<LengthUnit> c = length(36, LengthUnit.INCHES);

            assertTrue(a.equals(b) && b.equals(c) && a.equals(c));
        }

        @Test
        void shouldFollowEqualityContract_reflexivity() {
            Quantity<LengthUnit> a = length(5, LengthUnit.FEET);
            assertEquals(a, a);
        }

        @Test
        void shouldFollowEqualityContract_symmetry() {
            Quantity<LengthUnit> a = length(1, LengthUnit.FEET);
            Quantity<LengthUnit> b = length(12, LengthUnit.INCHES);
            assertEquals(a, b);
            assertEquals(b, a);
        }
    }

    // UC5 : Length Conversion 

    @Nested
    class LengthConversionTests {

        @Test
        void shouldConvertFeetToInches() {
            Quantity<LengthUnit> result = length(1, LengthUnit.FEET)
                    .convertTo(LengthUnit.INCHES);

            assertEquals(length(12, LengthUnit.INCHES), result);
        }

        @Test
        void shouldConvertFeetToInches_multipleValues() {
            Quantity<LengthUnit> result = length(3, LengthUnit.FEET)
                    .convertTo(LengthUnit.INCHES);

            assertEquals(length(36, LengthUnit.INCHES), result);
        }

        @Test
        void shouldConvertYardsToFeet() {
            Quantity<LengthUnit> result = length(3, LengthUnit.YARDS)
                    .convertTo(LengthUnit.FEET);

            assertEquals(length(9, LengthUnit.FEET), result);
        }

        @Test
        void shouldConvertInchesToYards() {
            Quantity<LengthUnit> result = length(36, LengthUnit.INCHES)
                    .convertTo(LengthUnit.YARDS);

            assertEquals(length(1, LengthUnit.YARDS), result);
        }

        @Test
        void shouldConvertCentimetersToFeet() {
            Quantity<LengthUnit> result = length(30.48, LengthUnit.CENTIMETERS)
                    .convertTo(LengthUnit.FEET);

            assertEquals(length(1, LengthUnit.FEET), result);
        }

        @Test
        void shouldConvertCentimetersToInches() {
            Quantity<LengthUnit> result = length(1, LengthUnit.CENTIMETERS)
                    .convertTo(LengthUnit.INCHES);

            assertEquals(length(0.39, LengthUnit.INCHES), result);
        }

        @Test
        void shouldReturnSameInstance_whenConvertingToSameUnit() {
            Quantity<LengthUnit> original = length(5, LengthUnit.FEET);
            assertSame(original, original.convertTo(LengthUnit.FEET));
        }

        @Test
        void shouldThrowException_whenTargetUnitIsNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> length(1, LengthUnit.FEET).convertTo(null));
        }
    }

    // UC6 : Length Addition

    @Nested
    class LengthAdditionTests {

        @Test
        void shouldAddSameUnit() {
            Quantity<LengthUnit> result = length(2, LengthUnit.FEET)
                    .add(length(3, LengthUnit.FEET));

            assertEquals(length(5, LengthUnit.FEET), result);
        }

        @Test
        void shouldAddCrossUnit_FeetAndInches() {
            Quantity<LengthUnit> result = length(1, LengthUnit.FEET)
                    .add(length(6, LengthUnit.INCHES));

            assertEquals(length(1.5, LengthUnit.FEET), result);
        }

        @Test
        void shouldAddCrossUnit_YardsAndFeet() {
            Quantity<LengthUnit> result = length(1, LengthUnit.YARDS)
                    .add(length(3, LengthUnit.FEET));

            assertEquals(length(2, LengthUnit.YARDS), result);
        }

        @Test
        void shouldAddCrossUnit_CentimetersAndInches() {
            Quantity<LengthUnit> result = length(30.48, LengthUnit.CENTIMETERS)
                    .add(length(12, LengthUnit.INCHES));

            assertEquals(length(2, LengthUnit.FEET).convertTo(LengthUnit.CENTIMETERS), result);
        }

        @Test
        void shouldThrowException_whenAddingNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> length(1, LengthUnit.FEET).add(null));
        }
    }

    // UC7 : Length Addition

    @Nested
    class LengthTargetAdditionTests {

        @Test
        void shouldAddFeetAndInches_inFeet() {
            Quantity<LengthUnit> result = Quantity.add(
                    length(1, LengthUnit.FEET),
                    length(12, LengthUnit.INCHES),
                    LengthUnit.FEET);

            assertEquals(length(2, LengthUnit.FEET), result);
        }

        @Test
        void shouldAddFeetAndInches_inInches() {
            Quantity<LengthUnit> result = Quantity.add(
                    length(1, LengthUnit.FEET),
                    length(12, LengthUnit.INCHES),
                    LengthUnit.INCHES);

            assertEquals(length(24, LengthUnit.INCHES), result);
        }

        @Test
        void shouldAddFeetAndInches_inYards() {
            Quantity<LengthUnit> result = Quantity.add(
                    length(1, LengthUnit.FEET),
                    length(12, LengthUnit.INCHES),
                    LengthUnit.YARDS);

            assertEquals(length(0.67, LengthUnit.YARDS), result);
        }

        @Test
        void shouldAddYardsAndFeet_inYards() {
            Quantity<LengthUnit> result = Quantity.add(
                    length(1, LengthUnit.YARDS),
                    length(3, LengthUnit.FEET),
                    LengthUnit.YARDS);

            assertEquals(length(2, LengthUnit.YARDS), result);
        }

        @Test
        void shouldAddInchesAndYards_inFeet() {
            Quantity<LengthUnit> result = Quantity.add(
                    length(36, LengthUnit.INCHES),
                    length(1, LengthUnit.YARDS),
                    LengthUnit.FEET);

            assertEquals(length(6, LengthUnit.FEET), result);
        }

        @Test
        void shouldAddWithZeroValue_inYards() {
            Quantity<LengthUnit> result = Quantity.add(
                    length(5, LengthUnit.FEET),
                    length(0, LengthUnit.INCHES),
                    LengthUnit.YARDS);

            assertEquals(length(1.67, LengthUnit.YARDS), result);
        }

        @Test
        void shouldAddWithNegativeValue_inInches() {
            Quantity<LengthUnit> result = Quantity.add(
                    length(5, LengthUnit.FEET),
                    length(-2, LengthUnit.FEET),
                    LengthUnit.INCHES);

            assertEquals(length(36, LengthUnit.INCHES), result);
        }

        @Test
        void shouldThrowException_whenTargetUnitIsNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> Quantity.add(length(1, LengthUnit.FEET),
                                       length(1, LengthUnit.FEET), null));
        }
    }

    // UC9 : Weight Equality 

    @Nested
    class WeightEqualityTests {

        @Test
        void shouldBeEqual_whenSameValueAndUnit() {
            assertEquals(weight(2, WeightUnit.KILOGRAM),
                         weight(2, WeightUnit.KILOGRAM));
        }

        @Test
        void shouldBeEqual_whenKilogramEqualsGram() {
            assertEquals(weight(1, WeightUnit.KILOGRAM),
                         weight(1000, WeightUnit.GRAM));
        }

        @Test
        void shouldBeEqual_whenGramEqualsPound() {
            assertEquals(weight(453.592, WeightUnit.GRAM),
                         weight(1, WeightUnit.POUND));
        }

        @Test
        void shouldNotBeEqual_whenDifferentValue() {
            assertNotEquals(weight(1, WeightUnit.KILOGRAM),
                            weight(2, WeightUnit.KILOGRAM));
        }

        @Test
        void shouldReturnFalse_whenComparedWithNull() {
            assertNotEquals(weight(1, WeightUnit.KILOGRAM), null);
        }

        @Test
        void shouldReturnFalse_whenComparedWithDifferentCategory() {
            assertNotEquals((Object) weight(1, WeightUnit.KILOGRAM),
                            (Object) length(1, LengthUnit.FEET));
        }

        @Test
        void shouldFollowEqualityContract_symmetry() {
            Quantity<WeightUnit> a = weight(1, WeightUnit.KILOGRAM);
            Quantity<WeightUnit> b = weight(1000, WeightUnit.GRAM);
            assertEquals(a, b);
            assertEquals(b, a);
        }
    }

    // UC9 : Weight Conversion

    @Nested
    class WeightConversionTests {

        @Test
        void shouldConvertKilogramToGram() {
            Quantity<WeightUnit> result = weight(1, WeightUnit.KILOGRAM)
                    .convertTo(WeightUnit.GRAM);

            assertEquals(weight(1000, WeightUnit.GRAM), result);
        }

        @Test
        void shouldConvertKilogramToPound() {
            Quantity<WeightUnit> result = weight(1, WeightUnit.KILOGRAM)
                    .convertTo(WeightUnit.POUND);

            assertEquals(2.2, result.getValue(), 0.01);
        }

        @Test
        void shouldConvertPoundToKilogram() {
            Quantity<WeightUnit> result = weight(2.20462, WeightUnit.POUND)
                    .convertTo(WeightUnit.KILOGRAM);

            assertEquals(1, result.getValue(), 1e-4);
        }

        @Test
        void shouldConvertGramToKilogram() {
            Quantity<WeightUnit> result = weight(2000, WeightUnit.GRAM)
                    .convertTo(WeightUnit.KILOGRAM);

            assertEquals(weight(2, WeightUnit.KILOGRAM), result);
        }

        @Test
        void shouldConvertPoundToGram() {
            Quantity<WeightUnit> result = weight(1, WeightUnit.POUND)
                    .convertTo(WeightUnit.GRAM);

            assertEquals(453.59, result.getValue(), 0.01);
        }

        @Test
        void shouldReturnSameInstance_whenConvertingToSameUnit() {
            Quantity<WeightUnit> original = weight(5, WeightUnit.KILOGRAM);
            assertSame(original, original.convertTo(WeightUnit.KILOGRAM));
        }

        @Test
        void shouldThrowException_whenTargetUnitIsNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> weight(1, WeightUnit.KILOGRAM).convertTo(null));
        }
    }


    @Nested
    class WeightAdditionTests {

        @Test
        void shouldAddSameUnit_kilograms() {
            Quantity<WeightUnit> result = weight(2, WeightUnit.KILOGRAM)
                    .add(weight(3, WeightUnit.KILOGRAM));

            assertEquals(weight(5, WeightUnit.KILOGRAM), result);
        }

        @Test
        void shouldAddSameUnit_grams() {
            Quantity<WeightUnit> result = weight(300, WeightUnit.GRAM)
                    .add(weight(200, WeightUnit.GRAM));

            assertEquals(weight(500, WeightUnit.GRAM), result);
        }

        @Test
        void shouldAddCrossUnit_KilogramAndGram() {
            Quantity<WeightUnit> result = weight(1, WeightUnit.KILOGRAM)
                    .add(weight(1000, WeightUnit.GRAM));

            assertEquals(weight(2, WeightUnit.KILOGRAM), result);
        }

        @Test
        void shouldThrowException_whenAddingNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> weight(1, WeightUnit.KILOGRAM).add(null));
        }
    }

    @Nested
    class WeightTargetAdditionTests {

        @Test
        void shouldAddKilogramAndGram_inGram() {
            Quantity<WeightUnit> result = Quantity.add(
                    weight(1, WeightUnit.KILOGRAM),
                    weight(1000, WeightUnit.GRAM),
                    WeightUnit.GRAM);

            assertEquals(weight(2000, WeightUnit.GRAM), result);
        }

        @Test
        void shouldAddKilogramAndGram_inKilogram() {
            Quantity<WeightUnit> result = Quantity.add(
                    weight(1, WeightUnit.KILOGRAM),
                    weight(1000, WeightUnit.GRAM),
                    WeightUnit.KILOGRAM);

            assertEquals(weight(2, WeightUnit.KILOGRAM), result);
        }

        @Test
        void shouldAddPoundAndGram_inPound() {
            Quantity<WeightUnit> result = Quantity.add(
                    weight(1, WeightUnit.POUND),
                    weight(453.592, WeightUnit.GRAM),
                    WeightUnit.POUND);

            assertEquals(weight(2, WeightUnit.POUND), result);
        }

        @Test
        void shouldAddKilogramAndPound_inKilogram() {
            Quantity<WeightUnit> result = Quantity.add(
                    weight(2, WeightUnit.KILOGRAM),
                    weight(4, WeightUnit.POUND),
                    WeightUnit.KILOGRAM);

            assertEquals(3.81, result.getValue(), 0.01);
        }

        @Test
        void shouldThrowException_whenTargetUnitIsNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> Quantity.add(weight(1, WeightUnit.KILOGRAM),
                                       weight(1, WeightUnit.GRAM), null));
        }
    }

    // UC10 : Constructor Validation 

    @Nested
    class ConstructorValidationTests {

        @Test
        void shouldThrowException_whenLengthUnitIsNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Quantity<>(1.0, (LengthUnit) null));
        }

        @Test
        void shouldThrowException_whenWeightUnitIsNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Quantity<>(1.0, (WeightUnit) null));
        }

        @Test
        void shouldThrowException_whenValueIsNaN() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Quantity<>(Double.NaN, LengthUnit.FEET));
        }

        @Test
        void shouldThrowException_whenValueIsInfinite() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Quantity<>(Double.POSITIVE_INFINITY, WeightUnit.KILOGRAM));
        }
    }

    // UC10 : Cross-Category Prevention

    @Nested
    class CrossCategoryTests {

        @Test
        void shouldNotBeEqual_lengthVsWeight() {
            assertNotEquals((Object) length(1, LengthUnit.FEET),
                            (Object) weight(1, WeightUnit.KILOGRAM));
        }

        @Test
        void shouldNotBeEqual_evenIfSameNumericValue() {
            assertNotEquals((Object) length(1000, LengthUnit.INCHES),
                            (Object) weight(1000, WeightUnit.GRAM));
        }

        @Test
        void shouldHaveDifferentHashCodes_acrossCategories() {
            Quantity<LengthUnit> l = length(1, LengthUnit.FEET);
            Quantity<WeightUnit> w = weight(1, WeightUnit.KILOGRAM);
            assertNotEquals(l.hashCode(), w.hashCode());
        }
    }

    // UC10 : Scalability — Generics

    @Nested
    class ScalabilityTests {

        enum VolumeUnit implements IMeasurable {
            LITRE(1.0), MILLILITRE(0.001);

            private final double factor;
            VolumeUnit(double f) { this.factor = f; }

            @Override public double getConversionFactor()          { return factor; }
            @Override public double convertToBaseUnit(double v)    { return v * factor; }
            @Override public double convertFromBaseUnit(double bv) { return bv / factor; }
            @Override public String getUnitName()                  { return this.name(); }
        }

        private Quantity<VolumeUnit> volume(double value, VolumeUnit unit) {
            return new Quantity<>(value, unit);
        }

        @Test
        void shouldBeEqual_whenLitreEqualsMillilitre() {
            assertEquals(volume(1, VolumeUnit.LITRE),
                         volume(1000, VolumeUnit.MILLILITRE));
        }

        @Test
        void shouldConvertLitreToMillilitre() {
            Quantity<VolumeUnit> result = volume(1, VolumeUnit.LITRE)
                    .convertTo(VolumeUnit.MILLILITRE);

            assertEquals(volume(1000, VolumeUnit.MILLILITRE), result);
        }

        @Test
        void shouldAddLitreAndMillilitre() {
            Quantity<VolumeUnit> result = Quantity.add(
                    volume(1, VolumeUnit.LITRE),
                    volume(500, VolumeUnit.MILLILITRE),
                    VolumeUnit.LITRE);

            assertEquals(volume(1.5, VolumeUnit.LITRE), result);
        }

        @Test
        void shouldNotBeEqual_volumeVsWeight() {
            assertNotEquals((Object) volume(1, VolumeUnit.LITRE),
                            (Object) weight(1, WeightUnit.KILOGRAM));
        }
    }
}