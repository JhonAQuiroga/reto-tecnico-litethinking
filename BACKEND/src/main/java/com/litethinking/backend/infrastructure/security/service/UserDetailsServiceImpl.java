package com.litethinking.backend.infrastructure.security.service;

import com.litethinking.backend.application.port.out.UsuarioRepositoryPort;
import com.litethinking.backend.domain.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación de UserDetailsService de Spring Security.
 *
 * <p>Carga el usuario desde la base de datos por email y construye
 * el objeto UserDetails que Spring Security usa para la autenticación.
 * El prefijo ROLE_ es requerido por Spring Security para la autorización
 * basada en roles con @PreAuthorize("hasRole('...')").</p>
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepositoryPort usuarioRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepositoryPort.buscarPorEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado con email: " + email
                ));

        if (!usuario.isActivo()) {
            throw new UsernameNotFoundException("Usuario desactivado: " + email);
        }

        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getNombre().name());

        return new User(
                usuario.getEmail(),
                usuario.getPasswordHash(),
                List.of(authority)
        );
    }
}
