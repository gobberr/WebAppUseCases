package com.webapp.backend.database;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class ConnectionMonitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionMonitor.class);

    @Autowired
    private HikariDataSource hikariDataSource;

    @Scheduled(fixedDelay = 10_000) // Adjust every 10 seconds
    public void adjustPoolSize() {
        int activeConnections = hikariDataSource.getHikariPoolMXBean().getActiveConnections();
        int waitingThreads = hikariDataSource.getHikariPoolMXBean().getThreadsAwaitingConnection();
        int currentMaxPoolSize = hikariDataSource.getMaximumPoolSize();

        LOGGER.info("Active connections: {}, Waiting threads: {}, Current max pool size: {}",
                activeConnections, waitingThreads, currentMaxPoolSize);

        if (waitingThreads > Math.max(2, currentMaxPoolSize / 10)) {
            int newMaxPoolSize = Math.min(currentMaxPoolSize + 10, 100); // Maximum at 100
            hikariDataSource.setMaximumPoolSize(newMaxPoolSize);
            hikariDataSource.setMinimumIdle(newMaxPoolSize / 2);
            LOGGER.info("Increased max pool size to {}", newMaxPoolSize);
        } else if (currentMaxPoolSize > 10 && activeConnections < currentMaxPoolSize / 2) {
            int newMaxPoolSize = Math.max(currentMaxPoolSize - 10, 10); // Minimum at 10
            hikariDataSource.setMaximumPoolSize(newMaxPoolSize);
            hikariDataSource.setMinimumIdle(newMaxPoolSize / 2);
            LOGGER.info("Decreased max pool size to {}", newMaxPoolSize);
        }
    }
}
