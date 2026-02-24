package com.app.quantitymeasurement;

public class QuantityMeasurementApp {

    // Equality From UC1 to UC4

    public static <U extends IMeasurable> boolean demonstrateEquality(
            Quantity<U> q1, Quantity<U> q2) {
        return q1.equals(q2);
    }

    private static <U extends IMeasurable> void checkEquality(
            Quantity<U> q1, Quantity<U> q2) {
        System.out.println(q1 + " and " + q2 +
                " Equal: " + demonstrateEquality(q1, q2));
    }

    // Conversion From UC5 

    // Value + unit conversion (unit-to-unit style)
    public static <U extends IMeasurable> void demonstrateConversion(
            double value, U fromUnit, U toUnit) {
        Quantity<U> q = new Quantity<>(value, fromUnit);
        System.out.println(value + " " + fromUnit.getUnitName() +
                " is " + q.convertTo(toUnit).getValue() + " " + toUnit.getUnitName());
    }

    // Quantity-to-Quantity conversion
    public static <U extends IMeasurable> void demonstrateConversion(
            Quantity<U> q, U toUnit) {
        System.out.println(q + " converted to " +
                toUnit.getUnitName() + " is " + q.convertTo(toUnit));
    }

    // Addition From UC6
    // Addition (implicit target unit — result in first operand's unit)
    public static <U extends IMeasurable> void demonstrateAddition(
            double value1, U unit1, double value2, U unit2) {
        Quantity<U> q1 = new Quantity<>(value1, unit1);
        Quantity<U> q2 = new Quantity<>(value2, unit2);
        Quantity<U> result = q1.add(q2);
        System.out.println(q1 + " + " + q2 + " = " + result);
    }

    // Addition (explicit target unit)
    public static <U extends IMeasurable> void demonstrateAddition(
            double value1, U unit1, double value2, U unit2, U targetUnit) {
        Quantity<U> q1 = new Quantity<>(value1, unit1);
        Quantity<U> q2 = new Quantity<>(value2, unit2);
        Quantity<U> result = Quantity.add(q1, q2, targetUnit);
        System.out.println("add(" + q1 + ", " + q2 + ", " +
                targetUnit.getUnitName() + ") → " + result);
    }

    public static void main(String[] args) {

        // Length: Equality (UC1–UC4)
        // Same unit
        checkEquality(new Quantity<>(1.0, LengthUnit.FEET),    new Quantity<>(1.0, LengthUnit.FEET));
        checkEquality(new Quantity<>(5.0, LengthUnit.INCHES),  new Quantity<>(5.0, LengthUnit.INCHES));

        // Cross unit
        checkEquality(new Quantity<>(1.0,  LengthUnit.FEET),        new Quantity<>(12.0,     LengthUnit.INCHES));
        checkEquality(new Quantity<>(1.0,  LengthUnit.YARDS),       new Quantity<>(3.0,      LengthUnit.FEET));
        checkEquality(new Quantity<>(1.0,  LengthUnit.CENTIMETERS), new Quantity<>(0.393701, LengthUnit.INCHES));

        // Length: Conversion (UC5)
        // Unit-to-unit
        demonstrateConversion(1.0,  LengthUnit.FEET,        LengthUnit.INCHES);
        demonstrateConversion(3.0,  LengthUnit.YARDS,       LengthUnit.FEET);
        demonstrateConversion(36.0, LengthUnit.INCHES,      LengthUnit.YARDS);
        demonstrateConversion(1.0,  LengthUnit.CENTIMETERS, LengthUnit.INCHES);

        // Quantity-to-quantity
        demonstrateConversion(new Quantity<>(2.0, LengthUnit.YARDS), LengthUnit.INCHES);

        // Length: Addition (UC6) 
        demonstrateAddition(1.0,   LengthUnit.FEET,        12.0, LengthUnit.INCHES);
        demonstrateAddition(1.0,   LengthUnit.YARDS,       3.0,  LengthUnit.FEET);
        demonstrateAddition(30.48, LengthUnit.CENTIMETERS, 12.0, LengthUnit.INCHES);

        // Length: Addition with Target Unit (UC7)
        demonstrateAddition(1.0,  LengthUnit.FEET,   12.0, LengthUnit.INCHES, LengthUnit.FEET);
        demonstrateAddition(1.0,  LengthUnit.FEET,   12.0, LengthUnit.INCHES, LengthUnit.INCHES);
        demonstrateAddition(1.0,  LengthUnit.FEET,   12.0, LengthUnit.INCHES, LengthUnit.YARDS);
        demonstrateAddition(1.0,  LengthUnit.YARDS,  3.0,  LengthUnit.FEET,   LengthUnit.YARDS);
        demonstrateAddition(36.0, LengthUnit.INCHES, 1.0,  LengthUnit.YARDS,  LengthUnit.FEET);
        demonstrateAddition(2.54, LengthUnit.CENTIMETERS, 1.0, LengthUnit.INCHES, LengthUnit.CENTIMETERS);
        demonstrateAddition(5.0,  LengthUnit.FEET,  0.0,  LengthUnit.INCHES, LengthUnit.YARDS);
        demonstrateAddition(5.0,  LengthUnit.FEET,  -2.0, LengthUnit.FEET,   LengthUnit.INCHES);

        // Weight: Equality UC9

        // Same unit
        checkEquality(new Quantity<>(2.0,   WeightUnit.KILOGRAM), new Quantity<>(2.0,   WeightUnit.KILOGRAM));
        checkEquality(new Quantity<>(500.0, WeightUnit.GRAM),     new Quantity<>(500.0, WeightUnit.GRAM));
        checkEquality(new Quantity<>(3.0,   WeightUnit.POUND),    new Quantity<>(3.0,   WeightUnit.POUND));

        // Cross unit
        checkEquality(new Quantity<>(1.0,     WeightUnit.KILOGRAM), new Quantity<>(1000.0, WeightUnit.GRAM));
        checkEquality(new Quantity<>(453.592, WeightUnit.GRAM),     new Quantity<>(1.0,    WeightUnit.POUND));

        // Weight: Conversion (UC9)
        demonstrateConversion(1.0,    WeightUnit.KILOGRAM, WeightUnit.POUND);
        demonstrateConversion(1.0,    WeightUnit.KILOGRAM, WeightUnit.GRAM);
        demonstrateConversion(2000.0, WeightUnit.GRAM,     WeightUnit.KILOGRAM);
        demonstrateConversion(500.0,  WeightUnit.GRAM,     WeightUnit.POUND);
        demonstrateConversion(2.0,    WeightUnit.POUND,    WeightUnit.KILOGRAM);
        demonstrateConversion(1.0,    WeightUnit.POUND,    WeightUnit.GRAM);

        // Weight: Addition implicit (UC9)
        demonstrateAddition(1.0,   WeightUnit.KILOGRAM, 1000.0, WeightUnit.GRAM);
        demonstrateAddition(2.0,   WeightUnit.KILOGRAM, 3.0,    WeightUnit.KILOGRAM);
        demonstrateAddition(300.0, WeightUnit.GRAM,     200.0,  WeightUnit.GRAM);

        // Weight: Addition with Target Unit (UC9)
        demonstrateAddition(1.0,     WeightUnit.KILOGRAM, 1000.0,  WeightUnit.GRAM,     WeightUnit.GRAM);
        demonstrateAddition(1.0,     WeightUnit.POUND,    453.592, WeightUnit.GRAM,     WeightUnit.POUND);
        demonstrateAddition(2.0,     WeightUnit.KILOGRAM, 4.0,     WeightUnit.POUND,    WeightUnit.KILOGRAM);
    }
}