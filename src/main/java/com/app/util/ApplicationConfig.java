package com.app.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads database configuration from {@code application.properties} on the classpath.
 * System properties take precedence over file properties, enabling environment-specific
 * overrides at runtime (e.g. {@code -Ddb.url=jdbc:h2:mem:test}).
 */
public class ApplicationConfig {

    private static final Logger log = LoggerFactory.getLogger(ApplicationConfig.class);
    private static final String PROPERTIES_FILE = "application.properties";

    // ── Defaults ──────────────────────────────────────────────────────────────
    private static final String DEFAULT_DRIVER   = "org.h2.Driver";
    private static final String DEFAULT_URL      = "jdbc:h2:mem:quantity_db;DB_CLOSE_DELAY=-1";
    private static final String DEFAULT_USER     = "sa";
    private static final String DEFAULT_PASSWORD = "";
    private static final int    DEFAULT_POOL_SIZE          = 10;
    private static final long   DEFAULT_TIMEOUT_MS         = 30_000L;
    private static final long   DEFAULT_IDLE_TIMEOUT_MS    = 600_000L;
    private static final long   DEFAULT_MAX_WAIT_MS        = 5_000L;
    private static final String DEFAULT_REPO_TYPE          = "database";

    // ── Singleton ─────────────────────────────────────────────────────────────
    private static volatile ApplicationConfig instance;
    private final Properties props = new Properties();

    private ApplicationConfig() {
        loadProperties();
    }

    public static synchronized ApplicationConfig getInstance() {
        if (instance == null) {
            instance = new ApplicationConfig();
        }
        return instance;
    }

    // ── Load ──────────────────────────────────────────────────────────────────
    private void loadProperties() {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (in != null) {
                props.load(in);
                log.info("Loaded configuration from {}", PROPERTIES_FILE);
            } else {
                log.warn("{} not found on classpath; using built-in defaults", PROPERTIES_FILE);
            }
        } catch (IOException e) {
            log.error("Failed to load {}: {}. Using defaults.", PROPERTIES_FILE, e.getMessage());
        }
    }

    // ── Accessor helpers ──────────────────────────────────────────────────────
    private String get(String key, String defaultValue) {
        // System property takes highest precedence
        String sysVal = System.getProperty(key);
        if (sysVal != null && !sysVal.isBlank()) return sysVal;
        return props.getProperty(key, defaultValue);
    }

    private int getInt(String key, int defaultValue) {
        try { return Integer.parseInt(get(key, String.valueOf(defaultValue))); }
        catch (NumberFormatException e) { return defaultValue; }
    }

    private long getLong(String key, long defaultValue) {
        try { return Long.parseLong(get(key, String.valueOf(defaultValue))); }
        catch (NumberFormatException e) { return defaultValue; }
    }

    // ── Public accessors ──────────────────────────────────────────────────────
    public String getDbDriver()        { return get("db.driver",           DEFAULT_DRIVER);   }
    public String getDbUrl()           { return get("db.url",              DEFAULT_URL);      }
    public String getDbUsername()      { return get("db.username",         DEFAULT_USER);     }
    public String getDbPassword()      { return get("db.password",         DEFAULT_PASSWORD); }
    public int    getPoolSize()        { return getInt ("pool.size",        DEFAULT_POOL_SIZE);       }
    public long   getPoolTimeoutMs()   { return getLong("pool.timeout.ms", DEFAULT_TIMEOUT_MS);      }
    public long   getIdleTimeoutMs()   { return getLong("pool.idle.timeout.ms", DEFAULT_IDLE_TIMEOUT_MS); }
    public long   getMaxWaitMs()       { return getLong("pool.max.wait.ms",    DEFAULT_MAX_WAIT_MS);  }
    public String getRepositoryType()  { return get("repository.type",     DEFAULT_REPO_TYPE); }

    /** Returns true if the application should use the database repository. */
    public boolean isDatabaseRepository() {
        return "database".equalsIgnoreCase(getRepositoryType());
    }

    @Override
    public String toString() {
        return String.format(
            "ApplicationConfig{driver='%s', url='%s', user='%s', pool=%d, repoType='%s'}",
            getDbDriver(), getDbUrl(), getDbUsername(), getPoolSize(), getRepositoryType());
    }
}
