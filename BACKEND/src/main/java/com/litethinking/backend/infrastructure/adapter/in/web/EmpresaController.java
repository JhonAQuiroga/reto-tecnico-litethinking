package com.litethinking.backend.infrastructure.adapter.in.web;

import com.litethinking.backend.application.dto.request.EmpresaRequest;
import com.litethinking.backend.application.dto.response.EmpresaResponse;
import com.litethinking.backend.application.port.in.empresa.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/empresas")
@RequiredArgsConstructor
public class EmpresaController {

    private final CrearEmpresaUseCase crearEmpresaUseCase;
    private final ConsultarEmpresaUseCase consultarEmpresaUseCase;
    private final ActualizarEmpresaUseCase actualizarEmpresaUseCase;
    private final EliminarEmpresaUseCase eliminarEmpresaUseCase;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmpresaResponse> crear(@Valid @RequestBody EmpresaRequest request) {
        EmpresaResponse response = crearEmpresaUseCase.ejecutar(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<EmpresaResponse>> listarTodas() {
        return ResponseEntity.ok(consultarEmpresaUseCase.listarTodas());
    }

    @GetMapping("/nit/{nit}")
    public ResponseEntity<EmpresaResponse> obtenerPorNit(@PathVariable String nit) {
        return ResponseEntity.ok(consultarEmpresaUseCase.buscarPorNit(nit));
    }

    @PutMapping("/{nit}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmpresaResponse> actualizar(@PathVariable String nit, @Valid @RequestBody EmpresaRequest request) {
        return ResponseEntity.ok(actualizarEmpresaUseCase.ejecutar(nit, request));
    }

    @DeleteMapping("/{nit}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable String nit) {
        eliminarEmpresaUseCase.ejecutar(nit);
        return ResponseEntity.noContent().build();
    }
}
