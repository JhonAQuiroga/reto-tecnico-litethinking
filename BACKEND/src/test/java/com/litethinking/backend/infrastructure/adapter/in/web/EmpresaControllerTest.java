package com.litethinking.backend.infrastructure.adapter.in.web;

import com.litethinking.backend.application.dto.response.EmpresaResponse;
import com.litethinking.backend.application.port.in.empresa.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.litethinking.backend.infrastructure.security.jwt.JwtTokenProvider;
import org.springframework.security.core.userdetails.UserDetailsService;

@WebMvcTest(controllers = EmpresaController.class)
@AutoConfigureMockMvc(addFilters = false) // Desactiva los filtros de seguridad de Spring Security para aislar la prueba del controlador
class EmpresaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private CrearEmpresaUseCase crearEmpresaUseCase;

    @MockBean
    private ConsultarEmpresaUseCase consultarEmpresaUseCase;

    @MockBean
    private ActualizarEmpresaUseCase actualizarEmpresaUseCase;

    @MockBean
    private EliminarEmpresaUseCase eliminarEmpresaUseCase;

    @Test
    void listarTodas_DeberiaRetornar200YListaDeEmpresas() throws Exception {
        // Arrange
        EmpresaResponse empresa1 = new EmpresaResponse("900123456", "Empresa A", "1234567", "Calle 1", true, null, null);
        EmpresaResponse empresa2 = new EmpresaResponse("900654321", "Empresa B", "7654321", "Calle 2", true, null, null);
        
        when(consultarEmpresaUseCase.listarTodas()).thenReturn(List.of(empresa1, empresa2));

        // Act & Assert
        mockMvc.perform(get("/empresas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].nit").value("900123456"))
                .andExpect(jsonPath("$[0].nombre").value("Empresa A"))
                .andExpect(jsonPath("$[1].nit").value("900654321"))
                .andExpect(jsonPath("$[1].nombre").value("Empresa B"));
    }
}
