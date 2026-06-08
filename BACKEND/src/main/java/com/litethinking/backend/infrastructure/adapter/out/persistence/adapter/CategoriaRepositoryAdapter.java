package com.litethinking.backend.infrastructure.adapter.out.persistence.adapter;

import com.litethinking.backend.application.port.out.CategoriaRepositoryPort;
import com.litethinking.backend.domain.model.Categoria;
import com.litethinking.backend.infrastructure.adapter.out.persistence.entity.CategoriaEntity;
import com.litethinking.backend.infrastructure.adapter.out.persistence.repository.CategoriaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CategoriaRepositoryAdapter implements CategoriaRepositoryPort {

    private final CategoriaJpaRepository jpaRepository;

    @Override
    public List<Categoria> listarTodas() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Categoria> buscarPorIds(List<Long> ids) {
        return jpaRepository.findAllById(ids).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Categoria> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Categoria guardar(Categoria categoria) {
        CategoriaEntity entity = toEntity(categoria);
        return toDomain(jpaRepository.save(entity));
    }

    // -------------------------------------------------------------------------
    // Mapeo privado
    // -------------------------------------------------------------------------

    private Categoria toDomain(CategoriaEntity e) {
        return new Categoria(
                e.getId(),
                e.getNombre(),
                e.getDescripcion(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    private CategoriaEntity toEntity(Categoria c) {
        CategoriaEntity entity = new CategoriaEntity();
        entity.setId(c.getId());
        entity.setNombre(c.getNombre());
        entity.setDescripcion(c.getDescripcion());
        return entity;
    }
}
