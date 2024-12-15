package com.user.management.security;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenBlacklist {
    private final Map<String, Long> blacklist = new ConcurrentHashMap<>();

    /**
     * Adds a token to the blacklist with its expiration time.
     *
     * @param token      the JWT token
     * @param expiration the expiration time in milliseconds
     */
    public void add(String token, Long expiration) {
        blacklist.put(token, expiration);
    }

    /**
     * Checks if a token is blacklisted or expired.
     *
     * @param token the JWT token
     * @return true if the token is blacklisted, false otherwise
     */
    public boolean isBlacklisted(String token) {
        Long expiration = blacklist.get(token);
        return expiration != null && expiration < System.currentTimeMillis();
    }
}
