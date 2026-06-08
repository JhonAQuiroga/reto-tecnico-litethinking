package com.litethinking.backend.application.port.out;

import com.litethinking.backend.application.dto.response.InventarioResponse;

import java.util.List;

/**
 * Puerto de salida: contrato para la generación de archivos PDF.
 *
 * <p>Clean Architecture — Application Layer: define las operaciones necesarias
 * para generar reportes en formato PDF. La implementación concreta residirá
 * en la capa de infraestructura utilizando librerías como iText u OpenPDF.</p>
 */
public interface GeneradorPdfPort {

    /**
     * Genera un reporte de inventario en formato PDF.
     *
     * @param items lista de registros de inventario a incluir en el reporte.
     * @param nombreEmpresa nombre de la empresa a la que pertenece el inventario.
     * @return arreglo de bytes con el contenido del archivo PDF.
     */
    byte[] generarReporteInventario(List<InventarioResponse> items, String nombreEmpresa);
}
