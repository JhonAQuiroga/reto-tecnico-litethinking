package com.litethinking.backend.infrastructure.adapter.out.persistence.adapter;

import com.litethinking.backend.application.port.out.UsuarioRepositoryPort;
import com.litethinking.backend.domain.model.Rol;
import com.litethinking.backend.domain.model.Usuario;
import com.litethinking.backend.infrastructure.adapter.out.persistence.entity.UsuarioEntity;
import com.litethinking.backend.infrastructure.adapter.out.persistence.repository.UsuarioJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de salida: implementación JPA del puerto {@link UsuarioRepositoryPort}.
 *
 * <p>Infrastructure Layer: traduce entre la entidad de dominio {@link Usuario} y la
 * entidad JPA {@link UsuarioEntity}. Usa constructores explícitos del dominio puro.</p>
 */
@Component
@RequiredArgsConstructor
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {

    private final UsuarioJpaRepository jpaRepository;

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Usuario> listarTodos() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existePorEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        // La creación de usuarios desde la API no está en el alcance del reto;
        // este método se provee para completitud del puerto.
        throw new UnsupportedOperationException("Creación de usuarios no implementada en esta versión");
    }

    // -------------------------------------------------------------------------
    // Mapeo privado
    // -------------------------------------------------------------------------

    /**
     * Convierte la entidad JPA a la entidad de dominio usando los constructores explícitos.
     * El campo {@code password} de la entidad JPA se mapea a {@code passwordHash} del dominio.
     */
    private Usuario toDomain(UsuarioEntity entity) {
        Rol rol = new Rol(
                entity.getRol().getId(),
                entity.getRol().getNombre(),
                entity.getRol().getDescripcion(),
                entity.getRol().getCreatedAt(),
                entity.getRol().getUpdatedAt()
        );

        return new Usuario(
                entity.getId(),
                entity.getNombre(),
                entity.getEmail(),
                entity.getPassword(),   // campo password en JPA = passwordHash en dominio
                entity.isActivo(),
                rol,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
