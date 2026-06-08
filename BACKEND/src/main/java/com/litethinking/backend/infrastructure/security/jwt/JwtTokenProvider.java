package com.litethinking.backend.infrastructure.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Componente responsable de la generación, validación y parseo de tokens JWT.
 *
 * <p>Utiliza HMAC-SHA512 como algoritmo de firma. La clave secreta se obtiene
 * de las propiedades de configuración y nunca se expone en los logs.</p>
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private static final String CLAIM_ROLES = "roles";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtProperties jwtProperties;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    /**
     * Genera un token JWT firmado para el usuario autenticado.
     *
     * @param authentication objeto de autenticación de Spring Security
     * @return token JWT como String
     */
    public String generarToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date ahora    = new Date();
        Date expira   = new Date(ahora.getTime() + jwtProperties.expirationMs());

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim(CLAIM_ROLES, roles)
                .issuedAt(ahora)
                .expiration(expira)
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    /**
     * Extrae el email (subject) del token JWT.
     */
    public String extraerEmail(String token) {
        return parsearClaims(token).getSubject();
    }

    /**
     * Valida la firma y la expiración del token.
     *
     * @return true si el token es válido, false en caso contrario
     */
    public boolean esValido(String token) {
        try {
            parsearClaims(token);
            return true;
        } catch (SecurityException exception) {
            log.warn("Firma JWT inválida: {}", exception.getMessage());
        } catch (MalformedJwtException exception) {
            log.warn("Token JWT malformado: {}", exception.getMessage());
        } catch (ExpiredJwtException exception) {
            log.warn("Token JWT expirado: {}", exception.getMessage());
        } catch (UnsupportedJwtException exception) {
            log.warn("Token JWT no soportado: {}", exception.getMessage());
        } catch (IllegalArgumentException exception) {
            log.warn("Claims JWT vacíos: {}", exception.getMessage());
        }
        return false;
    }

    public long getExpirationMs() {
        return jwtProperties.expirationMs();
    }

    // ── Métodos privados ─────────────────────────────────────────────────────

    private Claims parsearClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtProperties.secret().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
