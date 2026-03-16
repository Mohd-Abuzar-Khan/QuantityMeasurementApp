package com.app.service;

import com.app.entity.IMeasureableUnit;
import com.app.entity.QuantityDTO;
import com.app.entity.QuantityMeasurementEntity;
import com.app.entity.QuantityModel;
import com.app.exception.QuantityMeasurementException;
import com.app.repository.IQuantityMeasurementRepository;
import com.app.unit.IMeasurable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.DoubleBinaryOperator;

public class QuantityMeasurementServiceImpl implements IQuantityMeasurementService {

    private static final Logger log = LoggerFactory.getLogger(QuantityMeasurementServiceImpl.class);

    private static final double ROUND_FACTOR = 100.0;

    private final IQuantityMeasurementRepository repository;

    // ── Constructor injection ─────────────────────────────────────────────────
    public QuantityMeasurementServiceImpl(IQuantityMeasurementRepository repository) {
        if (repository == null)
            throw new QuantityMeasurementException("Repository cannot be null");
        this.repository = repository;
        log.info("QuantityMeasurementServiceImpl initialised with repository: {}",
            repository.getClass().getSimpleName());
    }

    // ── Internal arithmetic helper ────────────────────────────────────────────
    private enum ArithmeticOperation {
        ADD     ((a, b) -> a + b),
        SUBTRACT((a, b) -> a - b),
        DIVIDE  ((a, b) -> a / b);

        private final DoubleBinaryOperator operator;
        ArithmeticOperation(DoubleBinaryOperator op) { this.operator = op; }
        double compute(double a, double b) { return operator.applyAsDouble(a, b); }
    }

    // ── DTO <-> Model helpers ─────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    private <U extends IMeasurable> QuantityModel<U> toModel(QuantityDTO dto) {
        U unit = (U) dto.getUnit().getMeasurableUnit();
        return new QuantityModel<>(dto.getValue(), unit);
    }

    private static double round(double value) {
        return Math.round(value * ROUND_FACTOR) / ROUND_FACTOR;
    }

    // ── Validation helpers ────────────────────────────────────────────────────
    private void validateNotNull(QuantityDTO dto, String label) {
        if (dto == null)
            throw new QuantityMeasurementException(label + " cannot be null");
    }

    @SuppressWarnings("rawtypes")
    private void validateSameCategory(QuantityModel q1, QuantityModel q2) {
        String t1 = q1.getUnit().getMeasurementType();
        String t2 = q2.getUnit().getMeasurementType();
        if (!t1.equals(t2))
            throw new QuantityMeasurementException(
                "Cannot operate on different measurement categories: " + t1 + " and " + t2);
    }

    @SuppressWarnings("rawtypes")
    private void validateOperationSupport(QuantityModel q, String operation) {
        try {
            q.getUnit().validateOperationSupport(operation);
        } catch (UnsupportedOperationException e) {
            throw new QuantityMeasurementException("Unsupported operation: " + e.getMessage());
        }
    }

    @SuppressWarnings("rawtypes")
    private double performBaseArithmetic(QuantityModel q1, QuantityModel q2,
                                         ArithmeticOperation operation) {
        double base1 = q1.getUnit().convertToBaseUnit(q1.getValue());
        double base2 = q2.getUnit().convertToBaseUnit(q2.getValue());
        if (operation == ArithmeticOperation.DIVIDE && base2 == 0.0)
            throw new QuantityMeasurementException(
                "Division by zero: divisor quantity has a base value of zero");
        return operation.compute(base1, base2);
    }

    // ── IQuantityMeasurementService ───────────────────────────────────────────

    @Override
    @SuppressWarnings("rawtypes")
    public boolean compare(QuantityDTO q1, QuantityDTO q2) {
        validateNotNull(q1, "First quantity");
        validateNotNull(q2, "Second quantity");
        log.debug("compare({}, {})", q1, q2);

        try {
            QuantityModel m1 = toModel(q1);
            QuantityModel m2 = toModel(q2);
            validateSameCategory(m1, m2);

            double base1  = round(m1.getUnit().convertToBaseUnit(m1.getValue()));
            double base2  = round(m2.getUnit().convertToBaseUnit(m2.getValue()));
            boolean result = Double.compare(base1, base2) == 0;
            log.debug("compare result: {}", result);

            repository.save(new QuantityMeasurementEntity(
                "COMPARE",
                q1.getValue(), q1.getUnit().getUnitName(),
                q2.getValue(), q2.getUnit().getUnitName(),
                result ? 1.0 : 0.0, "BOOLEAN"));

            return result;

        } catch (QuantityMeasurementException e) {
            repository.save(new QuantityMeasurementEntity(
                "COMPARE",
                String.valueOf(q1.getValue()), q1.getUnit().getUnitName(),
                String.valueOf(q2.getValue()), q2.getUnit().getUnitName(),
                e.getMessage()));
            throw e;
        } catch (Exception e) {
            throw new QuantityMeasurementException("Comparison failed: " + e.getMessage(), e);
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    public QuantityDTO convert(QuantityDTO source, QuantityDTO targetUnit) {
        validateNotNull(source,     "Source quantity");
        validateNotNull(targetUnit, "Target unit");
        log.debug("convert({} -> {})", source, targetUnit.getUnit().getUnitName());

        try {
            QuantityModel sourceModel = toModel(source);
            IMeasurable   target      = targetUnit.getUnit().getMeasurableUnit();

            if (sourceModel.getUnit() == target) {
                repository.save(new QuantityMeasurementEntity(
                    "CONVERT",
                    source.getValue(), source.getUnit().getUnitName(),
                    source.getValue(), targetUnit.getUnit().getUnitName()));
                return source;
            }

            double baseValue      = sourceModel.getUnit().convertToBaseUnit(sourceModel.getValue());
            double convertedValue = round(target.convertFromBaseUnit(baseValue));
            QuantityDTO result    = new QuantityDTO(convertedValue, targetUnit.getUnit());

            log.debug("convert result: {}", result);
            repository.save(new QuantityMeasurementEntity(
                "CONVERT",
                source.getValue(), source.getUnit().getUnitName(),
                convertedValue,    targetUnit.getUnit().getUnitName()));

            return result;

        } catch (QuantityMeasurementException e) {
            repository.save(new QuantityMeasurementEntity(
                "CONVERT",
                String.valueOf(source.getValue()), source.getUnit().getUnitName(),
                null, targetUnit.getUnit().getUnitName(),
                e.getMessage()));
            throw e;
        } catch (Exception e) {
            throw new QuantityMeasurementException("Conversion failed: " + e.getMessage(), e);
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    public QuantityDTO add(QuantityDTO q1, QuantityDTO q2, QuantityDTO targetUnit) {
        validateNotNull(q1,         "First operand");
        validateNotNull(q2,         "Second operand");
        validateNotNull(targetUnit, "Target unit");
        log.debug("add({}, {})", q1, q2);

        try {
            QuantityModel m1 = toModel(q1);
            QuantityModel m2 = toModel(q2);
            validateSameCategory(m1, m2);
            validateOperationSupport(m1, "addition");

            double      baseResult    = performBaseArithmetic(m1, m2, ArithmeticOperation.ADD);
            IMeasurable target        = targetUnit.getUnit().getMeasurableUnit();
            double      converted     = round(target.convertFromBaseUnit(baseResult));
            QuantityDTO result        = new QuantityDTO(converted, targetUnit.getUnit());

            log.debug("add result: {}", result);
            repository.save(new QuantityMeasurementEntity(
                "ADD",
                q1.getValue(), q1.getUnit().getUnitName(),
                q2.getValue(), q2.getUnit().getUnitName(),
                converted,     targetUnit.getUnit().getUnitName()));

            return result;

        } catch (QuantityMeasurementException e) {
            repository.save(new QuantityMeasurementEntity(
                "ADD",
                String.valueOf(q1.getValue()), q1.getUnit().getUnitName(),
                String.valueOf(q2.getValue()), q2.getUnit().getUnitName(),
                e.getMessage()));
            throw e;
        } catch (Exception e) {
            throw new QuantityMeasurementException("Addition failed: " + e.getMessage(), e);
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    public QuantityDTO subtract(QuantityDTO q1, QuantityDTO q2, QuantityDTO targetUnit) {
        validateNotNull(q1,         "First operand");
        validateNotNull(q2,         "Second operand");
        validateNotNull(targetUnit, "Target unit");
        log.debug("subtract({}, {})", q1, q2);

        try {
            QuantityModel m1 = toModel(q1);
            QuantityModel m2 = toModel(q2);
            validateSameCategory(m1, m2);
            validateOperationSupport(m1, "subtraction");

            double      baseResult = performBaseArithmetic(m1, m2, ArithmeticOperation.SUBTRACT);
            IMeasurable target     = targetUnit.getUnit().getMeasurableUnit();
            double      converted  = round(target.convertFromBaseUnit(baseResult));
            QuantityDTO result     = new QuantityDTO(converted, targetUnit.getUnit());

            log.debug("subtract result: {}", result);
            repository.save(new QuantityMeasurementEntity(
                "SUBTRACT",
                q1.getValue(), q1.getUnit().getUnitName(),
                q2.getValue(), q2.getUnit().getUnitName(),
                converted,     targetUnit.getUnit().getUnitName()));

            return result;

        } catch (QuantityMeasurementException e) {
            repository.save(new QuantityMeasurementEntity(
                "SUBTRACT",
                String.valueOf(q1.getValue()), q1.getUnit().getUnitName(),
                String.valueOf(q2.getValue()), q2.getUnit().getUnitName(),
                e.getMessage()));
            throw e;
        } catch (Exception e) {
            throw new QuantityMeasurementException("Subtraction failed: " + e.getMessage(), e);
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    public double divide(QuantityDTO q1, QuantityDTO q2) {
        validateNotNull(q1, "Dividend");
        validateNotNull(q2, "Divisor");
        log.debug("divide({}, {})", q1, q2);

        try {
            QuantityModel m1 = toModel(q1);
            QuantityModel m2 = toModel(q2);
            validateSameCategory(m1, m2);
            validateOperationSupport(m1, "division");

            double ratio = performBaseArithmetic(m1, m2, ArithmeticOperation.DIVIDE);
            log.debug("divide result: {}", ratio);

            repository.save(new QuantityMeasurementEntity(
                "DIVIDE",
                q1.getValue(), q1.getUnit().getUnitName(),
                q2.getValue(), q2.getUnit().getUnitName(),
                ratio,         "RATIO"));

            return ratio;

        } catch (QuantityMeasurementException e) {
            repository.save(new QuantityMeasurementEntity(
                "DIVIDE",
                String.valueOf(q1.getValue()), q1.getUnit().getUnitName(),
                String.valueOf(q2.getValue()), q2.getUnit().getUnitName(),
                e.getMessage()));
            throw e;
        } catch (Exception e) {
            throw new QuantityMeasurementException("Division failed: " + e.getMessage(), e);
        }
    }
}
