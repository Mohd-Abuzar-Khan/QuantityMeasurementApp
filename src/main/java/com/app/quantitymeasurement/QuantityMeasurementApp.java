package com.app.quantitymeasurement;

import com.app.controller.QuantityMeasurementController;
import com.app.entity.IMeasureableUnit;
import com.app.entity.QuantityDTO;
import com.app.entity.QuantityDTO.LengthUnit;
import com.app.entity.QuantityDTO.WeightUnit;
import com.app.entity.QuantityDTO.VolumeUnit;
import com.app.entity.QuantityDTO.TemperatureUnit;
import com.app.entity.QuantityMeasurementEntity;
import com.app.repository.IQuantityMeasurementRepository;
import com.app.repository.QuantityMeasurementDatabaseRepository;
import com.app.service.IQuantityMeasurementService;
import com.app.service.QuantityMeasurementServiceImpl;
import com.app.util.ApplicationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class QuantityMeasurementApp {

    private static final Logger log = LoggerFactory.getLogger(QuantityMeasurementApp.class);

    // ── Singleton ─────────────────────────────────────────────────────────────
    private static QuantityMeasurementApp instance;

    private final QuantityMeasurementController controller;
    private final IQuantityMeasurementRepository repository;

    private QuantityMeasurementApp() {
        log.info("Initialising Quantity Measurement Application (UC16)");
        ApplicationConfig config = ApplicationConfig.getInstance();
        log.info("Configuration: {}", config);

        this.repository = createRepository(config);
        IQuantityMeasurementService service = new QuantityMeasurementServiceImpl(repository);
        this.controller = new QuantityMeasurementController(service);

        log.info("Application wiring complete – using repository: {}",
            repository.getClass().getSimpleName());
    }

    public static synchronized QuantityMeasurementApp getInstance() {
        if (instance == null) {
            instance = new QuantityMeasurementApp();
        }
        return instance;
    }

    // ── Factory ───────────────────────────────────────────────────────────────
    private IQuantityMeasurementRepository createRepository(ApplicationConfig config) {
        if (config.isDatabaseRepository()) {
            log.info("Using JDBC database repository");
            return QuantityMeasurementDatabaseRepository.getInstance();
        }
        // Fall back to H2 singleton (keeps UC15 cache-style behaviour)
        log.info("Using H2 singleton repository (cache mode)");
        return com.app.repository.QuantityMeasurementH2Repository.getInstance();
    }

    // ── Convenience builder ───────────────────────────────────────────────────
    private static QuantityDTO q(double value, IMeasureableUnit unit) {
        return new QuantityDTO(value, unit);
    }

    // ── Public management methods (UC16) ──────────────────────────────────────

    /**
     * Deletes all persisted measurements from the repository.
     * Useful for resetting application state or before a test run.
     */
    public void deleteAllMeasurements() {
        log.info("Deleting all measurements from repository …");
        repository.clear();
        log.info("All measurements deleted. Count is now: {}", repository.count());
    }

    /**
     * Releases resources held by the repository (e.g. closes the connection pool).
     * Should be called when the application is shutting down.
     */
    public void closeResources() {
        log.info("Closing application resources …");
        repository.releaseResources();
        log.info("Resources released.");
    }


    public void reportStoredMeasurements() {
        List<QuantityMeasurementEntity> all = repository.findAll();
        log.info("═══════════════════════════════════════════════════════════");
        log.info("  STORED MEASUREMENTS REPORT  ({} records)", all.size());
        log.info("═══════════════════════════════════════════════════════════");
        for (int i = 0; i < all.size(); i++) {
            log.info("  [{}] {}", i + 1, all.get(i));
        }
        log.info("Pool statistics: {}", repository.getPoolStatistics());
        log.info("═══════════════════════════════════════════════════════════");
    }

    // ── Demonstration delegates ───────────────────────────────────────────────

    private void demonstrateEquality(QuantityDTO q1, QuantityDTO q2) {
        controller.performComparison(q1, q2);
    }

    private void demonstrateConversion(double value, IMeasureableUnit from, IMeasureableUnit to) {
        controller.performConversion(q(value, from), q(0.0, to));
    }

    private void demonstrateAddition(double v1, IMeasureableUnit u1,
                                     double v2, IMeasureableUnit u2) {
        controller.performAddition(q(v1, u1), q(v2, u2), q(0.0, u1));
    }

    private void demonstrateAddition(double v1, IMeasureableUnit u1,
                                     double v2, IMeasureableUnit u2,
                                     IMeasureableUnit target) {
        controller.performAddition(q(v1, u1), q(v2, u2), q(0.0, target));
    }

    private void demonstrateSubtraction(double v1, IMeasureableUnit u1,
                                        double v2, IMeasureableUnit u2) {
        controller.performSubtraction(q(v1, u1), q(v2, u2), q(0.0, u1));
    }

    private void demonstrateSubtraction(double v1, IMeasureableUnit u1,
                                        double v2, IMeasureableUnit u2,
                                        IMeasureableUnit target) {
        controller.performSubtraction(q(v1, u1), q(v2, u2), q(0.0, target));
    }

    private void demonstrateDivision(double v1, IMeasureableUnit u1,
                                     double v2, IMeasureableUnit u2) {
        controller.performDivision(q(v1, u1), q(v2, u2));
    }

    // ── Run ───────────────────────────────────────────────────────────────────
    private void run() {

        // UC1–UC4: Length equality
        demonstrateEquality(q(1.0, LengthUnit.FEET),        q(1.0,      LengthUnit.FEET));
        demonstrateEquality(q(1.0, LengthUnit.FEET),        q(12.0,     LengthUnit.INCHES));
        demonstrateEquality(q(1.0, LengthUnit.YARDS),       q(3.0,      LengthUnit.FEET));
        demonstrateEquality(q(1.0, LengthUnit.CENTIMETERS), q(0.393701, LengthUnit.INCHES));

        // UC5: Length conversion
        demonstrateConversion(1.0,  LengthUnit.FEET,   LengthUnit.INCHES);
        demonstrateConversion(3.0,  LengthUnit.YARDS,  LengthUnit.FEET);
        demonstrateConversion(36.0, LengthUnit.INCHES, LengthUnit.YARDS);
        demonstrateConversion(1.0,  LengthUnit.METERS, LengthUnit.FEET);

        // UC6–UC7: Length addition
        demonstrateAddition(1.0, LengthUnit.FEET,  12.0, LengthUnit.INCHES);
        demonstrateAddition(1.0, LengthUnit.YARDS,  3.0, LengthUnit.FEET);
        demonstrateAddition(1.0, LengthUnit.FEET,  12.0, LengthUnit.INCHES, LengthUnit.YARDS);

        // UC9: Weight equality
        demonstrateEquality(q(1.0,     WeightUnit.KILOGRAMS), q(1000.0,  WeightUnit.GRAMS));
        demonstrateEquality(q(453.592, WeightUnit.GRAMS),     q(1.0,     WeightUnit.POUNDS));
        demonstrateEquality(q(1.0,     WeightUnit.OUNCES),    q(28.3495, WeightUnit.GRAMS));

        // UC9: Weight conversion
        demonstrateConversion(1.0,  WeightUnit.KILOGRAMS, WeightUnit.POUNDS);
        demonstrateConversion(16.0, WeightUnit.OUNCES,    WeightUnit.POUNDS);

        // UC9: Weight addition
        demonstrateAddition(1.0, WeightUnit.KILOGRAMS, 1000.0, WeightUnit.GRAMS);
        demonstrateAddition(1.0, WeightUnit.KILOGRAMS, 1000.0, WeightUnit.GRAMS, WeightUnit.GRAMS);

        // UC11: Volume equality
        demonstrateEquality(q(1.0,     VolumeUnit.LITERS),      q(1000.0, VolumeUnit.MILLILITERS));
        demonstrateEquality(q(3.78541, VolumeUnit.LITERS),      q(1.0,    VolumeUnit.GALLONS));

        // UC11: Volume conversion
        demonstrateConversion(1.0, VolumeUnit.LITERS,  VolumeUnit.MILLILITERS);
        demonstrateConversion(1.0, VolumeUnit.GALLONS, VolumeUnit.LITERS);

        // UC11: Volume addition
        demonstrateAddition(1.0, VolumeUnit.LITERS, 1000.0, VolumeUnit.MILLILITERS);

        // UC12: Subtraction
        demonstrateSubtraction(10.0, LengthUnit.FEET,      6.0,    LengthUnit.INCHES);
        demonstrateSubtraction(10.0, LengthUnit.FEET,      6.0,    LengthUnit.INCHES, LengthUnit.INCHES);
        demonstrateSubtraction(10.0, WeightUnit.KILOGRAMS, 5000.0, WeightUnit.GRAMS);
        demonstrateSubtraction(5.0,  VolumeUnit.LITERS,   500.0,  VolumeUnit.MILLILITERS);

        // UC12: Division
        demonstrateDivision(10.0, LengthUnit.FEET,      2.0,  LengthUnit.FEET);
        demonstrateDivision(24.0, LengthUnit.INCHES,    2.0,  LengthUnit.FEET);
        demonstrateDivision(10.0, WeightUnit.KILOGRAMS, 5.0,  WeightUnit.KILOGRAMS);
        demonstrateDivision(5.0,  VolumeUnit.LITERS,   10.0, VolumeUnit.LITERS);

        // UC14: Temperature equality
        demonstrateEquality(q(0.0,   TemperatureUnit.CELSIUS),    q(32.0,   TemperatureUnit.FAHRENHEIT));
        demonstrateEquality(q(100.0, TemperatureUnit.CELSIUS),    q(212.0,  TemperatureUnit.FAHRENHEIT));
        demonstrateEquality(q(0.0,   TemperatureUnit.CELSIUS),    q(273.15, TemperatureUnit.KELVIN));

        // UC14: Temperature conversion
        demonstrateConversion(100.0,  TemperatureUnit.CELSIUS,    TemperatureUnit.FAHRENHEIT);
        demonstrateConversion(32.0,   TemperatureUnit.FAHRENHEIT, TemperatureUnit.CELSIUS);
        demonstrateConversion(273.15, TemperatureUnit.KELVIN,     TemperatureUnit.CELSIUS);

        // UC14: Temperature arithmetic – should display errors
        log.info("── Temperature arithmetic (should fail) ────");
        controller.performAddition(
            q(100.0, TemperatureUnit.CELSIUS),
            q(50.0,  TemperatureUnit.CELSIUS),
            q(0.0,   TemperatureUnit.CELSIUS));

        controller.performSubtraction(
            q(100.0, TemperatureUnit.CELSIUS),
            q(50.0,  TemperatureUnit.CELSIUS),
            q(0.0,   TemperatureUnit.CELSIUS));

        controller.performDivision(
            q(100.0, TemperatureUnit.CELSIUS),
            q(50.0,  TemperatureUnit.CELSIUS));

        // ── UC16: Post-run reporting ──────────────────────────────────────────
        reportStoredMeasurements();

        // ── UC16: Cleanup demo ────────────────────────────────────────────────
        log.info("Deleting all measurements (cleanup demo) …");
        deleteAllMeasurements();
    }

    // ── Main ──────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        QuantityMeasurementApp app = QuantityMeasurementApp.getInstance();
        try {
            app.run();
        } finally {
            app.closeResources();
        }
    }
}
