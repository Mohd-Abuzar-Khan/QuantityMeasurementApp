package com.app.controller;

import com.app.dto.QuantityDTO;
import com.app.exception.QuantityMeasurementException;
import com.app.service.IQuantityMeasurementService;

/**
 * Controller layer — orchestrates user interaction and delegates to the service.
 * Thin controller: no business logic, only routing, input validation, and display.
 *
 * Renamed from QuantityController to QuantityMeasurementController per UC15.
 * Methods are named performXXX to reflect REST-readiness.
 */
public class QuantityMeasurementController {

    private final IQuantityMeasurementService service;

    // ── Constructor injection (Dependency Injection) ───────────────────────────
    public QuantityMeasurementController(IQuantityMeasurementService service) {
        if (service == null)
            throw new QuantityMeasurementException("Service cannot be null");
        this.service = service;
    }

    // ── API methods ───────────────────────────────────────────────────────────

    public boolean performComparison(QuantityDTO q1, QuantityDTO q2) {
        System.out.println("\n── Comparison ──────────────────────────────");
        System.out.println("  Input 1 : " + formatDTO(q1));
        System.out.println("  Input 2 : " + formatDTO(q2));

        try {
            boolean result = service.compare(q1, q2);
            System.out.println("  Equal   : " + result);
            return result;
        } catch (QuantityMeasurementException e) {
            displayError("Comparison failed", e);
            return false;
        }
    }

    public QuantityDTO performConversion(QuantityDTO source, QuantityDTO targetUnit) {
        System.out.println("\n── Conversion ──────────────────────────────");
        System.out.println("  Input      : " + formatDTO(source));
        System.out.println("  Target Unit: " + (targetUnit != null ? targetUnit.getUnit() : "null"));

        try {
            QuantityDTO result = service.convert(source, targetUnit);
            displayResult("Converted", result);
            return result;
        } catch (QuantityMeasurementException e) {
            displayError("Conversion failed", e);
            return new QuantityDTO(Double.NaN, source != null ? source.getUnit() : null);
        }
    }

    public QuantityDTO performAddition(QuantityDTO q1, QuantityDTO q2, QuantityDTO targetUnit) {
        System.out.println("\n── Addition ────────────────────────────────");
        System.out.println("  Input 1    : " + formatDTO(q1));
        System.out.println("  Input 2    : " + formatDTO(q2));
        System.out.println("  Target Unit: " + (targetUnit != null ? targetUnit.getUnit() : "null"));

        try {
            QuantityDTO result = service.add(q1, q2, targetUnit);
            displayResult("Sum", result);
            return result;
        } catch (QuantityMeasurementException e) {
            displayError("Addition failed", e);
            return new QuantityDTO(Double.NaN, q1 != null ? q1.getUnit() : null);
        }
    }

    public QuantityDTO performSubtraction(QuantityDTO q1, QuantityDTO q2, QuantityDTO targetUnit) {
        System.out.println("\n── Subtraction ─────────────────────────────");
        System.out.println("  Input 1    : " + formatDTO(q1));
        System.out.println("  Input 2    : " + formatDTO(q2));
        System.out.println("  Target Unit: " + (targetUnit != null ? targetUnit.getUnit() : "null"));

        try {
            QuantityDTO result = service.subtract(q1, q2, targetUnit);
            displayResult("Difference", result);
            return result;
        } catch (QuantityMeasurementException e) {
            displayError("Subtraction failed", e);
            return new QuantityDTO(Double.NaN, q1 != null ? q1.getUnit() : null);
        }
    }

    public double performDivision(QuantityDTO q1, QuantityDTO q2) {
        System.out.println("\n── Division ────────────────────────────────");
        System.out.println("  Dividend : " + formatDTO(q1));
        System.out.println("  Divisor  : " + formatDTO(q2));


            double result = service.divide(q1, q2);
            System.out.printf("  Ratio    : %.4f%n", result);
            return result;
        
    }

    // ── Display helpers ───────────────────────────────────────────────────────

    private String formatDTO(QuantityDTO dto) {
        if (dto == null) return "null";
        return String.format("%.4f %s", dto.getValue(), dto.getUnit());
    }

    private void displayResult(String label, QuantityDTO result) {
        if (result == null) {
            System.out.println("  " + label + " : null");
        } else {
            System.out.printf("  %-10s : %.4f %s%n",
                label, result.getValue(), result.getUnit());
        }
    }

    private void displayError(String context, QuantityMeasurementException e) {
        System.out.println("  [ERROR] " + context + " → " + e.getMessage());
    }
}
