package com.litethinking.backend.infrastructure.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Propiedades de configuración JWT leídas desde application.yml.
 * Enlazadas mediante @ConfigurationPropertiesScan en BackendApplication.
 */
@ConfigurationProperties(prefix = "application.jwt")
public record JwtProperties(
        String secret,
        long expirationMs
) {}
