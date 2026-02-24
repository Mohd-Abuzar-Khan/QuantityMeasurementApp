package com.app.quantitymeasurement;


public class QuantityMeasurementApp {

    // Delegates to equals() and returns the boolean result
    public static <U extends IMeasurable> boolean demonstrateEquality(
            Quantity<U> q1, Quantity<U> q2) {
        return q1.equals(q2);
    }

    // Prints equality result for two quantities of the same unit type
    private static <U extends IMeasurable> void checkEquality(
            Quantity<U> q1, Quantity<U> q2) {
        System.out.println(q1 + " and " + q2 +
                " Equal: " + demonstrateEquality(q1, q2));
    }

    // Unit-to-unit conversion: creates a Quantity from value + fromUnit and converts to toUnit
    public static <U extends IMeasurable> void demonstrateConversion(double value, U fromUnit, U toUnit) {
        Quantity<U> q = new Quantity<>(value, fromUnit);
        System.out.println(value + " " + fromUnit.getUnitName() +
                " is " + q.convertTo(toUnit).getValue() + " " + toUnit.getUnitName());
    }

    // Quantity-to-quantity conversion: converts an existing Quantity to the target unit
    public static <U extends IMeasurable> void demonstrateConversion( Quantity<U> q, U toUnit) {
        System.out.println(q + " converted to " +
                toUnit.getUnitName() + " is " + q.convertTo(toUnit));
    }

    // Implicit addition: result unit defaults to the first operand's unit
    public static <U extends IMeasurable> void demonstrateAddition( double value1, U unit1, double value2, U unit2) {
        Quantity<U> q1 = new Quantity<>(value1, unit1);
        Quantity<U> q2 = new Quantity<>(value2, unit2);
        System.out.println(q1 + " + " + q2 + " = " + q1.add(q2));
    }

    // Explicit addition: result is expressed in the specified target unit
    public static <U extends IMeasurable> void demonstrateAddition( double value1, U unit1, double value2, U unit2, U targetUnit) {
        Quantity<U> q1 = new Quantity<>(value1, unit1);
        Quantity<U> q2 = new Quantity<>(value2, unit2);
        System.out.println("add(" + q1 + ", " + q2 + ", " +
                targetUnit.getUnitName() + ") → " + Quantity.add(q1, q2, targetUnit));
    }

    // Implicit subtraction: result unit defaults to the first operand's unit.
    public static <U extends IMeasurable> void demonstrateSubtraction( double value1, U unit1, double value2, U unit2) {
        Quantity<U> q1 = new Quantity<>(value1, unit1);
        Quantity<U> q2 = new Quantity<>(value2, unit2);
        System.out.println(q1 + " - " + q2 + " = " + q1.subtract(q2));
    }

    // Explicit subtraction: result is expressed in the specified target unit
    public static <U extends IMeasurable> void demonstrateSubtraction( double value1, U unit1, double value2, U unit2, U targetUnit) {
        Quantity<U> q1 = new Quantity<>(value1, unit1);
        Quantity<U> q2 = new Quantity<>(value2, unit2);
        System.out.println("subtract(" + q1 + ", " + q2 + ", " +
                targetUnit.getUnitName() + ") → " + q1.subtract(q2, targetUnit));
    }

    // Division returns a dimensionless scalar ratio — no unit on the result.
    public static <U extends IMeasurable> void demonstrateDivision( double value1, U unit1, double value2, U unit2) {
        Quantity<U> q1 = new Quantity<>(value1, unit1);
        Quantity<U> q2 = new Quantity<>(value2, unit2);
        System.out.println(q1 + " ÷ " + q2 + " = " + q1.divide(q2));
    }

    public static void main(String[] args) {

        // UC1–UC4: Length equality — same unit and cross-unit comparisons
        checkEquality(new Quantity<>(1.0, LengthUnit.FEET),        new Quantity<>(1.0,      LengthUnit.FEET));
        checkEquality(new Quantity<>(5.0, LengthUnit.INCHES),      new Quantity<>(5.0,      LengthUnit.INCHES));
        checkEquality(new Quantity<>(1.0, LengthUnit.FEET),        new Quantity<>(12.0,     LengthUnit.INCHES));
        checkEquality(new Quantity<>(1.0, LengthUnit.YARDS),       new Quantity<>(3.0,      LengthUnit.FEET));
        checkEquality(new Quantity<>(1.0, LengthUnit.CENTIMETERS), new Quantity<>(0.393701, LengthUnit.INCHES));

        // UC5: Length unit-to-unit conversion
        demonstrateConversion(1.0,  LengthUnit.FEET,        LengthUnit.INCHES);
        demonstrateConversion(3.0,  LengthUnit.YARDS,       LengthUnit.FEET);
        demonstrateConversion(36.0, LengthUnit.INCHES,      LengthUnit.YARDS);
        demonstrateConversion(1.0,  LengthUnit.CENTIMETERS, LengthUnit.INCHES);

        // UC5: Length Quantity-to-Quantity conversion
        demonstrateConversion(new Quantity<>(2.0, LengthUnit.YARDS), LengthUnit.INCHES);

        // UC6: Length addition with implicit target unit
        demonstrateAddition(1.0,   LengthUnit.FEET,        12.0, LengthUnit.INCHES);
        demonstrateAddition(1.0,   LengthUnit.YARDS,       3.0,  LengthUnit.FEET);
        demonstrateAddition(30.48, LengthUnit.CENTIMETERS, 12.0, LengthUnit.INCHES);

        // UC7: Length addition with explicit target unit
        demonstrateAddition(1.0,  LengthUnit.FEET,        12.0, LengthUnit.INCHES, LengthUnit.FEET);
        demonstrateAddition(1.0,  LengthUnit.FEET,        12.0, LengthUnit.INCHES, LengthUnit.INCHES);
        demonstrateAddition(1.0,  LengthUnit.FEET,        12.0, LengthUnit.INCHES, LengthUnit.YARDS);
        demonstrateAddition(1.0,  LengthUnit.YARDS,       3.0,  LengthUnit.FEET,   LengthUnit.YARDS);
        demonstrateAddition(36.0, LengthUnit.INCHES,      1.0,  LengthUnit.YARDS,  LengthUnit.FEET);
        demonstrateAddition(2.54, LengthUnit.CENTIMETERS, 1.0,  LengthUnit.INCHES, LengthUnit.CENTIMETERS);
        demonstrateAddition(5.0,  LengthUnit.FEET,        0.0,  LengthUnit.INCHES, LengthUnit.YARDS);
        demonstrateAddition(5.0,  LengthUnit.FEET,       -2.0,  LengthUnit.FEET,   LengthUnit.INCHES);

        // UC9: Weight equality — same unit and cross-unit comparisons
        checkEquality(new Quantity<>(2.0,     WeightUnit.KILOGRAM), new Quantity<>(2.0,    WeightUnit.KILOGRAM));
        checkEquality(new Quantity<>(500.0,   WeightUnit.GRAM),     new Quantity<>(500.0,  WeightUnit.GRAM));
        checkEquality(new Quantity<>(3.0,     WeightUnit.POUND),    new Quantity<>(3.0,    WeightUnit.POUND));
        checkEquality(new Quantity<>(1.0,     WeightUnit.KILOGRAM), new Quantity<>(1000.0, WeightUnit.GRAM));
        checkEquality(new Quantity<>(453.592, WeightUnit.GRAM),     new Quantity<>(1.0,    WeightUnit.POUND));

        // UC9: Weight unit-to-unit conversion
        demonstrateConversion(1.0,    WeightUnit.KILOGRAM, WeightUnit.POUND);
        demonstrateConversion(1.0,    WeightUnit.KILOGRAM, WeightUnit.GRAM);
        demonstrateConversion(2000.0, WeightUnit.GRAM,     WeightUnit.KILOGRAM);
        demonstrateConversion(500.0,  WeightUnit.GRAM,     WeightUnit.POUND);
        demonstrateConversion(2.0,    WeightUnit.POUND,    WeightUnit.KILOGRAM);
        demonstrateConversion(1.0,    WeightUnit.POUND,    WeightUnit.GRAM);

        // UC9: Weight addition with implicit target unit
        demonstrateAddition(1.0,   WeightUnit.KILOGRAM, 1000.0, WeightUnit.GRAM);
        demonstrateAddition(2.0,   WeightUnit.KILOGRAM, 3.0,    WeightUnit.KILOGRAM);
        demonstrateAddition(300.0, WeightUnit.GRAM,     200.0,  WeightUnit.GRAM);

        // UC9: Weight addition with explicit target unit
        demonstrateAddition(1.0,     WeightUnit.KILOGRAM, 1000.0,  WeightUnit.GRAM,  WeightUnit.GRAM);
        demonstrateAddition(1.0,     WeightUnit.POUND,    453.592, WeightUnit.GRAM,  WeightUnit.POUND);
        demonstrateAddition(2.0,     WeightUnit.KILOGRAM, 4.0,     WeightUnit.POUND, WeightUnit.KILOGRAM);

        // UC11: Volume equality — same unit and cross-unit comparisons
        checkEquality(new Quantity<>(1.0,   VolumeUnit.LITRE),       new Quantity<>(1.0,      VolumeUnit.LITRE));
        checkEquality(new Quantity<>(500.0, VolumeUnit.MILLILITRE),  new Quantity<>(500.0,    VolumeUnit.MILLILITRE));
        checkEquality(new Quantity<>(1.0,   VolumeUnit.GALLON),      new Quantity<>(1.0,      VolumeUnit.GALLON));
        checkEquality(new Quantity<>(1.0,   VolumeUnit.LITRE),       new Quantity<>(1000.0,   VolumeUnit.MILLILITRE));
        checkEquality(new Quantity<>(3.78541, VolumeUnit.LITRE),     new Quantity<>(1.0,      VolumeUnit.GALLON));
        checkEquality(new Quantity<>(0.264172, VolumeUnit.GALLON),   new Quantity<>(1.0,      VolumeUnit.LITRE));
        checkEquality(new Quantity<>(1000.0, VolumeUnit.MILLILITRE), new Quantity<>(0.264172, VolumeUnit.GALLON));

        // UC11: Volume unit-to-unit conversion
        demonstrateConversion(1.0,     VolumeUnit.LITRE,      VolumeUnit.MILLILITRE);
        demonstrateConversion(1000.0,  VolumeUnit.MILLILITRE, VolumeUnit.LITRE);
        demonstrateConversion(1.0,     VolumeUnit.GALLON,     VolumeUnit.LITRE);
        demonstrateConversion(2.0,     VolumeUnit.GALLON,     VolumeUnit.LITRE);
        demonstrateConversion(500.0,   VolumeUnit.MILLILITRE, VolumeUnit.GALLON);
        demonstrateConversion(3.78541, VolumeUnit.LITRE,      VolumeUnit.GALLON);

        // UC11: Volume Quantity-to-Quantity conversion
        demonstrateConversion(new Quantity<>(2.0, VolumeUnit.LITRE), VolumeUnit.MILLILITRE);

        // UC11: Volume addition with implicit target unit
        demonstrateAddition(1.0,   VolumeUnit.LITRE,      2.0,     VolumeUnit.LITRE);
        demonstrateAddition(1.0,   VolumeUnit.LITRE,      1000.0,  VolumeUnit.MILLILITRE);
        demonstrateAddition(500.0, VolumeUnit.MILLILITRE, 0.5,     VolumeUnit.LITRE);
        demonstrateAddition(2.0,   VolumeUnit.GALLON,     3.78541, VolumeUnit.LITRE);

        // UC11: Volume addition with explicit target unit
        demonstrateAddition(1.0,   VolumeUnit.LITRE,      1000.0,  VolumeUnit.MILLILITRE, VolumeUnit.MILLILITRE);
        demonstrateAddition(1.0,   VolumeUnit.GALLON,     3.78541, VolumeUnit.LITRE,      VolumeUnit.GALLON);
        demonstrateAddition(500.0, VolumeUnit.MILLILITRE, 1.0,     VolumeUnit.LITRE,      VolumeUnit.GALLON);
        demonstrateAddition(2.0,   VolumeUnit.LITRE,      4.0,     VolumeUnit.GALLON,     VolumeUnit.LITRE);

        // UC12: Length subtraction with implicit target unit
        demonstrateSubtraction(10.0, LengthUnit.FEET,   6.0,   LengthUnit.INCHES);
        demonstrateSubtraction(5.0,  LengthUnit.FEET,   10.0,  LengthUnit.FEET);
        demonstrateSubtraction(10.0, LengthUnit.FEET,   120.0, LengthUnit.INCHES);
        demonstrateSubtraction(5.0,  LengthUnit.FEET,   -2.0,  LengthUnit.FEET);

        // UC12: Length subtraction with explicit target unit
        demonstrateSubtraction(10.0, LengthUnit.FEET,   6.0,  LengthUnit.INCHES, LengthUnit.INCHES);
        demonstrateSubtraction(10.0, LengthUnit.FEET,   6.0,  LengthUnit.INCHES, LengthUnit.FEET);
        demonstrateSubtraction(36.0, LengthUnit.INCHES, 1.0,  LengthUnit.FEET,   LengthUnit.YARDS);

        // UC12: Weight subtraction with implicit target unit
        demonstrateSubtraction(10.0, WeightUnit.KILOGRAM, 5000.0, WeightUnit.GRAM);
        demonstrateSubtraction(2.0,  WeightUnit.KILOGRAM, 5.0,    WeightUnit.KILOGRAM);

        // UC12: Weight subtraction with explicit target unit
        demonstrateSubtraction(10.0, WeightUnit.KILOGRAM, 5000.0, WeightUnit.GRAM, WeightUnit.GRAM);
        demonstrateSubtraction(10.0, WeightUnit.KILOGRAM, 5000.0, WeightUnit.GRAM, WeightUnit.KILOGRAM);

        // UC12: Volume subtraction with implicit target unit
        demonstrateSubtraction(5.0, VolumeUnit.LITRE,      500.0, VolumeUnit.MILLILITRE);
        demonstrateSubtraction(1.0, VolumeUnit.LITRE,      1000.0, VolumeUnit.MILLILITRE);
        demonstrateSubtraction(2.0, VolumeUnit.GALLON,     3.78541, VolumeUnit.LITRE);

        // UC12: Volume subtraction with explicit target unit
        demonstrateSubtraction(5.0, VolumeUnit.LITRE, 2.0, VolumeUnit.LITRE, VolumeUnit.MILLILITRE);
        demonstrateSubtraction(5.0, VolumeUnit.LITRE, 2.0, VolumeUnit.LITRE, VolumeUnit.GALLON);

        // UC12: Length division — returns a dimensionless scalar ratio
        demonstrateDivision(10.0, LengthUnit.FEET,   2.0,  LengthUnit.FEET);
        demonstrateDivision(10.0, LengthUnit.FEET,   5.0,  LengthUnit.FEET);
        demonstrateDivision(24.0, LengthUnit.INCHES, 2.0,  LengthUnit.FEET);
        demonstrateDivision(5.0,  LengthUnit.FEET,   10.0, LengthUnit.FEET);

        // UC12: Weight division
        demonstrateDivision(10.0,   WeightUnit.KILOGRAM, 5.0,    WeightUnit.KILOGRAM);
        demonstrateDivision(2000.0, WeightUnit.GRAM,     1.0,    WeightUnit.KILOGRAM);
        demonstrateDivision(1.0,    WeightUnit.KILOGRAM, 2000.0, WeightUnit.GRAM);

        // UC12: Volume division
        demonstrateDivision(5.0,    VolumeUnit.LITRE,      10.0,  VolumeUnit.LITRE);
        demonstrateDivision(1000.0, VolumeUnit.MILLILITRE, 1.0,   VolumeUnit.LITRE);
        demonstrateDivision(1.0,    VolumeUnit.GALLON,     3.78541, VolumeUnit.LITRE);
    }
}