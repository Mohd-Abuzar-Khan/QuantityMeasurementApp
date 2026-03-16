package com.app.repository;

import com.app.entity.QuantityMeasurementEntity;
import com.app.exception.DatabaseException;
import com.app.util.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class QuantityMeasurementDatabaseRepository implements IQuantityMeasurementRepository {

    private static final Logger log = LoggerFactory.getLogger(QuantityMeasurementDatabaseRepository.class);

    private static final String CREATE_TABLE_SQL =
        "CREATE TABLE IF NOT EXISTS QUANTITY_MEASUREMENTS (" +
        "  ID             BIGINT AUTO_INCREMENT PRIMARY KEY, " +
        "  OPERATION_TYPE VARCHAR(20)  NOT NULL, " +
        "  OPERAND1_VALUE VARCHAR(50), " +
        "  OPERAND1_UNIT  VARCHAR(50), " +
        "  OPERAND2_VALUE VARCHAR(50), " +
        "  OPERAND2_UNIT  VARCHAR(50), " +
        "  RESULT_VALUE   VARCHAR(50), " +
        "  RESULT_UNIT    VARCHAR(50), " +
        "  HAS_ERROR      BOOLEAN      NOT NULL DEFAULT FALSE, " +
        "  ERROR_MESSAGE  VARCHAR(500), " +
        "  CREATED_AT     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP" +
        ")";

    private static final String CREATE_IDX_OP   =
        "CREATE INDEX IF NOT EXISTS IDX_QM_OPERATION ON QUANTITY_MEASUREMENTS (OPERATION_TYPE)";
    private static final String CREATE_IDX_UNIT  =
        "CREATE INDEX IF NOT EXISTS IDX_QM_UNIT1 ON QUANTITY_MEASUREMENTS (OPERAND1_UNIT)";

    private static final String INSERT_SQL =
        "INSERT INTO QUANTITY_MEASUREMENTS " +
        "(OPERATION_TYPE,OPERAND1_VALUE,OPERAND1_UNIT,OPERAND2_VALUE,OPERAND2_UNIT," +
        " RESULT_VALUE,RESULT_UNIT,HAS_ERROR,ERROR_MESSAGE) " +
        "VALUES (?,?,?,?,?,?,?,?,?)";

    private static final String SELECT_ALL_SQL =
        "SELECT OPERATION_TYPE,OPERAND1_VALUE,OPERAND1_UNIT,OPERAND2_VALUE,OPERAND2_UNIT," +
        "       RESULT_VALUE,RESULT_UNIT,HAS_ERROR,ERROR_MESSAGE,CREATED_AT " +
        "FROM QUANTITY_MEASUREMENTS ORDER BY ID";

    private static final String SELECT_BY_IDX_SQL =
        "SELECT OPERATION_TYPE,OPERAND1_VALUE,OPERAND1_UNIT,OPERAND2_VALUE,OPERAND2_UNIT," +
        "       RESULT_VALUE,RESULT_UNIT,HAS_ERROR,ERROR_MESSAGE,CREATED_AT " +
        "FROM QUANTITY_MEASUREMENTS ORDER BY ID " +
        "OFFSET ? ROWS FETCH FIRST 1 ROW ONLY";

    private static final String SELECT_BY_OP_SQL =
        "SELECT OPERATION_TYPE,OPERAND1_VALUE,OPERAND1_UNIT,OPERAND2_VALUE,OPERAND2_UNIT," +
        "       RESULT_VALUE,RESULT_UNIT,HAS_ERROR,ERROR_MESSAGE,CREATED_AT " +
        "FROM QUANTITY_MEASUREMENTS WHERE UPPER(OPERATION_TYPE)=UPPER(?) ORDER BY ID";

    // Measurement-type filter: match rows whose first-operand unit name ends with
    // the category string, e.g. "FEET" ends with the pattern for "LengthUnit"
    // by checking against a helper lookup table we build in memory.
    // Simpler: we store unit names (FEET, INCHES, …) and filter in Java after
    // fetching by operation – but that's wasteful. Instead we rely on the unit
    // name being unique to its category and do a LIKE match on a sub-string map.
    // The cleanest DB approach: add a MEASUREMENT_TYPE column. We don't have that
    // in the current entity, so we derive it from the unit name set at Java level.
    private static final String SELECT_ALL_FOR_TYPE_FILTER =
        "SELECT OPERATION_TYPE,OPERAND1_VALUE,OPERAND1_UNIT,OPERAND2_VALUE,OPERAND2_UNIT," +
        "       RESULT_VALUE,RESULT_UNIT,HAS_ERROR,ERROR_MESSAGE,CREATED_AT " +
        "FROM QUANTITY_MEASUREMENTS ORDER BY ID";

    private static final String COUNT_SQL   = "SELECT COUNT(*) FROM QUANTITY_MEASUREMENTS";
    private static final String DELETE_SQL  = "DELETE FROM QUANTITY_MEASUREMENTS";

    private static volatile QuantityMeasurementDatabaseRepository instance;

    private final ConnectionPool pool;

    private QuantityMeasurementDatabaseRepository(ConnectionPool pool) {
        this.pool = pool;
        log.info("Initialising QuantityMeasurementDatabaseRepository");
        initSchema();
        log.info("Schema initialised successfully");
    }

    public static synchronized QuantityMeasurementDatabaseRepository getInstance() {
        if (instance == null) {
            instance = new QuantityMeasurementDatabaseRepository(ConnectionPool.getInstance());
        }
        return instance;
    }

    /** Package-visible reset used by tests. */
    public static synchronized void resetInstance() {
        instance = null;
    }

    private void initSchema() {
        Connection conn = pool.acquire();
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(CREATE_TABLE_SQL);
            stmt.execute(CREATE_IDX_OP);
            stmt.execute(CREATE_IDX_UNIT);
            log.debug("DDL executed: tables and indexes verified");
        } catch (SQLException e) {
            throw new DatabaseException("INIT_SCHEMA", "Failed to initialise schema: " + e.getMessage(), e);
        } finally {
            pool.release(conn);
        }
    }


    @Override
    public void save(QuantityMeasurementEntity entity) {
        if (entity == null) {
            log.warn("save() called with null entity – skipping");
            return;
        }
        Connection conn = pool.acquire();
        try (PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {
            ps.setString (1, entity.getOperationType());
            ps.setString (2, entity.getOperand1Value());
            ps.setString (3, entity.getOperand1Unit());
            ps.setString (4, entity.getOperand2Value());
            ps.setString (5, entity.getOperand2Unit());
            ps.setString (6, entity.getResultValue());
            ps.setString (7, entity.getResultUnit());
            ps.setBoolean(8, entity.hasError());
            ps.setString (9, entity.getErrorMessage());
            ps.executeUpdate();
            log.debug("Saved entity: {}", entity);
        } catch (SQLException e) {
            throw new DatabaseException("SAVE", "Failed to save entity: " + e.getMessage(), e);
        } finally {
            pool.release(conn);
        }
    }

    @Override
    public List<QuantityMeasurementEntity> findAll() {
        log.debug("findAll() invoked");
        Connection conn = pool.acquire();
        List<QuantityMeasurementEntity> results = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(SELECT_ALL_SQL)) {
            while (rs.next()) results.add(mapRow(rs));
            log.debug("findAll() returned {} records", results.size());
        } catch (SQLException e) {
            throw new DatabaseException("FIND_ALL", "Failed to query measurements: " + e.getMessage(), e);
        } finally {
            pool.release(conn);
        }
        return results;
    }

    @Override
    public QuantityMeasurementEntity findById(int id) {
        log.debug("findById({})", id);
        Connection conn = pool.acquire();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_IDX_SQL)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new DatabaseException("FIND_BY_ID", "Failed to query by id: " + e.getMessage(), e);
        } finally {
            pool.release(conn);
        }
        throw new DatabaseException("FIND_BY_ID", "No measurement entity found at index " + id);
    }

    @Override
    public List<QuantityMeasurementEntity> findByOperationType(String operationType) {
        if (operationType == null || operationType.isBlank())
            throw new DatabaseException("FIND_BY_OP", "operationType must not be blank");
        log.debug("findByOperationType({})", operationType);
        Connection conn = pool.acquire();
        List<QuantityMeasurementEntity> results = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_OP_SQL)) {
            ps.setString(1, operationType.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) results.add(mapRow(rs));
            }
            log.debug("findByOperationType({}) returned {} records", operationType, results.size());
        } catch (SQLException e) {
            throw new DatabaseException("FIND_BY_OP",
                "Failed to query by operation type: " + e.getMessage(), e);
        } finally {
            pool.release(conn);
        }
        return results;
    }


    @Override
    public List<QuantityMeasurementEntity> findByMeasurementType(String measurementType) {
        if (measurementType == null || measurementType.isBlank())
            throw new DatabaseException("FIND_BY_TYPE", "measurementType must not be blank");
        log.debug("findByMeasurementType({})", measurementType);

        List<QuantityMeasurementEntity> all     = findAll();
        List<QuantityMeasurementEntity> results = new ArrayList<>();
        for (QuantityMeasurementEntity e : all) {
            if (unitBelongsToType(e.getOperand1Unit(), measurementType)) {
                results.add(e);
            }
        }
        log.debug("findByMeasurementType({}) returned {} records", measurementType, results.size());
        return results;
    }

    @Override
    public void clear() {
        log.info("Clearing all measurement records");
        Connection conn = pool.acquire();
        try (Statement stmt = conn.createStatement()) {
            int deleted = stmt.executeUpdate(DELETE_SQL);
            log.info("Deleted {} records", deleted);
        } catch (SQLException e) {
            throw new DatabaseException("CLEAR", "Failed to clear measurements: " + e.getMessage(), e);
        } finally {
            pool.release(conn);
        }
    }

    @Override
    public int count() {
        Connection conn = pool.acquire();
        try (Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(COUNT_SQL)) {
            if (rs.next()) {
                int c = rs.getInt(1);
                log.debug("count() = {}", c);
                return c;
            }
        } catch (SQLException e) {
            throw new DatabaseException("COUNT", "Failed to count measurements: " + e.getMessage(), e);
        } finally {
            pool.release(conn);
        }
        return 0;
    }

    @Override
    public String getPoolStatistics() {
        return pool.getStatistics();
    }

    @Override
    public void releaseResources() {
        log.info("Releasing database repository resources");
        pool.closeAll();
    }

    // ── Row mapper ────────────────────────────────────────────────────────────
    private QuantityMeasurementEntity mapRow(ResultSet rs) throws SQLException {
        String  operationType = rs.getString ("OPERATION_TYPE");
        String  op1Value      = rs.getString ("OPERAND1_VALUE");
        String  op1Unit       = rs.getString ("OPERAND1_UNIT");
        String  op2Value      = rs.getString ("OPERAND2_VALUE");
        String  op2Unit       = rs.getString ("OPERAND2_UNIT");
        String  resultValue   = rs.getString ("RESULT_VALUE");
        String  resultUnit    = rs.getString ("RESULT_UNIT");
        boolean hasError      = rs.getBoolean("HAS_ERROR");
        String  errorMessage  = rs.getString ("ERROR_MESSAGE");

        if (hasError) {
            return new QuantityMeasurementEntity(
                operationType, op1Value, op1Unit, op2Value, op2Unit, errorMessage);
        }
        if (op2Value != null) {
            return new QuantityMeasurementEntity(
                operationType,
                toDouble(op1Value), op1Unit,
                toDouble(op2Value), op2Unit,
                toDouble(resultValue), resultUnit);
        }
        return new QuantityMeasurementEntity(
            operationType,
            toDouble(op1Value), op1Unit,
            toDouble(resultValue), resultUnit);
    }

    private double toDouble(String s) {
        if (s == null) return 0.0;
        try { return Double.parseDouble(s); }
        catch (NumberFormatException e) { return 0.0; }
    }

    // ── Unit-to-category helper ───────────────────────────────────────────────
    private static final java.util.Set<String> LENGTH_UNITS =
        new java.util.HashSet<>(java.util.Arrays.asList(
            "FEET","INCHES","YARDS","CENTIMETERS","METERS"));

    private static final java.util.Set<String> WEIGHT_UNITS =
        new java.util.HashSet<>(java.util.Arrays.asList(
            "GRAM","KILOGRAM","POUND","OUNCE","TONNE",
            // DTO names
            "GRAMS","KILOGRAMS","POUNDS","OUNCES","TONNES"));

    private static final java.util.Set<String> VOLUME_UNITS =
        new java.util.HashSet<>(java.util.Arrays.asList(
            "LITRE","MILLILITRE","GALLON",
            // DTO names
            "LITERS","MILLILITERS","GALLONS"));

    private static final java.util.Set<String> TEMPERATURE_UNITS =
        new java.util.HashSet<>(java.util.Arrays.asList(
            "CELSIUS","FAHRENHEIT","KELVIN"));

    private boolean unitBelongsToType(String unitName, String measurementType) {
        if (unitName == null) return false;
        String u = unitName.toUpperCase();
        switch (measurementType) {
            case "LengthUnit":      return LENGTH_UNITS.contains(u);
            case "WeightUnit":      return WEIGHT_UNITS.contains(u);
            case "VolumeUnit":      return VOLUME_UNITS.contains(u);
            case "TemperatureUnit": return TEMPERATURE_UNITS.contains(u);
            default: return false;
        }
    }
}
