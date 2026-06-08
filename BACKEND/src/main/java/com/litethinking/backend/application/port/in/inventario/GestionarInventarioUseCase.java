package com.litethinking.backend.application.port.in.inventario;

import com.litethinking.backend.application.dto.request.InventarioPdfEmailRequest;
import com.litethinking.backend.application.dto.response.InventarioResponse;

import java.util.List;

/**
 * Puerto de entrada: caso de uso maestro para <strong>gestionar el inventario</strong>.
 *
 * <p>Clean Architecture — Application Layer: este puerto unifica toda la gestión del
 * inventario: consultas de stock, ajuste de cantidades y la generación + envío del
 * reporte PDF de inventario por correo electrónico.</p>
 *
 * <p><strong>Operaciones disponibles:</strong></p>
 * <ul>
 *   <li>Consultar el inventario completo o filtrado por empresa.</li>
 *   <li>Agregar stock a un producto de una empresa (reposición).</li>
 *   <li>Reducir stock al confirmar una orden (lógica validada en dominio).</li>
 *   <li>Generar el reporte PDF del inventario en bytes.</li>
 *   <li>Enviar el reporte PDF por correo al destinatario indicado.</li>
 * </ul>
 *
 * <p><strong>Invariantes protegidas:</strong></p>
 * <ul>
 *   <li>El stock nunca puede quedar negativo (protección en la entidad de dominio
 *       {@link com.litethinking.backend.domain.model.Inventario}).</li>
 *   <li>Solo se puede ajustar el inventario de productos y empresas existentes.</li>
 * </ul>
 */
public interface GestionarInventarioUseCase {

    /**
     * Recupera todos los registros de inventario del sistema.
     *
     * @return lista de {@link InventarioResponse} con datos enriquecidos;
     *         nunca {@code null}, puede estar vacía.
     */
    List<InventarioResponse> listarTodos();

    /**
     * Recupera el inventario de todos los productos de una empresa específica.
     *
     * <p>Método principal para generar el reporte de stock por empresa.</p>
     *
     * @param empresaNit NIT de la empresa (ej.: "900123456-1"); nunca {@code null}.
     * @return lista de {@link InventarioResponse} de la empresa; nunca {@code null}.
     * @throws com.litethinking.backend.domain.exception.EmpresaNotFoundException
     *         si la empresa no existe.
     */
    List<InventarioResponse> listarPorEmpresa(String empresaNit);

    /**
     * Agrega unidades al stock de un producto en una empresa (reposición de inventario).
     *
     * @param empresaNit     NIT de la empresa propietaria del stock; nunca {@code null}.
     * @param productoCodigo código del producto a reponer; nunca {@code null}.
     * @param cantidad       número de unidades a agregar; debe ser positivo.
     * @return {@link InventarioResponse} actualizado con el nuevo stock; nunca {@code null}.
     * @throws com.litethinking.backend.domain.exception.EmpresaNotFoundException
     *         si la empresa no existe.
     * @throws com.litethinking.backend.domain.exception.ProductoNotFoundException
     *         si el producto no existe en el inventario de la empresa.
     * @throws IllegalArgumentException si la cantidad es menor o igual a cero.
     */
    InventarioResponse agregarStock(String empresaNit, String productoCodigo, int cantidad);

    /**
     * Reduce el stock de un producto en una empresa (al confirmar una orden de compra).
     *
     * <p>La validación de disponibilidad de stock se delega a la entidad de dominio
     * {@link com.litethinking.backend.domain.model.Inventario}, que lanza
     * {@link com.litethinking.backend.domain.model.Inventario.StockInsuficienteException}
     * si no hay suficiente stock disponible.</p>
     *
     * @param empresaNit     NIT de la empresa propietaria del stock; nunca {@code null}.
     * @param productoCodigo código del producto; nunca {@code null}.
     * @param cantidad       número de unidades a descontar; debe ser positivo.
     * @return {@link InventarioResponse} actualizado con el nuevo stock; nunca {@code null}.
     * @throws com.litethinking.backend.domain.model.Inventario.StockInsuficienteException
     *         si el stock disponible es menor que la cantidad solicitada.
     */
    InventarioResponse reducirStock(String empresaNit, String productoCodigo, int cantidad);

    /**
     * Establece el stock de un producto en una cantidad absoluta.
     *
     * @param empresaNit     NIT de la empresa propietaria del stock; nunca {@code null}.
     * @param productoCodigo código del producto; nunca {@code null}.
     * @param nuevoStock     cantidad absoluta a establecer; no puede ser negativa.
     * @return {@link InventarioResponse} actualizado con el nuevo stock; nunca {@code null}.
     */
    InventarioResponse establecerStock(String empresaNit, String productoCodigo, int nuevoStock);

    /**
     * Genera el reporte PDF del inventario de una empresa y lo retorna como bytes.
     *
     * <p>El PDF incluye la lista completa de productos con su stock actual y precios
     * en COP, USD y EUR.</p>
     *
     * @param empresaNit NIT de la empresa; nunca {@code null}.
     * @return arreglo de bytes del PDF generado; nunca {@code null} ni vacío.
     * @throws com.litethinking.backend.domain.exception.EmpresaNotFoundException
     *         si la empresa no existe.
     */
    byte[] generarPdfInventario(String empresaNit);

    /**
     * Genera el PDF del inventario de una empresa y lo envía por correo electrónico.
     *
     * <p>Si la generación del PDF falla, el correo no se envía. Si el envío falla,
     * se propaga la excepción del proveedor hacia el adaptador de entrada.</p>
     *
     * @param request DTO con el NIT de empresa, destinatario, asunto y mensaje opcional;
     *                nunca {@code null}.
     * @throws com.litethinking.backend.domain.exception.EmpresaNotFoundException
     *         si la empresa referenciada no existe.
     * @throws RuntimeException si el envío del correo falla por problemas del proveedor SMTP.
     */
    void generarYEnviarPdfPorCorreo(InventarioPdfEmailRequest request);
}
