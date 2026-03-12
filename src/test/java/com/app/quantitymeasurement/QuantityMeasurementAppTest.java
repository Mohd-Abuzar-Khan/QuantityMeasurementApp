package com.app.quantitymeasurement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.app.controller.QuantityController;
import com.app.dto.QuantityDTO;
import com.app.dto.QuantityDTO.TemperatureUnit;
import com.app.dto.QuantityDTO.LengthUnit;
import com.app.dto.QuantityDTO.WeightUnit;
import com.app.dto.QuantityDTO.VolumeUnit; 
import com.app.service.IQuantityService;
import com.app.service.QuantityServiceImpl;

import static org.junit.jupiter.api.Assertions.*;

class QuantityControllerTest {

    private QuantityController controller;
    private IQuantityService service;


    @BeforeEach
    void setUp() {
        service    = new QuantityServiceImpl();
        controller = new QuantityController(service);
    }


    private QuantityDTO temp(double value, TemperatureUnit unit) {
        return new QuantityDTO(value, unit);
    }

    private QuantityDTO length(double value, LengthUnit unit) {
        return new QuantityDTO(value, unit);
    }

    private QuantityDTO weight(double value, WeightUnit unit) {
        return new QuantityDTO(value, unit);
    }

    private QuantityDTO volume(double value, VolumeUnit unit) {
        return new QuantityDTO(value, unit);
    }

    @Nested
    class TemperatureEqualityTests {

        @Test
        void testTemperatureEquality_CelsiusToCelsius_SameValue() {
            assertTrue(controller.performComparison(temp(0.0, TemperatureUnit.CELSIUS), temp(0.0, TemperatureUnit.CELSIUS)));
        }

        @Test
        void testTemperatureEquality_FahrenheitToFahrenheit_SameValue() {
            assertTrue(controller.performComparison(temp(32.0, TemperatureUnit.FAHRENHEIT), temp(32.0, TemperatureUnit.FAHRENHEIT)));
        }

        @Test
        void testTemperatureEquality_KelvinToKelvin_SameValue() {
            assertTrue(controller.performComparison(temp(273.15, TemperatureUnit.KELVIN), temp(273.15, TemperatureUnit.KELVIN)));
        }

        @Test
        void testTemperatureEquality_CelsiusToFahrenheit_0Celsius32Fahrenheit() {
            assertTrue(controller.performComparison(temp(0.0, TemperatureUnit.CELSIUS), temp(32.0, TemperatureUnit.FAHRENHEIT)));
        }

        @Test
        void testTemperatureEquality_CelsiusToFahrenheit_100Celsius212Fahrenheit() {
            assertTrue(controller.performComparison(temp(100.0, TemperatureUnit.CELSIUS), temp(212.0, TemperatureUnit.FAHRENHEIT)));
        }

        @Test
        void testTemperatureEquality_CelsiusToKelvin_0Celsius273Kelvin() {
            assertTrue(controller.performComparison(temp(0.0, TemperatureUnit.CELSIUS), temp(273.15, TemperatureUnit.KELVIN)));
        }

        @Test
        void testTemperatureEquality_FahrenheitToKelvin_32Fahrenheit273Kelvin() {
            assertTrue(controller.performComparison(temp(32.0, TemperatureUnit.FAHRENHEIT), temp(273.15, TemperatureUnit.KELVIN)));
        }

        @Test
        void testTemperatureEquality_CelsiusToFahrenheit_Negative40Equal() {
            assertTrue(controller.performComparison(temp(-40.0, TemperatureUnit.CELSIUS), temp(-40.0, TemperatureUnit.FAHRENHEIT)));
        }

        @Test
        void testTemperatureEquality_SymmetricProperty() {
            QuantityDTO celsius     = temp(0.0, TemperatureUnit.CELSIUS);
            QuantityDTO fahrenheit  = temp(32.0, TemperatureUnit.FAHRENHEIT);
            assertTrue(controller.performComparison(celsius, fahrenheit));
            assertTrue(controller.performComparison(fahrenheit, celsius));
        }

        @Test
        void testTemperatureEquality_TransitiveProperty() {
            QuantityDTO celsius    = temp(0.0, TemperatureUnit.CELSIUS);
            QuantityDTO fahrenheit = temp(32.0, TemperatureUnit.FAHRENHEIT);
            QuantityDTO kelvin     = temp(273.15, TemperatureUnit.KELVIN);
            assertTrue(controller.performComparison(celsius, fahrenheit));
            assertTrue(controller.performComparison(fahrenheit, kelvin));
            assertTrue(controller.performComparison(celsius, kelvin));
        }

        @Test
        void testTemperatureEquality_ReflexiveProperty() {
            QuantityDTO temperature = temp(25.0, TemperatureUnit.CELSIUS);
            assertTrue(controller.performComparison(temperature, temperature));
        }
    }

    @Nested
    class TemperatureConversionTests {

        @Test
        void testTemperatureConversion_CelsiusToFahrenheit_VariousValues() {
            assertEquals(122.0,  controller.performConversion(temp(50.0,   TemperatureUnit.CELSIUS), temp(0.0,    TemperatureUnit.FAHRENHEIT)).getValue());
            assertEquals(-4.0,   controller.performConversion(temp(-20.0,  TemperatureUnit.CELSIUS), temp(0.0,    TemperatureUnit.FAHRENHEIT)).getValue());
            assertEquals(212.0,  controller.performConversion(temp(100.0,  TemperatureUnit.CELSIUS), temp(0.0,    TemperatureUnit.FAHRENHEIT)).getValue());
        }

        @Test
        void testTemperatureConversion_FahrenheitToCelsius_VariousValues() {
            assertEquals(50.0,   controller.performConversion(temp(122.0,  TemperatureUnit.FAHRENHEIT), temp(0.0,    TemperatureUnit.CELSIUS)).getValue());
            assertEquals(-20.0,  controller.performConversion(temp(-4.0,   TemperatureUnit.FAHRENHEIT), temp(0.0,    TemperatureUnit.CELSIUS)).getValue());
            assertEquals(100.0,  controller.performConversion(temp(212.0,  TemperatureUnit.FAHRENHEIT), temp(0.0,    TemperatureUnit.CELSIUS)).getValue());
        }

        @Test
        void testTemperatureConversion_RoundTrip_PreservesValue() {
            QuantityDTO original     = temp(25.0, TemperatureUnit.CELSIUS);
            QuantityDTO toFahrenheit = controller.performConversion(original, temp(0.0, TemperatureUnit.FAHRENHEIT));
            QuantityDTO backToCelsius = controller.performConversion(toFahrenheit, temp(0.0, TemperatureUnit.CELSIUS));
            assertTrue(controller.performComparison(original, backToCelsius));
        }

        @Test
        void testTemperatureConversion_SameUnit_ReturnsSameValue() {
            QuantityDTO original  = temp(25.0, TemperatureUnit.CELSIUS);
            QuantityDTO converted = controller.performConversion(original, temp(0.0, TemperatureUnit.CELSIUS));
            assertEquals(original.getValue(), converted.getValue());
            assertEquals(original.getUnit(),  converted.getUnit());
        }

        @Test
        void testTemperatureConversion_ZeroValue() {
            assertEquals(32.0, controller.performConversion(temp(0.0, TemperatureUnit.CELSIUS), temp(0.0, TemperatureUnit.FAHRENHEIT)).getValue());
        }

        @Test
        void testTemperatureConversion_NegativeValues() {
            assertEquals(-40.0, controller.performConversion(temp(-40.0, TemperatureUnit.CELSIUS), temp(0.0,   TemperatureUnit.FAHRENHEIT)).getValue());
        }

        @Test
        void testTemperatureConversion_LargeValues() {
            assertEquals(1832.0, controller.performConversion(temp(1000.0, TemperatureUnit.CELSIUS), temp(0.0,    TemperatureUnit.FAHRENHEIT)).getValue());
        }

        @Test
        void testTemperatureConversion_EdgeCase_AbsoluteZero() {
            assertEquals(-459.67, controller.performConversion(temp(-273.15, TemperatureUnit.CELSIUS), temp(0.0,     TemperatureUnit.FAHRENHEIT)).getValue(), 0.01);
            assertEquals(0.0, controller.performConversion(temp(-273.15, TemperatureUnit.CELSIUS), temp(0.0,     TemperatureUnit.KELVIN)).getValue(), 0.01);
        }

        @Test
        void testTemperatureConversionPrecision_Epsilon() {
            assertTrue(controller.performComparison(temp(0.001,   TemperatureUnit.CELSIUS), temp(32.0018, TemperatureUnit.FAHRENHEIT)));
        }

        @Test
        void testTemperatureConversionEdgeCase_VerySmallDifference() {
            assertTrue(controller.performComparison(temp(0.0001,    TemperatureUnit.CELSIUS), temp(32.00018,  TemperatureUnit.FAHRENHEIT)));
        }
    }

    @Nested
    class TemperatureUnsupportedOperationTests {

        @Test
        void testTemperatureUnsupportedOperation_Add() {
            // Controller catches the exception and returns null for unsupported ops
            QuantityDTO result = controller.performAddition(
                temp(100.0, TemperatureUnit.CELSIUS),
                temp(50.0,  TemperatureUnit.CELSIUS),
                temp(0.0,   TemperatureUnit.CELSIUS)
            );
            assertNull(result);
        }

        @Test
        void testTemperatureUnsupportedOperation_Subtract() {
            QuantityDTO result = controller.performSubtraction(
                temp(100.0, TemperatureUnit.CELSIUS),
                temp(50.0,  TemperatureUnit.CELSIUS),
                temp(0.0,   TemperatureUnit.CELSIUS)
            );
            assertNull(result);
        }

        @Test
        void testTemperatureUnsupportedOperation_Divide() {
            double result = controller.performDivision(
                temp(100.0, TemperatureUnit.CELSIUS),
                temp(50.0,  TemperatureUnit.CELSIUS)
            );
            assertTrue(Double.isNaN(result));
        }

        @Test
        void testTemperatureCrossUnitAdditionAttempt() {
            QuantityDTO result = controller.performAddition(
                temp(100.0, TemperatureUnit.CELSIUS),
                temp(50.0,  TemperatureUnit.FAHRENHEIT),
                temp(0.0,   TemperatureUnit.CELSIUS)
            );
            assertNull(result);
        }
    }

    @Nested
    class TemperatureTypeSafetyTests {

        @Test
        void testTemperatureVsLengthIncompatibility() {
            // Cross-category comparison returns false (controller catches exception)
            assertFalse(controller.performComparison(temp(100.0,  TemperatureUnit.CELSIUS), length(100.0, LengthUnit.FEET)));
        }

        @Test
        void testTemperatureVsWeightIncompatibility() {
            assertFalse(controller.performComparison(temp(50.0,   TemperatureUnit.CELSIUS), weight(50.0,  WeightUnit.KILOGRAMS)));
        }

        @Test
        void testTemperatureVsVolumeIncompatibility() {
            assertFalse(controller.performComparison(temp(25.0,   TemperatureUnit.CELSIUS), volume(25.0,  VolumeUnit.LITERS)));
        }
    }

    // ─── Temperature Validation Tests ─────────────────────────────────────────

    @Nested
    class TemperatureValidationTests {

        @Test
        void testTemperatureNullInputComparison_ReturnsFalse() {
            // Controller validates null inputs and returns false for comparison
            assertFalse(controller.performComparison(null, temp(25.0, TemperatureUnit.CELSIUS)));
            assertFalse(controller.performComparison(temp(25.0, TemperatureUnit.CELSIUS), null));
        }

        @Test
        void testTemperatureNullInputConversion_ReturnsNull() {
            assertNull(controller.performConversion(null, temp(0.0, TemperatureUnit.FAHRENHEIT)));
            assertNull(controller.performConversion(temp(25.0, TemperatureUnit.CELSIUS), null));
        }

        @Test
        void testTemperatureDifferentValuesInequality() {
            assertFalse(controller.performComparison(temp(50.0,  TemperatureUnit.CELSIUS), temp(100.0, TemperatureUnit.CELSIUS)));
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
            assertEquals("CELSIUS",    TemperatureUnit.CELSIUS.getUnitName());
            assertEquals("FAHRENHEIT", TemperatureUnit.FAHRENHEIT.getUnitName());
            assertEquals("KELVIN",     TemperatureUnit.KELVIN.getUnitName());
        }

        @Test
        void testTemperatureUnit_NonLinearConversion() {
            QuantityDTO result = controller.performConversion(temp(0.0, TemperatureUnit.CELSIUS), temp(0.0, TemperatureUnit.FAHRENHEIT));
            assertEquals(32.0, result.getValue());
        }
    }

    @Nested
    class TemperatureIntegrationTests {

        @Test
        void testTemperatureIntegrationWithController_ConversionAndComparison() {
            QuantityDTO celsius    = temp(25.0, TemperatureUnit.CELSIUS);
            QuantityDTO fahrenheit = controller.performConversion(celsius, temp(0.0, TemperatureUnit.FAHRENHEIT));

            assertNotNull(fahrenheit);
            assertEquals(77.0, fahrenheit.getValue());
            assertEquals(TemperatureUnit.FAHRENHEIT, fahrenheit.getUnit());
        }

        @Test
        void testBackwardCompatibility_LengthEquality() {
            assertTrue(controller.performComparison(length(1.0,  LengthUnit.FEET), length(12.0, LengthUnit.INCHES)));
        }

        @Test
        void testBackwardCompatibility_WeightEquality() {
            assertTrue(controller.performComparison(weight(1.0,    WeightUnit.KILOGRAMS), weight(1000.0, WeightUnit.GRAMS)));
        }

        @Test
        void testBackwardCompatibility_VolumeEquality() {
            assertTrue(controller.performComparison(volume(1.0,    VolumeUnit.LITERS), volume(1000.0, VolumeUnit.MILLILITERS)));
        }
    }
}