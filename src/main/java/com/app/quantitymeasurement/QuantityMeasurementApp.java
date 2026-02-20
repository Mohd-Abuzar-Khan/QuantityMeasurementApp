package com.app.quantitymeasurement;

public class QuantityMeasurementApp {


    public static void main(String[] args) {
    	
    	// Feature One : Feet
        Feet f1 = new Feet(1.0);
        Feet f2 = new Feet(1.0);

        boolean result = f1.equals(f2);

        System.out.println("Are they equal? " + result);
    }
}