package com.app.exception;

public class DatabaseException extends QuantityMeasurementException {

    private final String operation;

    public DatabaseException(String message) {
        super(message);
        this.operation = "UNKNOWN";
    }

    public DatabaseException(String operation, String message) {
        super("[DB:" + operation + "] " + message);
        this.operation = operation;
    }

    public DatabaseException(String operation, String message, Throwable cause) {
        super("[DB:" + operation + "] " + message, cause);
        this.operation = operation;
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
        this.operation = "UNKNOWN";
    }

    /** Returns the database operation that caused the exception. */
    public String getOperation() {
        return operation;
    }
}
