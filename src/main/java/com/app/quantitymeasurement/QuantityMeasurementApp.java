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

    public static void demonstrateLengthConversion( double value, LengthUnit fromUnit, LengthUnit toUnit) {
    // Unit to Unit Conversion    
    System.out.println(value + " " + fromUnit + " is " + Length.convert(value, fromUnit, toUnit) + " " + toUnit);
    
    }

    public static void demonstrateLengthConversion(Length length, LengthUnit toUnit) {
        // Length to Length Conversion
        System.out.println(length + " converted to " + toUnit + " is " + length.convertTo(toUnit));
    }

    public static void demonstrateLengthAddition( double value1, LengthUnit unit1, double value2, LengthUnit unit2) {

        Length length1 = new Length(value1, unit1);
        Length length2 = new Length(value2, unit2);

        Length result = length1.add(length2);

        System.out.println(length1 + " + " + length2 + " = " + result);
    }

    public static void demonstrateLengthAddition( double value1, LengthUnit unit1, double value2, LengthUnit unit2, LengthUnit targetUnit) {

        Length l1 = new Length(value1, unit1);
        Length l2 = new Length(value2, unit2);

        Length result = Length.add(l1, l2, targetUnit);

        System.out.println(
                "add(" + l1 + ", " + l2 + ", " + targetUnit + ") → " + result );
    }



    // UC9 : Weight Class Methods 

    public static boolean demonstrateWeightEquality(QuantityWeight w1, QuantityWeight w2) {
        return w1.equals(w2);
    }
    
    private static void checkWeightEquality(QuantityWeight w1, QuantityWeight w2) {
        System.out.println(w1 + " and " + w2 +
                " Equal: " + demonstrateWeightEquality(w1, w2));
    }

    // Weight conversion 
    // Unit-to-Unit Conversion (Static style like Length.convert)
    public static void demonstrateWeightConversion(double value, WeightUnit fromUnit, WeightUnit toUnit) {

        QuantityWeight weight = new QuantityWeight(value, fromUnit);
        QuantityWeight converted = weight.convertTo(toUnit);

        System.out.println(value + " " + fromUnit +
                " is " + converted.getValue() + " " + toUnit);
    }


    // QuantityWeight-to-QuantityWeight Conversion
    public static void demonstrateWeightConversion(QuantityWeight weight, WeightUnit toUnit) {
        System.out.println(weight + " converted to " + toUnit + " is " + weight.convertTo(toUnit));
    }


    // Weight Addition on Default Unit
    public static void demonstrateWeightAddition(double value1, WeightUnit unit1, double value2, WeightUnit unit2) {
        QuantityWeight w1 = new QuantityWeight(value1, unit1);
        QuantityWeight w2 = new QuantityWeight(value2, unit2);
        QuantityWeight result = w1.add(w2);
        System.out.println(w1 + " + " + w2 + " = " + result);
    }

    // Weigth Addition on Target Unit
    public static void demonstrateWeightAddition(double value1, WeightUnit unit1, double value2, WeightUnit unit2, WeightUnit targetUnit) {

            QuantityWeight w1 = new QuantityWeight(value1, unit1);
            QuantityWeight w2 = new QuantityWeight(value2, unit2);
            QuantityWeight result = QuantityWeight.add(w1, w2, targetUnit);
            System.out.println(
            "add(" + w1 + ", " + w2 + ", " + targetUnit + ") → " + result
            );
    }




    public static void main(String[] args) {

        // Same Unit
        checkEquality(new Length(1.0, LengthUnit.FEET),new Length(1.0, LengthUnit.FEET));
        checkEquality(new Length(5.0, LengthUnit.INCHES), new Length(5.0, LengthUnit.INCHES));

        // Cross Unit
        checkEquality(new Length(1.0, LengthUnit.FEET), new Length(12.0, LengthUnit.INCHES));
        checkEquality(new Length(1.0, LengthUnit.YARDS), new Length(3.0, LengthUnit.FEET));
        checkEquality(new Length(1.0, LengthUnit.CENTIMETERS), new Length(0.393701, LengthUnit.INCHES));


        // UC5 : Unit to Unit Conversion
        demonstrateLengthConversion(1.0, LengthUnit.FEET, LengthUnit.INCHES);
        demonstrateLengthConversion(3.0, LengthUnit.YARDS, LengthUnit.FEET);
        demonstrateLengthConversion(36.0, LengthUnit.INCHES, LengthUnit.YARDS);
        demonstrateLengthConversion(1.0, LengthUnit.CENTIMETERS, LengthUnit.INCHES);
        
        // UC5 : Length to Length Conversion
        demonstrateLengthConversion( new Length(2.0, LengthUnit.YARDS), LengthUnit.INCHES);


        // UC6 : Addition demonstrations 
        demonstrateLengthAddition(1.0, LengthUnit.FEET, 12.0, LengthUnit.INCHES);
        demonstrateLengthAddition(1.0, LengthUnit.YARDS, 3.0, LengthUnit.FEET);
        demonstrateLengthAddition(30.48, LengthUnit.CENTIMETERS, 12.0, LengthUnit.INCHES);

        // UC7 : Target Addition demonstrations 
        demonstrateLengthAddition(1.0, LengthUnit.FEET, 12.0, LengthUnit.INCHES, LengthUnit.FEET);
        demonstrateLengthAddition(1.0, LengthUnit.FEET, 12.0, LengthUnit.INCHES, LengthUnit.INCHES);
        demonstrateLengthAddition(1.0, LengthUnit.FEET, 12.0, LengthUnit.INCHES, LengthUnit.YARDS);
        demonstrateLengthAddition(1.0, LengthUnit.YARDS, 3.0, LengthUnit.FEET, LengthUnit.YARDS);
        demonstrateLengthAddition(36.0, LengthUnit.INCHES, 1.0, LengthUnit.YARDS, LengthUnit.FEET);
        demonstrateLengthAddition(2.54, LengthUnit.CENTIMETERS, 1.0, LengthUnit.INCHES, LengthUnit.CENTIMETERS);
        demonstrateLengthAddition(5.0, LengthUnit.FEET, 0.0, LengthUnit.INCHES, LengthUnit.YARDS);
        demonstrateLengthAddition(5.0, LengthUnit.FEET, -2.0, LengthUnit.FEET, LengthUnit.INCHES);

        // UC9 : Weight Class Operations
        
        // Equality for Same Unit
        checkWeightEquality( new QuantityWeight(1.0, WeightUnit.KILOGRAM), new QuantityWeight(1000.0, WeightUnit.GRAM));
        checkWeightEquality(new QuantityWeight(2.0, WeightUnit.KILOGRAM), new QuantityWeight(2.0, WeightUnit.KILOGRAM));
        checkWeightEquality( new QuantityWeight(500.0, WeightUnit.GRAM), new QuantityWeight(500.0, WeightUnit.GRAM));
        checkWeightEquality( new QuantityWeight(3.0, WeightUnit.POUND), new QuantityWeight(3.0, WeightUnit.POUND));
        
        // Equality for Cross Unit
        checkWeightEquality( new QuantityWeight(1.0, WeightUnit.KILOGRAM), new QuantityWeight(1000.0, WeightUnit.GRAM));
        checkWeightEquality( new QuantityWeight(453.592, WeightUnit.GRAM), new QuantityWeight(1.0, WeightUnit.POUND));
        
        // Conversion
        demonstrateWeightConversion(1.0, WeightUnit.KILOGRAM, WeightUnit.POUND);
        demonstrateWeightConversion(1.0, WeightUnit.KILOGRAM, WeightUnit.GRAM);
        demonstrateWeightConversion(1.0, WeightUnit.KILOGRAM, WeightUnit.POUND);
        demonstrateWeightConversion(2000.0, WeightUnit.GRAM, WeightUnit.KILOGRAM);
        demonstrateWeightConversion(500.0, WeightUnit.GRAM, WeightUnit.POUND);
        demonstrateWeightConversion(2.0, WeightUnit.POUND, WeightUnit.KILOGRAM);
        demonstrateWeightConversion(1.0, WeightUnit.POUND, WeightUnit.GRAM);

        // Addition (implicit)
        demonstrateWeightAddition(1.0, WeightUnit.KILOGRAM, 1000.0, WeightUnit.GRAM);
        demonstrateWeightAddition( 2.0, WeightUnit.KILOGRAM, 3.0, WeightUnit.KILOGRAM);
        demonstrateWeightAddition( 300.0, WeightUnit.GRAM, 200.0, WeightUnit.GRAM);

        // Addition (explicit)
        demonstrateWeightAddition(1.0, WeightUnit.KILOGRAM, 1000.0, WeightUnit.GRAM, WeightUnit.GRAM);
        demonstrateWeightAddition( 1.0, WeightUnit.KILOGRAM, 1000.0, WeightUnit.GRAM, WeightUnit.GRAM);
        demonstrateWeightAddition( 1.0, WeightUnit.POUND, 453.592, WeightUnit.GRAM, WeightUnit.POUND);
        demonstrateWeightAddition(2.0, WeightUnit.KILOGRAM, 4.0, WeightUnit.POUND, WeightUnit.KILOGRAM);
    }
}
