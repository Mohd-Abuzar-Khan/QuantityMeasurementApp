package com.app.controller;

import com.app.entity.QuantityDTO;
import com.app.entity.QuantityDTO.LengthUnit;
import com.app.entity.QuantityDTO.TemperatureUnit;
import com.app.exception.QuantityMeasurementException;
import com.app.service.IQuantityMeasurementService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link QuantityMeasurementController}.
 * The service layer is mocked so tests focus purely on controller behaviour.
 */
public class QuantityMeasurementControllerTest {

    private IQuantityMeasurementService mockService;
    private QuantityMeasurementController controller;

    @Before
    public void setUp() {
        mockService = Mockito.mock(IQuantityMeasurementService.class);
        controller  = new QuantityMeasurementController(mockService);
    }

    // ── Constructor ───────────────────────────────────────────────────────────

    @Test(expected = QuantityMeasurementException.class)
    public void testConstructor_NullService_Throws() {
        new QuantityMeasurementController(null);
    }

    // ── performComparison ─────────────────────────────────────────────────────

    @Test
    public void testPerformComparison_ServiceReturnsTrue_ControllerReturnsTrue() {
        when(mockService.compare(any(), any())).thenReturn(true);
        assertTrue(controller.performComparison(q(1.0, LengthUnit.FEET), q(12.0, LengthUnit.INCHES)));
    }

    @Test
    public void testPerformComparison_ServiceReturnsFalse_ControllerReturnsFalse() {
        when(mockService.compare(any(), any())).thenReturn(false);
        assertFalse(controller.performComparison(q(1.0, LengthUnit.FEET), q(2.0, LengthUnit.FEET)));
    }

    @Test
    public void testPerformComparison_ServiceThrows_ReturnsFalse() {
        when(mockService.compare(any(), any()))
            .thenThrow(new QuantityMeasurementException("Incompatible categories"));
        // Should not propagate – controller swallows and returns false
        assertFalse(controller.performComparison(q(1.0, LengthUnit.FEET), q(1.0, LengthUnit.FEET)));
    }

    // ── performConversion ─────────────────────────────────────────────────────

    @Test
    public void testPerformConversion_ServiceReturnsResult_ControllerReturnsIt() {
        QuantityDTO expected = new QuantityDTO(12.0, LengthUnit.INCHES);
        when(mockService.convert(any(), any())).thenReturn(expected);
        QuantityDTO result = controller.performConversion(q(1.0, LengthUnit.FEET), q(0.0, LengthUnit.INCHES));
        assertEquals(12.0, result.getValue(), 0.001);
    }

    @Test
    public void testPerformConversion_ServiceThrows_ReturnsNaN() {
        when(mockService.convert(any(), any()))
            .thenThrow(new QuantityMeasurementException("Conversion error"));
        QuantityDTO result = controller.performConversion(q(1.0, LengthUnit.FEET), q(0.0, LengthUnit.INCHES));
        assertTrue(Double.isNaN(result.getValue()));
    }

    // ── performAddition ───────────────────────────────────────────────────────

    @Test
    public void testPerformAddition_ServiceReturnsResult_ControllerReturnsIt() {
        QuantityDTO expected = new QuantityDTO(2.0, LengthUnit.FEET);
        when(mockService.add(any(), any(), any())).thenReturn(expected);
        QuantityDTO result = controller.performAddition(
            q(1.0, LengthUnit.FEET), q(12.0, LengthUnit.INCHES), q(0.0, LengthUnit.FEET));
        assertEquals(2.0, result.getValue(), 0.001);
    }

    @Test
    public void testPerformAddition_TemperatureThrows_ReturnsNaN() {
        when(mockService.add(any(), any(), any()))
            .thenThrow(new QuantityMeasurementException("Temperature does not support addition"));
        QuantityDTO result = controller.performAddition(
            q(100.0, TemperatureUnit.CELSIUS),
            q(50.0,  TemperatureUnit.CELSIUS),
            q(0.0,   TemperatureUnit.CELSIUS));
        assertTrue(Double.isNaN(result.getValue()));
    }

    // ── performSubtraction ────────────────────────────────────────────────────

    @Test
    public void testPerformSubtraction_ServiceReturnsResult_ControllerReturnsIt() {
        QuantityDTO expected = new QuantityDTO(9.5, LengthUnit.FEET);
        when(mockService.subtract(any(), any(), any())).thenReturn(expected);
        QuantityDTO result = controller.performSubtraction(
            q(10.0, LengthUnit.FEET), q(6.0, LengthUnit.INCHES), q(0.0, LengthUnit.FEET));
        assertEquals(9.5, result.getValue(), 0.001);
    }

    @Test
    public void testPerformSubtraction_ServiceThrows_ReturnsNaN() {
        when(mockService.subtract(any(), any(), any()))
            .thenThrow(new QuantityMeasurementException("Subtraction error"));
        QuantityDTO result = controller.performSubtraction(
            q(10.0, LengthUnit.FEET), q(6.0, LengthUnit.INCHES), q(0.0, LengthUnit.FEET));
        assertTrue(Double.isNaN(result.getValue()));
    }

    // ── performDivision ───────────────────────────────────────────────────────

    @Test
    public void testPerformDivision_ServiceReturnsRatio_ControllerReturnsIt() {
        when(mockService.divide(any(), any())).thenReturn(5.0);
        double ratio = controller.performDivision(q(10.0, LengthUnit.FEET), q(2.0, LengthUnit.FEET));
        assertEquals(5.0, ratio, 0.001);
    }

    @Test
    public void testPerformDivision_ServiceThrows_ReturnsNaN() {
        when(mockService.divide(any(), any()))
            .thenThrow(new QuantityMeasurementException("Division by zero"));
        double ratio = controller.performDivision(q(10.0, LengthUnit.FEET), q(0.0, LengthUnit.FEET));
        assertTrue(Double.isNaN(ratio));
    }

    // ── helper ────────────────────────────────────────────────────────────────
    private static QuantityDTO q(double value,
                                  com.app.entity.IMeasureableUnit unit) {
        return new QuantityDTO(value, unit);
    }
}
