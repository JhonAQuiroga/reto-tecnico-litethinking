package com.litethinking.backend.application.service;

import com.litethinking.backend.application.dto.request.LoginRequest;
import com.litethinking.backend.application.dto.response.AuthResponse;
import com.litethinking.backend.application.port.out.UsuarioRepositoryPort;
import com.litethinking.backend.domain.enums.RolNombre;
import com.litethinking.backend.domain.model.Rol;
import com.litethinking.backend.domain.model.Usuario;
import com.litethinking.backend.infrastructure.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    private LoginRequest loginRequest;
    private Usuario usuario;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@litethinking.com");
        loginRequest.setPassword("password123");

        Rol rol = new Rol(1L, RolNombre.ADMIN, "Administrador", null, null);
        usuario = new Usuario(1L, "Test Admin", "test@litethinking.com", "hashed_pwd", true, rol, null, null);

        authentication = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
    }

    @Test
    void ejecutar_DeberiaRetornarAuthResponse_CuandoCredencialesSonCorrectas() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(usuarioRepositoryPort.buscarPorEmail(loginRequest.getEmail()))
                .thenReturn(Optional.of(usuario));
        when(jwtTokenProvider.generarToken(authentication))
                .thenReturn("mocked.jwt.token");
        when(jwtTokenProvider.getExpirationMs())
                .thenReturn(3600000L);

        // Act
        AuthResponse response = authService.ejecutar(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("mocked.jwt.token", response.token());
        assertEquals("test@litethinking.com", response.email());
        assertEquals("ADMIN", response.rol());
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(usuarioRepositoryPort).buscarPorEmail(loginRequest.getEmail());
        verify(jwtTokenProvider).generarToken(authentication);
    }

    @Test
    void ejecutar_DeberiaLanzarExcepcion_CuandoCredencialesSonIncorrectas() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> authService.ejecutar(loginRequest));

        verify(usuarioRepositoryPort, never()).buscarPorEmail(anyString());
        verify(jwtTokenProvider, never()).generarToken(any());
    }
}
