package com.litethinking.backend.infrastructure.adapter.in.web;

import com.litethinking.backend.application.dto.request.ProductoRequest;
import com.litethinking.backend.application.dto.response.ProductoResponse;
import com.litethinking.backend.application.port.in.producto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final CrearProductoUseCase crearProductoUseCase;
    private final ConsultarProductoUseCase consultarProductoUseCase;
    private final ActualizarProductoUseCase actualizarProductoUseCase;
    private final EliminarProductoUseCase eliminarProductoUseCase;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductoResponse> crear(@Valid @RequestBody ProductoRequest request) {
        ProductoResponse response = crearProductoUseCase.ejecutar(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProductoResponse>> listarTodos() {
        return ResponseEntity.ok(consultarProductoUseCase.listarTodos());
    }

    @GetMapping("/empresa/{empresaNit}")
    public ResponseEntity<List<ProductoResponse>> listarPorEmpresa(@PathVariable String empresaNit) {
        return ResponseEntity.ok(consultarProductoUseCase.listarPorEmpresa(empresaNit));
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<ProductoResponse> obtenerPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(consultarProductoUseCase.buscarPorCodigo(codigo));
    }

    @PutMapping("/{codigo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductoResponse> actualizar(@PathVariable String codigo, @Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.ok(actualizarProductoUseCase.ejecutar(codigo, request));
    }

    @DeleteMapping("/{codigo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable String codigo) {
        eliminarProductoUseCase.ejecutar(codigo);
        return ResponseEntity.noContent().build();
    }
}
