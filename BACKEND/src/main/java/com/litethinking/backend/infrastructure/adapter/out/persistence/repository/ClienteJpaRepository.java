package com.litethinking.backend.infrastructure.adapter.out.persistence.repository;

import com.litethinking.backend.infrastructure.adapter.out.persistence.entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteJpaRepository extends JpaRepository<ClienteEntity, Long> {
    boolean existsByEmail(String email);
}
