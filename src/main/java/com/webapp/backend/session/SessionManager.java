package com.webapp.backend.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionManager implements SessionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionManager.class);
    private final RedisTemplate<String, Object> redisTemplate;
    private final Map<String, String> localStore = new ConcurrentHashMap<>();
    private final boolean redisAvailable;
    private static final String PREFIX = "session:";
    private static final Duration TTL = Duration.ofMinutes(30);

    public SessionManager(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        boolean ok = true;
        try {
            redisTemplate.getConnectionFactory().getConnection().ping();
        } catch (Exception e) {
            LOGGER.warn("Redis unavailable; falling back to local store", e);
            ok = false;
        }
        this.redisAvailable = ok;
    }

    @Override
    public String login(String userId) {
        String sessionId = "SESSION_" + UUID.randomUUID();
        if (redisAvailable) {
            try {
                // use setIfAbsent to avoid race for same user
                Boolean set = redisTemplate.opsForValue().setIfAbsent(PREFIX + userId, sessionId, TTL);
                if (Boolean.FALSE.equals(set)) {
                    Object existing = redisTemplate.opsForValue().get(PREFIX + userId);
                    return "User already logged in. Session ID: " + existing;
                }
                return "Login successful in Redis. Session ID: " + sessionId;
            } catch (Exception e) {
                LOGGER.error("Redis error during login; fallback to local store", e);
            }
        }

        String existing = localStore.putIfAbsent(userId, sessionId);
        if (existing != null) {
            return "User already logged in. Session ID: " + existing;
        }
        return "Login successful in localStorage. Session ID: " + sessionId;
    }

    @Override
    public String logout(String userId) {
        if (redisAvailable) {
            try {
                Boolean existed = redisTemplate.delete(PREFIX + userId);
                if (Boolean.TRUE.equals(existed)) return "Logout successful.";
                return "User not logged in.";
            } catch (Exception e) {
                LOGGER.error("Redis error during logout; fallback to local store", e);
            }
        }
        return Optional.ofNullable(localStore.remove(userId)).map(s -> "Logout successful.").orElse("User not logged in.");
    }

    @Override
    public Optional<String> getSession(String userId) {
        if (redisAvailable) {
            try {
                Object val = redisTemplate.opsForValue().get(PREFIX + userId);
                return Optional.ofNullable(val).map(Object::toString);
            } catch (Exception e) {
                LOGGER.error("Redis error during getSession; fallback to local store", e);
            }
        }
        return Optional.ofNullable(localStore.get(userId));
    }
}
