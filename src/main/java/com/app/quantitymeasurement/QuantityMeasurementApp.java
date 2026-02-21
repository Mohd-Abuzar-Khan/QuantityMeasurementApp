package com.app.quantitymeasurement;

public class QuantityMeasurementApp {

    public static boolean demonstrateLengthEquality(Length l1, Length l2) {
        return l1.equals(l2);
    }

    public static void demonstrateFeetEquality() {
        Length length1 = new Length(1.0, LengthUnit.FEET);
        Length length2 = new Length(1.0, LengthUnit.FEET);

        System.out.println(length1 + " and " + length2 +
                " Equal: " + demonstrateLengthEquality(length1, length2));
    }

    public static void demonstrateInchesEquality() {
        Length length1 = new Length(1.0, LengthUnit.INCHES);
        Length length2 = new Length(1.0, LengthUnit.INCHES);

        System.out.println(length1 + " and " + length2 +
                " Equal: " + demonstrateLengthEquality(length1, length2));
    }

    public static void demonstrateFeetInchesComparison() {
        Length length1 = new Length(1.0, LengthUnit.FEET);
        Length length2 = new Length(12.0, LengthUnit.INCHES);

        System.out.println(length1 + " and " + length2 +
                " Equal: " + demonstrateLengthEquality(length1, length2));
    }

    public static void main(String[] args) {

        demonstrateFeetEquality();
        demonstrateInchesEquality();
        demonstrateFeetInchesComparison();
    }
}