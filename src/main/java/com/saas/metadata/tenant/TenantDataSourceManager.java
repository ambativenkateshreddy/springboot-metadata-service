package com.saas.metadata.tenant;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;
import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the lifecycle of per-tenant HikariCP DataSource pools.
 *
 * Each tenant gets its own connection pool pointed at:
 *   jdbc:mysql://{host}:{port}/{tenantId}
 *
 * Pools are created lazily on first access and cached for the application lifetime.
 * On shutdown all pools are closed cleanly.
 */
@Slf4j
@Component
public class TenantDataSourceManager {

    @Value("${tenant.datasource.host:localhost}")
    private String host;

    @Value("${tenant.datasource.port:3306}")
    private int port;

    @Value("${tenant.datasource.username}")
    private String username;

    @Value("${tenant.datasource.password}")
    private String password;

    @Value("${tenant.datasource.hikari.maximum-pool-size:5}")
    private int maxPoolSize;

    @Value("${tenant.datasource.hikari.minimum-idle:1}")
    private int minIdle;

    @Value("${tenant.datasource.hikari.connection-timeout:20000}")
    private long connectionTimeout;

    private final ConcurrentHashMap<String, HikariDataSource> tenantPools = new ConcurrentHashMap<>();

    /**
     * Returns the DataSource for a given tenant, creating it if it doesn't exist yet.
     */
    public DataSource getDataSourceForTenant(String tenantId) {
        return tenantPools.computeIfAbsent(tenantId, this::createDataSource);
    }

    /**
     * Builds all registered tenant datasources as a map for AbstractRoutingDataSource.
     */
    public Map<Object, Object> buildTargetDataSources(Iterable<String> tenantIds) {
        Map<Object, Object> targets = new ConcurrentHashMap<>();
        for (String tenantId : tenantIds) {
            targets.put(tenantId, getDataSourceForTenant(tenantId));
            log.info("Registered datasource for tenant: {}", tenantId);
        }
        return targets;
    }

    /**
     * Adds a newly provisioned tenant datasource at runtime (no restart required).
     */
    public void addTenant(String tenantId) {
        tenantPools.computeIfAbsent(tenantId, this::createDataSource);
        log.info("Added new tenant datasource: {}", tenantId);
    }

    public boolean tenantExists(String tenantId) {
        return tenantPools.containsKey(tenantId);
    }

    private HikariDataSource createDataSource(String tenantId) {
        String jdbcUrl = String.format(
            "jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
            host, port, tenantId
        );

        HikariConfig config = new HikariConfig();
        config.setPoolName("TenantPool-" + tenantId);
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setMaximumPoolSize(maxPoolSize);
        config.setMinimumIdle(minIdle);
        config.setConnectionTimeout(connectionTimeout);
        config.setAutoCommit(false);

        // Performance tuning
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");

        log.info("Creating HikariCP pool for tenant '{}' → {}", tenantId, jdbcUrl);
        return new HikariDataSource(config);
    }

    @PreDestroy
    public void closeAll() {
        tenantPools.forEach((tenantId, ds) -> {
            log.info("Closing pool for tenant: {}", tenantId);
            ds.close();
        });
    }
}
