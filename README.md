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

---

## UC3: Generic Quantity Class (DRY Principle)

## Description
Refactors Feet and Inches into a single `QuantityLength` class using a `LengthUnit` enum. Eliminates code duplication and supports cross-unit equality (e.g., 1 foot == 12 inches).

## Flow
1. Input value + unit type → validate.
2. Convert both values to base unit (feet).
3. Compare converted values → return `true` / `false`.

## Key Concepts
- `LengthUnit` enum with conversion factors
- Single class handles all unit types (DRY)
- Cross-unit equality via base unit normalization

---

## UC4: Extended Unit Support (Yards & Centimeters)

## Description
Extends UC3 by adding YARDS (1 yd = 3 ft) and CENTIMETERS (1 cm = 0.393701 in) to the `LengthUnit` enum. No changes to `QuantityLength` class required.

## Flow
1. Input value + unit (feet/inches/yards/cm) → validate.
2. Convert both to base unit (feet).
3. Compare → return `true` / `false`.

## Conversion Factors
| Unit | Factor (to feet) |
|------|-----------------|
| YARDS | 3.0 |
| CENTIMETERS | ~0.0328 |

---

