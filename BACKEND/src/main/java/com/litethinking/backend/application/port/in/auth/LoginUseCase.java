package com.litethinking.backend.application.port.in.auth;

import com.litethinking.backend.application.dto.request.LoginRequest;
import com.litethinking.backend.application.dto.response.AuthResponse;

/**
 * Puerto de entrada — Caso de uso: Autenticación de usuario.
 */
public interface LoginUseCase {

    AuthResponse ejecutar(LoginRequest request);
}
