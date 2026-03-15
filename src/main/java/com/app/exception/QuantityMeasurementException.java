package com.app.exception;

public class QuantityMeasurementException extends RuntimeException {

    public QuantityMeasurementException(String message) {
        super(message);
    }

    public QuantityMeasurementException(String message, Throwable cause) {
        super(message, cause);
    }

    // Main method for testing purposes
    public static void main(String[] args) {
        try {
            throw new QuantityMeasurementException("This is a test exception for quantity measurement.");
        } catch (QuantityMeasurementException ex) {
            System.out.println("Caught QuantityMeasurementException: " + ex.getMessage());
        }
    }
}
