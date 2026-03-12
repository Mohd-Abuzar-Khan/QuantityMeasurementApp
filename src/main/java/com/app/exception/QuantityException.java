package com.app.exception;

public class QuantityException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public enum ErrorType {
        NULL_VALUE,
        INVALID_VALUE,
        CROSS_CATEGORY_OPERATION,
        UNSUPPORTED_OPERATION,
        DIVISION_BY_ZERO,
        UNKNOWN_ERROR
    }

    private final ErrorType errorType;

    public QuantityException(String message) {
        super(message);
        this.errorType = ErrorType.UNKNOWN_ERROR;
    }

    public QuantityException(String message, ErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }

    public QuantityException(String message, Throwable cause) {
        super(message, cause);
        this.errorType = ErrorType.UNKNOWN_ERROR;
    }

    public QuantityException(String message, ErrorType errorType, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public static QuantityException nullValue(String paramName) {
        return new QuantityException( paramName + "cannot be null", ErrorType.NULL_VALUE );
    }

    public static QuantityException invalidValue(String detail) {
        return new QuantityException( "Invalid numeric value: " + detail, ErrorType.INVALID_VALUE);
    }

    public static QuantityException crossCategoryOperation(String category1, String category2) {
        return new QuantityException("Cannot perform operation on different measurement categories: " + category1 + " and " + category2, ErrorType.CROSS_CATEGORY_OPERATION);
    }

    public static QuantityException unsupportedOperation(String detail) {
        return new QuantityException( "Unsupported operation: " + detail, ErrorType.UNSUPPORTED_OPERATION );
    }

    public static QuantityException divisionByZero() {
        return new QuantityException( "Division by zero: divisor quantity has a base value of zero", ErrorType.DIVISION_BY_ZERO );
    }
}