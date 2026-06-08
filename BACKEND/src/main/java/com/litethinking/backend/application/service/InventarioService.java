package com.litethinking.backend.application.service;

import com.litethinking.backend.application.dto.request.InventarioPdfEmailRequest;
import com.litethinking.backend.application.dto.response.InventarioResponse;
import com.litethinking.backend.application.port.in.inventario.GenerarPdfInventarioUseCase;
import com.litethinking.backend.application.port.in.inventario.GestionarInventarioUseCase;
import com.litethinking.backend.application.port.out.EmpresaRepositoryPort;
import com.litethinking.backend.application.port.out.GeneradorPdfPort;
import com.litethinking.backend.application.port.out.InventarioRepositoryPort;
import com.litethinking.backend.application.port.out.NotificacionEmailPort;
import com.litethinking.backend.domain.exception.EmpresaNotFoundException;
import com.litethinking.backend.domain.exception.ProductoNotFoundException;
import com.litethinking.backend.domain.model.Inventario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Servicio de aplicación para la gestión integral del inventario.
 *
 * <p>Clean Architecture — Application Layer: orquesta los casos de uso relacionados
 * con el stock de productos por empresa. Implementa {@link GestionarInventarioUseCase}
 * y {@link GenerarPdfInventarioUseCase}.</p>
 *
 * <p><strong>Responsabilidades:</strong></p>
 * <ul>
 *   <li>Consultar el inventario completo o filtrado por empresa.</li>
 *   <li>Agregar y reducir stock delegando la validación a la entidad de dominio
 *       {@link Inventario} (que protege la invariante de stock no negativo).</li>
 *   <li>Generar el reporte PDF del inventario (delega la generación al puerto de
 *       infraestructura — el servicio solo orquesta).</li>
 *   <li>Enviar el PDF por correo electrónico a través de {@link NotificacionEmailPort}.</li>
 * </ul>
 *
 * <p><strong>Nota sobre generación de PDF:</strong> la generación real del PDF está
 * delegada a la capa de infraestructura a través del método interno
 * {@link #construirPdfBytes(List, String)}. En la FASE 5 (infraestructura) se
 * inyectará un puerto dedicado para la generación de PDFs (ej.: iText, JasperReports).
 * Mientras tanto, este método genera un PDF mínimo válido para testing.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InventarioService implements GestionarInventarioUseCase, GenerarPdfInventarioUseCase {

    private final InventarioRepositoryPort inventarioRepositoryPort;
    private final EmpresaRepositoryPort    empresaRepositoryPort;
    private final NotificacionEmailPort    notificacionEmailPort;
    private final GeneradorPdfPort         generadorPdfPort;

    // -------------------------------------------------------------------------
    // GestionarInventarioUseCase — Consultas
    // -------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<InventarioResponse> listarTodos() {
        return inventarioRepositoryPort.listarTodos().stream()
                .map(InventarioResponse::desde)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioResponse> listarPorEmpresa(String empresaNit) {
        // Verificar que la empresa existe antes de consultar su inventario
        empresaRepositoryPort.buscarPorNit(empresaNit)
                .orElseThrow(() -> new EmpresaNotFoundException(empresaNit));

        return inventarioRepositoryPort.listarPorEmpresa(empresaNit).stream()
                .map(InventarioResponse::desde)
                .toList();
    }

    // -------------------------------------------------------------------------
    // GestionarInventarioUseCase — Mutaciones de stock
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public InventarioResponse agregarStock(String empresaNit, String productoCodigo, int cantidad) {
        Inventario inventario = buscarInventarioOFallar(empresaNit, productoCodigo);

        // La lógica de validación (cantidad > 0) vive en la entidad de dominio
        inventario.agregarStock(cantidad, ZonedDateTime.now());

        Inventario guardado = inventarioRepositoryPort.guardar(inventario);
        log.info("Stock agregado: +{} unidades de '{}' en empresa '{}'",
                cantidad, productoCodigo, empresaNit);
        return InventarioResponse.desde(guardado);
    }

    @Override
    @Transactional
    public InventarioResponse reducirStock(String empresaNit, String productoCodigo, int cantidad) {
        Inventario inventario = buscarInventarioOFallar(empresaNit, productoCodigo);

        // La invariante de stock no negativo se protege en la entidad Inventario
        // Lanza Inventario.StockInsuficienteException si no hay stock suficiente
        inventario.reducirStock(cantidad, ZonedDateTime.now());

        Inventario guardado = inventarioRepositoryPort.guardar(inventario);
        log.info("Stock reducido: -{} unidades de '{}' en empresa '{}'",
                cantidad, productoCodigo, empresaNit);
        return InventarioResponse.desde(guardado);
    }

    @Override
    @Transactional
    public InventarioResponse establecerStock(String empresaNit, String productoCodigo, int nuevoStock) {
        Inventario inventario = buscarInventarioOFallar(empresaNit, productoCodigo);
        
        inventario.establecerStock(nuevoStock, ZonedDateTime.now());
        
        Inventario guardado = inventarioRepositoryPort.guardar(inventario);
        log.info("Stock actualizado a: {} unidades de '{}' en empresa '{}'",
                nuevoStock, productoCodigo, empresaNit);
        return InventarioResponse.desde(guardado);
    }

    // -------------------------------------------------------------------------
    // GestionarInventarioUseCase — Generación y envío de PDF
    // -------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public byte[] generarPdfInventario(String empresaNit) {
        var empresa = empresaRepositoryPort.buscarPorNit(empresaNit)
                .orElseThrow(() -> new EmpresaNotFoundException(empresaNit));

        List<Inventario> inventarios = inventarioRepositoryPort.listarPorEmpresa(empresaNit);
        List<InventarioResponse> items = inventarios.stream()
                .map(InventarioResponse::desde)
                .toList();

        log.info("Generando PDF de inventario para empresa '{}' ({} productos)",
                empresa.getNombre(), items.size());
        return generadorPdfPort.generarReporteInventario(items, empresa.getNombre());
    }

    @Override
    @Transactional(readOnly = true)
    public void generarYEnviarPdfPorCorreo(InventarioPdfEmailRequest request) {
        var empresa = empresaRepositoryPort.buscarPorNit(request.empresaNit())
                .orElseThrow(() -> new EmpresaNotFoundException(request.empresaNit()));

        byte[] pdfBytes = generarPdfInventario(request.empresaNit());

        String nombreArchivo = "inventario-" + request.empresaNit().replace("-", "") + ".pdf";
        String cuerpo = construirCuerpoEmail(empresa.getNombre(), request.mensajeAdicional());

        notificacionEmailPort.enviarConPdfAdjunto(
                request.destinatario(),
                request.asunto(),
                cuerpo,
                pdfBytes,
                nombreArchivo
        );

        log.info("PDF de inventario de empresa '{}' enviado a '{}'",
                empresa.getNombre(), request.destinatario());
    }

    // -------------------------------------------------------------------------
    // GenerarPdfInventarioUseCase (alias — delega a GestionarInventarioUseCase)
    // -------------------------------------------------------------------------

    @Override
    public byte[] generarPdf(String empresaNit) {
        return generarPdfInventario(empresaNit);
    }

    @Override
    public void generarYEnviarPorCorreo(InventarioPdfEmailRequest request) {
        generarYEnviarPdfPorCorreo(request);
    }

    // -------------------------------------------------------------------------
    // Helpers privados
    // -------------------------------------------------------------------------

    /**
     * Busca un registro de inventario por empresa + producto o lanza una excepción
     * de dominio si no existe.
     *
     * @param empresaNit     NIT de la empresa.
     * @param productoCodigo código del producto.
     * @return entidad {@link Inventario} encontrada; nunca {@code null}.
     * @throws ProductoNotFoundException si no hay registro de inventario para esa combinación.
     */
    private Inventario buscarInventarioOFallar(String empresaNit, String productoCodigo) {
        return inventarioRepositoryPort
                .buscarPorEmpresaYProducto(empresaNit, productoCodigo)
                .orElseThrow(() -> new ProductoNotFoundException(
                        "No se encontró inventario para producto '" + productoCodigo +
                        "' en empresa '" + empresaNit + "'."
                ));
    }

    // Método construirPdfBytes eliminado a favor de GeneradorPdfPort

    /** Construye el cuerpo del email de envío de reporte. */
    private String construirCuerpoEmail(String nombreEmpresa, String mensajeAdicional) {
        StringBuilder sb = new StringBuilder();
        sb.append("Estimado destinatario,\n\n");
        sb.append("Adjunto encontrará el reporte de inventario de la empresa: ")
          .append(nombreEmpresa).append(".\n\n");
        if (mensajeAdicional != null && !mensajeAdicional.isBlank()) {
            sb.append("Mensaje adicional:\n").append(mensajeAdicional).append("\n\n");
        }
        sb.append("Este reporte fue generado automáticamente por el sistema LiteThinking.\n");
        return sb.toString();
    }

    /** Trunca una cadena a la longitud máxima indicada para formato de tabla. */
    private String truncar(String texto, int maxLen) {
        if (texto == null) return "";
        return texto.length() > maxLen ? texto.substring(0, maxLen - 3) + "..." : texto;
    }
}
