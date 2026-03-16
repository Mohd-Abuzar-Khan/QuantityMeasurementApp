package com.app.repository;

import com.app.entity.QuantityMeasurementEntity;
import com.app.exception.DatabaseException;
import com.app.util.ApplicationConfig;
import com.app.util.ConnectionPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link QuantityMeasurementDatabaseRepository}.
 * Uses H2 in-memory database — no external database required.
 */
public class QuantityMeasurementDatabaseRepositoryTest {

    private QuantityMeasurementDatabaseRepository repository;

    @Before
    public void setUp() {
        // Point to a fresh in-memory H2 database for each test
        System.setProperty("db.url", "jdbc:h2:mem:test_db_repo;DB_CLOSE_DELAY=-1");
        System.setProperty("db.driver", "org.h2.Driver");
        System.setProperty("db.username", "sa");
        System.setProperty("db.password", "");
        System.setProperty("pool.size", "5");
        System.setProperty("repository.type", "database");

        // Reset singletons so each test gets a clean instance
        QuantityMeasurementDatabaseRepository.resetInstance();
        ConnectionPool.resetInstance();

        repository = QuantityMeasurementDatabaseRepository.getInstance();
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

    // ── save & findAll ────────────────────────────────────────────────────────

    @Test
    public void testSave_SingleOperandEntity_PersistsCorrectly() {
        QuantityMeasurementEntity entity = new QuantityMeasurementEntity(
            "CONVERT", 1.0, "FEET", 12.0, "INCHES");
        repository.save(entity);

        List<QuantityMeasurementEntity> all = repository.findAll();
        assertEquals(1, all.size());
        assertEquals("CONVERT", all.get(0).getOperationType());
        assertEquals("FEET",    all.get(0).getOperand1Unit());
    }

    @Test
    public void testSave_TwoOperandEntity_PersistsCorrectly() {
        QuantityMeasurementEntity entity = new QuantityMeasurementEntity(
            "ADD", 1.0, "FEET", 12.0, "INCHES", 2.0, "FEET");
        repository.save(entity);

        List<QuantityMeasurementEntity> all = repository.findAll();
        assertEquals(1, all.size());
        assertEquals("ADD",    all.get(0).getOperationType());
        assertEquals("INCHES", all.get(0).getOperand2Unit());
    }

    @Test
    public void testSave_ErrorEntity_PersistsWithError() {
        QuantityMeasurementEntity entity = new QuantityMeasurementEntity(
            "ADD", "100.0", "CELSIUS", "50.0", "CELSIUS", "Temperature does not support addition");
        repository.save(entity);

        List<QuantityMeasurementEntity> all = repository.findAll();
        assertEquals(1, all.size());
        assertTrue(all.get(0).hasError());
        assertNotNull(all.get(0).getErrorMessage());
    }

    @Test
    public void testSave_NullEntity_DoesNotThrow() {
        repository.save(null);   // should silently skip
        assertEquals(0, repository.count());
    }

    // ── count ─────────────────────────────────────────────────────────────────

    @Test
    public void testCount_EmptyRepository_ReturnsZero() {
        assertEquals(0, repository.count());
    }

    @Test
    public void testCount_AfterMultipleSaves_ReturnsCorrectCount() {
        repository.save(new QuantityMeasurementEntity("COMPARE", 1.0, "FEET", 1.0, "FEET", 1.0, "BOOLEAN"));
        repository.save(new QuantityMeasurementEntity("CONVERT", 1.0, "FEET", 12.0, "INCHES"));
        repository.save(new QuantityMeasurementEntity("ADD",     1.0, "FEET", 12.0, "INCHES", 2.0, "FEET"));
        assertEquals(3, repository.count());
    }

    // ── clear ─────────────────────────────────────────────────────────────────

    @Test
    public void testClear_RemovesAllRecords() {
        repository.save(new QuantityMeasurementEntity("COMPARE", 1.0, "FEET", 1.0, "FEET", 1.0, "BOOLEAN"));
        repository.save(new QuantityMeasurementEntity("CONVERT", 1.0, "FEET", 12.0, "INCHES"));
        assertTrue(repository.count() > 0);

        repository.clear();
        assertEquals(0, repository.count());
    }

    // ── findById ──────────────────────────────────────────────────────────────

    @Test
    public void testFindById_ExistingRecord_ReturnsEntity() {
        repository.save(new QuantityMeasurementEntity("CONVERT", 1.0, "FEET", 12.0, "INCHES"));
        QuantityMeasurementEntity found = repository.findById(0);
        assertNotNull(found);
        assertEquals("CONVERT", found.getOperationType());
    }

    @Test(expected = DatabaseException.class)
    public void testFindById_NonExistentIndex_ThrowsDatabaseException() {
        repository.findById(999);
    }

    // ── findByOperationType ───────────────────────────────────────────────────

    @Test
    public void testFindByOperationType_FiltersCorrectly() {
        repository.save(new QuantityMeasurementEntity("ADD",     1.0, "FEET",   12.0, "INCHES",    2.0, "FEET"));
        repository.save(new QuantityMeasurementEntity("COMPARE", 1.0, "FEET",    1.0, "FEET",      1.0, "BOOLEAN"));
        repository.save(new QuantityMeasurementEntity("ADD",     1.0, "KILOGRAM",1.0, "KILOGRAM",  2.0, "KILOGRAM"));

        List<QuantityMeasurementEntity> adds = repository.findByOperationType("ADD");
        assertEquals(2, adds.size());
        adds.forEach(e -> assertEquals("ADD", e.getOperationType()));
    }

    @Test
    public void testFindByOperationType_CaseInsensitive() {
        repository.save(new QuantityMeasurementEntity("COMPARE", 1.0, "FEET", 1.0, "FEET", 1.0, "BOOLEAN"));
        List<QuantityMeasurementEntity> found = repository.findByOperationType("compare");
        assertEquals(1, found.size());
    }

    @Test
    public void testFindByOperationType_NoMatch_ReturnsEmpty() {
        repository.save(new QuantityMeasurementEntity("CONVERT", 1.0, "FEET", 12.0, "INCHES"));
        List<QuantityMeasurementEntity> found = repository.findByOperationType("DIVIDE");
        assertTrue(found.isEmpty());
    }

    // ── findByMeasurementType ─────────────────────────────────────────────────

    @Test
    public void testFindByMeasurementType_LengthUnit_ReturnsLengthRecords() {
        repository.save(new QuantityMeasurementEntity("ADD",     1.0, "FEET",    12.0, "INCHES",   2.0, "FEET"));
        repository.save(new QuantityMeasurementEntity("COMPARE", 1.0, "KILOGRAM", 1.0, "KILOGRAM", 1.0, "BOOLEAN"));

        List<QuantityMeasurementEntity> lengths = repository.findByMeasurementType("LengthUnit");
        assertEquals(1, lengths.size());
        assertEquals("FEET", lengths.get(0).getOperand1Unit());
    }

    @Test
    public void testFindByMeasurementType_WeightUnit_ReturnsWeightRecords() {
        repository.save(new QuantityMeasurementEntity("ADD", 1.0, "KILOGRAM", 1.0, "KILOGRAM", 2.0, "KILOGRAM"));
        repository.save(new QuantityMeasurementEntity("ADD", 1.0, "FEET",     1.0, "FEET",     2.0, "FEET"));

        List<QuantityMeasurementEntity> weights = repository.findByMeasurementType("WeightUnit");
        assertEquals(1, weights.size());
    }

    @Test
    public void testFindByMeasurementType_Unknown_ReturnsEmpty() {
        repository.save(new QuantityMeasurementEntity("ADD", 1.0, "FEET", 1.0, "FEET", 2.0, "FEET"));
        List<QuantityMeasurementEntity> result = repository.findByMeasurementType("UnknownUnit");
        assertTrue(result.isEmpty());
    }

    // ── pool statistics ───────────────────────────────────────────────────────

    @Test
    public void testGetPoolStatistics_ReturnsNonNullString() {
        String stats = repository.getPoolStatistics();
        assertNotNull(stats);
        assertFalse(stats.isEmpty());
    }

    // ── large dataset ─────────────────────────────────────────────────────────

    @Test
    public void testLargeDataSet_100Entities_AllPersisted() {
        for (int i = 0; i < 100; i++) {
            repository.save(new QuantityMeasurementEntity(
                "CONVERT", (double) i, "FEET", (double) (i * 12), "INCHES"));
        }
        assertEquals(100, repository.count());
        assertEquals(100, repository.findAll().size());
    }

    // ── SQL injection prevention ──────────────────────────────────────────────

    @Test
    public void testSQLInjection_OperationType_TreatedAsLiteral() {
        // The malicious string should be treated as a literal value, not executed as SQL
        String malicious = "'; DROP TABLE QUANTITY_MEASUREMENTS; --";
        List<QuantityMeasurementEntity> result = repository.findByOperationType(malicious);
        assertTrue(result.isEmpty());
        // Table must still exist
        assertTrue(repository.count() >= 0);
    }
}
