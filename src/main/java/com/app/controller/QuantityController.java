package com.app.controller;

import com.app.dto.QuantityDTO;
import com.app.exception.QuantityException;
import com.app.service.IQuantityService;

public class QuantityController {

    private final IQuantityService service;

    public QuantityController(IQuantityService service) {
        if (service == null) {
            throw new IllegalArgumentException("Service cannot be null");
        }
        this.service = service;
    }

    public boolean performComparison(QuantityDTO q1, QuantityDTO q2) {
        System.out.println("\n── Comparison ──────────────────────────────");
        System.out.println("  Input 1 : " + formatDTO(q1));
        System.out.println("  Input 2 : " + formatDTO(q2));

        try {
            validateInputNotNull(q1, "First quantity");
            validateInputNotNull(q2, "Second quantity");

            boolean result = service.compare(q1, q2);

            System.out.println("  Result  : " + q1 + (result ? " == " : " != ") + q2);
            System.out.println("  Equal   : " + result);
            return result;

        } catch (QuantityException e) {
            displayError("Comparison failed", e);
            return false;
        }
    }

    public QuantityDTO performConversion(QuantityDTO source, QuantityDTO targetUnit) {
        System.out.println("\n── Conversion ──────────────────────────────");
        System.out.println("  Input      : " + formatDTO(source));
        System.out.println("  Target Unit: " + (targetUnit != null ? targetUnit.getUnit() : "null"));

        try {
            validateInputNotNull(source, "Source quantity");
            validateInputNotNull(targetUnit, "Target unit");

            QuantityDTO result = service.convert(source, targetUnit);

            displayResult("Converted", result);
            return result;

        } catch (QuantityException e) {
            displayError("Conversion failed", e);
            return null;
        }
    }

    public QuantityDTO performAddition(QuantityDTO q1, QuantityDTO q2, QuantityDTO targetUnit) {
        System.out.println("\n── Addition ────────────────────────────────");
        System.out.println("  Input 1    : " + formatDTO(q1));
        System.out.println("  Input 2    : " + formatDTO(q2));
        System.out.println("  Target Unit: " + (targetUnit != null ? targetUnit.getUnit() : "null"));

        try {
            validateInputNotNull(q1, "First operand");
            validateInputNotNull(q2, "Second operand");
            validateInputNotNull(targetUnit, "Target unit");

            QuantityDTO result = service.add(q1, q2, targetUnit);

            displayResult("Sum", result);
            return result;

        } catch (QuantityException e) {
            displayError("Addition failed", e);
            return null;
        }
    }

    public QuantityDTO performSubtraction(QuantityDTO q1, QuantityDTO q2, QuantityDTO targetUnit) {
        System.out.println("\n── Subtraction ─────────────────────────────");
        System.out.println("  Input 1    : " + formatDTO(q1));
        System.out.println("  Input 2    : " + formatDTO(q2));
        System.out.println("  Target Unit: " + (targetUnit != null ? targetUnit.getUnit() : "null"));

        try {
            validateInputNotNull(q1, "First operand");
            validateInputNotNull(q2, "Second operand");
            validateInputNotNull(targetUnit, "Target unit");

            QuantityDTO result = service.subtract(q1, q2, targetUnit);

            displayResult("Difference", result);
            return result;

        } catch (QuantityException e) {
            displayError("Subtraction failed", e);
            return null;
        }
    }

    public double performDivision(QuantityDTO q1, QuantityDTO q2) {
        System.out.println("\n── Division ────────────────────────────────");
        System.out.println("  Dividend : " + formatDTO(q1));
        System.out.println("  Divisor  : " + formatDTO(q2));

        try {
            validateInputNotNull(q1, "Dividend");
            validateInputNotNull(q2, "Divisor");

            double result = service.divide(q1, q2);

            System.out.printf("  Ratio    : %.4f%n", result);
            return result;

        } catch (QuantityException e) {
            displayError("Division failed", e);
            return Double.NaN;
        }
    }


    private void validateInputNotNull(QuantityDTO dto, String label) {
        if (dto == null) {
            throw QuantityException.nullValue(label);
        }
    }

    private String formatDTO(QuantityDTO dto) {
        if (dto == null) return "null";
        return String.format("%.4f %s", dto.getValue(), dto.getUnit());
    }

    private void displayResult(String label, QuantityDTO result) {
        if (result == null) {
            System.out.println("  " + label + " : null");
        } else {
            System.out.printf("  %-10s : %.4f %s%n", label, result.getValue(), result.getUnit());
        }
    }

    private void displayError(String context, QuantityException e) {
        System.out.println("  [ERROR] " + context + " → " + e.getMessage()
            + " (type: " + e.getErrorType() + ")");
    }
}