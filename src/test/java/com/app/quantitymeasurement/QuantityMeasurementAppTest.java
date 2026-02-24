package com.app.quantitymeasurement;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuantityTest {

    private Quantity<LengthUnit> length(double value, LengthUnit unit) {
        return new Quantity<>(value, unit);
    }

    private Quantity<WeightUnit> weight(double value, WeightUnit unit) {
        return new Quantity<>(value, unit);
    }

    private Quantity<VolumeUnit> volume(double value, VolumeUnit unit) {
        return new Quantity<>(value, unit);
    }

    private Quantity<TemperatureUnit> temp(double value, TemperatureUnit unit) {
        return new Quantity<>(value, unit);
    }

    @SuppressWarnings("unchecked")
    private <A extends IMeasurable, B extends IMeasurable> void subtractCrossCategory(
            Quantity<A> q1, Quantity<B> q2) {
        ((Quantity<A>) (Quantity<?>) q1).subtract((Quantity<A>) (Quantity<?>) q2);
    }

    @SuppressWarnings("unchecked")
    private <A extends IMeasurable, B extends IMeasurable> void divideCrossCategory(
            Quantity<A> q1, Quantity<B> q2) {
        ((Quantity<A>) (Quantity<?>) q1).divide((Quantity<A>) (Quantity<?>) q2);
    }


    // UC1–UC4: Length equality across same unit, cross unit, and contract properties
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
        void shouldBeEqual_whenCrossUnit_FeetAndInches() {
            assertEquals(length(1, LengthUnit.FEET), length(12, LengthUnit.INCHES));
        }

        @Test
        void shouldBeEqual_whenCrossUnit_YardsAndFeet() {
            assertEquals(length(1, LengthUnit.YARDS), length(3, LengthUnit.FEET));
        }

        @Test
        void shouldBeEqual_whenCrossUnit_CentimetersAndInches() {
            assertEquals(length(1, LengthUnit.CENTIMETERS), length(0.393701, LengthUnit.INCHES));
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
    }


    // UC5: Length conversion between all supported unit pairs
    @Nested
    class LengthConversionTests {

        @Test
        void shouldConvertFeetToInches() {
            assertEquals(length(12, LengthUnit.INCHES),
                    length(1, LengthUnit.FEET).convertTo(LengthUnit.INCHES));
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
        void shouldReturnSameInstance_whenSameUnit() {
            Quantity<LengthUnit> original = length(5, LengthUnit.FEET);
            assertSame(original, original.convertTo(LengthUnit.FEET));
        }

        @Test
        void shouldThrowException_whenTargetUnitIsNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> length(1, LengthUnit.FEET).convertTo(null));
        }
    }


    // UC6: Length addition with result expressed in the first operand's unit
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
        void shouldThrowException_whenAddingNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> length(1, LengthUnit.FEET).add(null));
        }
    }


    // UC7: Length addition with result expressed in a specified target unit
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
        void shouldThrowException_whenTargetUnitIsNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> Quantity.add(length(1, LengthUnit.FEET), length(1, LengthUnit.FEET), null));
        }
    }


    // UC9: Weight equality across same unit and cross unit comparisons
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
        void shouldReturnFalse_whenComparedWithDifferentCategory() {
            assertNotEquals((Object) weight(1, WeightUnit.KILOGRAM),
                    (Object) length(1, LengthUnit.FEET));
        }
    }


    // UC9: Weight conversion between all supported unit pairs
    @Nested
    class WeightConversionTests {

        @Test
        void shouldConvertKilogramToGram() {
            assertEquals(weight(1000, WeightUnit.GRAM),
                    weight(1, WeightUnit.KILOGRAM).convertTo(WeightUnit.GRAM));
        }

        @Test
        void shouldConvertKilogramToPound() {
            assertEquals(2.2,
                    weight(1, WeightUnit.KILOGRAM).convertTo(WeightUnit.POUND).getValue(), 0.01);
        }

        @Test
        void shouldConvertGramToKilogram() {
            assertEquals(weight(2, WeightUnit.KILOGRAM),
                    weight(2000, WeightUnit.GRAM).convertTo(WeightUnit.KILOGRAM));
        }

        @Test
        void shouldReturnSameInstance_whenSameUnit() {
            Quantity<WeightUnit> original = weight(5, WeightUnit.KILOGRAM);
            assertSame(original, original.convertTo(WeightUnit.KILOGRAM));
        }

        @Test
        void shouldThrowException_whenTargetUnitIsNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> weight(1, WeightUnit.KILOGRAM).convertTo(null));
        }
    }


    // UC9: Weight addition with implicit and explicit target unit
    @Nested
    class WeightAdditionTests {

        @Test
        void shouldAddCrossUnit_KilogramAndGram() {
            assertEquals(weight(2, WeightUnit.KILOGRAM),
                    weight(1, WeightUnit.KILOGRAM).add(weight(1000, WeightUnit.GRAM)));
        }

        @Test
        void shouldAddKilogramAndGram_inGram() {
            assertEquals(weight(2000, WeightUnit.GRAM),
                    Quantity.add(weight(1, WeightUnit.KILOGRAM), weight(1000, WeightUnit.GRAM), WeightUnit.GRAM));
        }

        @Test
        void shouldAddKilogramAndPound_inKilogram() {
            assertEquals(3.81,
                    Quantity.add(weight(2, WeightUnit.KILOGRAM), weight(4, WeightUnit.POUND), WeightUnit.KILOGRAM).getValue(),
                    0.01);
        }

        @Test
        void shouldThrowException_whenAddingNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> weight(1, WeightUnit.KILOGRAM).add(null));
        }
    }


    // UC11: Volume equality across same unit, cross unit, and edge cases
    @Nested
    class VolumeEqualityTests {

        @Test
        void shouldBeEqual_whenSameUnit_Litre() {
            assertEquals(volume(1, VolumeUnit.LITRE), volume(1, VolumeUnit.LITRE));
        }

        @Test
        void shouldBeEqual_whenLitreEqualsMillilitre() {
            assertEquals(volume(1, VolumeUnit.LITRE), volume(1000, VolumeUnit.MILLILITRE));
        }

        @Test
        void shouldBeEqual_whenGallonEqualsLitre() {
            assertEquals(volume(1, VolumeUnit.GALLON), volume(3.78541, VolumeUnit.LITRE));
        }

        @Test
        void shouldNotBeEqual_whenDifferentValue() {
            assertNotEquals(volume(1, VolumeUnit.LITRE), volume(2, VolumeUnit.LITRE));
        }

        @Test
        void shouldBeEqual_whenZeroAcrossUnits() {
            assertEquals(volume(0, VolumeUnit.LITRE), volume(0, VolumeUnit.MILLILITRE));
        }

        @Test
        void shouldBeEqual_whenNegativeAcrossUnits() {
            assertEquals(volume(-1, VolumeUnit.LITRE), volume(-1000, VolumeUnit.MILLILITRE));
        }
    }


    // UC11: Volume conversion between all supported unit pairs
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
            assertEquals(3.79,
                    volume(1, VolumeUnit.GALLON).convertTo(VolumeUnit.LITRE).getValue(), 0.01);
        }

        @Test
        void shouldConvertMillilitreToGallon() {
            assertEquals(0.26,
                    volume(1000, VolumeUnit.MILLILITRE).convertTo(VolumeUnit.GALLON).getValue(), 0.01);
        }

        @Test
        void shouldReturnSameInstance_whenSameUnit() {
            Quantity<VolumeUnit> original = volume(5, VolumeUnit.LITRE);
            assertSame(original, original.convertTo(VolumeUnit.LITRE));
        }

        @Test
        void shouldThrowException_whenTargetUnitIsNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> volume(1, VolumeUnit.LITRE).convertTo(null));
        }
    }


    // UC11: Volume addition with implicit and explicit target unit
    @Nested
    class VolumeAdditionTests {

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
        void shouldAddLitreAndMillilitre_inMillilitre() {
            assertEquals(volume(2000, VolumeUnit.MILLILITRE),
                    Quantity.add(volume(1, VolumeUnit.LITRE), volume(1000, VolumeUnit.MILLILITRE), VolumeUnit.MILLILITRE));
        }

        @Test
        void shouldAddGallonAndLitre_inGallon() {
            assertEquals(2.0,
                    Quantity.add(volume(1, VolumeUnit.GALLON), volume(3.78541, VolumeUnit.LITRE), VolumeUnit.GALLON).getValue(),
                    0.01);
        }

        @Test
        void shouldThrowException_whenAddingNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> volume(1, VolumeUnit.LITRE).add(null));
        }
    }


    // UC12: Length subtraction with implicit and explicit target unit
    @Nested
    class LengthSubtractionTests {

        @Test
        void shouldSubtractCrossUnit_FeetMinusInches() {
            assertEquals(length(9.5, LengthUnit.FEET),
                    length(10, LengthUnit.FEET).subtract(length(6, LengthUnit.INCHES)));
        }

        @Test
        void shouldSubtractSameUnit() {
            assertEquals(length(5, LengthUnit.FEET),
                    length(10, LengthUnit.FEET).subtract(length(5, LengthUnit.FEET)));
        }

        @Test
        void shouldReturnNegative_whenSecondOperandIsLarger() {
            assertEquals(length(-5, LengthUnit.FEET),
                    length(5, LengthUnit.FEET).subtract(length(10, LengthUnit.FEET)));
        }

        @Test
        void shouldReturnZero_whenEquivalentLengths() {
            assertEquals(length(0, LengthUnit.FEET),
                    length(10, LengthUnit.FEET).subtract(length(120, LengthUnit.INCHES)));
        }

        @Test
        void shouldSubtractFeetAndInches_inInches() {
            assertEquals(length(114, LengthUnit.INCHES),
                    length(10, LengthUnit.FEET).subtract(length(6, LengthUnit.INCHES), LengthUnit.INCHES));
        }

        @Test
        void shouldSubtractIntoYards() {
            assertEquals(length(0.5, LengthUnit.YARDS),
                    length(36, LengthUnit.INCHES).subtract(length(1, LengthUnit.FEET), LengthUnit.YARDS));
        }

        @Test
        void shouldBeNonCommutative() {
            assertNotEquals(
                    length(10, LengthUnit.FEET).subtract(length(5, LengthUnit.FEET)),
                    length(5, LengthUnit.FEET).subtract(length(10, LengthUnit.FEET)));
        }

        @Test
        void shouldSupportChainedSubtraction() {
            assertEquals(length(7, LengthUnit.FEET),
                    length(10, LengthUnit.FEET)
                            .subtract(length(2, LengthUnit.FEET))
                            .subtract(length(1, LengthUnit.FEET)));
        }

        @Test
        void shouldNotMutateOriginals() {
            Quantity<LengthUnit> a = length(10, LengthUnit.FEET);
            Quantity<LengthUnit> b = length(3, LengthUnit.FEET);
            a.subtract(b);
            assertEquals(length(10, LengthUnit.FEET), a);
            assertEquals(length(3, LengthUnit.FEET), b);
        }

        @Test
        void shouldThrowException_whenSubtractingNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> length(10, LengthUnit.FEET).subtract(null));
        }

        @Test
        void shouldThrowException_whenTargetUnitIsNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> length(10, LengthUnit.FEET).subtract(length(5, LengthUnit.FEET), null));
        }

        @Test
        void shouldThrowException_whenCrossCategory() {
            assertThrows(IllegalArgumentException.class,
                    () -> subtractCrossCategory(length(10, LengthUnit.FEET), weight(5, WeightUnit.KILOGRAM)));
        }
    }


    // UC12: Weight subtraction with implicit and explicit target unit
    @Nested
    class WeightSubtractionTests {

        @Test
        void shouldSubtractCrossUnit_KilogramMinusGram() {
            assertEquals(weight(5, WeightUnit.KILOGRAM),
                    weight(10, WeightUnit.KILOGRAM).subtract(weight(5000, WeightUnit.GRAM)));
        }

        @Test
        void shouldReturnNegative_whenSecondIsLarger() {
            assertEquals(weight(-3, WeightUnit.KILOGRAM),
                    weight(2, WeightUnit.KILOGRAM).subtract(weight(5, WeightUnit.KILOGRAM)));
        }

        @Test
        void shouldSubtractWithExplicitTargetUnit_inGram() {
            assertEquals(weight(5000, WeightUnit.GRAM),
                    weight(10, WeightUnit.KILOGRAM).subtract(weight(5000, WeightUnit.GRAM), WeightUnit.GRAM));
        }

        @Test
        void shouldThrowException_whenSubtractingNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> weight(10, WeightUnit.KILOGRAM).subtract(null));
        }

        @Test
        void shouldThrowException_whenCrossCategory() {
            assertThrows(IllegalArgumentException.class,
                    () -> subtractCrossCategory(weight(10, WeightUnit.KILOGRAM), volume(5, VolumeUnit.LITRE)));
        }
    }


    // UC12: Volume subtraction with implicit and explicit target unit
    @Nested
    class VolumeSubtractionTests {

        @Test
        void shouldSubtractCrossUnit_LitreMinusMillilitre() {
            assertEquals(volume(4.5, VolumeUnit.LITRE),
                    volume(5, VolumeUnit.LITRE).subtract(volume(500, VolumeUnit.MILLILITRE)));
        }

        @Test
        void shouldReturnZero_whenEquivalentVolumes() {
            assertEquals(volume(0, VolumeUnit.LITRE),
                    volume(1, VolumeUnit.LITRE).subtract(volume(1000, VolumeUnit.MILLILITRE)));
        }

        @Test
        void shouldSubtractWithExplicitTargetUnit_inMillilitre() {
            assertEquals(volume(3000, VolumeUnit.MILLILITRE),
                    volume(5, VolumeUnit.LITRE).subtract(volume(2, VolumeUnit.LITRE), VolumeUnit.MILLILITRE));
        }

        @Test
        void shouldThrowException_whenSubtractingNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> volume(5, VolumeUnit.LITRE).subtract(null));
        }

        @Test
        void shouldThrowException_whenCrossCategory() {
            assertThrows(IllegalArgumentException.class,
                    () -> subtractCrossCategory(volume(5, VolumeUnit.LITRE), length(5, LengthUnit.FEET)));
        }
    }


    // UC12: Division returning a dimensionless scalar ratio across all categories
    @Nested
    class DivisionTests {

        @Test
        void shouldDivideSameUnit_length() {
            assertEquals(5.0, length(10, LengthUnit.FEET).divide(length(2, LengthUnit.FEET)), 1e-9);
        }

        @Test
        void shouldDivideCrossUnit_InchesAndFeet() {
            assertEquals(1.0, length(24, LengthUnit.INCHES).divide(length(2, LengthUnit.FEET)), 1e-9);
        }

        @Test
        void shouldReturnRatioLessThanOne_length() {
            assertEquals(0.5, length(5, LengthUnit.FEET).divide(length(10, LengthUnit.FEET)), 1e-9);
        }

        @Test
        void shouldDivideCrossUnit_GramAndKilogram() {
            assertEquals(2.0, weight(2000, WeightUnit.GRAM).divide(weight(1, WeightUnit.KILOGRAM)), 1e-9);
        }

        @Test
        void shouldReturnOne_whenBothAreEqual_weight() {
            assertEquals(1.0, weight(1, WeightUnit.KILOGRAM).divide(weight(1000, WeightUnit.GRAM)), 1e-9);
        }

        @Test
        void shouldDivideCrossUnit_MillilitreAndLitre() {
            assertEquals(1.0, volume(1000, VolumeUnit.MILLILITRE).divide(volume(1, VolumeUnit.LITRE)), 1e-9);
        }

        @Test
        void shouldReturnRatioLessThanOne_volume() {
            assertEquals(0.5, volume(5, VolumeUnit.LITRE).divide(volume(10, VolumeUnit.LITRE)), 1e-9);
        }

        @Test
        void shouldBeNonCommutative() {
            double ab = length(10, LengthUnit.FEET).divide(length(5, LengthUnit.FEET));
            double ba = length(5, LengthUnit.FEET).divide(length(10, LengthUnit.FEET));
            assertNotEquals(ab, ba);
        }

        @Test
        void shouldThrowArithmeticException_whenDividingByZero() {
            assertThrows(ArithmeticException.class,
                    () -> length(10, LengthUnit.FEET).divide(length(0, LengthUnit.FEET)));
        }

        @Test
        void shouldThrowException_whenDividingByNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> length(10, LengthUnit.FEET).divide(null));
        }

        @Test
        void shouldThrowException_whenCrossCategory() {
            assertThrows(IllegalArgumentException.class,
                    () -> divideCrossCategory(length(10, LengthUnit.FEET), weight(5, WeightUnit.KILOGRAM)));
        }

        @Test
        void shouldNotMutateOriginals() {
            Quantity<LengthUnit> a = length(10, LengthUnit.FEET);
            Quantity<LengthUnit> b = length(2, LengthUnit.FEET);
            a.divide(b);
            assertEquals(length(10, LengthUnit.FEET), a);
            assertEquals(length(2, LengthUnit.FEET), b);
        }
    }


    // UC10/UC11/UC12: Cross-category equality, subtract, and divide must all be rejected
    @Nested
    class CrossCategoryTests {

        @Test
        void shouldNotBeEqual_volumeVsLength() {
            assertNotEquals((Object) volume(1, VolumeUnit.LITRE), (Object) length(1, LengthUnit.FEET));
        }

        @Test
        void shouldNotBeEqual_volumeVsWeight() {
            assertNotEquals((Object) volume(1, VolumeUnit.LITRE), (Object) weight(1, WeightUnit.KILOGRAM));
        }

        @Test
        void shouldNotBeEqual_weightVsLength() {
            assertNotEquals((Object) weight(1, WeightUnit.KILOGRAM), (Object) length(1, LengthUnit.FEET));
        }

        @Test
        void shouldHaveDifferentHashCodes_acrossCategories() {
            assertNotEquals(
                    volume(1, VolumeUnit.LITRE).hashCode(),
                    weight(1, WeightUnit.KILOGRAM).hashCode());
        }

        @Test
        void shouldThrowException_onSubtract_crossCategory() {
            assertThrows(IllegalArgumentException.class,
                    () -> subtractCrossCategory(length(10, LengthUnit.FEET), weight(5, WeightUnit.KILOGRAM)));
        }

        @Test
        void shouldThrowException_onDivide_crossCategory() {
            assertThrows(IllegalArgumentException.class,
                    () -> divideCrossCategory(volume(10, VolumeUnit.LITRE), length(5, LengthUnit.FEET)));
        }
    }


    // UC10: Constructor validation applies to all categories
    @Nested
    class ConstructorValidationTests {

        @Test
        void shouldThrowException_whenUnitIsNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Quantity<>(1.0, (LengthUnit) null));
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


    // UC11: VolumeUnit enum conversion factor and method correctness
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
        void shouldConvertFromBaseUnit_LitreToMillilitre() {
            assertEquals(1000.0, VolumeUnit.MILLILITRE.convertFromBaseUnit(1.0), 1e-9);
        }

        @Test
        void shouldReturnCorrectUnitName() {
            assertEquals("LITRE", VolumeUnit.LITRE.getUnitName());
            assertEquals("MILLILITRE", VolumeUnit.MILLILITRE.getUnitName());
            assertEquals("GALLON", VolumeUnit.GALLON.getUnitName());
        }
    }


    // UC13: Centralized arithmetic via ArithmeticOperation enum and helper methods
    @Nested
    class CentralizedArithmeticTests {

        @Test
        void shouldThrowSameException_forNullOperand_acrossAllOperations() {
            assertThrows(IllegalArgumentException.class, () -> length(10, LengthUnit.FEET).add(null));
            assertThrows(IllegalArgumentException.class, () -> length(10, LengthUnit.FEET).subtract(null));
            assertThrows(IllegalArgumentException.class, () -> length(10, LengthUnit.FEET).divide(null));
        }

        @Test
        void shouldThrowException_forNullTargetUnit_addAndSubtract() {
            assertThrows(IllegalArgumentException.class,
                    () -> length(10, LengthUnit.FEET).add(length(5, LengthUnit.FEET), null));
            assertThrows(IllegalArgumentException.class,
                    () -> length(10, LengthUnit.FEET).subtract(length(5, LengthUnit.FEET), null));
        }

        @Test
        void shouldUseImplicitTargetUnit_forAddAndSubtract() {
            assertEquals(LengthUnit.FEET,
                    length(1, LengthUnit.FEET).add(length(12, LengthUnit.INCHES)).getUnit());
            assertEquals(LengthUnit.FEET,
                    length(10, LengthUnit.FEET).subtract(length(6, LengthUnit.INCHES)).getUnit());
        }

        @Test
        void shouldUseExplicitTargetUnit_forAddAndSubtract() {
            assertEquals(LengthUnit.INCHES,
                    length(1, LengthUnit.FEET).add(length(12, LengthUnit.INCHES), LengthUnit.INCHES).getUnit());
            assertEquals(LengthUnit.INCHES,
                    length(10, LengthUnit.FEET).subtract(length(6, LengthUnit.INCHES), LengthUnit.INCHES).getUnit());
        }

        @Test
        void shouldThrowArithmeticException_divideByZero_acrossAllCategories() {
            assertThrows(ArithmeticException.class,
                    () -> length(10, LengthUnit.FEET).divide(length(0, LengthUnit.FEET)));
            assertThrows(ArithmeticException.class,
                    () -> weight(10, WeightUnit.KILOGRAM).divide(weight(0, WeightUnit.KILOGRAM)));
            assertThrows(ArithmeticException.class,
                    () -> volume(10, VolumeUnit.LITRE).divide(volume(0, VolumeUnit.LITRE)));
        }

        @Test
        void shouldNotRoundDivisionResult() {
            double result = length(1, LengthUnit.FEET).divide(length(3, LengthUnit.FEET));
            assertTrue(result > 0.333 && result < 0.334);
        }

        @Test
        void shouldSatisfyAddSubtractInverse_acrossAllCategories() {
            Quantity<LengthUnit> la = length(7, LengthUnit.FEET), lb = length(4, LengthUnit.FEET);
            assertEquals(la, la.add(lb).subtract(lb));

            Quantity<WeightUnit> wa = weight(7, WeightUnit.KILOGRAM), wb = weight(4, WeightUnit.KILOGRAM);
            assertEquals(wa, wa.add(wb).subtract(wb));

            Quantity<VolumeUnit> va = volume(7, VolumeUnit.LITRE), vb = volume(4, VolumeUnit.LITRE);
            assertEquals(va, va.add(vb).subtract(vb));
        }

        @Test
        void shouldSupportChainedArithmeticOperations() {
            Quantity<LengthUnit> result = length(10, LengthUnit.FEET).subtract(length(4, LengthUnit.FEET));
            assertEquals(3.0, result.divide(length(2, LengthUnit.FEET)), 1e-9);
        }

        @Test
        void shouldNotMutateOriginals_acrossAllOperations() {
            Quantity<LengthUnit> a = length(10, LengthUnit.FEET), b = length(3, LengthUnit.FEET);
            a.add(b);
            a.subtract(b);
            a.divide(b);
            assertEquals(length(10, LengthUnit.FEET), a);
            assertEquals(length(3, LengthUnit.FEET), b);
        }
    }

    @Nested
    class TemperatureEqualityTests {

        @Test
        void shouldBeEqual_whenSameCelsiusValue() {
            assertEquals(temp(25.0, TemperatureUnit.CELSIUS), temp(25.0, TemperatureUnit.CELSIUS));
        }

        @Test
        void shouldBeEqual_whenSameFahrenheitValue() {
            assertEquals(temp(32.0, TemperatureUnit.FAHRENHEIT), temp(32.0, TemperatureUnit.FAHRENHEIT));
        }

        @Test
        void shouldBeEqual_whenSameKelvinValue() {
            assertEquals(temp(273.15, TemperatureUnit.KELVIN), temp(273.15, TemperatureUnit.KELVIN));
        }

        @Test
        void shouldBeEqual_whenCrossUnit_0Celsius_equals_32Fahrenheit() {
            assertEquals(temp(0.0, TemperatureUnit.CELSIUS), temp(32.0, TemperatureUnit.FAHRENHEIT));
        }

        @Test
        void shouldBeEqual_whenCrossUnit_100Celsius_equals_212Fahrenheit() {
            assertEquals(temp(100.0, TemperatureUnit.CELSIUS), temp(212.0, TemperatureUnit.FAHRENHEIT));
        }

        @Test
        void shouldBeEqual_whenCrossUnit_Negative40_equalPoint() {
            assertEquals(temp(-40.0, TemperatureUnit.CELSIUS), temp(-40.0, TemperatureUnit.FAHRENHEIT));
        }

        @Test
        void shouldBeEqual_whenCrossUnit_0Celsius_equals_27315Kelvin() {
            assertEquals(temp(0.0, TemperatureUnit.CELSIUS), temp(273.15, TemperatureUnit.KELVIN));
        }

        @Test
        void shouldBeEqual_whenCrossUnit_100Celsius_equals_37315Kelvin() {
            assertEquals(temp(100.0, TemperatureUnit.CELSIUS), temp(373.15, TemperatureUnit.KELVIN));
        }

        @Test
        void shouldBeEqual_whenCrossUnit_27315Kelvin_equals_32Fahrenheit() {
            assertEquals(temp(273.15, TemperatureUnit.KELVIN), temp(32.0, TemperatureUnit.FAHRENHEIT));
        }

        @Test
        void shouldBeEqual_whenCrossUnit_50Celsius_equals_122Fahrenheit() {
            assertEquals(temp(50.0, TemperatureUnit.CELSIUS), temp(122.0, TemperatureUnit.FAHRENHEIT));
        }

        @Test
        void shouldFollowSymmetricProperty() {
            Quantity<TemperatureUnit> celsius = temp(0.0, TemperatureUnit.CELSIUS);
            Quantity<TemperatureUnit> fahrenheit = temp(32.0, TemperatureUnit.FAHRENHEIT);
            assertTrue(celsius.equals(fahrenheit) && fahrenheit.equals(celsius));
        }

        @Test
        void shouldFollowReflexiveProperty() {
            Quantity<TemperatureUnit> t = temp(100.0, TemperatureUnit.CELSIUS);
            assertEquals(t, t);
        }

        @Test
        void shouldFollowTransitiveProperty() {
            Quantity<TemperatureUnit> a = temp(0.0, TemperatureUnit.CELSIUS);
            Quantity<TemperatureUnit> b = temp(32.0, TemperatureUnit.FAHRENHEIT);
            Quantity<TemperatureUnit> c = temp(273.15, TemperatureUnit.KELVIN);
            assertTrue(a.equals(b) && b.equals(c) && a.equals(c));
        }

        @Test
        void shouldNotBeEqual_whenDifferentCelsiusValues() {
            assertNotEquals(temp(50.0, TemperatureUnit.CELSIUS), temp(100.0, TemperatureUnit.CELSIUS));
        }

        @Test
        void shouldReturnFalse_whenComparedWithNull() {
            assertNotEquals(temp(100.0, TemperatureUnit.CELSIUS), null);
        }

        @Test
        void shouldBeEqual_whenAbsoluteZero_acrossUnits() {
            // −273.15°C = 0 K = −459.67°F
            assertEquals(temp(-273.15, TemperatureUnit.CELSIUS), temp(0.0, TemperatureUnit.KELVIN));
        }
    }


    // UC14: Temperature conversion accuracy
    @Nested
    class TemperatureConversionTests {

        @Test
        void shouldConvertCelsiusToFahrenheit_boilingPoint() {
            assertEquals(212.0,
                    temp(100.0, TemperatureUnit.CELSIUS).convertTo(TemperatureUnit.FAHRENHEIT).getValue(), 1e-9);
        }

        @Test
        void shouldConvertFahrenheitToCelsius_freezingPoint() {
            assertEquals(0.0,
                    temp(32.0, TemperatureUnit.FAHRENHEIT).convertTo(TemperatureUnit.CELSIUS).getValue(), 1e-9);
        }

        @Test
        void shouldConvertKelvinToCelsius_freezingPoint() {
            assertEquals(0.0,
                    temp(273.15, TemperatureUnit.KELVIN).convertTo(TemperatureUnit.CELSIUS).getValue(), 1e-9);
        }

        @Test
        void shouldConvertCelsiusToKelvin_freezingPoint() {
            assertEquals(273.15,
                    temp(0.0, TemperatureUnit.CELSIUS).convertTo(TemperatureUnit.KELVIN).getValue(), 1e-9);
        }

        @Test
        void shouldConvertCelsiusToFahrenheit_equalPoint() {
            assertEquals(-40.0,
                    temp(-40.0, TemperatureUnit.CELSIUS).convertTo(TemperatureUnit.FAHRENHEIT).getValue(), 1e-9);
        }

        @Test
        void shouldConvertFahrenheitToKelvin_boilingPoint() {
            assertEquals(373.15,
                    temp(212.0, TemperatureUnit.FAHRENHEIT).convertTo(TemperatureUnit.KELVIN).getValue(), 1e-9);
        }

        @Test
        void shouldConvertCelsiusToFahrenheit_bodyTemp() {
            assertEquals(98.6,
                    temp(37.0, TemperatureUnit.CELSIUS).convertTo(TemperatureUnit.FAHRENHEIT).getValue(), 0.01);
        }

        @Test
        void shouldConvertCelsiusToFahrenheit_negativeValue() {
            assertEquals(-4.0,
                    temp(-20.0, TemperatureUnit.CELSIUS).convertTo(TemperatureUnit.FAHRENHEIT).getValue(), 1e-9);
        }

        @Test
        void shouldReturnSameInstance_whenConvertingToSameUnit() {
            Quantity<TemperatureUnit> original = temp(100.0, TemperatureUnit.CELSIUS);
            assertSame(original, original.convertTo(TemperatureUnit.CELSIUS));
        }

        @Test
        void shouldSatisfyRoundTrip_CelsiusToFahrenheitAndBack() {
            double original = 37.0;
            double roundTripped = temp(original, TemperatureUnit.CELSIUS)
                    .convertTo(TemperatureUnit.FAHRENHEIT)
                    .convertTo(TemperatureUnit.CELSIUS)
                    .getValue();
            assertEquals(original, roundTripped, 1e-9);
        }

        @Test
        void shouldSatisfyRoundTrip_CelsiusToKelvinAndBack() {
            double original = 100.0;
            double roundTripped = temp(original, TemperatureUnit.CELSIUS)
                    .convertTo(TemperatureUnit.KELVIN)
                    .convertTo(TemperatureUnit.CELSIUS)
                    .getValue();
            assertEquals(original, roundTripped, 1e-9);
        }

        @Test
        void shouldConvertAbsoluteZero_CelsiusToKelvin() {
            assertEquals(0.0,
                    temp(-273.15, TemperatureUnit.CELSIUS).convertTo(TemperatureUnit.KELVIN).getValue(), 1e-9);
        }

        @Test
        void shouldThrowException_whenTargetUnitIsNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> temp(100.0, TemperatureUnit.CELSIUS).convertTo(null));
        }

        @Test
        void shouldReturnCorrectUnit_afterConversion() {
            assertEquals(TemperatureUnit.FAHRENHEIT,
                    temp(100.0, TemperatureUnit.CELSIUS).convertTo(TemperatureUnit.FAHRENHEIT).getUnit());
        }
    }


    // UC14: Unsupported arithmetic operations on temperature
    @Nested
    class TemperatureUnsupportedOperationTests {

        @Test
        void shouldThrowUnsupportedOperationException_onAdd_Celsius() {
            assertThrows(UnsupportedOperationException.class,
                    () -> temp(100.0, TemperatureUnit.CELSIUS).add(temp(50.0, TemperatureUnit.CELSIUS)));
        }

        @Test
        void shouldThrowUnsupportedOperationException_onSubtract_Celsius() {
            assertThrows(UnsupportedOperationException.class,
                    () -> temp(100.0, TemperatureUnit.CELSIUS).subtract(temp(50.0, TemperatureUnit.CELSIUS)));
        }

        @Test
        void shouldThrowUnsupportedOperationException_onDivide_Celsius() {
            assertThrows(UnsupportedOperationException.class,
                    () -> temp(100.0, TemperatureUnit.CELSIUS).divide(temp(50.0, TemperatureUnit.CELSIUS)));
        }

        @Test
        void shouldThrowUnsupportedOperationException_onAdd_Fahrenheit() {
            assertThrows(UnsupportedOperationException.class,
                    () -> temp(212.0, TemperatureUnit.FAHRENHEIT).add(temp(32.0, TemperatureUnit.FAHRENHEIT)));
        }

        @Test
        void shouldThrowUnsupportedOperationException_onSubtract_Fahrenheit() {
            assertThrows(UnsupportedOperationException.class,
                    () -> temp(212.0, TemperatureUnit.FAHRENHEIT).subtract(temp(32.0, TemperatureUnit.FAHRENHEIT)));
        }

        @Test
        void shouldThrowUnsupportedOperationException_onSubtract_Kelvin() {
            assertThrows(UnsupportedOperationException.class,
                    () -> temp(373.15, TemperatureUnit.KELVIN).subtract(temp(273.15, TemperatureUnit.KELVIN)));
        }

        @Test
        void shouldThrowUnsupportedOperationException_onDivide_Kelvin() {
            assertThrows(UnsupportedOperationException.class,
                    () -> temp(373.15, TemperatureUnit.KELVIN).divide(temp(273.15, TemperatureUnit.KELVIN)));
        }

        @Test
        void shouldThrowUnsupportedOperationException_onSubtractWithTargetUnit() {
            assertThrows(UnsupportedOperationException.class,
                    () -> temp(100.0, TemperatureUnit.CELSIUS)
                            .subtract(temp(50.0, TemperatureUnit.CELSIUS), TemperatureUnit.CELSIUS));
        }

        @Test
        void shouldThrowUnsupportedOperationException_onStaticAdd() {
            assertThrows(UnsupportedOperationException.class,
                    () -> Quantity.add(temp(100.0, TemperatureUnit.CELSIUS),
                            temp(50.0, TemperatureUnit.CELSIUS),
                            TemperatureUnit.CELSIUS));
        }

        @Test
        void shouldContainDescriptiveMessage_onUnsupportedOperation() {
            UnsupportedOperationException ex = assertThrows(UnsupportedOperationException.class,
                    () -> temp(100.0, TemperatureUnit.CELSIUS).add(temp(50.0, TemperatureUnit.CELSIUS)));
            assertNotNull(ex.getMessage());
            assertFalse(ex.getMessage().isBlank());
        }

        @Test
        void shouldThrowUnsupportedOperation_notNullPointer_forAdd() {
            // Confirm the exception type is exactly UnsupportedOperationException, not NPE
            Exception ex = assertThrows(UnsupportedOperationException.class,
                    () -> temp(100.0, TemperatureUnit.CELSIUS).add(temp(0.0, TemperatureUnit.CELSIUS)));
            assertInstanceOf(UnsupportedOperationException.class, ex);
        }
    }


    // UC14: Temperature — cross-category type safety
    @Nested
    class TemperatureCrossCategoryTests {

        @Test
        void shouldNotBeEqual_temperatureVsLength() {
            assertNotEquals((Object) temp(100.0, TemperatureUnit.CELSIUS),
                    (Object) length(100.0, LengthUnit.FEET));
        }

        @Test
        void shouldNotBeEqual_temperatureVsWeight() {
            assertNotEquals((Object) temp(50.0, TemperatureUnit.CELSIUS),
                    (Object) weight(50.0, WeightUnit.KILOGRAM));
        }

        @Test
        void shouldNotBeEqual_temperatureVsVolume() {
            assertNotEquals((Object) temp(25.0, TemperatureUnit.CELSIUS),
                    (Object) volume(25.0, VolumeUnit.LITRE));
        }

        @Test
        void shouldNotBeEqual_lengthVsTemperature() {
            assertNotEquals((Object) length(100.0, LengthUnit.FEET),
                    (Object) temp(100.0, TemperatureUnit.CELSIUS));
        }
    }


    // UC14: SupportsArithmetic capability flag
    @Nested
    class OperationSupportTests {

        @Test
        void shouldThrowUnsupportedOperationException_whenValidateOperationCalledOnTemperature() {
            assertThrows(UnsupportedOperationException.class,
                    () -> TemperatureUnit.CELSIUS.validateOperationSupport("addition"));
        }

        @Test
        void shouldNotThrow_whenValidateOperationCalledOnLength() {
            assertDoesNotThrow(() -> LengthUnit.FEET.validateOperationSupport("ADD"));
        }

        @Test
        void shouldNotThrow_whenValidateOperationCalledOnWeight() {
            assertDoesNotThrow(() -> WeightUnit.KILOGRAM.validateOperationSupport("SUBTRACT"));
        }

        @Test
        void shouldNotThrow_whenValidateOperationCalledOnVolume() {
            assertDoesNotThrow(() -> VolumeUnit.LITRE.validateOperationSupport("DIVIDE"));
        }
    }


    // UC14: TemperatureUnit enum structure
    @Nested
    class TemperatureUnitEnumTests {

        @Test
        void shouldHaveCorrectUnitName_Celsius() {
            assertEquals("Celsius", TemperatureUnit.CELSIUS.getUnitName());
        }

        @Test
        void shouldHaveCorrectUnitName_Fahrenheit() {
            assertEquals("Fahrenheit", TemperatureUnit.FAHRENHEIT.getUnitName());
        }

        @Test
        void shouldHaveCorrectUnitName_Kelvin() {
            assertEquals("Kelvin", TemperatureUnit.KELVIN.getUnitName());
        }

        @Test
        void shouldImplementIMeasurable() {
            assertTrue(TemperatureUnit.CELSIUS instanceof IMeasurable);
        }


        @Test
        void shouldConvertToBaseUnit_CelsiusToKelvin() {
            assertEquals(373.15, TemperatureUnit.CELSIUS.convertToBaseUnit(100.0), 1e-9);
        }

        @Test
        void shouldConvertFromBaseUnit_KelvinToCelsius() {
            assertEquals(100.0, TemperatureUnit.CELSIUS.convertFromBaseUnit(373.15), 1e-9);
        }

        @Test
        void shouldConvertToBaseUnit_FahrenheitToKelvin() {
            assertEquals(373.15, TemperatureUnit.FAHRENHEIT.convertToBaseUnit(212.0), 1e-9);
        }

        @Test
        void shouldConvertFromBaseUnit_KelvinToFahrenheit() {
            assertEquals(212.0, TemperatureUnit.FAHRENHEIT.convertFromBaseUnit(373.15), 1e-9);
        }

        @Test
        void shouldConvertToBaseUnit_KelvinIdentity() {
            assertEquals(300.0, TemperatureUnit.KELVIN.convertToBaseUnit(300.0), 1e-9);
        }
    }


    // UC14: Constructor validation for TemperatureUnit
    @Nested
    class TemperatureConstructorValidationTests {

        @Test
        void shouldThrowException_whenTemperatureUnitIsNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Quantity<>(100.0, (TemperatureUnit) null));
        }

        @Test
        void shouldThrowException_whenTemperatureValueIsNaN() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Quantity<>(Double.NaN, TemperatureUnit.CELSIUS));
        }

        @Test
        void shouldThrowException_whenTemperatureValueIsInfinite() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Quantity<>(Double.POSITIVE_INFINITY, TemperatureUnit.FAHRENHEIT));
        }

        @Test
        void shouldReturnFalse_whenEqualsNull() {
            assertNotEquals(temp(100.0, TemperatureUnit.CELSIUS), null);
        }
    }


    // UC14: Backward compatibility — UC1–UC13 unaffected by UC14 changes
    @Nested
    class BackwardCompatibilityTests {

        @Test
        void shouldStillAddLength_afterUC14Refactoring() {
            assertEquals(length(2, LengthUnit.FEET),
                    length(1, LengthUnit.FEET).add(length(12, LengthUnit.INCHES)));
        }

        @Test
        void shouldStillSubtractWeight_afterUC14Refactoring() {
            assertEquals(weight(5, WeightUnit.KILOGRAM),
                    weight(10, WeightUnit.KILOGRAM).subtract(weight(5000, WeightUnit.GRAM)));
        }

        @Test
        void shouldStillDivideVolume_afterUC14Refactoring() {
            assertEquals(1.0,
                    volume(1000, VolumeUnit.MILLILITRE).divide(volume(1, VolumeUnit.LITRE)), 1e-9);
        }

        @Test
        void shouldStillConvertLength_afterUC14Refactoring() {
            assertEquals(length(12, LengthUnit.INCHES),
                    length(1, LengthUnit.FEET).convertTo(LengthUnit.INCHES));
        }

        @Test
        void shouldStillCompareCrossUnit_weight_afterUC14Refactoring() {
            assertEquals(weight(1, WeightUnit.KILOGRAM), weight(1000, WeightUnit.GRAM));
        }

        @Test
        void shouldIMeasurableDefaultsStillWork_forLength() {
            assertDoesNotThrow(() -> LengthUnit.FEET.validateOperationSupport("ADD"));
        }
    }
}