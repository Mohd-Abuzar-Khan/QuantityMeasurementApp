package com.app.model;

public enum OperationType {
    ADD,
    SUBTRACT,
    MULTIPLY,
    DIVIDE,
    COMPARE,
    CONVERT;

    public static OperationType of(String name) {
        return OperationType.valueOf(name.toUpperCase());
    }
}