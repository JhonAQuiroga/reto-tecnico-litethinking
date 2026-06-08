package com.litethinking.backend.infrastructure.adapter.out.pdf;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.litethinking.backend.application.dto.response.InventarioResponse;
import com.litethinking.backend.application.port.out.GeneradorPdfPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
public class PdfReportGenerator implements GeneradorPdfPort {

    @Override
    public byte[] generarReporteInventario(List<InventarioResponse> items, String nombreEmpresa) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);
            document.setMargins(36, 36, 36, 36);

            // Título
            Paragraph titulo = new Paragraph("REPORTE DE INVENTARIO")
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(titulo);

            // Información de cabecera
            String fechaGeneracion = ZonedDateTime.now().format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z")
            );
            document.add(new Paragraph("Empresa: " + nombreEmpresa).setBold().setMarginTop(10));
            document.add(new Paragraph("Fecha de generación: " + fechaGeneracion).setMarginBottom(20));

            // Tabla de productos
            Table table = new Table(UnitValue.createPercentArray(new float[]{15, 35, 10, 15, 12, 13}))
                    .useAllAvailableWidth();

            // Encabezados
            String[] encabezados = {"Código", "Producto", "Stock", "Precio COP", "Precio USD", "Precio EUR"};
            for (String encabezado : encabezados) {
                Cell cell = new Cell().add(new Paragraph(encabezado).setBold())
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                        .setTextAlignment(TextAlignment.CENTER);
                table.addHeaderCell(cell);
            }

            // Datos
            for (InventarioResponse item : items) {
                table.addCell(new Cell().add(new Paragraph(item.productoCodigo())));
                table.addCell(new Cell().add(new Paragraph(item.productoNombre())));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(item.cantidad()))).setTextAlignment(TextAlignment.RIGHT));
                table.addCell(new Cell().add(new Paragraph(item.precioCop() != null ? item.precioCop().toPlainString() : "N/A")).setTextAlignment(TextAlignment.RIGHT));
                table.addCell(new Cell().add(new Paragraph(item.precioUsd() != null ? item.precioUsd().toPlainString() : "N/A")).setTextAlignment(TextAlignment.RIGHT));
                table.addCell(new Cell().add(new Paragraph(item.precioEur() != null ? item.precioEur().toPlainString() : "N/A")).setTextAlignment(TextAlignment.RIGHT));
            }

            document.add(table);
            document.add(new Paragraph("Total de productos: " + items.size()).setMarginTop(10));

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Error generando PDF de inventario para empresa {}", nombreEmpresa, e);
            throw new RuntimeException("Error al generar el reporte PDF: " + e.getMessage(), e);
        }
    }
}
