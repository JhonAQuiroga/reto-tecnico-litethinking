package com.litethinking.backend.application.dto.response;

/**
 * DTO de respuesta tras un login exitoso.
 *
 * <p>Application Layer — DTO: record inmutable de Java 21 que devuelve el token JWT
 * y los datos básicos del usuario autenticado al adaptador de entrada (controlador REST).</p>
 *
 * <p><strong>Campos:</strong></p>
 * <ul>
 *   <li>{@code token}: token JWT firmado con HMAC-SHA512.</li>
 *   <li>{@code tipo}: siempre {@code "Bearer"}.</li>
 *   <li>{@code email}: email del usuario autenticado.</li>
 *   <li>{@code nombre}: nombre completo del usuario autenticado.</li>
 *   <li>{@code rol}: nombre del rol asignado (ej.: "ADMIN", "USUARIO").</li>
 *   <li>{@code expiresIn}: tiempo hasta expiración del token en milisegundos.</li>
 * </ul>
 */
public record AuthResponse(
        String token,
        String tipo,
        String email,
        String nombre,
        String rol,
        long expiresIn
) {}
