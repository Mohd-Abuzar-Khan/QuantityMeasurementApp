package com.quantity.app.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.quantity.app.dto.QuantityDTO;
import com.quantity.app.dto.QuantityMeasurementDTO;
import com.quantity.app.exception.QuantityMeasurementException;
import com.quantity.app.model.QuantityMeasurementEntity;
import com.quantity.app.repository.QuantityMeasurementRepository;
import com.quantity.app.unit.IMeasurable;

import java.util.List;
import java.util.function.DoubleBinaryOperator;

/**
 * Core business logic for all quantity measurement operations.
 *
 * All operations follow this pattern:
 *   1. Validate inputs (same measurement category, operation support)
 *   2. Resolve IMeasurable enum constants from DTOs
 *   3. Convert both operands to their base unit
 *   4. Perform the operation in base-unit space
 *   5. Convert result back to the target unit
 *   6. Persist and return the result DTO
 *
 * On error: persist an error record, then re-throw the exception.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QuantityMeasurementServiceImpl implements IQuantityMeasurementService {

    private static final double ROUND_FACTOR = 100.0;

    private final QuantityMeasurementRepository repository;

    // ── Arithmetic helper enum ──────────────────────────────────────────────

    private enum ArithmeticOp {
        ADD     ((a, b) -> a + b),
        SUBTRACT((a, b) -> a - b),
        DIVIDE  ((a, b) -> a / b);

        private final DoubleBinaryOperator fn;
        ArithmeticOp(DoubleBinaryOperator fn) { this.fn = fn; }
        double apply(double a, double b)       { return fn.applyAsDouble(a, b); }
    }

    // ── Private helpers ─────────────────────────────────────────────────────

    private static double round(double v) {
        return Math.round(v * ROUND_FACTOR) / ROUND_FACTOR;
    }

    private void validateSameCategory(QuantityDTO q1, QuantityDTO q2, String opName) {
        if (!q1.getMeasurementType().equals(q2.getMeasurementType())) {
            throw new QuantityMeasurementException(
                opName + " error: Cannot mix measurement categories — " +
                q1.getMeasurementType() + " vs " + q2.getMeasurementType());
        }
    }

    private void validateOperationSupport(IMeasurable unit, String operation) {
        try {
            unit.validateOperationSupport(operation);
        } catch (UnsupportedOperationException e) {
            throw new QuantityMeasurementException("Unsupported operation: " + e.getMessage());
        }
    }

    private double baseArithmetic(IMeasurable u1, double v1,
                                   IMeasurable u2, double v2,
                                   ArithmeticOp op) {
        double base1 = u1.convertToBaseUnit(v1);
        double base2 = u2.convertToBaseUnit(v2);
        if (op == ArithmeticOp.DIVIDE && base2 == 0.0) {
            throw new QuantityMeasurementException(
                "Division by zero: divisor has a base-unit value of zero");
        }
        return op.apply(base1, base2);
    }

    // ── Persistence helpers ─────────────────────────────────────────────────

    private QuantityMeasurementEntity buildEntity(String operation,
                                                   QuantityDTO q1, QuantityDTO q2,
                                                   Double resultValue, String resultUnit,
                                                   String resultMeasType, String resultString,
                                                   boolean isError, String errorMessage) {
        QuantityMeasurementEntity e = new QuantityMeasurementEntity();
        e.setOperation           (operation);
        e.setThisValue           (q1 != null ? String.valueOf(q1.getValue()) : null);
        e.setThisUnit            (q1 != null ? q1.getUnit()            : null);
        e.setThisMeasurementType (q1 != null ? q1.getMeasurementType() : null);
        e.setThatValue           (q2 != null ? String.valueOf(q2.getValue()) : null);
        e.setThatUnit            (q2 != null ? q2.getUnit()            : null);
        e.setThatMeasurementType (q2 != null ? q2.getMeasurementType() : null);
        e.setResultValue         (resultValue  != null ? String.valueOf(resultValue)  : null);
        e.setResultUnit          (resultUnit);
        e.setResultMeasurementType(resultMeasType);
        e.setResultString        (resultString);
        e.setError               (isError);
        e.setErrorMessage        (errorMessage);
        return e;
    }

    private QuantityMeasurementDTO save(QuantityMeasurementEntity entity) {
        return QuantityMeasurementDTO.fromEntity(repository.save(entity));
    }

    // ── Operations ──────────────────────────────────────────────────────────

    @Override
    public QuantityMeasurementDTO compare(QuantityDTO q1, QuantityDTO q2) {
        log.debug("compare({}, {})", q1, q2);
        try {
            validateSameCategory(q1, q2, "compare");
            IMeasurable m1 = q1.resolveIMeasurable();
            IMeasurable m2 = q2.resolveIMeasurable();
            double base1  = round(m1.convertToBaseUnit(q1.getValue()));
            double base2  = round(m2.convertToBaseUnit(q2.getValue()));
            boolean equal = Double.compare(base1, base2) == 0;
            return save(buildEntity("compare", q1, q2, null, null, null,
                                    String.valueOf(equal), false, null));
        } catch (QuantityMeasurementException ex) {
            save(buildEntity("compare", q1, q2, null, null, null, null, true, ex.getMessage()));
            throw ex;
        }
    }

    @Override
    public QuantityMeasurementDTO convert(QuantityDTO source, QuantityDTO targetUnit) {
        log.debug("convert({} -> {})", source, targetUnit);
        try {
            validateSameCategory(source, targetUnit, "convert");
            IMeasurable src    = source.resolveIMeasurable();
            IMeasurable target = targetUnit.resolveIMeasurable();
            double base        = src.convertToBaseUnit(source.getValue());
            double converted   = round(target.convertFromBaseUnit(base));
            return save(buildEntity("convert", source, targetUnit,
                                    converted, targetUnit.getUnit(),
                                    targetUnit.getMeasurementType(), null, false, null));
        } catch (QuantityMeasurementException ex) {
            save(buildEntity("convert", source, targetUnit, null, null, null, null, true, ex.getMessage()));
            throw ex;
        }
    }

    @Override
    public QuantityMeasurementDTO add(QuantityDTO q1, QuantityDTO q2, QuantityDTO targetUnit) {
        log.debug("add({}, {})", q1, q2);
        try {
            validateSameCategory(q1, q2, "add");
            IMeasurable m1     = q1.resolveIMeasurable();
            IMeasurable m2     = q2.resolveIMeasurable();
            validateOperationSupport(m1, "addition");
            double baseResult  = baseArithmetic(m1, q1.getValue(), m2, q2.getValue(), ArithmeticOp.ADD);
            IMeasurable target  = targetUnit.resolveIMeasurable();
            double converted   = round(target.convertFromBaseUnit(baseResult));
            return save(buildEntity("add", q1, q2, converted,
                                    targetUnit.getUnit(), targetUnit.getMeasurementType(),
                                    null, false, null));
        } catch (QuantityMeasurementException ex) {
            save(buildEntity("add", q1, q2, null, null, null, null, true, ex.getMessage()));
            throw ex;
        }
    }

    @Override
    public QuantityMeasurementDTO subtract(QuantityDTO q1, QuantityDTO q2, QuantityDTO targetUnit) {
        log.debug("subtract({}, {})", q1, q2);
        try {
            validateSameCategory(q1, q2, "subtract");
            IMeasurable m1     = q1.resolveIMeasurable();
            IMeasurable m2     = q2.resolveIMeasurable();
            validateOperationSupport(m1, "subtraction");
            double baseResult  = baseArithmetic(m1, q1.getValue(), m2, q2.getValue(), ArithmeticOp.SUBTRACT);
            IMeasurable target  = targetUnit.resolveIMeasurable();
            double converted   = round(target.convertFromBaseUnit(baseResult));
            return save(buildEntity("subtract", q1, q2, converted,
                                    targetUnit.getUnit(), targetUnit.getMeasurementType(),
                                    null, false, null));
        } catch (QuantityMeasurementException ex) {
            save(buildEntity("subtract", q1, q2, null, null, null, null, true, ex.getMessage()));
            throw ex;
        }
    }

    @Override
    public QuantityMeasurementDTO divide(QuantityDTO q1, QuantityDTO q2) {
        log.debug("divide({}, {})", q1, q2);
        try {
            validateSameCategory(q1, q2, "divide");
            IMeasurable m1 = q1.resolveIMeasurable();
            IMeasurable m2 = q2.resolveIMeasurable();
            validateOperationSupport(m1, "division");
            double ratio = baseArithmetic(m1, q1.getValue(), m2, q2.getValue(), ArithmeticOp.DIVIDE);
            return save(buildEntity("divide", q1, q2, ratio, "RATIO", null, null, false, null));
        } catch (QuantityMeasurementException ex) {
            save(buildEntity("divide", q1, q2, null, null, null, null, true, ex.getMessage()));
            throw ex;
        }
    }

    // ── History / analytics ─────────────────────────────────────────────────

    @Override
    public List<QuantityMeasurementDTO> getHistoryByOperation(String operation) {
        return QuantityMeasurementDTO.fromEntityList(
            repository.findByOperation(operation.toUpperCase()));
    }

    @Override
    public List<QuantityMeasurementDTO> getHistoryByMeasurementType(String measurementType) {
        return QuantityMeasurementDTO.fromEntityList(
            repository.findByThisMeasurementType(measurementType));
    }

    @Override
    public long getOperationCount(String operation) {
        return repository.countByOperationAndIsErrorFalse(operation.toLowerCase());
    }

    @Override
    public List<QuantityMeasurementDTO> getErrorHistory() {
        return QuantityMeasurementDTO.fromEntityList(repository.findByIsErrorTrue());
    }
    
}


