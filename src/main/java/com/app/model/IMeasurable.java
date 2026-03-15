package com.app.model;

public interface IMeasurable {

    double convertToBaseUnit(double value);

    double convertFromBaseUnit(double baseValue);

    String getUnitName();

    // UC15: returns the measurement category e.g. "LengthUnit", "WeightUnit"
    String getMeasurementType();

    // UC15: returns a unit instance by its name within the same category
    IMeasurable getUnitByName(String name);

    default boolean supportsAddition() {
        return true;
    }

    default boolean supportsSubtraction() {
        return true;
    }

    default boolean supportsDivision() {
        return true;
    }

    default boolean supportsMultiplication() {
        return true;
    }

    default void validateOperationSupport(String operation) {
        switch (operation.toLowerCase()) {
            case "addition":
                if (!supportsAddition())
                    throw new UnsupportedOperationException(getUnitName() + " does not support addition");
                break;
            case "subtraction":
                if (!supportsSubtraction())
                    throw new UnsupportedOperationException(getUnitName() + " does not support subtraction");
                break;
            case "division":
                if (!supportsDivision())
                    throw new UnsupportedOperationException(getUnitName() + " does not support division");
                break;
            case "multiplication":
                if (!supportsMultiplication())
                    throw new UnsupportedOperationException(getUnitName() + " does not support multiplication");
                break;
            default:
                throw new IllegalArgumentException("Unknown operation: " + operation);
        }
    }
}
