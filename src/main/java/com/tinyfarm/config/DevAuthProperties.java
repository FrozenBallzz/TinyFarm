package com.tinyfarm.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.dev-auth")
public record DevAuthProperties(
    boolean enabled,
    String token
) {
    public boolean matches(String candidateToken) {
        return enabled && token != null && !token.isBlank() && token.equals(candidateToken);
    }
}
