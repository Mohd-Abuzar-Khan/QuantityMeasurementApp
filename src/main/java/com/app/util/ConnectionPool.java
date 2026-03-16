package com.app.util;

import com.app.exception.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class ConnectionPool {

    private static final Logger log = LoggerFactory.getLogger(ConnectionPool.class);

    private final BlockingQueue<Connection> pool;
    private final ApplicationConfig         config;
    private final AtomicInteger             totalCreated  = new AtomicInteger(0);
    private final AtomicInteger             totalAcquired = new AtomicInteger(0);
    private final AtomicInteger             totalReleased = new AtomicInteger(0);

    private static volatile ConnectionPool instance;

    private ConnectionPool(ApplicationConfig config) {
        this.config = config;
        int size = config.getPoolSize();
        this.pool = new ArrayBlockingQueue<>(size);
        log.info("Initializing connection pool: size={}, url={}", size, config.getDbUrl());
        initPool(size);
        log.info("Connection pool initialized with {} connections", pool.size());
    }

    public static synchronized ConnectionPool getInstance() {
        if (instance == null) {
            instance = new ConnectionPool(ApplicationConfig.getInstance());
        }
        return instance;
    }

    public static synchronized void resetInstance() {
        if (instance != null) {
            instance.closeAll();
            instance = null;
        }
    }

    private void initPool(int size) {
        try {
            Class.forName(config.getDbDriver());
        } catch (ClassNotFoundException e) {
            throw new DatabaseException("INIT", "JDBC driver not found: " + config.getDbDriver(), e);
        }
        for (int i = 0; i < size; i++) {
            pool.offer(createConnection());
        }
    }

    private Connection createConnection() {
        try {
            Connection conn = DriverManager.getConnection(
                config.getDbUrl(), config.getDbUsername(), config.getDbPassword());
            conn.setAutoCommit(true);
            totalCreated.incrementAndGet();
            log.debug("Created new connection #{}", totalCreated.get());
            return conn;
        } catch (SQLException e) {
            throw new DatabaseException("CREATE_CONNECTION", "Failed to create database connection: " + e.getMessage(), e);
        }
    }

    // ── Public API ────────────────────────────────────────────────────────────

    public Connection acquire() {
        try {
            Connection conn = pool.poll(config.getMaxWaitMs(), TimeUnit.MILLISECONDS);
            if (conn == null) {
                throw new DatabaseException("ACQUIRE",
                    "Connection pool exhausted – no connection available within "
                    + config.getMaxWaitMs() + " ms");
            }
            if (!isValid(conn)) {
                log.warn("Stale connection detected; replacing with a fresh one");
                conn = createConnection();
            }
            totalAcquired.incrementAndGet();
            log.debug("Acquired connection (pool remaining: {})", pool.size());
            return conn;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DatabaseException("ACQUIRE", "Interrupted while waiting for a connection", e);
        }
    }

    public void release(Connection conn) {
        if (conn == null) return;
        try {
            if (isValid(conn)) {
                pool.offer(conn);
            } else {
                log.warn("Discarding invalid connection; creating replacement");
                pool.offer(createConnection());
            }
            totalReleased.incrementAndGet();
            log.debug("Released connection (pool size: {})", pool.size());
        } catch (Exception e) {
            log.error("Error releasing connection: {}", e.getMessage());
        }
    }

    private boolean isValid(Connection conn) {
        try {
            return conn != null && !conn.isClosed() && conn.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }

    // ── Statistics ────────────────────────────────────────────────────────────

    public String getStatistics() {
        return String.format(
            "ConnectionPool{available=%d, capacity=%d, created=%d, acquired=%d, released=%d}",
            pool.size(), config.getPoolSize(),
            totalCreated.get(), totalAcquired.get(), totalReleased.get());
    }

    public int getAvailableConnections() { return pool.size(); }
    public int getPoolCapacity()         { return config.getPoolSize(); }
    public int getTotalCreated()         { return totalCreated.get(); }
    public int getTotalAcquired()        { return totalAcquired.get(); }
    public int getTotalReleased()        { return totalReleased.get(); }

    // ── Shutdown ──────────────────────────────────────────────────────────────

    public void closeAll() {
        log.info("Closing all connections in pool ({} available)", pool.size());
        Connection conn;
        while ((conn = pool.poll()) != null) {
            try { conn.close(); } catch (SQLException ignored) {}
        }
    }
}
