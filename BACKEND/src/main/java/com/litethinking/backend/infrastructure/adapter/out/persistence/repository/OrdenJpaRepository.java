package com.litethinking.backend.infrastructure.adapter.out.persistence.repository;

import com.litethinking.backend.infrastructure.adapter.out.persistence.entity.OrdenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrdenJpaRepository extends JpaRepository<OrdenEntity, Long> {

    @Query("SELECT o FROM OrdenEntity o JOIN FETCH o.cliente WHERE o.cliente.id = :clienteId")
    List<OrdenEntity> findByClienteIdFetched(@Param("clienteId") Long clienteId);

    Optional<OrdenEntity> findByNumeroOrden(String numeroOrden);
}
