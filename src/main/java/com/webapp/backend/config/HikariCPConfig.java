package com.webapp.backend.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class HikariCPConfig {

    @Value("${db.jdbcUrl:jdbc:mysql://localhost:3306/mydb}")
    private String jdbcUrl;

    @Value("${db.username:dbuser}")
    private String username;

    @Value("${db.password:dbpassword}")
    private String password;

    @Value("${db.maximumPoolSize:20}")
    private int maximumPoolSize;

    @Value("${db.minimumIdle:5}")
    private int minimumIdle;

    @Value("${db.connectionTimeout:30000}")
    private long connectionTimeout;

    @Value("${db.idleTimeout:600000}")
    private long idleTimeout;

    @Value("${db.maxLifetime:1800000}")
    private long maxLifetime;

    private HikariDataSource ds;

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(maximumPoolSize);
        config.setMinimumIdle(minimumIdle);
        config.setConnectionTimeout(connectionTimeout);
        config.setIdleTimeout(idleTimeout);
        config.setMaxLifetime(maxLifetime);
        ds = new HikariDataSource(config);
        return ds;
    }

    @PreDestroy
    public void close() {
        if (ds != null && !ds.isClosed()) ds.close();
    }
}
