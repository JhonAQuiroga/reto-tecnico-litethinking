package com.litethinking.backend.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Swagger UI / OpenAPI 3 con soporte para JWT Bearer.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title       = "LiteThinking API",
                version     = "1.0.0",
                description = "API REST del Reto Técnico LiteThinking — Clean Architecture con Spring Boot 3",
                contact     = @Contact(name = "LiteThinking", email = "dev@litethinking.com")
        ),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name            = "bearerAuth",
        type            = SecuritySchemeType.HTTP,
        scheme          = "bearer",
        bearerFormat    = "JWT",
        description     = "Ingresa el token JWT obtenido en POST /auth/login"
)
public class OpenApiConfig {
    // La configuración se realiza completamente mediante anotaciones OpenAPI.
}
