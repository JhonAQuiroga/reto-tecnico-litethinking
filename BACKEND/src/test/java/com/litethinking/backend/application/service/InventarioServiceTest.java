package com.litethinking.backend.application.service;

import com.litethinking.backend.application.dto.request.InventarioPdfEmailRequest;
import com.litethinking.backend.application.port.out.EmpresaRepositoryPort;
import com.litethinking.backend.application.port.out.GeneradorPdfPort;
import com.litethinking.backend.application.port.out.InventarioRepositoryPort;
import com.litethinking.backend.application.port.out.NotificacionEmailPort;
import com.litethinking.backend.domain.model.Empresa;
import com.litethinking.backend.domain.model.Inventario;
import com.litethinking.backend.domain.model.Producto;
import com.litethinking.backend.domain.valueobject.NitEmpresa;
import com.litethinking.backend.domain.valueobject.CodigoProducto;
import com.litethinking.backend.domain.valueobject.Dinero;
import com.litethinking.backend.domain.enums.Moneda;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventarioServiceTest {

    @Mock
    private InventarioRepositoryPort inventarioRepositoryPort;

    @Mock
    private EmpresaRepositoryPort empresaRepositoryPort;

    @Mock
    private GeneradorPdfPort generadorPdfPort;

    @Mock
    private NotificacionEmailPort notificacionEmailPort;

    @InjectMocks
    private InventarioService inventarioService;

    private Empresa mockedEmpresa;
    private Inventario mockedInventario;
    private InventarioPdfEmailRequest validEmailRequest;

    @BeforeEach
    void setUp() {
        mockedEmpresa = new Empresa(
                new NitEmpresa("900123456-1"),
                "Mi Empresa",
                "Calle Falsa 123",
                "3001234567",
                true,
                ZonedDateTime.now(),
                ZonedDateTime.now()
        );

        Producto mockedProducto = new Producto(
                new CodigoProducto("PRD-001"),
                "Producto Mock",
                "Descripción",
                new Dinero(new BigDecimal("1000"), Moneda.COP),
                new Dinero(new BigDecimal("10"), Moneda.USD),
                new Dinero(new BigDecimal("8"), Moneda.EUR),
                mockedEmpresa.getNit(),
                true,
                List.of(),
                ZonedDateTime.now(),
                ZonedDateTime.now()
        );

        mockedInventario = new Inventario(
                1L,
                mockedEmpresa.getNitValor(),
                mockedProducto.getCodigoValor(),
                50,
                ZonedDateTime.now(),
                ZonedDateTime.now(),
                mockedEmpresa,
                mockedProducto
        );

        validEmailRequest = new InventarioPdfEmailRequest(
                "gerente@empresa.com",
                "Reporte de Inventario Mensual",
                "Adjunto los detalles.",
                "900123456-1"
        );
    }

    @Test
    void generarYEnviarPdfPorCorreo_Successful() {
        // Arrange
        when(empresaRepositoryPort.buscarPorNit(validEmailRequest.empresaNit()))
                .thenReturn(Optional.of(mockedEmpresa));
        
        when(inventarioRepositoryPort.listarPorEmpresa(validEmailRequest.empresaNit()))
                .thenReturn(List.of(mockedInventario));

        byte[] fakePdfBytes = new byte[]{1, 2, 3};
        when(generadorPdfPort.generarReporteInventario(anyList(), eq(mockedEmpresa.getNombre())))
                .thenReturn(fakePdfBytes);

        // Act
        inventarioService.generarYEnviarPdfPorCorreo(validEmailRequest);

        // Assert
        verify(empresaRepositoryPort, times(2)).buscarPorNit(validEmailRequest.empresaNit());
        verify(inventarioRepositoryPort, times(1)).listarPorEmpresa(validEmailRequest.empresaNit());
        verify(generadorPdfPort, times(1)).generarReporteInventario(anyList(), eq(mockedEmpresa.getNombre()));
        
        verify(notificacionEmailPort, times(1)).enviarConPdfAdjunto(
                eq(validEmailRequest.destinatario()),
                eq(validEmailRequest.asunto()),
                contains("Adjunto encontrará el reporte de inventario de la empresa: " + mockedEmpresa.getNombre()),
                eq(fakePdfBytes),
                eq("inventario-9001234561.pdf")
        );
    }
}
