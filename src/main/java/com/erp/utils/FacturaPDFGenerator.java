/*
package com.erp.utils;

import com.erp.model.Venta;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class FacturaPDFGenerator {

    private static final String DEST = "factura.pdf"; // Default output file name

    public static void generateInvoicePDF(Venta venta) throws IOException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();

        PdfWriter writer = new PdfWriter(DEST);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Title
        document.add(new Paragraph("FACTURA").setFontSize(24).setBold());
        document.add(new Paragraph("Fecha: " + venta.getFechaVenta().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));

        // Client Information
        document.add(new Paragraph("Datos del Cliente:").setFontSize(14).setBold());
        if (venta.getCliente() != null) {
            document.add(new Paragraph("Nombre: " + venta.getCliente().getNombre() + " " + venta.getCliente().getApellidos()));
            document.add(new Paragraph("CIF/NIF: " + venta.getCliente().getCifnif()));
            // Add more client details as needed
        }

        // Sale Details Table
        document.add(new Paragraph("Detalles de la Venta:").setFontSize(14).setBold());
        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 1, 1, 1}));
        table.setWidth(UnitValue.createPercentValue(100));

        table.addHeaderCell("Producto");
        table.addHeaderCell("Cantidad");
        table.addHeaderCell("Precio Unitario");
        table.addHeaderCell("Subtotal");

        venta.getDetallesVenta().forEach(detalle -> {
            table.addCell(detalle.getProducto().getNombre());
            table.addCell(String.valueOf(detalle.getCantidad()));
            table.addCell(String.format("%.2f€", detalle.getPrecioUnitario()));
            table.addCell(String.format("%.2f€", detalle.getSubTotal()));
        });
        document.add(table);

        // Totals
        document.add(new Paragraph("Total: " + String.format("%.2f€", venta.getTotalFinal())).setFontSize(16).setBold());

        document.close();
        System.out.println("Factura generada en: " + file.getAbsolutePath());
    }

    public static String getInvoiceFilePath() {
        return new File(DEST).getAbsolutePath();
    }
}
*/