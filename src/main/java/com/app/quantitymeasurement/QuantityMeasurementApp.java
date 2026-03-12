package com.app.quantitymeasurement;

import com.app.controller.QuantityController;
import com.app.dto.IMeasureableUnit;
import com.app.dto.QuantityDTO;
import com.app.dto.QuantityDTO.LengthUnit;
import com.app.dto.QuantityDTO.WeightUnit;
import com.app.dto.QuantityDTO.VolumeUnit;
import com.app.dto.QuantityDTO.TemperatureUnit;
import com.app.service.IQuantityService;
import com.app.service.QuantityServiceImpl;

public class QuantityMeasurementApp {


    private static final IQuantityService   SERVICE    = new QuantityServiceImpl();
    private static final QuantityController CONTROLLER = new QuantityController(SERVICE);

    private static QuantityDTO q(double value, IMeasureableUnit unit) {
        return new QuantityDTO(value, unit);
    }

    private static void checkEquality(QuantityDTO q1, QuantityDTO q2) {
        boolean result = CONTROLLER.performComparison(q1, q2);
        System.out.println(q1 + " == " + q2 + " → " + result);
    }

    private static void demonstrateConversion(double value,
                                              IMeasureableUnit from,
                                              IMeasureableUnit to) {
        QuantityDTO result = CONTROLLER.performConversion(q(value, from), q(0.0, to));
        System.out.println(value + " " + from + " → " + result.getValue() + " " + to);
    }

    private static void demonstrateConversion(QuantityDTO source, IMeasureableUnit to) {
        QuantityDTO result = CONTROLLER.performConversion(source, q(0.0, to));
        System.out.println(source + " → " + result);
    }

    private static void demonstrateAddition(double v1, IMeasureableUnit u1,
                                            double v2, IMeasureableUnit u2) {
        QuantityDTO result = CONTROLLER.performAddition(q(v1, u1), q(v2, u2), q(0.0, u1));
        System.out.println(q(v1, u1) + " + " + q(v2, u2) + " = " + result);
    }

    private static void demonstrateAddition(double v1, IMeasureableUnit u1,
                                            double v2, IMeasureableUnit u2,
                                            IMeasureableUnit targetUnit) {
        QuantityDTO result = CONTROLLER.performAddition(q(v1, u1), q(v2, u2), q(0.0, targetUnit));
        System.out.println("add(" + q(v1,u1) + ", " + q(v2,u2) + ", " + targetUnit + ") → " + result);
    }

    private static void demonstrateSubtraction(double v1, IMeasureableUnit u1,
                                               double v2, IMeasureableUnit u2) {
        QuantityDTO result = CONTROLLER.performSubtraction(q(v1, u1), q(v2, u2), q(0.0, u1));
        System.out.println(q(v1, u1) + " - " + q(v2, u2) + " = " + result);
    }

    private static void demonstrateSubtraction(double v1, IMeasureableUnit u1,
                                               double v2, IMeasureableUnit u2,
                                               IMeasureableUnit targetUnit) {
        QuantityDTO result = CONTROLLER.performSubtraction(q(v1, u1), q(v2, u2), q(0.0, targetUnit));
        System.out.println("sub(" + q(v1,u1) + ", " + q(v2,u2) + ", " + targetUnit + ") → " + result);
    }

    private static void demonstrateDivision(double v1, IMeasureableUnit u1,
                                            double v2, IMeasureableUnit u2) {
        double result = CONTROLLER.performDivision(q(v1, u1), q(v2, u2));
        System.out.println(q(v1, u1) + " ÷ " + q(v2, u2) + " = " + result);
    }

    public static void main(String[] args) {

        // ── UC1–UC4: Length equality ──────────────────────────────────────────
        checkEquality(q(1.0, LengthUnit.FEET),        q(1.0,      LengthUnit.FEET));
        checkEquality(q(5.0, LengthUnit.INCHES),      q(5.0,      LengthUnit.INCHES));
        checkEquality(q(1.0, LengthUnit.FEET),        q(12.0,     LengthUnit.INCHES));
        checkEquality(q(1.0, LengthUnit.YARDS),       q(3.0,      LengthUnit.FEET));
        checkEquality(q(1.0, LengthUnit.CENTIMETERS), q(0.393701, LengthUnit.INCHES));

        // ── UC5: Length conversion ────────────────────────────────────────────
        demonstrateConversion(1.0,  LengthUnit.FEET,        LengthUnit.INCHES);
        demonstrateConversion(3.0,  LengthUnit.YARDS,       LengthUnit.FEET);
        demonstrateConversion(36.0, LengthUnit.INCHES,      LengthUnit.YARDS);
        demonstrateConversion(1.0,  LengthUnit.CENTIMETERS, LengthUnit.INCHES);
        demonstrateConversion(q(2.0, LengthUnit.YARDS),     LengthUnit.INCHES);

        // ── UC6: Length addition (implicit target) ────────────────────────────
        demonstrateAddition(1.0,   LengthUnit.FEET,        12.0, LengthUnit.INCHES);
        demonstrateAddition(1.0,   LengthUnit.YARDS,       3.0,  LengthUnit.FEET);
        demonstrateAddition(30.48, LengthUnit.CENTIMETERS, 12.0, LengthUnit.INCHES);

        // ── UC7: Length addition (explicit target) ────────────────────────────
        demonstrateAddition(1.0,  LengthUnit.FEET,        12.0, LengthUnit.INCHES, LengthUnit.FEET);
        demonstrateAddition(1.0,  LengthUnit.FEET,        12.0, LengthUnit.INCHES, LengthUnit.INCHES);
        demonstrateAddition(1.0,  LengthUnit.FEET,        12.0, LengthUnit.INCHES, LengthUnit.YARDS);
        demonstrateAddition(1.0,  LengthUnit.YARDS,       3.0,  LengthUnit.FEET,   LengthUnit.YARDS);
        demonstrateAddition(36.0, LengthUnit.INCHES,      1.0,  LengthUnit.YARDS,  LengthUnit.FEET);
        demonstrateAddition(2.54, LengthUnit.CENTIMETERS, 1.0,  LengthUnit.INCHES, LengthUnit.CENTIMETERS);
        demonstrateAddition(5.0,  LengthUnit.FEET,        0.0,  LengthUnit.INCHES, LengthUnit.YARDS);
        demonstrateAddition(5.0,  LengthUnit.FEET,       -2.0,  LengthUnit.FEET,   LengthUnit.INCHES);

        // ── UC9: Weight equality ──────────────────────────────────────────────
        checkEquality(q(2.0,     WeightUnit.KILOGRAMS), q(2.0,    WeightUnit.KILOGRAMS));
        checkEquality(q(500.0,   WeightUnit.GRAMS),     q(500.0,  WeightUnit.GRAMS));
        checkEquality(q(3.0,     WeightUnit.POUNDS),    q(3.0,    WeightUnit.POUNDS));
        checkEquality(q(1.0,     WeightUnit.KILOGRAMS), q(1000.0, WeightUnit.GRAMS));
        checkEquality(q(453.592, WeightUnit.GRAMS),     q(1.0,    WeightUnit.POUNDS));

        // ── UC9: Weight conversion ────────────────────────────────────────────
        demonstrateConversion(1.0,    WeightUnit.KILOGRAMS, WeightUnit.POUNDS);
        demonstrateConversion(1.0,    WeightUnit.KILOGRAMS, WeightUnit.GRAMS);
        demonstrateConversion(2000.0, WeightUnit.GRAMS,     WeightUnit.KILOGRAMS);
        demonstrateConversion(500.0,  WeightUnit.GRAMS,     WeightUnit.POUNDS);
        demonstrateConversion(2.0,    WeightUnit.POUNDS,    WeightUnit.KILOGRAMS);
        demonstrateConversion(1.0,    WeightUnit.POUNDS,    WeightUnit.GRAMS);

        // ── UC9: Weight addition ──────────────────────────────────────────────
        demonstrateAddition(1.0,   WeightUnit.KILOGRAMS, 1000.0, WeightUnit.GRAMS);
        demonstrateAddition(2.0,   WeightUnit.KILOGRAMS, 3.0,    WeightUnit.KILOGRAMS);
        demonstrateAddition(300.0, WeightUnit.GRAMS,     200.0,  WeightUnit.GRAMS);
        demonstrateAddition(1.0,   WeightUnit.KILOGRAMS, 1000.0, WeightUnit.GRAMS,     WeightUnit.GRAMS);
        demonstrateAddition(1.0,   WeightUnit.POUNDS,    453.592,WeightUnit.GRAMS,     WeightUnit.POUNDS);
        demonstrateAddition(2.0,   WeightUnit.KILOGRAMS, 4.0,    WeightUnit.POUNDS,    WeightUnit.KILOGRAMS);

        // ── UC11: Volume equality ─────────────────────────────────────────────
        checkEquality(q(1.0,      VolumeUnit.LITERS),      q(1.0,      VolumeUnit.LITERS));
        checkEquality(q(500.0,    VolumeUnit.MILLILITERS), q(500.0,    VolumeUnit.MILLILITERS));
        checkEquality(q(1.0,      VolumeUnit.GALLONS),     q(1.0,      VolumeUnit.GALLONS));
        checkEquality(q(1.0,      VolumeUnit.LITERS),      q(1000.0,   VolumeUnit.MILLILITERS));
        checkEquality(q(3.78541,  VolumeUnit.LITERS),      q(1.0,      VolumeUnit.GALLONS));
        checkEquality(q(0.264172, VolumeUnit.GALLONS),     q(1.0,      VolumeUnit.LITERS));
        checkEquality(q(1000.0,   VolumeUnit.MILLILITERS), q(0.264172, VolumeUnit.GALLONS));

        // ── UC11: Volume conversion ───────────────────────────────────────────
        demonstrateConversion(1.0,     VolumeUnit.LITERS,      VolumeUnit.MILLILITERS);
        demonstrateConversion(1000.0,  VolumeUnit.MILLILITERS, VolumeUnit.LITERS);
        demonstrateConversion(1.0,     VolumeUnit.GALLONS,     VolumeUnit.LITERS);
        demonstrateConversion(2.0,     VolumeUnit.GALLONS,     VolumeUnit.LITERS);
        demonstrateConversion(500.0,   VolumeUnit.MILLILITERS, VolumeUnit.GALLONS);
        demonstrateConversion(3.78541, VolumeUnit.LITERS,      VolumeUnit.GALLONS);
        demonstrateConversion(q(2.0,   VolumeUnit.LITERS),     VolumeUnit.MILLILITERS);

        // ── UC11: Volume addition ─────────────────────────────────────────────
        demonstrateAddition(1.0,   VolumeUnit.LITERS,      2.0,     VolumeUnit.LITERS);
        demonstrateAddition(1.0,   VolumeUnit.LITERS,      1000.0,  VolumeUnit.MILLILITERS);
        demonstrateAddition(500.0, VolumeUnit.MILLILITERS, 0.5,     VolumeUnit.LITERS);
        demonstrateAddition(2.0,   VolumeUnit.GALLONS,     3.78541, VolumeUnit.LITERS);
        demonstrateAddition(1.0,   VolumeUnit.LITERS,      1000.0,  VolumeUnit.MILLILITERS, VolumeUnit.MILLILITERS);
        demonstrateAddition(1.0,   VolumeUnit.GALLONS,     3.78541, VolumeUnit.LITERS,      VolumeUnit.GALLONS);
        demonstrateAddition(500.0, VolumeUnit.MILLILITERS, 1.0,     VolumeUnit.LITERS,      VolumeUnit.GALLONS);
        demonstrateAddition(2.0,   VolumeUnit.LITERS,      4.0,     VolumeUnit.GALLONS,     VolumeUnit.LITERS);

        // ── UC12: Length subtraction ──────────────────────────────────────────
        demonstrateSubtraction(10.0, LengthUnit.FEET,   6.0,   LengthUnit.INCHES);
        demonstrateSubtraction(5.0,  LengthUnit.FEET,   10.0,  LengthUnit.FEET);
        demonstrateSubtraction(10.0, LengthUnit.FEET,   120.0, LengthUnit.INCHES);
        demonstrateSubtraction(5.0,  LengthUnit.FEET,  -2.0,   LengthUnit.FEET);
        demonstrateSubtraction(10.0, LengthUnit.FEET,   6.0,   LengthUnit.INCHES, LengthUnit.INCHES);
        demonstrateSubtraction(10.0, LengthUnit.FEET,   6.0,   LengthUnit.INCHES, LengthUnit.FEET);
        demonstrateSubtraction(36.0, LengthUnit.INCHES, 1.0,   LengthUnit.FEET,   LengthUnit.YARDS);

        // ── UC12: Weight subtraction ──────────────────────────────────────────
        demonstrateSubtraction(10.0, WeightUnit.KILOGRAMS, 5000.0, WeightUnit.GRAMS);
        demonstrateSubtraction(2.0,  WeightUnit.KILOGRAMS, 5.0,    WeightUnit.KILOGRAMS);
        demonstrateSubtraction(10.0, WeightUnit.KILOGRAMS, 5000.0, WeightUnit.GRAMS, WeightUnit.GRAMS);
        demonstrateSubtraction(10.0, WeightUnit.KILOGRAMS, 5000.0, WeightUnit.GRAMS, WeightUnit.KILOGRAMS);

        // ── UC12: Volume subtraction ──────────────────────────────────────────
        demonstrateSubtraction(5.0, VolumeUnit.LITERS,      500.0,   VolumeUnit.MILLILITERS);
        demonstrateSubtraction(1.0, VolumeUnit.LITERS,      1000.0,  VolumeUnit.MILLILITERS);
        demonstrateSubtraction(2.0, VolumeUnit.GALLONS,     3.78541, VolumeUnit.LITERS);
        demonstrateSubtraction(5.0, VolumeUnit.LITERS,      2.0,     VolumeUnit.LITERS, VolumeUnit.MILLILITERS);
        demonstrateSubtraction(5.0, VolumeUnit.LITERS,      2.0,     VolumeUnit.LITERS, VolumeUnit.GALLONS);

        // ── UC12: Division ────────────────────────────────────────────────────
        demonstrateDivision(10.0, LengthUnit.FEET,        2.0,     LengthUnit.FEET);
        demonstrateDivision(10.0, LengthUnit.FEET,        5.0,     LengthUnit.FEET);
        demonstrateDivision(24.0, LengthUnit.INCHES,      2.0,     LengthUnit.FEET);
        demonstrateDivision(5.0,  LengthUnit.FEET,        10.0,    LengthUnit.FEET);
        demonstrateDivision(10.0, WeightUnit.KILOGRAMS,   5.0,     WeightUnit.KILOGRAMS);
        demonstrateDivision(2000.0,WeightUnit.GRAMS,      1.0,     WeightUnit.KILOGRAMS);
        demonstrateDivision(1.0,  WeightUnit.KILOGRAMS,   2000.0,  WeightUnit.GRAMS);
        demonstrateDivision(5.0,  VolumeUnit.LITERS,      10.0,    VolumeUnit.LITERS);
        demonstrateDivision(1000.0,VolumeUnit.MILLILITERS,1.0,     VolumeUnit.LITERS);
        demonstrateDivision(1.0,  VolumeUnit.GALLONS,     3.78541, VolumeUnit.LITERS);

        // ── UC14: Temperature equality ────────────────────────────────────────
        checkEquality(q(0.0,   TemperatureUnit.CELSIUS),    q(32.0,   TemperatureUnit.FAHRENHEIT));
        checkEquality(q(100.0, TemperatureUnit.CELSIUS),    q(212.0,  TemperatureUnit.FAHRENHEIT));
        checkEquality(q(-40.0, TemperatureUnit.CELSIUS),    q(-40.0,  TemperatureUnit.FAHRENHEIT));
        checkEquality(q(0.0,   TemperatureUnit.CELSIUS),    q(273.15, TemperatureUnit.KELVIN));
        checkEquality(q(100.0, TemperatureUnit.CELSIUS),    q(373.15, TemperatureUnit.KELVIN));

        // ── UC14: Temperature conversion ──────────────────────────────────────
        demonstrateConversion(100.0,  TemperatureUnit.CELSIUS,    TemperatureUnit.FAHRENHEIT);
        demonstrateConversion(32.0,   TemperatureUnit.FAHRENHEIT, TemperatureUnit.CELSIUS);
        demonstrateConversion(273.15, TemperatureUnit.KELVIN,     TemperatureUnit.CELSIUS);
        demonstrateConversion(0.0,    TemperatureUnit.CELSIUS,    TemperatureUnit.KELVIN);
        demonstrateConversion(-40.0,  TemperatureUnit.CELSIUS,    TemperatureUnit.FAHRENHEIT);

        // ── UC14: Temperature — arithmetic not supported ───────────────────────
        System.out.println("100°C + 50°C → "
            + CONTROLLER.performAddition(
                q(100.0, TemperatureUnit.CELSIUS),
                q(50.0,  TemperatureUnit.CELSIUS),
                q(0.0,   TemperatureUnit.CELSIUS)));

        System.out.println("100°C - 50°C → "
            + CONTROLLER.performSubtraction(
                q(100.0, TemperatureUnit.CELSIUS),
                q(50.0,  TemperatureUnit.CELSIUS),
                q(0.0,   TemperatureUnit.CELSIUS)));

        System.out.println("100°C ÷ 50°C → "
            + CONTROLLER.performDivision(
                q(100.0, TemperatureUnit.CELSIUS),
                q(50.0,  TemperatureUnit.CELSIUS)));
    }
}