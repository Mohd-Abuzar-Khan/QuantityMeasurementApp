package com.app.service;

import java.util.function.DoubleBinaryOperator;

import com.app.dto.IMeasureableUnit;
import com.app.dto.QuantityDTO;
import com.app.model.IMeasurable;
import com.app.model.QuantityModel;
import com.app.exception.QuantityException;

public class QuantityServiceImpl implements IQuantityService {

    private static final double ROUND_FACTOR = 100.0;

    private enum ArithmeticOperation {
        ADD((a, b) -> a + b),
        SUBTRACT((a, b) -> a - b),
        DIVIDE((a, b) -> a / b);

        private final DoubleBinaryOperator operator;

        ArithmeticOperation(DoubleBinaryOperator operator) {
            this.operator = operator;
        }

        double compute(double a, double b) {
            return operator.applyAsDouble(a, b);
        }
    }

    @SuppressWarnings("unchecked")
    private <U extends IMeasurable> QuantityModel<U> toModel(QuantityDTO dto) {
        U unit = (U) dto.getUnit().getMeasurableUnit();
        return new QuantityModel<>(dto.getValue(), unit);
    }

    private QuantityDTO toDTO(QuantityModel<?> model) {
        String unitClassName = model.getUnit().getClass().getSimpleName();
        String unitName      = model.getUnit().getUnitName();

        IMeasureableUnit dtoUnit;

        switch (unitClassName) {
            case "LengthUnit":
                dtoUnit = QuantityDTO.LengthUnit.valueOf(unitName);
                break;
            case "WeightUnit":
                dtoUnit = QuantityDTO.WeightUnit.valueOf(unitName);
                break;
            case "VolumeUnit":
                dtoUnit = QuantityDTO.VolumeUnit.valueOf(unitName);
                break;
            case "TemperatureUnit":
                dtoUnit = QuantityDTO.TemperatureUnit.valueOf(unitName);
                break;
            default:
                throw new QuantityException("Unknown unit category: " + unitClassName);
        }

        return new QuantityDTO(model.getValue(), dtoUnit);
    }


    private static double round(double value) {
        return Math.round(value * ROUND_FACTOR) / ROUND_FACTOR;
    }

    private <U extends IMeasurable> void validateQuantity(QuantityModel<U> quantity, String label) {
        if (quantity == null) {
            throw QuantityException.nullValue(label);
        }
        if (Double.isNaN(quantity.getValue()) || Double.isInfinite(quantity.getValue())) {
            throw new QuantityException(label + " has an invalid numeric value");
        }
    }

    private void validateInputNotNull(QuantityDTO dto, String label) {
        if (dto == null) {
            throw QuantityException.nullValue(label);
        }
    }

    private <U extends IMeasurable> void validateSameCategory(QuantityModel<U> q1, QuantityModel<U> q2) {
        if (q1.getUnit().getClass() != q2.getUnit().getClass()) {
            throw new QuantityException(
                "Cannot perform arithmetic on different measurement categories: "
                + q1.getUnit().getClass().getSimpleName()
                + " and "
                + q2.getUnit().getClass().getSimpleName()
            );
        }
    }

    private <U extends IMeasurable> void validateOperands(QuantityModel<U> q1, QuantityModel<U> q2) {
        validateQuantity(q1, "First operand");
        validateQuantity(q2, "Second operand");
        validateSameCategory(q1, q2);
    }

    private void validateTargetUnit(QuantityDTO targetUnit) {
        if (targetUnit == null) {
            throw new QuantityException("Target unit cannot be null");
        }
    }

    private <U extends IMeasurable> double performBaseArithmetic(
            QuantityModel<U> q1, QuantityModel<U> q2, ArithmeticOperation operation) {

        double base1 = q1.getUnit().convertToBaseUnit(q1.getValue());
        double base2 = q2.getUnit().convertToBaseUnit(q2.getValue());

        if (operation == ArithmeticOperation.DIVIDE && base2 == 0.0) {
            throw new QuantityException("Division by zero: divisor quantity has a base value of zero");
        }

        return operation.compute(base1, base2);
    }

    @Override
    public boolean compare(QuantityDTO q1, QuantityDTO q2) {
        validateInputNotNull(q1, "First quantity");
        validateInputNotNull(q2, "Second quantity");

        QuantityModel<?> m1 = toModel(q1);
        QuantityModel<?> m2 = toModel(q2);

        // validateOperands needs same generic type — delegate to model equals logic
        validateSameCategory((QuantityModel) m1, (QuantityModel) m2);

        double base1 = round(m1.getUnit().convertToBaseUnit(m1.getValue()));
        double base2 = round(m2.getUnit().convertToBaseUnit(m2.getValue()));

        return Double.compare(base1, base2) == 0;
    }

    @Override
    public QuantityDTO convert(QuantityDTO source, QuantityDTO target) {
        validateInputNotNull(source, "Source quantity");
        validateTargetUnit(target);

        QuantityModel<?> sourceModel = toModel(source);
        IMeasurable targetUnit       = target.getUnit().getMeasurableUnit();

        if (sourceModel.getUnit() == targetUnit) {
            return source;
        }

        double baseValue      = sourceModel.getUnit().convertToBaseUnit(sourceModel.getValue());
        double convertedValue = round(targetUnit.convertFromBaseUnit(baseValue));

        return new QuantityDTO(convertedValue, target.getUnit());
    }

    @Override
    public QuantityDTO add(QuantityDTO q1, QuantityDTO q2, QuantityDTO targetUnit) {
        validateInputNotNull(q1, "First operand");
        validateInputNotNull(q2, "Second operand");
        validateTargetUnit(targetUnit);

        QuantityModel<?> m1 = toModel(q1);
        QuantityModel<?> m2 = toModel(q2);
        validateOperands((QuantityModel) m1, (QuantityModel) m2);

        double baseResult = performBaseArithmetic((QuantityModel) m1, (QuantityModel) m2, ArithmeticOperation.ADD);

        IMeasurable target    = targetUnit.getUnit().getMeasurableUnit();
        double convertedValue = round(target.convertFromBaseUnit(baseResult));

        return new QuantityDTO(convertedValue, targetUnit.getUnit());
    }

    @Override
    public QuantityDTO subtract(QuantityDTO q1, QuantityDTO q2, QuantityDTO targetUnit) {
        validateInputNotNull(q1, "First operand");
        validateInputNotNull(q2, "Second operand");
        validateTargetUnit(targetUnit);

        QuantityModel<?> m1 = toModel(q1);
        QuantityModel<?> m2 = toModel(q2);
        validateOperands((QuantityModel) m1, (QuantityModel) m2);

        double baseResult = performBaseArithmetic((QuantityModel) m1, (QuantityModel) m2, ArithmeticOperation.SUBTRACT);

        IMeasurable target    = targetUnit.getUnit().getMeasurableUnit();
        double convertedValue = round(target.convertFromBaseUnit(baseResult));

        return new QuantityDTO(convertedValue, targetUnit.getUnit());
    }

    @Override
    public double divide(QuantityDTO q1, QuantityDTO q2) {
        validateInputNotNull(q1, "Dividend");
        validateInputNotNull(q2, "Divisor");

        QuantityModel<?> m1 = toModel(q1);
        QuantityModel<?> m2 = toModel(q2);
        validateOperands((QuantityModel) m1, (QuantityModel) m2);

        return performBaseArithmetic((QuantityModel) m1, (QuantityModel) m2, ArithmeticOperation.DIVIDE);
    }


}