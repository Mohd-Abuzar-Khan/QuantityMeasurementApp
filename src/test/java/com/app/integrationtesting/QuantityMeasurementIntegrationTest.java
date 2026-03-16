package com.app.integrationtesting;

import com.app.controller.QuantityMeasurementController;
import com.app.entity.QuantityDTO;
import com.app.entity.QuantityDTO.LengthUnit;
import com.app.entity.QuantityDTO.WeightUnit;
import com.app.entity.QuantityDTO.VolumeUnit;
import com.app.entity.QuantityDTO.TemperatureUnit;
import com.app.entity.QuantityMeasurementEntity;
import com.app.repository.IQuantityMeasurementRepository;
import com.app.repository.QuantityMeasurementDatabaseRepository;
import com.app.service.QuantityMeasurementServiceImpl;
import com.app.util.ConnectionPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**A
 * Integration tests verifying end-to-end flows through Controller → Service → Repository → H2.
 * Each test gets a clean database.
 */
public class QuantityMeasurementIntegrationTest {

    private IQuantityMeasurementRepository repository;
    private QuantityMeasurementController  controller;

    @Before
    public void setUp() {
        System.setProperty("db.url",            "jdbc:h2:mem:it_db;DB_CLOSE_DELAY=-1");
        System.setProperty("db.driver",         "org.h2.Driver");
        System.setProperty("db.username",       "sa");
        System.setProperty("db.password",       "");
        System.setProperty("pool.size",         "5");
        System.setProperty("repository.type",   "database");

        QuantityMeasurementDatabaseRepository.resetInstance();
        ConnectionPool.resetInstance();

        repository = QuantityMeasurementDatabaseRepository.getInstance();
        QuantityMeasurementServiceImpl service = new QuantityMeasurementServiceImpl(repository);
        controller = new QuantityMeasurementController(service);
    }

    @After
    public void tearDown() {
        if (repository != null) {
            repository.clear();
            repository.releaseResources();
        }
        QuantityMeasurementDatabaseRepository.resetInstance();
        ConnectionPool.resetInstance();
    }

    // ── helper ────────────────────────────────────────────────────────────────
    private static QuantityDTO q(double v,
                                  com.app.entity.IMeasureableUnit u) {
        return new QuantityDTO(v, u);
    }

    // ── Compare ───────────────────────────────────────────────────────────────

    @Test
    public void testIntegration_Compare_FeetAndInches_Equal_PersistsToDatabase() {
        boolean result = controller.performComparison(
            q(1.0, LengthUnit.FEET), q(12.0, LengthUnit.INCHES));
        assertTrue(result);
        assertEquals(1, repository.count());
        List<QuantityMeasurementEntity> stored = repository.findByOperationType("COMPARE");
        assertEquals(1, stored.size());
        assertFalse(stored.get(0).hasError());
    }

    @Test
    public void testIntegration_Compare_KilogramsAndGrams_Equal() {
        boolean result = controller.performComparison(
            q(1.0, WeightUnit.KILOGRAMS), q(1000.0, WeightUnit.GRAMS));
        assertTrue(result);
    }

    @Test
    public void testIntegration_Compare_DifferentCategories_ReturnsFalse_PersistsError() {
        // Controller swallows the exception and returns false
        boolean result = controller.performComparison(
            q(1.0, LengthUnit.FEET), q(1.0, WeightUnit.KILOGRAMS));
        assertFalse(result);
        // Error entity saved
        assertEquals(1, repository.count());
        assertTrue(repository.findAll().get(0).hasError());
    }

    // ── Convert ───────────────────────────────────────────────────────────────

    @Test
    public void testIntegration_Convert_FeetToInches_PersistsResult() {
        QuantityDTO result = controller.performConversion(
            q(1.0, LengthUnit.FEET), q(0.0, LengthUnit.INCHES));
        assertEquals(12.0, result.getValue(), 0.01);
        assertEquals(1, repository.count());
        List<QuantityMeasurementEntity> stored = repository.findByOperationType("CONVERT");
        assertEquals("FEET", stored.get(0).getOperand1Unit());
    }

    @Test
    public void testIntegration_Convert_CelsiusToFahrenheit_PersistsResult() {
        QuantityDTO result = controller.performConversion(
            q(100.0, TemperatureUnit.CELSIUS), q(0.0, TemperatureUnit.FAHRENHEIT));
        assertEquals(212.0, result.getValue(), 0.01);
        assertEquals(1, repository.count());
    }

    @Test
    public void testIntegration_Convert_GallonsToLiters_PersistsResult() {
        QuantityDTO result = controller.performConversion(
            q(1.0, VolumeUnit.GALLONS), q(0.0, VolumeUnit.LITERS));
        assertEquals(3.78541, result.getValue(), 0.001);
    }

    // ── Add ───────────────────────────────────────────────────────────────────

    @Test
    public void testIntegration_Add_FeetAndInches_PersistsResult() {
        QuantityDTO result = controller.performAddition(
            q(1.0, LengthUnit.FEET), q(12.0, LengthUnit.INCHES), q(0.0, LengthUnit.FEET));
        assertEquals(2.0, result.getValue(), 0.01);
        assertEquals(1, repository.count());
    }

    @Test
    public void testIntegration_Add_KilogramsAndGrams_PersistsResult() {
        QuantityDTO result = controller.performAddition(
            q(1.0, WeightUnit.KILOGRAMS), q(1000.0, WeightUnit.GRAMS), q(0.0, WeightUnit.KILOGRAMS));
        assertEquals(2.0, result.getValue(), 0.01);
    }

    @Test
    public void testIntegration_Add_Temperature_PersistsError() {
        controller.performAddition(
            q(100.0, TemperatureUnit.CELSIUS),
            q(50.0,  TemperatureUnit.CELSIUS),
            q(0.0,   TemperatureUnit.CELSIUS));
        assertEquals(1, repository.count());
        assertTrue(repository.findAll().get(0).hasError());
    }

    // ── Subtract ─────────────────────────────────────────────────────────────

    @Test
    public void testIntegration_Subtract_FeetMinusInches_PersistsResult() {
        QuantityDTO result = controller.performSubtraction(
            q(10.0, LengthUnit.FEET), q(6.0, LengthUnit.INCHES), q(0.0, LengthUnit.FEET));
        assertEquals(9.5, result.getValue(), 0.01);
        assertEquals(1, repository.count());
    }

    // ── Divide ────────────────────────────────────────────────────────────────

    @Test
    public void testIntegration_Divide_FeetByFeet_PersistsRatio() {
        double ratio = controller.performDivision(
            q(10.0, LengthUnit.FEET), q(2.0, LengthUnit.FEET));
        assertEquals(5.0, ratio, 0.001);
        assertEquals(1, repository.count());
        assertEquals("DIVIDE", repository.findAll().get(0).getOperationType());
    }

    @Test
    public void testIntegration_Divide_InchesAndFeet_CrossUnit_PersistsRatio() {
        // 24 inches / 2 feet → both convert to feet → 2.0 / 2.0 = 1.0
        double ratio = controller.performDivision(
            q(24.0, LengthUnit.INCHES), q(2.0, LengthUnit.FEET));
        assertEquals(1.0, ratio, 0.001);
    }

    // ── Multiple operations accumulate ────────────────────────────────────────

    @Test
    public void testIntegration_MultipleOperations_AllPersisted() {
        controller.performComparison(q(1.0, LengthUnit.FEET),       q(12.0, LengthUnit.INCHES));
        controller.performConversion(q(1.0, LengthUnit.FEET),       q(0.0,  LengthUnit.INCHES));
        controller.performAddition(  q(1.0, LengthUnit.FEET), q(12.0, LengthUnit.INCHES), q(0.0, LengthUnit.FEET));
        controller.performSubtraction(q(10.0, LengthUnit.FEET), q(6.0, LengthUnit.INCHES), q(0.0, LengthUnit.FEET));
        controller.performDivision(  q(10.0, LengthUnit.FEET),      q(2.0,  LengthUnit.FEET));

        assertEquals(5, repository.count());
    }

    // ── findByOperationType ───────────────────────────────────────────────────

    @Test
    public void testIntegration_FindByOperationType_FilterWorks() {
        controller.performComparison(q(1.0, LengthUnit.FEET),  q(12.0, LengthUnit.INCHES));
        controller.performConversion(q(1.0, LengthUnit.FEET),  q(0.0,  LengthUnit.INCHES));
        controller.performConversion(q(3.0, LengthUnit.YARDS), q(0.0,  LengthUnit.FEET));

        List<QuantityMeasurementEntity> converts = repository.findByOperationType("CONVERT");
        assertEquals(2, converts.size());
    }

    // ── findByMeasurementType ─────────────────────────────────────────────────

    @Test
    public void testIntegration_FindByMeasurementType_LengthAndWeight_Separated() {
        controller.performConversion(q(1.0, LengthUnit.FEET),       q(0.0, LengthUnit.INCHES));
        controller.performConversion(q(1.0, WeightUnit.KILOGRAMS),  q(0.0, WeightUnit.POUNDS));
        controller.performConversion(q(1.0, VolumeUnit.LITERS),     q(0.0, VolumeUnit.MILLILITERS));

        List<QuantityMeasurementEntity> lengths  = repository.findByMeasurementType("LengthUnit");
        List<QuantityMeasurementEntity> weights  = repository.findByMeasurementType("WeightUnit");
        List<QuantityMeasurementEntity> volumes  = repository.findByMeasurementType("VolumeUnit");

        assertEquals(1, lengths.size());
        assertEquals(1, weights.size());
        assertEquals(1, volumes.size());
    }

    // ── Pool statistics ───────────────────────────────────────────────────────

    @Test
    public void testIntegration_PoolStatistics_AvailableAfterOperations() {
        controller.performComparison(q(1.0, LengthUnit.FEET), q(12.0, LengthUnit.INCHES));
        String stats = repository.getPoolStatistics();
        assertNotNull(stats);
        // Pool should have returned connections after operations
        assertTrue(stats.contains("ConnectionPool"));
    }
}
