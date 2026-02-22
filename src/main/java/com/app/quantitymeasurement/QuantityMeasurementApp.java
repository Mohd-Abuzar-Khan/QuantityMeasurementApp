package com.app.quantitymeasurement;

public class QuantityMeasurementApp {


    // UC1 to UC4 : Testing Equality and Comparison
    public static boolean demonstrateLengthEquality(Length l1, Length l2) {
        return l1.equals(l2);
    }

    private static void checkEquality(Length l1, Length l2) {
        System.out.println(l1 + " and " + l2 +
                " Equal: " + demonstrateLengthEquality(l1, l2));
    }

    public static void demonstrateFeetInchesComparison() {
        Length length1 = new Length(1.0, LengthUnit.FEET);
        Length length2 = new Length(12.0, LengthUnit.INCHES);

        System.out.println(length1 + " and " + length2 +
                " Equal: " + demonstrateLengthEquality(length1, length2));
    }

    public static void demonstrateLengthConversion( double value, LengthUnit fromUnit, LengthUnit toUnit) {
    
    // Unit to Unit Conversion    
    System.out.println(value + " " + fromUnit + " is " + Length.convert(value, fromUnit, toUnit) + " " + toUnit);
    
    }

    public static void demonstrateLengthConversion(Length length, LengthUnit toUnit) {
        // Length to Length Conversion
        System.out.println(length + " converted to " + toUnit + " is " + length.convertTo(toUnit));
    }

    public static void main(String[] args) {

        // Same Unit
        checkEquality(new Length(1.0, LengthUnit.FEET),new Length(1.0, LengthUnit.FEET));
        checkEquality(new Length(5.0, LengthUnit.INCHES), new Length(5.0, LengthUnit.INCHES));

        // Cross Unit
        checkEquality(new Length(1.0, LengthUnit.FEET), new Length(12.0, LengthUnit.INCHES));
        checkEquality(new Length(1.0, LengthUnit.YARDS), new Length(3.0, LengthUnit.FEET));
        checkEquality(new Length(1.0, LengthUnit.CENTIMETERS), new Length(0.393701, LengthUnit.INCHES));


        // Unit to Unit Conversion
        demonstrateLengthConversion(1.0, LengthUnit.FEET, LengthUnit.INCHES);
        demonstrateLengthConversion(3.0, LengthUnit.YARDS, LengthUnit.FEET);
        demonstrateLengthConversion(36.0, LengthUnit.INCHES, LengthUnit.YARDS);
        demonstrateLengthConversion(1.0, LengthUnit.CENTIMETERS, LengthUnit.INCHES);
        
        // Length to Length Conversion
        demonstrateLengthConversion( new Length(2.0, LengthUnit.YARDS), LengthUnit.INCHES
        );
    }
}
