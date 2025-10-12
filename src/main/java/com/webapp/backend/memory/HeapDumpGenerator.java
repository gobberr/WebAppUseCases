package com.webapp.backend.memory;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Random;

public class HeapDumpGenerator {

    public static void main(String[] args) {

        // Configure Redis connection factory
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory();
        connectionFactory.afterPropertiesSet();

        // Configure RedisTemplate
        RedisTemplate<String, byte[]> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.afterPropertiesSet();

        // Create a MemoryManager instance
        MemoryManager memoryManager = new MemoryManager(redisTemplate, new SimpleMeterRegistry());

        // Simulate high memory usage
        Random random = new Random();
        for (int i = 0; i < 700; i++) {
            byte[] data = new byte[10 * 1024 * 1024]; // 10 MB data
            random.nextBytes(data);
            memoryManager.putSessionData("session-" + i, data);

            // Call the monitor method to check heap usage and trigger heap dump
            memoryManager.monitorAndAdjustCacheSize();
        }
    }
}
