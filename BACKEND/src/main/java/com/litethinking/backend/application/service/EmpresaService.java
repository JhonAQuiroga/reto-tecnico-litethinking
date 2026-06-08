package com.litethinking.backend.application.service;

import com.litethinking.backend.application.dto.request.EmpresaRequest;
import com.litethinking.backend.application.dto.response.EmpresaResponse;
import com.litethinking.backend.application.port.in.empresa.ActualizarEmpresaUseCase;
import com.litethinking.backend.application.port.in.empresa.ConsultarEmpresaUseCase;
import com.litethinking.backend.application.port.in.empresa.CrearEmpresaUseCase;
import com.litethinking.backend.application.port.in.empresa.EliminarEmpresaUseCase;
import com.litethinking.backend.application.port.out.EmpresaRepositoryPort;
import com.litethinking.backend.domain.exception.EmpresaNotFoundException;
import com.litethinking.backend.domain.exception.RecursoYaExisteException;
import com.litethinking.backend.domain.model.Empresa;
import com.litethinking.backend.domain.valueobject.NitEmpresa;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Servicio de aplicación para el CRUD completo de Empresas.
 *
 * <p>Clean Architecture — Application Layer: este servicio orquesta los casos de uso
 * relacionados con la entidad {@link Empresa}. Implementa los cuatro puertos de entrada
 * de empresa e interactúa exclusivamente con el repositorio a través del puerto de salida
 * {@link EmpresaRepositoryPort}.</p>
 *
 * <p>La construcción de la entidad de dominio usa el constructor explícito de {@link Empresa}
 * (POJO puro, sin Lombok), respetando las invariantes del dominio.</p>
 */
import com.litethinking.backend.application.port.out.InventarioRepositoryPort;
import com.litethinking.backend.application.port.out.ProductoRepositoryPort;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmpresaService implements
        CrearEmpresaUseCase,
        ActualizarEmpresaUseCase,
        EliminarEmpresaUseCase,
        ConsultarEmpresaUseCase {

    private final EmpresaRepositoryPort empresaRepositoryPort;
    private final ProductoRepositoryPort productoRepositoryPort;
    private final InventarioRepositoryPort inventarioRepositoryPort;

    // -------------------------------------------------------------------------
    // ConsultarEmpresaUseCase
    // -------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<EmpresaResponse> listarTodas() {
        return empresaRepositoryPort.listarTodas().stream()
                .map(EmpresaResponse::desde)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EmpresaResponse buscarPorNit(String nit) {
        Empresa empresa = empresaRepositoryPort.buscarPorNit(nit)
                .orElseThrow(() -> new EmpresaNotFoundException(nit));
        return EmpresaResponse.desde(empresa);
    }

    // -------------------------------------------------------------------------
    // CrearEmpresaUseCase
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public EmpresaResponse ejecutar(EmpresaRequest request) {
        if (empresaRepositoryPort.existePorNit(request.nit())) {
            throw new RecursoYaExisteException("Empresa", request.nit());
        }

        // Construcción con constructor explícito del dominio puro (sin Lombok)
        Empresa empresa = new Empresa(
                new NitEmpresa(request.nit()),
                request.nombre(),
                request.direccion(),
                request.telefono(),
                true,           // activa por defecto
                null,           // creadoEn — asignado por la BD/auditoría JPA
                null            // actualizadoEn — asignado por la BD/auditoría JPA
        );

        Empresa guardada = empresaRepositoryPort.guardar(empresa);
        log.info("Empresa creada con NIT: {}", guardada.getNitValor());
        return EmpresaResponse.desde(guardada);
    }

    // -------------------------------------------------------------------------
    // ActualizarEmpresaUseCase
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public EmpresaResponse ejecutar(String nit, EmpresaRequest request) {
        Empresa existente = empresaRepositoryPort.buscarPorNit(nit)
                .orElseThrow(() -> new EmpresaNotFoundException(nit));

        // Re-construir con los datos actualizados manteniendo el NIT original y estado activo
        Empresa actualizada = new Empresa(
                existente.getNit(),
                request.nombre(),
                request.direccion(),
                request.telefono(),
                existente.isActiva(),
                existente.getCreadoEn(),
                ZonedDateTime.now()
        );

        Empresa guardada = empresaRepositoryPort.guardar(actualizada);
        log.info("Empresa actualizada con NIT: {}", guardada.getNitValor());
        return EmpresaResponse.desde(guardada);
    }

    // -------------------------------------------------------------------------
    // EliminarEmpresaUseCase
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public void ejecutar(String nit) {
        if (!empresaRepositoryPort.existePorNit(nit)) {
            throw new EmpresaNotFoundException(nit);
        }
        
        // La empresa puede estar siendo referenciada en el Inventario y en Productos.
        // Dado que estamos siguiendo una política de eliminación fuerte (cascada manual a nivel de aplicación),
        // eliminamos primero los dependientes para evitar 'DataIntegrityViolationException' por Foreign Keys.
        
        // 1. Eliminar todo el inventario de esta empresa
        inventarioRepositoryPort.eliminarPorEmpresaNit(nit);
        
        // 2. Eliminar todos los productos asociados a esta empresa
        productoRepositoryPort.eliminarPorEmpresa(nit);
        
        // 3. Eliminar la empresa
        empresaRepositoryPort.eliminar(nit);
        log.info("Empresa eliminada con NIT: {}", nit);
    }
}
