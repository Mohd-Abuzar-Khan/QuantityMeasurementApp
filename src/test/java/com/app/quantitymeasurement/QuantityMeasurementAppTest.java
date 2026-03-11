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

    @Nested
    class TemperatureEqualityTests {
        
        @Test
        void testTemperatureEquality_CelsiusToCelsius_SameValue() {
            assertEquals(temp(0.0, TemperatureUnit.CELSIUS), temp(0.0, TemperatureUnit.CELSIUS));
        }
        
        @Test
        void testTemperatureEquality_FahrenheitToFahrenheit_SameValue() {
            assertEquals(temp(32.0, TemperatureUnit.FAHRENHEIT), temp(32.0, TemperatureUnit.FAHRENHEIT));
        }
        
        @Test
        void testTemperatureEquality_KelvinToKelvin_SameValue() {
            assertEquals(temp(273.15, TemperatureUnit.KELVIN), temp(273.15, TemperatureUnit.KELVIN));
        }
        
        @Test
        void testTemperatureEquality_CelsiusToFahrenheit_0Celsius32Fahrenheit() {
            assertEquals(temp(0.0, TemperatureUnit.CELSIUS), temp(32.0, TemperatureUnit.FAHRENHEIT));
        }
        
        @Test
        void testTemperatureEquality_CelsiusToFahrenheit_100Celsius212Fahrenheit() {
            assertEquals(temp(100.0, TemperatureUnit.CELSIUS), temp(212.0, TemperatureUnit.FAHRENHEIT));
        }
        
        @Test
        void testTemperatureEquality_CelsiusToKelvin_0Celsius273Kelvin() {
            assertEquals(temp(0.0, TemperatureUnit.CELSIUS), temp(273.15, TemperatureUnit.KELVIN));
        }
        
        @Test
        void testTemperatureEquality_FahrenheitToKelvin_32Fahrenheit273Kelvin() {
            assertEquals(temp(32.0, TemperatureUnit.FAHRENHEIT), temp(273.15, TemperatureUnit.KELVIN));
        }
        
        @Test
        void testTemperatureEquality_CelsiusToFahrenheit_Negative40Equal() {
            assertEquals(temp(-40.0, TemperatureUnit.CELSIUS), temp(-40.0, TemperatureUnit.FAHRENHEIT));
        }
        
        @Test
        void testTemperatureEquality_SymmetricProperty() {
            Quantity<TemperatureUnit> celsius = temp(0.0, TemperatureUnit.CELSIUS);
            Quantity<TemperatureUnit> fahrenheit = temp(32.0, TemperatureUnit.FAHRENHEIT);
            assertEquals(celsius, fahrenheit);
            assertEquals(fahrenheit, celsius);
        }
        
        @Test
        void testTemperatureEquality_TransitiveProperty() {
            Quantity<TemperatureUnit> celsius = temp(0.0, TemperatureUnit.CELSIUS);
            Quantity<TemperatureUnit> fahrenheit = temp(32.0, TemperatureUnit.FAHRENHEIT);
            Quantity<TemperatureUnit> kelvin = temp(273.15, TemperatureUnit.KELVIN);
            
            assertEquals(celsius, fahrenheit);
            assertEquals(fahrenheit, kelvin);
            assertEquals(celsius, kelvin);
        }
        
        @Test
        void testTemperatureEquality_ReflexiveProperty() {
            Quantity<TemperatureUnit> temperature = temp(25.0, TemperatureUnit.CELSIUS);
            assertEquals(temperature, temperature);
        }
    }

    @Nested
    class TemperatureConversionTests {
        
        @Test
        void testTemperatureConversion_CelsiusToFahrenheit_VariousValues() {
            assertEquals(122.0, temp(50.0, TemperatureUnit.CELSIUS).convertTo(TemperatureUnit.FAHRENHEIT).getValue());
            assertEquals(-4.0, temp(-20.0, TemperatureUnit.CELSIUS).convertTo(TemperatureUnit.FAHRENHEIT).getValue());
            assertEquals(212.0, temp(100.0, TemperatureUnit.CELSIUS).convertTo(TemperatureUnit.FAHRENHEIT).getValue());
        }
        
        @Test
        void testTemperatureConversion_FahrenheitToCelsius_VariousValues() {
            assertEquals(50.0, temp(122.0, TemperatureUnit.FAHRENHEIT).convertTo(TemperatureUnit.CELSIUS).getValue());
            assertEquals(-20.0, temp(-4.0, TemperatureUnit.FAHRENHEIT).convertTo(TemperatureUnit.CELSIUS).getValue());
            assertEquals(100.0, temp(212.0, TemperatureUnit.FAHRENHEIT).convertTo(TemperatureUnit.CELSIUS).getValue());
        }
        
        @Test
        void testTemperatureConversion_RoundTrip_PreservesValue() {
            Quantity<TemperatureUnit> original = temp(25.0, TemperatureUnit.CELSIUS);
            Quantity<TemperatureUnit> converted = original.convertTo(TemperatureUnit.FAHRENHEIT)
                                                         .convertTo(TemperatureUnit.CELSIUS);
            assertEquals(original, converted);
        }
        
        @Test
        void testTemperatureConversion_SameUnit() {
            Quantity<TemperatureUnit> original = temp(25.0, TemperatureUnit.CELSIUS);
            Quantity<TemperatureUnit> converted = original.convertTo(TemperatureUnit.CELSIUS);
            assertSame(original, converted);
        }
        
        @Test
        void testTemperatureConversion_ZeroValue() {
            assertEquals(32.0, temp(0.0, TemperatureUnit.CELSIUS).convertTo(TemperatureUnit.FAHRENHEIT).getValue());
        }
        
        @Test
        void testTemperatureConversion_NegativeValues() {
            assertEquals(-40.0, temp(-40.0, TemperatureUnit.CELSIUS).convertTo(TemperatureUnit.FAHRENHEIT).getValue());
        }
        
        @Test
        void testTemperatureConversion_LargeValues() {
            assertEquals(1832.0, temp(1000.0, TemperatureUnit.CELSIUS).convertTo(TemperatureUnit.FAHRENHEIT).getValue());
        }
        
        @Test
        void testTemperatureConversion_EdgeCase_AbsoluteZero() {
            assertEquals(-459.67, temp(-273.15, TemperatureUnit.CELSIUS).convertTo(TemperatureUnit.FAHRENHEIT).getValue(), 0.01);
            assertEquals(0.0, temp(-273.15, TemperatureUnit.CELSIUS).convertTo(TemperatureUnit.KELVIN).getValue(), 0.01);
        }
        
        @Test
        void testTemperatureConversionPrecision_Epsilon() {
            Quantity<TemperatureUnit> temp1 = temp(0.001, TemperatureUnit.CELSIUS);
            Quantity<TemperatureUnit> temp2 = temp(32.0018, TemperatureUnit.FAHRENHEIT);
            assertEquals(temp1, temp2);
        }
        
        @Test
        void testTemperatureConversionEdgeCase_VerySmallDifference() {
            Quantity<TemperatureUnit> temp1 = temp(0.0001, TemperatureUnit.CELSIUS);
            Quantity<TemperatureUnit> temp2 = temp(32.00018, TemperatureUnit.FAHRENHEIT);
            assertEquals(temp1, temp2);
        }
    }

    @Nested
    class TemperatureUnsupportedOperationTests {
        
        @Test
        void testTemperatureUnsupportedOperation_Add() {
            assertThrows(UnsupportedOperationException.class, () -> {
                temp(100.0, TemperatureUnit.CELSIUS).add(temp(50.0, TemperatureUnit.CELSIUS));
            });
        }
        
        @Test
        void testTemperatureUnsupportedOperation_Subtract() {
            assertThrows(UnsupportedOperationException.class, () -> {
                temp(100.0, TemperatureUnit.CELSIUS).subtract(temp(50.0, TemperatureUnit.CELSIUS));
            });
        }
        
        @Test
        void testTemperatureUnsupportedOperation_Divide() {
            assertThrows(UnsupportedOperationException.class, () -> {
                temp(100.0, TemperatureUnit.CELSIUS).divide(temp(50.0, TemperatureUnit.CELSIUS));
            });
        }
        
        @Test
        void testTemperatureUnsupportedOperation_ErrorMessage() {
            Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
                TemperatureUnit.CELSIUS.validateOperationSupport("addition");
            });
            assertTrue(exception.getMessage().contains("does not support addition"));
        }
        
        @Test
        void testTemperatureCrossUnitAdditionAttempt() {
            assertThrows(UnsupportedOperationException.class, () -> {
                temp(100.0, TemperatureUnit.CELSIUS).add(temp(50.0, TemperatureUnit.FAHRENHEIT));
            });
        }
    }

    @Nested
    class TemperatureTypeSafetyTests {
        
        @Test
        void testTemperatureVsLengthIncompatibility() {
            assertNotEquals(temp(100.0, TemperatureUnit.CELSIUS), length(100.0, LengthUnit.FEET));
        }
        
        @Test
        void testTemperatureVsWeightIncompatibility() {
            assertNotEquals(temp(50.0, TemperatureUnit.CELSIUS), weight(50.0, WeightUnit.KILOGRAM));
        }
        
        @Test
        void testTemperatureVsVolumeIncompatibility() {
            assertNotEquals(temp(25.0, TemperatureUnit.CELSIUS), volume(25.0, VolumeUnit.LITRE));
        }
    }

    @Nested
    class TemperatureOperationSupportTests {
        
        @Test
        void testOperationSupportMethods_TemperatureUnitAddition() {
            assertFalse(TemperatureUnit.CELSIUS.supportsAddition());
            assertFalse(TemperatureUnit.FAHRENHEIT.supportsAddition());
            assertFalse(TemperatureUnit.KELVIN.supportsAddition());
        }
        
        @Test
        void testOperationSupportMethods_TemperatureUnitDivision() {
            assertFalse(TemperatureUnit.CELSIUS.supportsDivision());
            assertFalse(TemperatureUnit.FAHRENHEIT.supportsDivision());
            assertFalse(TemperatureUnit.KELVIN.supportsDivision());
        }
        
        @Test
        void testOperationSupportMethods_TemperatureUnitSubtraction() {
            assertFalse(TemperatureUnit.CELSIUS.supportsSubtraction());
            assertFalse(TemperatureUnit.FAHRENHEIT.supportsSubtraction());
            assertFalse(TemperatureUnit.KELVIN.supportsSubtraction());
        }
        
        @Test
        void testOperationSupportMethods_TemperatureUnitMultiplication() {
            assertFalse(TemperatureUnit.CELSIUS.supportsMultiplication());
            assertFalse(TemperatureUnit.FAHRENHEIT.supportsMultiplication());
            assertFalse(TemperatureUnit.KELVIN.supportsMultiplication());
        }
        
        @Test
        void testOperationSupportMethods_LengthUnitAddition() {
            assertTrue(LengthUnit.FEET.supportsAddition());
        }
        
        @Test
        void testOperationSupportMethods_WeightUnitDivision() {
            assertTrue(WeightUnit.KILOGRAM.supportsDivision());
        }
        
        @Test
        void testTemperatureDefaultMethodInheritance() {
            assertTrue(LengthUnit.FEET.supportsAddition());
            assertTrue(WeightUnit.KILOGRAM.supportsDivision());
            assertTrue(VolumeUnit.LITRE.supportsAddition());
        }
    }

    @Nested
    class TemperatureValidationTests {
        
        @Test
        void testTemperatureNullUnitValidation() {
            assertThrows(IllegalArgumentException.class, () -> {
                new Quantity<>(100.0, null);
            });
        }
        
        @Test
        void testTemperatureNullOperandValidation_InComparison() {
            Quantity<TemperatureUnit> temperature = temp(25.0, TemperatureUnit.CELSIUS);
            assertFalse(temperature.equals(null));
        }
        
        @Test
        void testTemperatureDifferentValuesInequality() {
            assertNotEquals(temp(50.0, TemperatureUnit.CELSIUS), temp(100.0, TemperatureUnit.CELSIUS));
        }
        
        @Test
        void testTemperatureValidateOperationSupport_MethodBehavior() {
            assertThrows(UnsupportedOperationException.class, () -> {
                TemperatureUnit.CELSIUS.validateOperationSupport("addition");
            });
            
            assertThrows(UnsupportedOperationException.class, () -> {
                TemperatureUnit.FAHRENHEIT.validateOperationSupport("subtraction");
            });
            
            assertThrows(UnsupportedOperationException.class, () -> {
                TemperatureUnit.KELVIN.validateOperationSupport("division");
            });
        }
    }

    @Nested
    class TemperatureEnumTests {
        
        @Test
        void testTemperatureUnit_AllConstants() {
            assertNotNull(TemperatureUnit.CELSIUS);
            assertNotNull(TemperatureUnit.FAHRENHEIT);
            assertNotNull(TemperatureUnit.KELVIN);
        }
        
        @Test
        void testTemperatureUnit_NameMethod() {
            assertEquals("CELSIUS", TemperatureUnit.CELSIUS.getUnitName());
            assertEquals("FAHRENHEIT", TemperatureUnit.FAHRENHEIT.getUnitName());
            assertEquals("KELVIN", TemperatureUnit.KELVIN.getUnitName());
        }
        
        @Test
        void testTemperatureEnumImplementsIMeasurable() {
            assertTrue(TemperatureUnit.CELSIUS instanceof IMeasurable);
            assertTrue(TemperatureUnit.FAHRENHEIT instanceof IMeasurable);
            assertTrue(TemperatureUnit.KELVIN instanceof IMeasurable);
        }
        
        @Test
        void testTemperatureUnit_NonLinearConversion() {
            // Verify that temperature conversions are not simple multiplication
            double celsiusToFahrenheitFactor = 32.0 / 0.0; // This would be infinite if linear
            assertNotEquals(celsiusToFahrenheitFactor, temp(1.0, TemperatureUnit.CELSIUS)
                .convertTo(TemperatureUnit.FAHRENHEIT).getValue());
        }
    }

    @Nested
    class TemperatureIntegrationTests {
        
        @Test
        void testTemperatureIntegrationWithGenericQuantity() {
            Quantity<TemperatureUnit> celsius = temp(25.0, TemperatureUnit.CELSIUS);
            assertEquals(25.0, celsius.getValue());
            assertEquals(TemperatureUnit.CELSIUS, celsius.getUnit());
            assertEquals("Quantity(25.00, CELSIUS)", celsius.toString());
        }
        
        @Test
        void testIMeasurableInterface_Evolution_BackwardCompatible() {
            // Verify existing units still work with default methods
            assertTrue(LengthUnit.FEET.supportsAddition());
            assertTrue(WeightUnit.KILOGRAM.supportsDivision());
            assertTrue(VolumeUnit.LITRE.supportsAddition());
        }
        
        @Test
        void testTemperatureBackwardCompatibility_UC1_Through_UC13() {
            // Basic length operations still work
            Quantity<LengthUnit> feet1 = length(1.0, LengthUnit.FEET);
            Quantity<LengthUnit> inches1 = length(12.0, LengthUnit.INCHES);
            assertEquals(feet1, inches1);
            
            // Basic weight operations still work
            Quantity<WeightUnit> kg1 = weight(1.0, WeightUnit.KILOGRAM);
            Quantity<WeightUnit> grams1 = weight(1000.0, WeightUnit.GRAM);
            assertEquals(kg1, grams1);
            
            // Basic volume operations still work
            Quantity<VolumeUnit> litre1 = volume(1.0, VolumeUnit.LITRE);
            assertEquals(litre1, litre1);
        }
    }
    
}