package com.webapp.backend.memory;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.webapp.backend.utils.HeapDumpUtil;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class MemoryManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(MemoryManager.class);
    private final Cache<String, byte[]> cache;
    private final RedisTemplate<String, byte[]> redisTemplate;

    public MemoryManager(RedisTemplate<String, byte[]> redisTemplate, MeterRegistry meterRegistry) {
        this.redisTemplate = redisTemplate;
        this.cache = Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterAccess(Duration.ofMinutes(15))
                .evictionListener((k, v, cause) -> LOGGER.debug("Evicted {} cause={}", k, cause))
                .recordStats() // Enable stats collection
                .build();

        meterRegistry.gauge("cache.size", cache, c -> c.estimatedSize());
    }

    @Scheduled(fixedRate = 5000) // Runs every 5 seconds
    public void monitorAndAdjustCacheSize() {
        Runtime runtime = Runtime.getRuntime();
        long maxHeap = runtime.maxMemory();
        long usedHeap = runtime.totalMemory() - runtime.freeMemory();

        double heapUsage = (double) usedHeap / maxHeap;
        LOGGER.info("Heap usage: {}%", heapUsage * 100);

        if (heapUsage > 0.8) { // If heap usage exceeds 80%
            cache.policy().eviction().ifPresent(eviction -> {
                long newMaxSize = Math.max((long) (eviction.getMaximum() * 0.5), 100); // Reduce size by 50%, minimum 100
                eviction.setMaximum(newMaxSize);
                LOGGER.info("Cache size adjusted to: {}", newMaxSize);
            });
            try {
                HeapDumpUtil.dumpHeap("./heap-dumps/heapdump.hprof", true);
                LOGGER.info("Heap dump created due to high memory usage.");
            } catch (Exception e) {
                LOGGER.error("Failed to create heap dump.", e);
            }
        }
    }

    public void putSessionData(String sessionId, byte[] data) {
        // Store in both Level-1 (Caffeine) and Level-2 (Redis) caches
        cache.put(sessionId, data);
        try {
            redisTemplate.opsForValue().set(sessionId, data, Duration.ofMinutes(30));
        } catch (Exception e) {
            LOGGER.error("Failed to store session data in Redis for sessionId: {}", sessionId, e);
        }
    }

    public Optional<byte[]> getSessionData(String sessionId) {
        // Check Level-1 cache (Caffeine)
        byte[] data = cache.getIfPresent(sessionId);
        if (data != null) {
            return Optional.of(data);
        }

        // Fallback to Level-2 cache (Redis)
        try {
            data = redisTemplate.opsForValue().get(sessionId);
            if (data != null) {
                // Populate Level-1 cache for future requests
                cache.put(sessionId, data);
            }
            return Optional.ofNullable(data);
        } catch (Exception e) {
            LOGGER.error("Failed to retrieve session data from Redis for sessionId: {}", sessionId, e);
            return Optional.empty();
        }
    }

    public void removeSessionData(String sessionId) {
        // Remove from both Level-1 and Level-2 caches
        cache.invalidate(sessionId);
        try {
            redisTemplate.delete(sessionId);
        } catch (Exception e) {
            LOGGER.error("Failed to remove session data from Redis for sessionId: {}", sessionId, e);
        }
    }
}
