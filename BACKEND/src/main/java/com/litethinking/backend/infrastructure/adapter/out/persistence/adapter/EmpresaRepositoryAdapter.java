package com.litethinking.backend.infrastructure.adapter.out.persistence.adapter;

import com.litethinking.backend.application.port.out.EmpresaRepositoryPort;
import com.litethinking.backend.domain.model.Empresa;
import com.litethinking.backend.domain.valueobject.NitEmpresa;
import com.litethinking.backend.infrastructure.adapter.out.persistence.entity.EmpresaEntity;
import com.litethinking.backend.infrastructure.adapter.out.persistence.repository.EmpresaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de salida: implementación JPA del puerto {@link EmpresaRepositoryPort}.
 *
 * <p>Infrastructure Layer: traduce entre la entidad de dominio {@link Empresa} y la
 * entidad JPA {@link EmpresaEntity}. Usa constructores explícitos del dominio puro.</p>
 */
@Component
@RequiredArgsConstructor
public class EmpresaRepositoryAdapter implements EmpresaRepositoryPort {

    private final EmpresaJpaRepository jpaRepository;

    @Override
    public List<Empresa> listarTodas() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Empresa> listarActivas() {
        return jpaRepository.findAll().stream()
                .filter(EmpresaEntity::isActiva)
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Empresa> buscarPorNit(String nit) {
        return jpaRepository.findById(nit).map(this::toDomain);
    }

    @Override
    public boolean existePorNit(String nit) {
        return jpaRepository.existsByNit(nit);
    }

    @Override
    public Empresa guardar(Empresa empresa) {
        EmpresaEntity entity = toEntity(empresa);
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public void eliminar(String nit) {
        jpaRepository.deleteById(nit);
    }

    // -------------------------------------------------------------------------
    // Mapeo privado
    // -------------------------------------------------------------------------

    /**
     * Convierte la entidad JPA a la entidad de dominio usando el constructor explícito.
     * El NIT de la BD es String; se envuelve en el VO {@link NitEmpresa}.
     */
    private Empresa toDomain(EmpresaEntity e) {
        return new Empresa(
                new NitEmpresa(e.getNit()),
                e.getNombre(),
                e.getDireccion(),
                e.getTelefono(),
                e.isActiva(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    /**
     * Convierte la entidad de dominio a la entidad JPA.
     * Extrae el valor String del VO {@link NitEmpresa} para persistirlo.
     */
    private EmpresaEntity toEntity(Empresa empresa) {
        EmpresaEntity entity = new EmpresaEntity();
        entity.setNit(empresa.getNitValor());
        entity.setNombre(empresa.getNombre());
        entity.setDireccion(empresa.getDireccion());
        entity.setTelefono(empresa.getTelefono());
        entity.setActiva(empresa.isActiva());
        return entity;
    }
}
