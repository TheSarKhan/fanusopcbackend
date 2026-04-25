package com.fanus.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final StringRedisTemplate redis;
    private final long refreshTokenExpirySeconds;

    public RefreshTokenService(
        StringRedisTemplate redis,
        @Value("${app.jwt.refresh-token-expiry-seconds}") long refreshTokenExpirySeconds
    ) {
        this.redis = redis;
        this.refreshTokenExpirySeconds = refreshTokenExpirySeconds;
    }

    public String create(Long userId) {
        String tokenId = UUID.randomUUID().toString();
        String key = redisKey(userId, tokenId);
        redis.opsForValue().set(key, userId.toString(), Duration.ofSeconds(refreshTokenExpirySeconds));
        return tokenId;
    }

    public boolean validate(Long userId, String tokenId) {
        return Boolean.TRUE.equals(redis.hasKey(redisKey(userId, tokenId)));
    }

    public void revoke(Long userId, String tokenId) {
        redis.delete(redisKey(userId, tokenId));
    }

    public void revokeAll(Long userId) {
        var keys = redis.keys("refresh:" + userId + ":*");
        if (keys != null && !keys.isEmpty()) redis.delete(keys);
    }

    private String redisKey(Long userId, String tokenId) {
        return "refresh:" + userId + ":" + tokenId;
    }
}
