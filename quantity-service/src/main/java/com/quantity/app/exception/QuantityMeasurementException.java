package com.quantity.app.exception;

/**
 * Domain exception thrown by the measurement service for invalid operations.
 */
public class QuantityMeasurementException extends RuntimeException {
    public QuantityMeasurementException(String message) {
        super(message);
    }
}
