package com.app.model;

import java.util.Objects;

public class QuantityModel<U extends IMeasurable>{
	private static final double ROUND_FACTOR = 100.0;
	
	private final double value;
	private final U unit;
	

	
	public QuantityModel(double value, U unit) {
		if(unit == null) {
			throw new IllegalArgumentException("Unit cannot be null");
		}
		
		if(Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException("Invalid numeric value");
        }
		
		this.value = value;
		this.unit = unit;
	}
	
	// Getter and Setter for Attributes
	public double getValue() {
		return value;
	}
	
	public U getUnit() {
		return unit;
	}
	
	// Rounding values for Consistent Output
	private static double round(double value) {
		return Math.round(value * ROUND_FACTOR) / ROUND_FACTOR;
	}
	
	// Explicitly compares rounded base-unit values after conversion.
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if(obj == null || getClass() != obj.getClass()) return false;
        QuantityModel<?> other = (QuantityModel<?>) obj;
        double base1 = round(this.unit.convertToBaseUnit(this.value));
        double base2 = round(other.unit.convertToBaseUnit(other.value));
        return Double.compare(base1, base2) == 0;
	}
	
    @Override
    public int hashCode() {
        double baseValue = round(unit.convertToBaseUnit(value));
        return Objects.hash(unit.getClass().getName(), baseValue);
    }

    @Override
    public String toString() {
        return String.format("Quantity(%.2f, %s)", value, unit.getUnitName());
    }
	
	
}




































































//package com.app.model;
//
//import java.util.Objects;
//import java.util.function.DoubleBinaryOperator;
//
//import com.app.quantitymeasurement.Quantity;
//
//public class QuantityModel<U extends IMeasurable> {
//	private static final double ROUND_FACTOR = 100.0;
//
//    private final double value;
//    private final U unit;
//
//
//    private enum ArithmeticOperation {
//
//        ADD((a, b) -> a + b),
//        SUBTRACT((a, b) -> a - b),
//        DIVIDE((a, b) -> a / b);
//
//        private final DoubleBinaryOperator operator;
//
//        ArithmeticOperation(DoubleBinaryOperator operator) {
//            this.operator = operator;
//        }
//
//        // Applies this operation to two base-unit double values
//        double compute(double a, double b) {
//            return operator.applyAsDouble(a, b);
//        }
//    }
//
//    // constructor initializes the object and validates the input.
//    public QuantityModel(double value, U unit) {
//        if (unit == null) {
//            throw new IllegalArgumentException("Unit cannot be null");
//        }
//
//        if (Double.isNaN(value) || Double.isInfinite(value)) {
//            throw new IllegalArgumentException("Invalid numeric value");
//        }
//
//        this.value = value;
//        this.unit = unit;
//    }
//    
//    public double getValue() {
//        return value;
//    }
//
//    public U getUnit() {
//        return unit;
//    }
//    
//    // Rounding values for consistent output.
//    private static double round(double value) {
//        return Math.round(value * ROUND_FACTOR) / ROUND_FACTOR;
//    }
//
//    // Centralized validation for all arithmetic operands.
//    private void validateArithmeticOperands(Quantity<?> other, U targetUnit, boolean targetUnitRequired) {
//        if (other == null) {
//            throw new IllegalArgumentException("Operand quantity cannot be null");
//        }
//        if (this.unit.getClass() != other.unit.getClass()) {
//            throw new IllegalArgumentException(
//                    "Cannot perform arithmetic on different measurement categories: "
//                    + this.unit.getClass().getSimpleName() + " and "
//                    + other.unit.getClass().getSimpleName());
//        }
//        if (Double.isNaN(other.value) || Double.isInfinite(other.value)) {
//            throw new IllegalArgumentException("Operand has invalid numeric value");
//        }
//        if (targetUnitRequired && targetUnit == null) {
//            throw new IllegalArgumentException("Target unit cannot be null");
//        }
//    }
//
//    // Core arithmetic helper — the single source of truth for conversion and computation.
//    private double performBaseArithmetic(Quantity<U> other, ArithmeticOperation operation) {
//        double base1 = this.unit.convertToBaseUnit(this.value);
//        double base2 = other.unit.convertToBaseUnit(other.value);
//        if (operation == ArithmeticOperation.DIVIDE && base2 == 0.0) {
//            throw new ArithmeticException("Division by zero: divisor quantity has a base value of zero");
//        }
//        return operation.compute(base1, base2);
//    }
//
//    // Converts this quantity to the given target unit.
//    public QuantityModel<U> convertTo(U targetUnit) {
//
//        if (targetUnit == null) {
//            throw new IllegalArgumentException("Target unit cannot be null");
//        }
//
//        if (this.unit == targetUnit) {
//            return this;
//        }
//
//        double baseValue = unit.convertToBaseUnit(value);
//        double convertedValue = round(targetUnit.convertFromBaseUnit(baseValue));
//
//        return new QuantityModel<>(convertedValue, targetUnit);
//    }
//
//
//    // Adds other to this; result expressed in this quantity's unit (implicit target).
//    public QuantityModel<U> add(QuantityModel<U> other) {
//        validateArithmeticOperands(other,null,false);
//        double base1 = this.unit.convertToBaseUnit(this.value);
//        double base2 = other.unit.convertToBaseUnit(other.value);
//        double baseResult = performBaseArithmetic(base1, base2);
//        return new Quantity<>(round(this.unit.convertFromBaseUnit(baseResult)), this.unit);
//    }
//
//    // Adds other to this; result expressed in the explicitly specified target unit.
//    public Quantity<U> add(Quantity<U> other, U targetUnit) {
//        validateArithmeticOperands(other, targetUnit, true);
//        double baseResult = performBaseArithmetic(other, ArithmeticOperation.ADD);
//        return new Quantity<>(round(targetUnit.convertFromBaseUnit(baseResult)), targetUnit);
//    }
//
//    // Static addition: adds two quantities and returns result in the specified target unit.
//    public static <U extends IMeasurable> Quantity<U> add(Quantity<U> q1, Quantity<U> q2, U targetUnit) {
//        if (q1 == null || q2 == null) {
//            throw new IllegalArgumentException("Operand quantity cannot be null");
//        }
//        if (targetUnit == null) {
//            throw new IllegalArgumentException("Target unit cannot be null");
//        }
//        double base1 = q1.unit.convertToBaseUnit(q1.value);
//        double base2 = q2.unit.convertToBaseUnit(q2.value);
//        return new Quantity<>(round(targetUnit.convertFromBaseUnit(base1 + base2)), targetUnit);
//    }
//
//    // Subtracts other from this; result expressed in this quantity's unit (implicit target).
//    public QuantityModel<U> subtract(QuantityModel<U> other) {
//
//        validateArithmeticOperands(other);
//
//        double base1 = this.unit.convertToBaseUnit(this.value);
//        double base2 = other.unit.convertToBaseUnit(other.value);
//
//        double result = base1 - base2;
//
//        return new QuantityModel<>(
//                round(this.unit.convertFromBaseUnit(result)),
//                this.unit
//        );
//    }
//
//    // Subtracts other from this; result expressed in the explicitly specified target unit.
//    public Quantity<U> subtract(Quantity<U> other, U targetUnit) {
//        validateArithmeticOperands(other, targetUnit, true);
//        double baseResult = performBaseArithmetic(other, ArithmeticOperation.SUBTRACT);
//        return new Quantity<>(round(targetUnit.convertFromBaseUnit(baseResult)), targetUnit);
//    }
//
//    // Divides this by other, returning a dimensionless scalar ratio.
//    public double divide(QuantityModel<U> other) {
//
//        validateArithmeticOperands(other);
//
//        double base1 = this.unit.convertToBaseUnit(this.value);
//        double base2 = other.unit.convertToBaseUnit(other.value);
//
//        if (base2 == 0) {
//            throw new ArithmeticException("Division by zero");
//        }
//        return base1 / base2;
//    }
//
//    // Equality compares rounded base-unit values after conversion.
//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj) return true;
//        if (obj == null || getClass() != obj.getClass()) return false;
//        Quantity<?> other = (Quantity<?>) obj;
//        if (this.unit.getClass() != other.unit.getClass()) return false;
//        double base1 = round(this.unit.convertToBaseUnit(this.value));
//        double base2 = round(other.unit.convertToBaseUnit(other.value));
//        return Double.compare(base1, base2) == 0;
//    }
//
//    @Override
//    public int hashCode() {
//        double baseValue = round(unit.convertToBaseUnit(value));
//        return Objects.hash(unit.getClass().getName(), baseValue);
//    }
//
//    @Override
//    public String toString() {
//        return String.format("Quantity(%.2f, %s)", value, unit.getUnitName());
//    }
//}
