package com.app.controller;

import com.app.entity.QuantityDTO;
import com.app.exception.QuantityMeasurementException;
import com.app.service.IQuantityMeasurementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuantityMeasurementController {

    private static final Logger log = LoggerFactory.getLogger(QuantityMeasurementController.class);

    private final IQuantityMeasurementService service;

    public QuantityMeasurementController(IQuantityMeasurementService service) {
        if (service == null)
            throw new QuantityMeasurementException("Service cannot be null");
        this.service = service;
        log.info("QuantityMeasurementController initialised");
    }


    public boolean performComparison(QuantityDTO q1, QuantityDTO q2) {
        log.info("── Comparison ──────────────────────────────");
        log.info("  Input 1 : {}", fmt(q1));
        log.info("  Input 2 : {}", fmt(q2));
        try {
            boolean result = service.compare(q1, q2);
            log.info("  Equal   : {}", result);
            return result;
        } catch (QuantityMeasurementException e) {
            logError("Comparison failed", e);
            return false;
        }
    }

    public QuantityDTO performConversion(QuantityDTO source, QuantityDTO targetUnit) {
        log.info("── Conversion ──────────────────────────────");
        log.info("  Input      : {}", fmt(source));
        log.info("  Target Unit: {}", targetUnit != null ? targetUnit.getUnit() : "null");
        try {
            QuantityDTO result = service.convert(source, targetUnit);
            logResult("Converted", result);
            return result;
        } catch (QuantityMeasurementException e) {
            logError("Conversion failed", e);
            return new QuantityDTO(Double.NaN, source != null ? source.getUnit() : null);
        }
    }

    public QuantityDTO performAddition(QuantityDTO q1, QuantityDTO q2, QuantityDTO targetUnit) {
        log.info("── Addition ────────────────────────────────");
        log.info("  Input 1    : {}", fmt(q1));
        log.info("  Input 2    : {}", fmt(q2));
        log.info("  Target Unit: {}", targetUnit != null ? targetUnit.getUnit() : "null");
        try {
            QuantityDTO result = service.add(q1, q2, targetUnit);
            logResult("Sum", result);
            return result;
        } catch (QuantityMeasurementException e) {
            logError("Addition failed", e);
            return new QuantityDTO(Double.NaN, q1 != null ? q1.getUnit() : null);
        }
    }

    public QuantityDTO performSubtraction(QuantityDTO q1, QuantityDTO q2, QuantityDTO targetUnit) {
        log.info("── Subtraction ─────────────────────────────");
        log.info("  Input 1    : {}", fmt(q1));
        log.info("  Input 2    : {}", fmt(q2));
        log.info("  Target Unit: {}", targetUnit != null ? targetUnit.getUnit() : "null");
        try {
            QuantityDTO result = service.subtract(q1, q2, targetUnit);
            logResult("Difference", result);
            return result;
        } catch (QuantityMeasurementException e) {
            logError("Subtraction failed", e);
            return new QuantityDTO(Double.NaN, q1 != null ? q1.getUnit() : null);
        }
    }

    public double performDivision(QuantityDTO q1, QuantityDTO q2) {
        log.info("── Division ────────────────────────────────");
        log.info("  Dividend : {}", fmt(q1));
        log.info("  Divisor  : {}", fmt(q2));
        try {
            double result = service.divide(q1, q2);
            log.info("  Ratio    : {}", String.format("%.4f", result));
            return result;
        } catch (QuantityMeasurementException e) {
            logError("Division failed", e);
            return Double.NaN;
        }
    }


    private String fmt(QuantityDTO dto) {
        if (dto == null) return "null";
        return String.format("%.4f %s", dto.getValue(), dto.getUnit());
    }

    private void logResult(String label, QuantityDTO result) {
        if (result == null) {
            log.info("  {} : null", label);
        } else {
            log.info("  {}: {} {}", String.format("%-10s", label),
                String.format("%.4f", result.getValue()), result.getUnit());
        }
    }

    private void logError(String context, QuantityMeasurementException e) {
        log.error("  [ERROR] {} → {}", context, e.getMessage());
    }
}
