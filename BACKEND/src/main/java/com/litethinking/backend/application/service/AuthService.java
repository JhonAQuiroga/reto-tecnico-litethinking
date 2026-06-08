package com.litethinking.backend.application.service;

import com.litethinking.backend.application.dto.request.LoginRequest;
import com.litethinking.backend.application.dto.response.AuthResponse;
import com.litethinking.backend.application.port.in.auth.LoginUseCase;
import com.litethinking.backend.application.port.out.UsuarioRepositoryPort;
import com.litethinking.backend.domain.exception.UsuarioNotFoundException;
import com.litethinking.backend.domain.model.Usuario;
import com.litethinking.backend.infrastructure.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de aplicación: Autenticación.
 *
 * <p>Orquesta el flujo de login: valida credenciales usando Spring Security,
 * recupera el usuario del repositorio y emite el token JWT.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements LoginUseCase {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final JwtTokenProvider      jwtTokenProvider;

    @Override
    @Transactional(readOnly = true)
    public AuthResponse ejecutar(LoginRequest request) {
        log.info("Intento de login para email: {}", request.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        Usuario usuario = usuarioRepositoryPort.buscarPorEmail(request.getEmail())
                .orElseThrow(() -> new UsuarioNotFoundException(request.getEmail()));

        String token = jwtTokenProvider.generarToken(authentication);

        log.info("Login exitoso para usuario: {} con rol: {}",
                usuario.getEmail(), usuario.getRol().getNombre());

        // Constructor canónico del record AuthResponse (sin .builder())
        return new AuthResponse(
                token,
                "Bearer",
                usuario.getEmail(),
                usuario.getNombre(),
                usuario.getRol().getNombre().name(),
                jwtTokenProvider.getExpirationMs()
        );
    }
}
