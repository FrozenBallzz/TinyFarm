package com.tinyfarm.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.github")
public record GithubOAuthProperties(
    @NotBlank String clientId,
    @NotBlank String clientSecret
) {
}
