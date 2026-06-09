package com.litethinking.backend.infrastructure.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.litethinking.backend.application.dto.request.LoginRequest;
import com.litethinking.backend.application.dto.response.AuthResponse;
import com.litethinking.backend.application.port.in.auth.LoginUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.litethinking.backend.infrastructure.security.jwt.JwtTokenProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.boot.test.mock.mockito.MockBean;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Desactiva los filtros de seguridad de Spring Security para aislar la prueba del controlador
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoginUseCase loginUseCase;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void login_DeberiaRetornar200YToken_CuandoCredencialesSonCorrectas() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("admin@litethinking.com");
        request.setPassword("password");

        AuthResponse authResponse = new AuthResponse(
                "mocked.jwt.token",
                "Bearer",
                "admin@litethinking.com",
                "Admin Test",
                "ADMIN",
                3600000
        );

        when(loginUseCase.ejecutar(any(LoginRequest.class))).thenReturn(authResponse);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked.jwt.token"))
                .andExpect(jsonPath("$.email").value("admin@litethinking.com"))
                .andExpect(jsonPath("$.rol").value("ADMIN"));
    }
}
