package com.erp.utils;

import com.erp.model.Cliente;
import com.erp.model.Descuento;
import com.erp.model.DetalleVenta;
import com.erp.model.Producto;
import com.erp.model.Venta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class FacturaPDFGeneratorTest {

    @TempDir
    Path tempDir; // Directorio temporal para los PDFs generados

    private Venta ventaDePrueba;
    private Cliente clienteDePrueba;
    private Producto producto1;
    private Producto producto2;
    private DetalleVenta detalle1;
    private DetalleVenta detalle2;
    private Descuento descuento1;

    @BeforeEach
    void setUp() {
        // Inicializar datos de prueba para cada test
        clienteDePrueba = Cliente.crearParticular(1, "juan.perez@example.com", "123456789", "Calle Falsa 123", "12345678A", LocalDate.now(), "Juan", "Perez");
        
        producto1 = new Producto(1, "Laptop", "Descripción de Laptop", "Electrónica", 1200.00, 10);
        producto2 = new Producto(2, "Ratón", "Descripción de Ratón", "Electrónica", 25.00, 50);

        detalle1 = new DetalleVenta(1, 1, producto1, 1, 1200.00);
        detalle2 = new DetalleVenta(2, 1, producto2, 2, 25.00);

        descuento1 = new Descuento(1, clienteDePrueba.getId(), "Descuento Navidad", 10.0, LocalDate.now(), LocalDate.now().plusDays(30));

        // Calculate total for ventaDePrueba
        double totalCalculated = detalle1.getSubTotal() + detalle2.getSubTotal();
        totalCalculated = totalCalculated * (1 - (descuento1.getPorcentaje() / 100.0));

        ventaDePrueba = new Venta(1, clienteDePrueba, Collections.singletonList(descuento1), Arrays.asList(detalle1, detalle2), LocalDate.now(), totalCalculated);
    }

    @Test
    @DisplayName("Debería generar un PDF de factura sin errores y verificar la creación del archivo")
    void deberiaGenerarPDFDeFacturaSinErroresYVerificarArchivo() {
        assertDoesNotThrow(() -> FacturaPDFGenerator.generateInvoicePDF(ventaDePrueba, tempDir.toString()),
                           "La generación del PDF no debería lanzar excepciones.");
        
        // Construir el nombre de archivo esperado
        String nombreArchivoEsperado = String.format("Factura_%d_%s.pdf", 
                                                    ventaDePrueba.getId() != null ? ventaDePrueba.getId() : 0, 
                                                    ventaDePrueba.getFecha().format(DateTimeFormatter.ISO_LOCAL_DATE));
        Path rutaArchivoEsperado = tempDir.resolve(nombreArchivoEsperado);

        // Verificar que el archivo existe
        assertTrue(Files.exists(rutaArchivoEsperado),
                   "El archivo PDF debería haber sido creado en el directorio temporal.");
        
        // Verificar que la ruta devuelta por getInvoiceFilePath() es correcta
        assertEquals(rutaArchivoEsperado.toString(), FacturaPDFGenerator.getInvoiceFilePath(),
                     "La ruta del archivo generada debería coincidir con la esperada.");
    }

    @Test
    @DisplayName("Debería generar un PDF de factura con múltiples detalles de venta")
    void deberiaGenerarPDFConMultiplesDetalles() {
        assertDoesNotThrow(() -> FacturaPDFGenerator.generateInvoicePDF(ventaDePrueba, tempDir.toString()),
                           "La generación del PDF con múltiples detalles no debería lanzar excepciones.");
    }

    @Test
    @DisplayName("Debería generar un PDF de factura sin descuentos")
    void deberiaGenerarPDFSinDescuentos() {
        double totalCalculatedWithoutDiscount = detalle1.getSubTotal() + detalle2.getSubTotal();
        Venta ventaSinDescuentos = new Venta(2, clienteDePrueba, Arrays.asList(detalle1, detalle2), LocalDate.now(), totalCalculatedWithoutDiscount);
        
        assertDoesNotThrow(() -> FacturaPDFGenerator.generateInvoicePDF(ventaSinDescuentos, tempDir.toString()),
                           "La generación del PDF sin descuentos no debería lanzar excepciones.");
    }

    @Test
    @DisplayName("Debería generar un PDF de factura para un cliente nulo")
    void deberiaGenerarPDFConClienteNulo() {
        double totalCalculatedWithoutDiscount = detalle1.getSubTotal() + detalle2.getSubTotal();
        Venta ventaConClienteNulo = new Venta(3, null, Arrays.asList(detalle1, detalle2), LocalDate.now(), totalCalculatedWithoutDiscount);
        
        assertDoesNotThrow(() -> FacturaPDFGenerator.generateInvoicePDF(ventaConClienteNulo, tempDir.toString()),
                           "La generación del PDF con cliente nulo no debería lanzar excepciones.");
    }

    @Test
    @DisplayName("Debería generar un PDF de factura con detalles de venta vacíos")
    void deberiaGenerarPDFConDetallesVacios() {
        Venta ventaConDetallesVacios = new Venta(4, clienteDePrueba, Collections.emptyList(), LocalDate.now(), 0.0);
        
        assertDoesNotThrow(() -> FacturaPDFGenerator.generateInvoicePDF(ventaConDetallesVacios, tempDir.toString()),
                           "La generación del PDF con detalles vacíos no debería lanzar excepciones.");
    }
}