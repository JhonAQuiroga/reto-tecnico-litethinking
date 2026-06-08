package com.litethinking.backend.infrastructure.adapter.out.persistence.adapter;

import com.litethinking.backend.application.port.out.ClienteRepositoryPort;
import com.litethinking.backend.domain.model.Cliente;
import com.litethinking.backend.infrastructure.adapter.out.persistence.entity.ClienteEntity;
import com.litethinking.backend.infrastructure.adapter.out.persistence.repository.ClienteJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ClienteRepositoryAdapter implements ClienteRepositoryPort {

    private final ClienteJpaRepository clienteJpaRepository;

    @Override
    public List<Cliente> listarTodos() {
        return clienteJpaRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Cliente> buscarPorId(Long id) {
        return clienteJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public boolean existePorEmail(String email) {
        return clienteJpaRepository.existsByEmail(email);
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        ClienteEntity entity = new ClienteEntity();
        if (cliente.getId() != null) {
            entity.setId(cliente.getId());
        }
        entity.setNombre(cliente.getNombre());
        entity.setEmail(cliente.getEmail());
        entity.setTelefono(cliente.getTelefono());
        entity.setDireccion(cliente.getDireccion());
        entity.setActivo(cliente.isActivo());

        return toDomain(clienteJpaRepository.save(entity));
    }

    @Override
    public void eliminar(Long id) {
        clienteJpaRepository.deleteById(id);
    }

    private Cliente toDomain(ClienteEntity e) {
        return new Cliente(
                e.getId(),
                e.getNombre(),
                e.getEmail(),
                e.getTelefono(),
                e.getDireccion(),
                e.isActivo(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
