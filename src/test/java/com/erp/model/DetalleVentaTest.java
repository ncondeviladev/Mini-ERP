package com.erp.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de tests para el modelo {@link DetalleVenta}.
 * Verifica la correcta inicialización de los detalles de venta y el cálculo del subtotal.
 */
class DetalleVentaTest {

    /**
     * Test para el constructor de {@code DetalleVenta}.
     * Comprueba que todos los atributos se asignan correctamente.
     */
    @Test
    void testConstructor() {
        Producto producto = new Producto(1, "Producto Test", "Descripción Test", "Categoría Test", 10.0, 100);
        DetalleVenta detalle = new DetalleVenta(1, 100, producto, 5, 10.0);

        assertEquals(1, detalle.getId());
        assertEquals(100, detalle.getVentaId());
        assertEquals(producto, detalle.getProducto());
        assertEquals(5, detalle.getCantidad());
        assertEquals(10.0, detalle.getPrecioUnitario());
    }

    /**
     * Test para el método {@code getSubTotal()}.
     * Verifica que el subtotal se calcula correctamente (cantidad * precioUnitario).
     */
    @Test
    void testGetSubTotal() {
        Producto producto = new Producto(1, "Producto Test", "Descripción Test", "Categoría Test", 10.0, 100);
        DetalleVenta detalle1 = new DetalleVenta(1, 100, producto, 5, 10.0);
        assertEquals(50.0, detalle1.getSubTotal(), 0.001); // Usar delta para doubles

        DetalleVenta detalle2 = new DetalleVenta(2, 101, producto, 2, 15.5);
        assertEquals(31.0, detalle2.getSubTotal(), 0.001);

        DetalleVenta detalle3 = new DetalleVenta(3, 102, producto, 0, 20.0);
        assertEquals(0.0, detalle3.getSubTotal(), 0.001);
    }
}
