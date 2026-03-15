package com.app.repository;

import com.app.entity.QuantityMeasurementEntity;
import com.app.exception.QuantityMeasurementException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuantityMeasurementH2Repository implements IQuantityMeasurementRepository {

    // Database mode

    public enum DbMode { IN_MEMORY, FILE }

    private static final String FILE_URL       = "jdbc:h2:./quantity_db;AUTO_SERVER=TRUE";
    private static final String DB_USER        = "sa";
    private static final String DB_PASSWORD    = "";

    // DDL

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

    // DML 

    private static final String INSERT_SQL =
        "INSERT INTO QUANTITY_MEASUREMENTS " +
        "(OPERATION_TYPE, OPERAND1_VALUE, OPERAND1_UNIT, OPERAND2_VALUE, OPERAND2_UNIT, " +
        " RESULT_VALUE, RESULT_UNIT, HAS_ERROR, ERROR_MESSAGE) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SELECT_ALL_SQL =
        "SELECT OPERATION_TYPE, OPERAND1_VALUE, OPERAND1_UNIT, OPERAND2_VALUE, OPERAND2_UNIT, " +
        "       RESULT_VALUE, RESULT_UNIT, HAS_ERROR, ERROR_MESSAGE, CREATED_AT " +
        "FROM QUANTITY_MEASUREMENTS ORDER BY ID";

    private static final String SELECT_BY_ID_SQL =
        "SELECT OPERATION_TYPE, OPERAND1_VALUE, OPERAND1_UNIT, OPERAND2_VALUE, OPERAND2_UNIT, " +
        "       RESULT_VALUE, RESULT_UNIT, HAS_ERROR, ERROR_MESSAGE, CREATED_AT " +
        "FROM QUANTITY_MEASUREMENTS ORDER BY ID " +
        "OFFSET ? ROWS FETCH FIRST 1 ROW ONLY";

    private static final String COUNT_SQL =
        "SELECT COUNT(*) FROM QUANTITY_MEASUREMENTS";

    private static final String DELETE_ALL_SQL =
        "DELETE FROM QUANTITY_MEASUREMENTS";

    // Singleton

    private static volatile QuantityMeasurementH2Repository instance;
    // Incrementing counter ensures each reset gets a brand-new in-memory DB name,
    // preventing H2's named in-memory databases from persisting across test resets.
    private static volatile int dbCounter = 0;

    private final Connection connection;

    private QuantityMeasurementH2Repository(DbMode mode) {
        String url;
        if (mode == DbMode.FILE) {
            url = FILE_URL;
        } else {
            url = "jdbc:h2:mem:quantity_db_" + dbCounter + ";DB_CLOSE_DELAY=-1";
        }
        try {
            Class.forName("org.h2.Driver");
            this.connection = DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
            this.connection.setAutoCommit(true);
            initSchema();
        } catch (ClassNotFoundException e) {
            throw new QuantityMeasurementException("Database initialization failed: H2 JDBC driver not found on classpath", e);
        } catch (SQLException e) {
            throw new QuantityMeasurementException("Database initialization failed: " + e.getMessage(), e);
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

    // Schema creation 

    private void initSchema() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_TABLE_SQL);
        } catch (SQLException e) {
            throw new QuantityMeasurementException("Database initialization failed: " + e.getMessage(), e);
        }
    }

    // IQuantityMeasurementRepository 

    @Override
    public synchronized void save(QuantityMeasurementEntity entity) {
        if (entity == null) return;
        try (PreparedStatement ps = connection.prepareStatement(INSERT_SQL)) {
            ps.setString (1, entity.getOperationType());
            ps.setString (2, entity.getOperand1Value());
            ps.setString (3, entity.getOperand1Unit());
            ps.setString (4, entity.getOperand2Value());   // nullable
            ps.setString (5, entity.getOperand2Unit());    // nullable
            ps.setString (6, entity.getResultValue());     // nullable
            ps.setString (7, entity.getResultUnit());      // nullable
            ps.setBoolean(8, entity.hasError());
            ps.setString (9, entity.getErrorMessage());    // nullable
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new QuantityMeasurementException("Failed to save measurement to database: " + e.getMessage(), e);
        }
    }

    @Override
    public synchronized List<QuantityMeasurementEntity> findAll() {
        List<QuantityMeasurementEntity> results = new ArrayList<>();
        try (Statement stmt    = connection.createStatement();
             ResultSet rs      = stmt.executeQuery(SELECT_ALL_SQL)) {
            while (rs.next()) {
                results.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new QuantityMeasurementException("Failed to query measurements from database: " + e.getMessage(), e);
        }
        return results;
    }

    @Override
    public synchronized QuantityMeasurementEntity findById(int id) {
        try (PreparedStatement ps = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new QuantityMeasurementException("Failed to query measurements from database: " + e.getMessage(), e);
        }
        throw new QuantityMeasurementException("No measurement entity found at index " + id);
    }

    @Override
    public synchronized void clear() {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(DELETE_ALL_SQL);
        } catch (SQLException e) {
            throw new QuantityMeasurementException("Failed to clear measurements from database: " + e.getMessage(), e);
        }
    }

    @Override
    public synchronized int count() {
        try (Statement stmt = connection.createStatement();
             ResultSet rs   = stmt.executeQuery(COUNT_SQL)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new QuantityMeasurementException("Failed to query measurements from database: " + e.getMessage(), e);
        }
        return 0;
    }

    // Row mapper 

    private QuantityMeasurementEntity mapRow(ResultSet rs) throws SQLException {
        String  operationType  = rs.getString ("OPERATION_TYPE");
        String  operand1Value  = rs.getString ("OPERAND1_VALUE");
        String  operand1Unit   = rs.getString ("OPERAND1_UNIT");
        String  operand2Value  = rs.getString ("OPERAND2_VALUE");
        String  operand2Unit   = rs.getString ("OPERAND2_UNIT");
        String  resultValue    = rs.getString ("RESULT_VALUE");
        String  resultUnit     = rs.getString ("RESULT_UNIT");
        boolean hasError       = rs.getBoolean("HAS_ERROR");
        String  errorMessage   = rs.getString ("ERROR_MESSAGE");
        long    createdAt      = rs.getTimestamp("CREATED_AT").getTime();

        QuantityMeasurementEntity entity;
        if (hasError) {
            entity = new QuantityMeasurementEntity(
                operationType, operand1Value, operand1Unit,
                operand2Value, operand2Unit, errorMessage);
        } else if (operand2Value != null) {
            // binary operand — parse stored string values back to double
            entity = new QuantityMeasurementEntity(
                operationType,
                parseDouble(operand1Value), operand1Unit,
                parseDouble(operand2Value), operand2Unit,
                parseDouble(resultValue),   resultUnit);
        } else {
            entity = new QuantityMeasurementEntity(
                operationType,
                parseDouble(operand1Value), operand1Unit,
                parseDouble(resultValue),   resultUnit);
        }
        return entity;
    }

    private double parseDouble(String s) {
        if (s == null) return 0.0;
        try { return Double.parseDouble(s); }
        catch (NumberFormatException e) { return 0.0; }
    }

    // Utility: expose connection for tests 

    public Connection getConnection() { return connection; }
}
