package com.erp.utils;

import com.erp.model.Cliente;
import com.erp.model.Descuento;
import com.erp.model.Venta;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class FacturaPDFGenerator {

    private static String lastGeneratedPath = "";
    private static final double TASA_IVA = 0.21;

    public static void generateInvoicePDF(Venta venta, String destDir) throws IOException {
        File dir = new File(destDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = String.format("Factura_%d_%s.pdf", 
                                    venta.getId() != null ? venta.getId() : 0, 
                                    venta.getFecha().format(DateTimeFormatter.ISO_LOCAL_DATE));
        String filePath = destDir + File.separator + fileName;
        lastGeneratedPath = filePath;

        PdfWriter writer = new PdfWriter(filePath);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        document.setMargins(36, 36, 36, 36);

        // --- DATOS DE LA EMPRESA Y FACTURA ---
        addHeader(document, venta);

        // --- DATOS DEL CLIENTE ---
        addClientInfo(document, venta.getCliente());

        // --- DETALLES DE LA VENTA ---
        addSaleDetails(document, venta);

        // --- TOTALES ---
        addTotals(document, venta);

        // --- PIE DE PÁGINA ---
        addFooter(document);

        document.close();
        System.out.println("Factura generada en: " + new File(filePath).getAbsolutePath());
    }

    private static void addHeader(Document document, Venta venta) {
        // --- DATOS DE EJEMPLO DE TU EMPRESA (MODIFICAR AQUÍ) ---
        String nombreEmpresa = "Tu Mini ERP, S.L.";
        String nifEmpresa = "B12345678";
        String direccionEmpresa = "Calle de la Factura, 123, 28080 Madrid";

        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
        headerTable.setWidth(UnitValue.createPercentValue(100));

        // Columna izquierda: Datos de la empresa
        Cell leftCell = new Cell().add(new Paragraph(nombreEmpresa).setBold().setFontSize(14));
        leftCell.add(new Paragraph(nifEmpresa));
        leftCell.add(new Paragraph(direccionEmpresa));
        leftCell.setBorder(null);
        headerTable.addCell(leftCell);

        // Columna derecha: Datos de la factura
        Cell rightCell = new Cell().setTextAlignment(TextAlignment.RIGHT);
        rightCell.add(new Paragraph("FACTURA").setBold().setFontSize(20));
        rightCell.add(new Paragraph("Nº Factura: " + (venta.getId() != null ? String.format("%05d", venta.getId()) : "N/A")));
        rightCell.add(new Paragraph("Fecha: " + venta.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        rightCell.setBorder(null);
        headerTable.addCell(rightCell);

        document.add(headerTable);
        document.add(new Paragraph("\n")); // Espacio
    }

    private static void addClientInfo(Document document, Cliente cliente) {
        document.add(new Paragraph("Datos del Cliente").setBold());
        if (cliente != null) {
            if (cliente.isEmpresa()) {
                document.add(new Paragraph("Razón Social: " + cliente.getRazonSocial()));
                if (cliente.getPersonaContacto() != null && !cliente.getPersonaContacto().isEmpty()) {
                    document.add(new Paragraph("A/A: " + cliente.getPersonaContacto()));
                }
            } else {
                document.add(new Paragraph("Nombre: " + cliente.getNombre() + " " + cliente.getApellidos()));
            }
            document.add(new Paragraph("CIF/NIF: " + cliente.getCifnif()));
        }
        document.add(new Paragraph("\n")); // Espacio
    }

    private static void addSaleDetails(Document document, Venta venta) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{4, 1, 2, 2}));
        table.setWidth(UnitValue.createPercentValue(100));

        // Cabecera de la tabla
        table.addHeaderCell(new Cell().add(new Paragraph("Producto")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold());
        table.addHeaderCell(new Cell().add(new Paragraph("Cantidad")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold().setTextAlignment(TextAlignment.CENTER));
        table.addHeaderCell(new Cell().add(new Paragraph("Precio Unitario")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold().setTextAlignment(TextAlignment.RIGHT));
        table.addHeaderCell(new Cell().add(new Paragraph("Subtotal")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold().setTextAlignment(TextAlignment.RIGHT));

        // Contenido de la tabla
        venta.getDetalleVenta().forEach(detalle -> {
            table.addCell(new Cell().add(new Paragraph(detalle.getProducto().getNombre())));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(detalle.getCantidad()))).setTextAlignment(TextAlignment.CENTER));
            table.addCell(new Cell().add(new Paragraph(String.format("%.2f €", detalle.getPrecioUnitario()))).setTextAlignment(TextAlignment.RIGHT));
            table.addCell(new Cell().add(new Paragraph(String.format("%.2f €", detalle.getSubTotal()))).setTextAlignment(TextAlignment.RIGHT));
        });

        document.add(table);
    }

    private static void addTotals(Document document, Venta venta) {
        // --- Cálculos ---
        double subtotalBruto = venta.getDetalleVenta().stream().mapToDouble(d -> d.getSubTotal()).sum();
        double porcentajeDescuentoTotal = venta.getDescuentos().stream().mapToDouble(Descuento::getPorcentaje).sum();
        double montoDescuento = subtotalBruto * (porcentajeDescuentoTotal / 100.0);
        double baseImponible = subtotalBruto - montoDescuento;
        double montoIva = baseImponible * TASA_IVA;
        double totalFinal = baseImponible + montoIva;

        // --- Tabla de Totales ---
        Table totalsTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
        totalsTable.setWidth(UnitValue.createPercentValue(50)).setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.RIGHT);

        totalsTable.addCell(createTotalCell("Subtotal:", false));
        totalsTable.addCell(createTotalCell(String.format("%.2f €", subtotalBruto), false));
        
        totalsTable.addCell(createTotalCell(String.format("Descuento (%.2f%%):", porcentajeDescuentoTotal), false));
        totalsTable.addCell(createTotalCell(String.format("-%.2f €", montoDescuento), false));

        totalsTable.addCell(createTotalCell("Base Imponible:", false));
        totalsTable.addCell(createTotalCell(String.format("%.2f €", baseImponible), false));

        totalsTable.addCell(createTotalCell(String.format("IVA (%.0f%%):", TASA_IVA * 100), false));
        totalsTable.addCell(createTotalCell(String.format("%.2f €", montoIva), false));

        totalsTable.addCell(createTotalCell("TOTAL:", true));
        totalsTable.addCell(createTotalCell(String.format("%.2f €", totalFinal), true));

        document.add(totalsTable);
    }

    private static Cell createTotalCell(String text, boolean isBold) {
        Paragraph p = new Paragraph(text);
        if (isBold) {
            p.setBold();
        }
        Cell cell = new Cell().add(p).setTextAlignment(TextAlignment.RIGHT);
        cell.setBorder(null);
        if(isBold) {
            cell.setBorderTop(new SolidBorder(ColorConstants.BLACK, 1));
        }
        return cell;
    }

    private static void addFooter(Document document) {
        document.add(new Paragraph("\n\nGracias por su compra.").setTextAlignment(TextAlignment.CENTER).setFontSize(10).setItalic());
    }

    public static String getInvoiceFilePath() {
        return lastGeneratedPath;
    }
}