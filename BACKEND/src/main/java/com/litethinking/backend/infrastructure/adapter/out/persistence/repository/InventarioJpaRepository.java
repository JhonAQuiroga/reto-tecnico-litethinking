package com.litethinking.backend.infrastructure.adapter.out.persistence.repository;

import com.litethinking.backend.infrastructure.adapter.out.persistence.entity.InventarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InventarioJpaRepository extends JpaRepository<InventarioEntity, Long> {

    @Query("SELECT i FROM InventarioEntity i JOIN FETCH i.empresa JOIN FETCH i.producto WHERE i.empresa.nit = :nit")
    List<InventarioEntity> findByEmpresaNitFetched(@Param("nit") String nit);

    @Query("SELECT i FROM InventarioEntity i JOIN FETCH i.empresa JOIN FETCH i.producto")
    List<InventarioEntity> findAllFetched();

    Optional<InventarioEntity> findByEmpresaNitAndProductoCodigo(String empresaNit, String productoCodigo);

    void deleteByProductoCodigo(String productoCodigo);

    void deleteByEmpresaNit(String empresaNit);
}
