package com.webapp.backend.controller;

import com.webapp.backend.database.DatabaseManager;
import com.zaxxer.hikari.HikariDataSource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.SQLException;

@RestController
@RequestMapping("/api/v1/database")
@Tag(name = "Database Management", description = "APIs for playing with DatabaseManager")
public class DatabaseController {

    @Autowired
    private DatabaseManager databaseManager;

    @Autowired
    private HikariDataSource hikariDataSource;

    @GetMapping("/test-connection")
    @Operation(summary = "Test database connection")
    public ResponseEntity<String> testConnection() {
        try (Connection connection = databaseManager.getConnection()) {
            if (connection != null && !connection.isClosed()) {
                return ResponseEntity.ok("Database connection is successful!");
            }
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Failed to connect to the database: " + e.getMessage());
        }
        return ResponseEntity.status(500).body("Database connection could not be established.");
    }

    @GetMapping("/pool-status")
    @Operation(summary = "Get connection pool status")
    public ResponseEntity<String> getPoolStatus() {
        int activeConnections = hikariDataSource.getHikariPoolMXBean().getActiveConnections();
        int idleConnections = hikariDataSource.getHikariPoolMXBean().getIdleConnections();
        int totalConnections = hikariDataSource.getHikariPoolMXBean().getTotalConnections();
        int waitingThreads = hikariDataSource.getHikariPoolMXBean().getThreadsAwaitingConnection();

        String status = String.format(
                "Active connections: %d, Idle connections: %d, Total connections: %d, Waiting threads: %d",
                activeConnections, idleConnections, totalConnections, waitingThreads
        );

        return ResponseEntity.ok(status);
    }

}