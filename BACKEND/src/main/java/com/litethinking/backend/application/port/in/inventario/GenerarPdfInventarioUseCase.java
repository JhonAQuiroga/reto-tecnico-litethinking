package com.litethinking.backend.application.port.in.inventario;

import com.litethinking.backend.application.dto.request.InventarioPdfEmailRequest;

/**
 * Puerto de entrada: caso de uso específico para <strong>generar y enviar el PDF de inventario</strong>.
 *
 * <p>Clean Architecture — Application Layer: este puerto es el contrato explícito para
 * los casos de uso de generación del reporte de inventario en formato PDF y su envío
 * por correo electrónico. Cumple el requerimiento no funcional del reto técnico.</p>
 *
 * <p><strong>Nota:</strong> Este puerto se mantiene separado de {@link GestionarInventarioUseCase}
 * para respetar el principio de segregación de interfaces (ISP). Los controladores REST
 * que solo necesiten generar o enviar el PDF inyectan únicamente este puerto.</p>
 */
public interface GenerarPdfInventarioUseCase {

    /**
     * Genera el reporte PDF del inventario de una empresa.
     *
     * <p>El PDF incluye: nombre y NIT de la empresa, fecha de generación, lista de
     * productos con código, nombre, características, stock actual y precios en
     * COP, USD y EUR.</p>
     *
     * @param empresaNit NIT de la empresa cuyo inventario se desea exportar; nunca {@code null}.
     * @return arreglo de bytes con el contenido binario del PDF generado; nunca {@code null} ni vacío.
     * @throws com.litethinking.backend.domain.exception.EmpresaNotFoundException
     *         si la empresa no existe.
     */
    byte[] generarPdf(String empresaNit);

    /**
     * Genera el reporte PDF del inventario de una empresa y lo envía por correo electrónico.
     *
     * <p>El flujo completo es:
     * <ol>
     *   <li>Consultar el inventario de la empresa.</li>
     *   <li>Generar el PDF con iText o similar (adaptador de infraestructura).</li>
     *   <li>Enviar el PDF como adjunto via {@code NotificacionEmailPort}.</li>
     * </ol>
     * </p>
     *
     * @param request DTO con el NIT de empresa, email destinatario, asunto y mensaje adicional;
     *                nunca {@code null}.
     * @throws com.litethinking.backend.domain.exception.EmpresaNotFoundException
     *         si la empresa referenciada en el request no existe.
     * @throws RuntimeException si el envío del correo falla por problemas del proveedor SMTP.
     */
    void generarYEnviarPorCorreo(InventarioPdfEmailRequest request);
}
