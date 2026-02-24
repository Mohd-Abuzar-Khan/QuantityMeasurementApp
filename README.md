# 📏 Quantity Measurement Application

A Java-based application that demonstrates measurement equality comparison using object-oriented principles.

---
# UC1: Feet Measurement Equality

## Description
Checks equality of two numerical values in feet, handling null, type mismatch, and floating-point precision.

## Flow
1. Input two numerical values in feet.
2. Validate inputs are numeric.
3. Compare for equality → return `true` or `false`.

## Key Concepts
- Override `equals()` using `Double.compare()` instead of `==`
- `private final` field for immutability
- Null & type safety to prevent exceptions


