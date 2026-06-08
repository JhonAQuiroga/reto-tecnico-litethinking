package com.litethinking.backend.infrastructure.adapter.out.persistence.repository;

import com.litethinking.backend.infrastructure.adapter.out.persistence.entity.ProductoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductoJpaRepository extends JpaRepository<ProductoEntity, String> {

    @Query("SELECT p FROM ProductoEntity p LEFT JOIN FETCH p.categorias WHERE p.empresa.nit = :nit")
    List<ProductoEntity> findByEmpresaNitWithCategorias(@Param("nit") String nit);

    @Query("SELECT p FROM ProductoEntity p LEFT JOIN FETCH p.categorias WHERE p.codigo = :codigo")
    Optional<ProductoEntity> findByCodigoWithCategorias(@Param("codigo") String codigo);

    boolean existsByCodigo(String codigo);

    void deleteByEmpresaNit(String nit);
}
