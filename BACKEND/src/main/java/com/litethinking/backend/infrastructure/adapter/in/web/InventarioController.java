package com.litethinking.backend.infrastructure.adapter.in.web;

import com.litethinking.backend.application.dto.request.InventarioPdfEmailRequest;
import com.litethinking.backend.application.dto.response.InventarioResponse;
import com.litethinking.backend.application.port.in.inventario.GestionarInventarioUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final GestionarInventarioUseCase gestionarInventarioUseCase;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('EXTERNO')")
    public ResponseEntity<List<InventarioResponse>> listarTodos() {
        return ResponseEntity.ok(gestionarInventarioUseCase.listarTodos());
    }

    @GetMapping("/empresa/{nit}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EXTERNO')")
    public ResponseEntity<List<InventarioResponse>> listarPorEmpresa(@PathVariable String nit) {
        return ResponseEntity.ok(gestionarInventarioUseCase.listarPorEmpresa(nit));
    }

    @GetMapping("/empresa/{nit}/pdf")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> descargarPdfInventario(@PathVariable String nit) {
        byte[] pdf = gestionarInventarioUseCase.generarPdfInventario(nit);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "inventario-" + nit + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }

    @PostMapping("/enviar-pdf")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> enviarPdfPorCorreo(@Valid @RequestBody InventarioPdfEmailRequest request) {
        gestionarInventarioUseCase.generarYEnviarPdfPorCorreo(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/empresa/{nit}/producto/{codigo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InventarioResponse> ajustarStock(
            @PathVariable String nit,
            @PathVariable String codigo,
            @Valid @RequestBody com.litethinking.backend.application.dto.request.AjustarStockRequest request) {
        return ResponseEntity.ok(gestionarInventarioUseCase.establecerStock(nit, codigo, request.cantidad()));
    }
}
