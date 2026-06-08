package com.litethinking.backend.application.port.out;

import com.litethinking.backend.domain.model.Categoria;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepositoryPort {

    List<Categoria> listarTodas();

    List<Categoria> buscarPorIds(List<Long> ids);

    Optional<Categoria> buscarPorId(Long id);

    Categoria guardar(Categoria categoria);
}
