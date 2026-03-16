package com.app.repository;

import com.app.entity.QuantityMeasurementEntity;
import com.app.exception.DatabaseException;
import com.app.exception.QuantityMeasurementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class QuantityMeasurementH2Repository implements IQuantityMeasurementRepository {

    private static final Logger log = LoggerFactory.getLogger(QuantityMeasurementH2Repository.class);

    public enum DbMode { IN_MEMORY, FILE }

    private static final String FILE_URL    = "jdbc:h2:./quantity_db;AUTO_SERVER=TRUE";
    private static final String DB_USER     = "sa";
    private static final String DB_PASSWORD = "";

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

    private static final String COUNT_SQL  = "SELECT COUNT(*) FROM QUANTITY_MEASUREMENTS";
    private static final String DELETE_SQL = "DELETE FROM QUANTITY_MEASUREMENTS";

    // ── Singleton ─────────────────────────────────────────────────────────────
    private static volatile QuantityMeasurementH2Repository instance;
    private static volatile int dbCounter = 0;

    private final Connection connection;

    private QuantityMeasurementH2Repository(DbMode mode) {
        String url = (mode == DbMode.FILE)
            ? FILE_URL
            : "jdbc:h2:mem:quantity_db_" + dbCounter + ";DB_CLOSE_DELAY=-1";
        try {
            Class.forName("org.h2.Driver");
            this.connection = DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
            this.connection.setAutoCommit(true);
            initSchema();
            log.info("H2Repository initialised (mode={}, url={})", mode, url);
        } catch (ClassNotFoundException e) {
            throw new DatabaseException("INIT", "H2 JDBC driver not found", e);
        } catch (SQLException e) {
            throw new DatabaseException("INIT", "H2 connection failed: " + e.getMessage(), e);
        }
    }

    public static synchronized QuantityMeasurementH2Repository getInstance() {
        return getInstance(DbMode.IN_MEMORY);
    }

    public static synchronized QuantityMeasurementH2Repository getInstance(DbMode mode) {
        if (instance == null) {
            instance = new QuantityMeasurementH2Repository(mode);
        }
        return instance;
    }

    public static synchronized void resetInstance() {
        if (instance != null) {
            try { instance.connection.close(); } catch (SQLException ignored) {}
            instance = null;
            dbCounter++;
        }
    }

    private void initSchema() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_TABLE_SQL);
        } catch (SQLException e) {
            throw new DatabaseException("INIT_SCHEMA", e.getMessage(), e);
        }
    }

    // ── IQuantityMeasurementRepository ────────────────────────────────────────

    @Override
    public synchronized void save(QuantityMeasurementEntity entity) {
        if (entity == null) return;
        try (PreparedStatement ps = connection.prepareStatement(INSERT_SQL)) {
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
        } catch (SQLException e) {
            throw new DatabaseException("SAVE", e.getMessage(), e);
        }
    }

    @Override
    public synchronized List<QuantityMeasurementEntity> findAll() {
        List<QuantityMeasurementEntity> results = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs   = stmt.executeQuery(SELECT_ALL_SQL)) {
            while (rs.next()) results.add(mapRow(rs));
        } catch (SQLException e) {
            throw new DatabaseException("FIND_ALL", e.getMessage(), e);
        }
        return results;
    }

    @Override
    public synchronized QuantityMeasurementEntity findById(int id) {
        try (PreparedStatement ps = connection.prepareStatement(SELECT_BY_IDX_SQL)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new DatabaseException("FIND_BY_ID", e.getMessage(), e);
        }
        throw new QuantityMeasurementException("No measurement entity found at index " + id);
    }

    @Override
    public synchronized List<QuantityMeasurementEntity> findByOperationType(String operationType) {
        List<QuantityMeasurementEntity> results = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(SELECT_BY_OP_SQL)) {
            ps.setString(1, operationType);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) results.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("FIND_BY_OP", e.getMessage(), e);
        }
        return results;
    }

    @Override
    public synchronized List<QuantityMeasurementEntity> findByMeasurementType(String measurementType) {
        // Filter in-memory since there's no MEASUREMENT_TYPE column
        List<QuantityMeasurementEntity> all     = findAll();
        List<QuantityMeasurementEntity> results = new ArrayList<>();
        for (QuantityMeasurementEntity e : all) {
            if (unitBelongsToType(e.getOperand1Unit(), measurementType)) results.add(e);
        }
        return results;
    }

    @Override
    public synchronized void clear() {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(DELETE_SQL);
        } catch (SQLException e) {
            throw new DatabaseException("CLEAR", e.getMessage(), e);
        }
    }

    @Override
    public synchronized int count() {
        try (Statement stmt = connection.createStatement();
             ResultSet rs   = stmt.executeQuery(COUNT_SQL)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new DatabaseException("COUNT", e.getMessage(), e);
        }
        return 0;
    }

    public Connection getConnection() { return connection; }

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

        if (hasError)
            return new QuantityMeasurementEntity(
                operationType, op1Value, op1Unit, op2Value, op2Unit, errorMessage);
        if (op2Value != null)
            return new QuantityMeasurementEntity(
                operationType,
                toDouble(op1Value), op1Unit,
                toDouble(op2Value), op2Unit,
                toDouble(resultValue), resultUnit);
        return new QuantityMeasurementEntity(
            operationType,
            toDouble(op1Value), op1Unit,
            toDouble(resultValue), resultUnit);
    }

    private double toDouble(String s) {
        if (s == null) return 0.0;
        try { return Double.parseDouble(s); } catch (NumberFormatException e) { return 0.0; }
    }

    private boolean unitBelongsToType(String unitName, String measurementType) {
        if (unitName == null) return false;
        String u = unitName.toUpperCase();
        switch (measurementType) {
            case "LengthUnit":      return java.util.Arrays.asList("FEET","INCHES","YARDS","CENTIMETERS","METERS").contains(u);
            case "WeightUnit":      return java.util.Arrays.asList("GRAM","KILOGRAM","POUND","OUNCE","TONNE","GRAMS","KILOGRAMS","POUNDS","OUNCES","TONNES").contains(u);
            case "VolumeUnit":      return java.util.Arrays.asList("LITRE","MILLILITRE","GALLON","LITERS","MILLILITERS","GALLONS").contains(u);
            case "TemperatureUnit": return java.util.Arrays.asList("CELSIUS","FAHRENHEIT","KELVIN").contains(u);
            default: return false;
        }
    }
}
