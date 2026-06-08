package com.litethinking.backend.application.service;

import com.litethinking.backend.application.dto.request.EmpresaRequest;
import com.litethinking.backend.application.dto.response.EmpresaResponse;
import com.litethinking.backend.application.port.out.EmpresaRepositoryPort;
import com.litethinking.backend.domain.exception.RecursoYaExisteException;
import com.litethinking.backend.domain.model.Empresa;
import com.litethinking.backend.domain.valueobject.NitEmpresa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmpresaServiceTest {

    @Mock
    private EmpresaRepositoryPort empresaRepositoryPort;

    @InjectMocks
    private EmpresaService empresaService;

    private EmpresaRequest validRequest;
    private Empresa mockedEmpresa;

    @BeforeEach
    void setUp() {
        validRequest = new EmpresaRequest(
                "900123456-1",
                "Mi Empresa S.A.S.",
                "Calle Falsa 123",
                "3001234567"
        );

        mockedEmpresa = new Empresa(
                new NitEmpresa("900123456-1"),
                "Mi Empresa S.A.S.",
                "Calle Falsa 123",
                "3001234567",
                true,
                ZonedDateTime.now(),
                ZonedDateTime.now()
        );
    }

    @Test
    void ejecutar_Successful_CreatesAndReturnsEmpresa() {
        // Arrange
        when(empresaRepositoryPort.existePorNit(validRequest.nit())).thenReturn(false);
        when(empresaRepositoryPort.guardar(any(Empresa.class))).thenReturn(mockedEmpresa);

        // Act
        EmpresaResponse response = empresaService.ejecutar(validRequest);

        // Assert
        assertNotNull(response);
        assertEquals(validRequest.nit(), response.nit());
        assertEquals(validRequest.nombre(), response.nombre());
        assertTrue(response.activa());

        // Verificar interacciones
        verify(empresaRepositoryPort, times(1)).existePorNit(validRequest.nit());
        verify(empresaRepositoryPort, times(1)).guardar(any(Empresa.class));
    }

    @Test
    void ejecutar_ThrowsException_WhenEmpresaAlreadyExists() {
        // Arrange
        when(empresaRepositoryPort.existePorNit(validRequest.nit())).thenReturn(true);

        // Act & Assert
        assertThrows(RecursoYaExisteException.class, () -> empresaService.ejecutar(validRequest));

        // Verificar que NUNCA intentó guardar
        verify(empresaRepositoryPort, times(1)).existePorNit(validRequest.nit());
        verify(empresaRepositoryPort, never()).guardar(any(Empresa.class));
    }
}
