package com.app.quantitymeasurement;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// QuantityTest covers all measurement categories: length (UC1–UC8), weight (UC9),
// and volume (UC11) using the generic Quantity<U> class introduced in UC10.
// Each category is organized into nested classes for equality, conversion, and addition.
class QuantityTest {

    // Helper methods to keep test code concise and readable
    private Quantity<LengthUnit> length(double value, LengthUnit unit) {
        return new Quantity<>(value, unit);
    }

    private Quantity<WeightUnit> weight(double value, WeightUnit unit) {
        return new Quantity<>(value, unit);
    }

    private Quantity<VolumeUnit> volume(double value, VolumeUnit unit) {
        return new Quantity<>(value, unit);
    }

    // UC1–UC4: Length equality tests covering same-unit, cross-unit, and contract properties
    @Nested
    class LengthEqualityTests {

        @Test
        void shouldBeEqual_whenSameValueAndUnit() {
            assertEquals(length(5, LengthUnit.FEET), length(5, LengthUnit.FEET));
        }

        @Test
        void shouldNotBeEqual_whenDifferentValue() {
            assertNotEquals(length(5, LengthUnit.FEET), length(6, LengthUnit.FEET));
        }

        @Test
        void shouldBeEqual_whenCrossUnitEquivalent_FeetAndInches() {
            assertEquals(length(1, LengthUnit.FEET), length(12, LengthUnit.INCHES));
        }

        @Test
        void shouldBeEqual_whenCrossUnitEquivalent_YardsAndFeet() {
            assertEquals(length(1, LengthUnit.YARDS), length(3, LengthUnit.FEET));
        }

        @Test
        void shouldBeEqual_whenCrossUnitEquivalent_CentimetersAndInches() {
            assertEquals(length(1, LengthUnit.CENTIMETERS), length(0.393701, LengthUnit.INCHES));
        }

        @Test
        void shouldReturnFalse_whenComparedWithNull() {
            assertNotEquals(length(2, LengthUnit.FEET), null);
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

        @Test
        void shouldFollowEqualityContract_transitivity() {
            Quantity<LengthUnit> a = length(1, LengthUnit.YARDS);
            Quantity<LengthUnit> b = length(3, LengthUnit.FEET);
            Quantity<LengthUnit> c = length(36, LengthUnit.INCHES);
            assertTrue(a.equals(b) && b.equals(c) && a.equals(c));
        }
    }

    // UC5: Length unit conversion tests
    @Nested
    class LengthConversionTests {

        @Test
        void shouldConvertFeetToInches() {
            assertEquals(length(12, LengthUnit.INCHES),
                    length(1, LengthUnit.FEET).convertTo(LengthUnit.INCHES));
        }

        @Test
        void shouldConvertFeetToInches_multipleValues() {
            assertEquals(length(36, LengthUnit.INCHES),
                    length(3, LengthUnit.FEET).convertTo(LengthUnit.INCHES));
        }

        @Test
        void shouldConvertYardsToFeet() {
            assertEquals(length(9, LengthUnit.FEET),
                    length(3, LengthUnit.YARDS).convertTo(LengthUnit.FEET));
        }

        @Test
        void shouldConvertInchesToYards() {
            assertEquals(length(1, LengthUnit.YARDS),
                    length(36, LengthUnit.INCHES).convertTo(LengthUnit.YARDS));
        }

        @Test
        void shouldConvertCentimetersToFeet() {
            assertEquals(length(1, LengthUnit.FEET),
                    length(30.48, LengthUnit.CENTIMETERS).convertTo(LengthUnit.FEET));
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

    // UC6: Length addition with implicit target unit (result in first operand's unit)
    @Nested
    class LengthAdditionTests {

        @Test
        void shouldAddSameUnit() {
            assertEquals(length(5, LengthUnit.FEET),
                    length(2, LengthUnit.FEET).add(length(3, LengthUnit.FEET)));
        }

        @Test
        void shouldAddCrossUnit_FeetAndInches() {
            assertEquals(length(1.5, LengthUnit.FEET),
                    length(1, LengthUnit.FEET).add(length(6, LengthUnit.INCHES)));
        }

        @Test
        void shouldAddCrossUnit_YardsAndFeet() {
            assertEquals(length(2, LengthUnit.YARDS),
                    length(1, LengthUnit.YARDS).add(length(3, LengthUnit.FEET)));
        }

        @Test
        void shouldThrowException_whenAddingNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> length(1, LengthUnit.FEET).add(null));
        }
    }

    // UC7: Length addition with explicit target unit
    @Nested
    class LengthTargetAdditionTests {

        @Test
        void shouldAddFeetAndInches_inFeet() {
            assertEquals(length(2, LengthUnit.FEET),
                    Quantity.add(length(1, LengthUnit.FEET), length(12, LengthUnit.INCHES), LengthUnit.FEET));
        }

        @Test
        void shouldAddFeetAndInches_inInches() {
            assertEquals(length(24, LengthUnit.INCHES),
                    Quantity.add(length(1, LengthUnit.FEET), length(12, LengthUnit.INCHES), LengthUnit.INCHES));
        }

        @Test
        void shouldAddYardsAndFeet_inYards() {
            assertEquals(length(2, LengthUnit.YARDS),
                    Quantity.add(length(1, LengthUnit.YARDS), length(3, LengthUnit.FEET), LengthUnit.YARDS));
        }

        @Test
        void shouldAddWithNegativeValue_inInches() {
            assertEquals(length(36, LengthUnit.INCHES),
                    Quantity.add(length(5, LengthUnit.FEET), length(-2, LengthUnit.FEET), LengthUnit.INCHES));
        }

        @Test
        void shouldThrowException_whenTargetUnitIsNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> Quantity.add(length(1, LengthUnit.FEET), length(1, LengthUnit.FEET), null));
        }
    }

    // UC9: Weight equality tests covering same-unit and cross-unit comparisons
    @Nested
    class WeightEqualityTests {

        @Test
        void shouldBeEqual_whenSameValueAndUnit() {
            assertEquals(weight(2, WeightUnit.KILOGRAM), weight(2, WeightUnit.KILOGRAM));
        }

        @Test
        void shouldBeEqual_whenKilogramEqualsGram() {
            assertEquals(weight(1, WeightUnit.KILOGRAM), weight(1000, WeightUnit.GRAM));
        }

        @Test
        void shouldBeEqual_whenGramEqualsPound() {
            assertEquals(weight(453.592, WeightUnit.GRAM), weight(1, WeightUnit.POUND));
        }

        @Test
        void shouldNotBeEqual_whenDifferentValue() {
            assertNotEquals(weight(1, WeightUnit.KILOGRAM), weight(2, WeightUnit.KILOGRAM));
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

    // UC9: Weight unit conversion tests
    @Nested
    class WeightConversionTests {

        @Test
        void shouldConvertKilogramToGram() {
            assertEquals(weight(1000, WeightUnit.GRAM),
                    weight(1, WeightUnit.KILOGRAM).convertTo(WeightUnit.GRAM));
        }

        @Test
        void shouldConvertKilogramToPound() {
            assertEquals(2.2, weight(1, WeightUnit.KILOGRAM).convertTo(WeightUnit.POUND).getValue(), 0.01);
        }

        @Test
        void shouldConvertPoundToKilogram() {
            assertEquals(1.0, weight(2.20462, WeightUnit.POUND).convertTo(WeightUnit.KILOGRAM).getValue(), 1e-4);
        }

        @Test
        void shouldConvertGramToKilogram() {
            assertEquals(weight(2, WeightUnit.KILOGRAM),
                    weight(2000, WeightUnit.GRAM).convertTo(WeightUnit.KILOGRAM));
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

    // UC9: Weight addition with implicit target unit
    @Nested
    class WeightAdditionTests {

        @Test
        void shouldAddSameUnit_kilograms() {
            assertEquals(weight(5, WeightUnit.KILOGRAM),
                    weight(2, WeightUnit.KILOGRAM).add(weight(3, WeightUnit.KILOGRAM)));
        }

        @Test
        void shouldAddSameUnit_grams() {
            assertEquals(weight(500, WeightUnit.GRAM),
                    weight(300, WeightUnit.GRAM).add(weight(200, WeightUnit.GRAM)));
        }

        @Test
        void shouldAddCrossUnit_KilogramAndGram() {
            assertEquals(weight(2, WeightUnit.KILOGRAM),
                    weight(1, WeightUnit.KILOGRAM).add(weight(1000, WeightUnit.GRAM)));
        }

        @Test
        void shouldThrowException_whenAddingNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> weight(1, WeightUnit.KILOGRAM).add(null));
        }
    }

    // UC9: Weight addition with explicit target unit
    @Nested
    class WeightTargetAdditionTests {

        @Test
        void shouldAddKilogramAndGram_inGram() {
            assertEquals(weight(2000, WeightUnit.GRAM),
                    Quantity.add(weight(1, WeightUnit.KILOGRAM), weight(1000, WeightUnit.GRAM), WeightUnit.GRAM));
        }

        @Test
        void shouldAddKilogramAndGram_inKilogram() {
            assertEquals(weight(2, WeightUnit.KILOGRAM),
                    Quantity.add(weight(1, WeightUnit.KILOGRAM), weight(1000, WeightUnit.GRAM), WeightUnit.KILOGRAM));
        }

        @Test
        void shouldAddPoundAndGram_inPound() {
            assertEquals(weight(2, WeightUnit.POUND),
                    Quantity.add(weight(1, WeightUnit.POUND), weight(453.592, WeightUnit.GRAM), WeightUnit.POUND));
        }

        @Test
        void shouldAddKilogramAndPound_inKilogram() {
            assertEquals(3.81,
                    Quantity.add(weight(2, WeightUnit.KILOGRAM), weight(4, WeightUnit.POUND), WeightUnit.KILOGRAM).getValue(),
                    0.01);
        }

        @Test
        void shouldThrowException_whenTargetUnitIsNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> Quantity.add(weight(1, WeightUnit.KILOGRAM), weight(1, WeightUnit.GRAM), null));
        }
    }

    // UC11: Volume equality tests covering same-unit, cross-unit, and edge cases
    @Nested
    class VolumeEqualityTests {

        @Test
        void shouldBeEqual_whenSameValueAndUnit_Litre() {
            assertEquals(volume(1, VolumeUnit.LITRE), volume(1, VolumeUnit.LITRE));
        }

        @Test
        void shouldBeEqual_whenSameValueAndUnit_Millilitre() {
            assertEquals(volume(500, VolumeUnit.MILLILITRE), volume(500, VolumeUnit.MILLILITRE));
        }

        @Test
        void shouldBeEqual_whenSameValueAndUnit_Gallon() {
            assertEquals(volume(1, VolumeUnit.GALLON), volume(1, VolumeUnit.GALLON));
        }

        @Test
        void shouldBeEqual_whenLitreEqualsMillilitre() {
            assertEquals(volume(1, VolumeUnit.LITRE), volume(1000, VolumeUnit.MILLILITRE));
        }

       @Test
        void shouldBeEqual_whenMillilitreEqualsLitre() {
            assertEquals(volume(1000, VolumeUnit.MILLILITRE), volume(1, VolumeUnit.LITRE));
        }

        @Test
        void shouldBeEqual_whenGallonEqualsLitre() {
            assertEquals(volume(1, VolumeUnit.GALLON), volume(3.78541, VolumeUnit.LITRE));
        }

        @Test
        void shouldBeEqual_whenLitreEqualsGallon() {
            assertEquals(volume(3.78541, VolumeUnit.LITRE), volume(1, VolumeUnit.GALLON));
        }

        @Test
        void shouldBeEqual_whenMillilitreEqualsGallon() {
            assertEquals(volume(1000, VolumeUnit.MILLILITRE), volume(0.264172, VolumeUnit.GALLON));
        }

        @Test
        void shouldNotBeEqual_whenDifferentValue() {
            assertNotEquals(volume(1, VolumeUnit.LITRE), volume(2, VolumeUnit.LITRE));
        }

        @Test
        void shouldReturnFalse_whenComparedWithNull() {
            assertNotEquals(volume(1, VolumeUnit.LITRE), null);
        }

        @Test
        void shouldFollowEqualityContract_reflexivity() {
            Quantity<VolumeUnit> a = volume(1, VolumeUnit.LITRE);
            assertEquals(a, a);
        }

        @Test
        void shouldFollowEqualityContract_symmetry() {
            Quantity<VolumeUnit> a = volume(1, VolumeUnit.LITRE);
            Quantity<VolumeUnit> b = volume(1000, VolumeUnit.MILLILITRE);
            assertEquals(a, b);
            assertEquals(b, a);
        }

        @Test
        void shouldBeEqual_whenZeroValueAcrossUnits() {
            assertEquals(volume(0, VolumeUnit.LITRE), volume(0, VolumeUnit.MILLILITRE));
        }

        @Test
        void shouldBeEqual_whenNegativeVolumeAcrossUnits() {
            assertEquals(volume(-1, VolumeUnit.LITRE), volume(-1000, VolumeUnit.MILLILITRE));
        }

        @Test
        void shouldBeEqual_whenLargeVolumeAcrossUnits() {
            assertEquals(volume(1000000, VolumeUnit.MILLILITRE), volume(1000, VolumeUnit.LITRE));
        }

        // Small values: 0.001 L == 1 mL
        @Test
        void shouldBeEqual_whenSmallVolumeAcrossUnits() {
            assertEquals(volume(0.001, VolumeUnit.LITRE), volume(1, VolumeUnit.MILLILITRE));
        }
    }

    // UC11: Volume unit conversion tests
    @Nested
    class VolumeConversionTests {

        @Test
        void shouldConvertLitreToMillilitre() {
            assertEquals(volume(1000, VolumeUnit.MILLILITRE),
                    volume(1, VolumeUnit.LITRE).convertTo(VolumeUnit.MILLILITRE));
        }

        @Test
        void shouldConvertMillilitreToLitre() {
            assertEquals(volume(1, VolumeUnit.LITRE),
                    volume(1000, VolumeUnit.MILLILITRE).convertTo(VolumeUnit.LITRE));
        }

        @Test
        void shouldConvertGallonToLitre() {
            assertEquals(3.79, volume(1, VolumeUnit.GALLON).convertTo(VolumeUnit.LITRE).getValue(), 0.01);
        }

        @Test
        void shouldConvertLitreToGallon() {
            assertEquals(1.0, volume(3.78541, VolumeUnit.LITRE).convertTo(VolumeUnit.GALLON).getValue(), 0.01);
        }

        @Test
        void shouldConvertMillilitreToGallon() {
            assertEquals(0.26, volume(1000, VolumeUnit.MILLILITRE).convertTo(VolumeUnit.GALLON).getValue(), 0.01);
        }

        @Test
        void shouldReturnSameInstance_whenConvertingToSameUnit() {
            Quantity<VolumeUnit> original = volume(5, VolumeUnit.LITRE);
            assertSame(original, original.convertTo(VolumeUnit.LITRE));
        }

        // Zero value conversion should stay zero
        @Test
        void shouldConvertZeroValue() {
            assertEquals(volume(0, VolumeUnit.MILLILITRE),
                    volume(0, VolumeUnit.LITRE).convertTo(VolumeUnit.MILLILITRE));
        }

        @Test
        void shouldConvertNegativeValue() {
            assertEquals(volume(-1000, VolumeUnit.MILLILITRE),
                    volume(-1, VolumeUnit.LITRE).convertTo(VolumeUnit.MILLILITRE));
        }

        @Test
        void shouldPreserveValue_onRoundTripConversion() {
            assertEquals(1.5,
                    volume(1.5, VolumeUnit.LITRE)
                            .convertTo(VolumeUnit.MILLILITRE)
                            .convertTo(VolumeUnit.LITRE)
                            .getValue(),
                    0.01);
        }

        @Test
        void shouldThrowException_whenTargetUnitIsNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> volume(1, VolumeUnit.LITRE).convertTo(null));
        }
    }

    // UC11: Volume addition with implicit target unit (result in first operand's unit)
    @Nested
    class VolumeAdditionTests {

        @Test
        void shouldAddSameUnit_litres() {
            assertEquals(volume(3, VolumeUnit.LITRE),
                    volume(1, VolumeUnit.LITRE).add(volume(2, VolumeUnit.LITRE)));
        }

        @Test
        void shouldAddSameUnit_millilitres() {
            assertEquals(volume(1000, VolumeUnit.MILLILITRE),
                    volume(500, VolumeUnit.MILLILITRE).add(volume(500, VolumeUnit.MILLILITRE)));
        }

        @Test
        void shouldAddCrossUnit_LitrePlusMillilitre() {
            assertEquals(volume(2, VolumeUnit.LITRE),
                    volume(1, VolumeUnit.LITRE).add(volume(1000, VolumeUnit.MILLILITRE)));
        }

        @Test
        void shouldAddCrossUnit_MillilitrePlusLitre() {
            assertEquals(volume(2000, VolumeUnit.MILLILITRE),
                    volume(1000, VolumeUnit.MILLILITRE).add(volume(1, VolumeUnit.LITRE)));
        }

        @Test
        void shouldAddCrossUnit_GallonPlusLitre() {
            assertEquals(2.0,
                    volume(1, VolumeUnit.GALLON).add(volume(3.78541, VolumeUnit.LITRE)).getValue(),
                    0.01);
        }

        @Test
        void shouldAddWithZero_actAsIdentity() {
            assertEquals(volume(5, VolumeUnit.LITRE),
                    volume(5, VolumeUnit.LITRE).add(volume(0, VolumeUnit.MILLILITRE)));
        }

        @Test
        void shouldAddNegativeValue() {
            assertEquals(volume(3, VolumeUnit.LITRE),
                    volume(5, VolumeUnit.LITRE).add(volume(-2000, VolumeUnit.MILLILITRE)));
        }

        @Test
        void shouldThrowException_whenAddingNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> volume(1, VolumeUnit.LITRE).add(null));
        }
    }

    // UC11: Volume addition with explicit target unit
    @Nested
    class VolumeTargetAdditionTests {

        @Test
        void shouldAddLitreAndMillilitre_inMillilitre() {
            assertEquals(volume(2000, VolumeUnit.MILLILITRE),
                    Quantity.add(volume(1, VolumeUnit.LITRE), volume(1000, VolumeUnit.MILLILITRE), VolumeUnit.MILLILITRE));
        }

        @Test
        void shouldAddLitreAndMillilitre_inLitre() {
            assertEquals(volume(2, VolumeUnit.LITRE),
                    Quantity.add(volume(1, VolumeUnit.LITRE), volume(1000, VolumeUnit.MILLILITRE), VolumeUnit.LITRE));
        }

        @Test
        void shouldAddGallonAndLitre_inGallon() {
            assertEquals(2.0,
                    Quantity.add(volume(1, VolumeUnit.GALLON), volume(3.78541, VolumeUnit.LITRE), VolumeUnit.GALLON).getValue(),
                    0.01);
        }
        @Test
        void shouldAddLitres_inGallon() {
            assertEquals(2.0,
                    Quantity.add(volume(3.78541, VolumeUnit.LITRE), volume(3.78541, VolumeUnit.LITRE), VolumeUnit.GALLON).getValue(),
                    0.01);
        }
        @Test
        void shouldAddLitreAndGallon_inLitre() {
            assertEquals(17.14,
                    Quantity.add(volume(2, VolumeUnit.LITRE), volume(4, VolumeUnit.GALLON), VolumeUnit.LITRE).getValue(),
                    0.01);
        }

        @Test
        void shouldThrowException_whenTargetUnitIsNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> Quantity.add(volume(1, VolumeUnit.LITRE), volume(1, VolumeUnit.LITRE), null));
        }
    }

    // UC10/UC11: Constructor validation applies uniformly to all measurement categories
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
        void shouldThrowException_whenVolumeUnitIsNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Quantity<>(1.0, (VolumeUnit) null));
        }

        @Test
        void shouldThrowException_whenValueIsNaN() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Quantity<>(Double.NaN, VolumeUnit.LITRE));
        }

        @Test
        void shouldThrowException_whenValueIsInfinite() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Quantity<>(Double.POSITIVE_INFINITY, VolumeUnit.LITRE));
        }
    }

    // UC10/UC11: Cross-category comparisons must always return false
    @Nested
    class CrossCategoryTests {

        @Test
        void shouldNotBeEqual_volumeVsLength() {
            assertNotEquals((Object) volume(1, VolumeUnit.LITRE),
                            (Object) length(1, LengthUnit.FEET));
        }

        @Test
        void shouldNotBeEqual_volumeVsWeight() {
            assertNotEquals((Object) volume(1, VolumeUnit.LITRE),
                            (Object) weight(1, WeightUnit.KILOGRAM));
        }

        @Test
        void shouldNotBeEqual_weightVsLength() {
            assertNotEquals((Object) weight(1, WeightUnit.KILOGRAM),
                            (Object) length(1, LengthUnit.FEET));
        }

        @Test
        void shouldNotBeEqual_evenIfSameNumericValue() {
            assertNotEquals((Object) volume(1000, VolumeUnit.MILLILITRE),
                            (Object) weight(1000, WeightUnit.GRAM));
        }
        @Test
        void shouldHaveDifferentHashCodes_acrossCategories() {
            assertNotEquals(volume(1, VolumeUnit.LITRE).hashCode(),
                            weight(1, WeightUnit.KILOGRAM).hashCode());
        }
    }

    // UC11: VolumeUnit enum method tests verify conversion factor correctness
    @Nested
    class VolumeUnitEnumTests {

        @Test
        void shouldHaveCorrectConversionFactor_Litre() {
            assertEquals(1.0, VolumeUnit.LITRE.getConversionFactor(), 1e-9);
        }

        @Test
        void shouldHaveCorrectConversionFactor_Millilitre() {
            assertEquals(0.001, VolumeUnit.MILLILITRE.getConversionFactor(), 1e-9);
        }

        @Test
        void shouldHaveCorrectConversionFactor_Gallon() {
            assertEquals(3.78541, VolumeUnit.GALLON.getConversionFactor(), 1e-5);
        }

        @Test
        void shouldConvertToBaseUnit_MillilitreToLitre() {
            assertEquals(1.0, VolumeUnit.MILLILITRE.convertToBaseUnit(1000.0), 1e-9);
        }

        @Test
        void shouldConvertToBaseUnit_GallonToLitre() {
            assertEquals(3.78541, VolumeUnit.GALLON.convertToBaseUnit(1.0), 1e-5);
        }

        @Test
        void shouldConvertFromBaseUnit_LitreToMillilitre() {
            assertEquals(1000.0, VolumeUnit.MILLILITRE.convertFromBaseUnit(1.0), 1e-9);
        }

        @Test
        void shouldConvertFromBaseUnit_LitreToGallon() {
            assertEquals(1.0, VolumeUnit.GALLON.convertFromBaseUnit(3.78541), 0.01);
        }

        @Test
        void shouldReturnCorrectUnitName_Litre() {
            assertEquals("LITRE", VolumeUnit.LITRE.getUnitName());
        }

        @Test
        void shouldReturnCorrectUnitName_Millilitre() {
            assertEquals("MILLILITRE", VolumeUnit.MILLILITRE.getUnitName());
        }

        @Test
        void shouldReturnCorrectUnitName_Gallon() {
            assertEquals("GALLON", VolumeUnit.GALLON.getUnitName());
        }
    }
}