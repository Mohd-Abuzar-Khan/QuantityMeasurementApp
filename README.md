# 📏 Quantity Measurement Application

A Java-based application that demonstrates measurement equality comparison using object-oriented principles.

---
## UC1: Feet Measurement Equality

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

---

## UC2: Feet and Inches Measurement Equality

## Description
Extends UC1 to support equality checks for both Feet and Inches independently using separate classes. Reduces main method dependency via static helper methods.

## Flow
1. Static method validates two feet values → compares equality.
2. Static method validates two inches values → compares equality.
3. Returns `true` / `false` for each comparison.

## Key Concepts
- Separate `Inches` class mirroring `Feet` (same equality logic)
- Static methods for Feet and Inches equality checks
- Violates DRY principle (addressed in UC3)
