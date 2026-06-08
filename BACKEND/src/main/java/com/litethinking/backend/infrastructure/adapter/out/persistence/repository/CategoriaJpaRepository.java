package com.litethinking.backend.infrastructure.adapter.out.persistence.repository;

import com.litethinking.backend.infrastructure.adapter.out.persistence.entity.CategoriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaJpaRepository extends JpaRepository<CategoriaEntity, Long> {
    List<CategoriaEntity> findByIdIn(List<Long> ids);
}
