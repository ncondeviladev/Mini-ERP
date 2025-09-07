package com.erp.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de tests para el modelo {@link Venta}.
 * Verifica la correcta inicialización de las ventas y la gestión de sus componentes.
 */
class VentaTest {

    /**
     * Test para el constructor completo de {@code Venta}.
     * Comprueba que todos los atributos, incluyendo las listas de detalles y descuentos,
     * se asignan correctamente y no son nulas.
     */
    @Test
    void testConstructorCompleto() {
        Cliente cliente = Cliente.crearParticular(1, "test@test.com", "123", "dir", "nif", LocalDate.now(), "Nombre", "Apellido");
        Producto producto = new Producto(1, "Prod1", "Desc1", "Cat1", 10.0, 10);
        DetalleVenta detalle1 = new DetalleVenta(1, 1, producto, 2, 10.0);
        List<DetalleVenta> detalles = Arrays.asList(detalle1);
        Descuento descuento1 = new Descuento(1, 1, "Desc1", 5.0, LocalDate.now(), LocalDate.now().plusDays(10));
        List<Descuento> descuentos = Arrays.asList(descuento1);
        LocalDate fecha = LocalDate.now();
        double total = 20.0;

        Venta venta = new Venta(1, cliente, descuentos, detalles, fecha, total);

        assertEquals(1, venta.getId());
        assertEquals(cliente, venta.getCliente());
        assertEquals(descuentos, venta.getDescuentos());
        assertEquals(detalles, venta.getDetalleVenta());
        assertEquals(fecha, venta.getFecha());
        assertEquals(total, venta.getTotal(), 0.001);
        assertNotNull(venta.getDescuentos());
        assertNotNull(venta.getDetalleVenta());
    }

    /**
     * Test para el constructor de conveniencia de {@code Venta} (sin descuentos iniciales).
     * Verifica que la lista de descuentos se inicializa como una lista vacía por defecto.
     */
    @Test
    void testConstructorSinDescuentos() {
        Cliente cliente = Cliente.crearParticular(1, "test@test.com", "123", "dir", "nif", LocalDate.now(), "Nombre", "Apellido");
        Producto producto = new Producto(1, "Prod1", "Desc1", "Cat1", 10.0, 10);
        DetalleVenta detalle1 = new DetalleVenta(1, 1, producto, 2, 10.0);
        List<DetalleVenta> detalles = Arrays.asList(detalle1);
        LocalDate fecha = LocalDate.now();
        double total = 20.0;

        Venta venta = new Venta(1, cliente, detalles, fecha, total);

        assertEquals(1, venta.getId());
        assertEquals(cliente, venta.getCliente());
        assertTrue(venta.getDescuentos().isEmpty(), "La lista de descuentos debe estar vacía por defecto.");
        assertEquals(detalles, venta.getDetalleVenta());
        assertEquals(fecha, venta.getFecha());
        assertEquals(total, venta.getTotal(), 0.001);
        assertNotNull(venta.getDescuentos());
        assertNotNull(venta.getDetalleVenta());
    }

    /**
     * Test para verificar que las listas de descuentos y detalles no son nulas
     * incluso si se pasan nulas al constructor completo.
     */
    @Test
    void testConstructorConListasNulas() {
        Cliente cliente = Cliente.crearParticular(1, "test@test.com", "123", "dir", "nif", LocalDate.now(), "Nombre", "Apellido");
        LocalDate fecha = LocalDate.now();
        double total = 0.0;

        Venta venta = new Venta(1, cliente, null, null, fecha, total);

        assertNotNull(venta.getDescuentos(), "La lista de descuentos no debería ser nula.");
        assertTrue(venta.getDescuentos().isEmpty(), "La lista de descuentos debería estar vacía.");
        assertNotNull(venta.getDetalleVenta(), "La lista de detalles de venta no debería ser nula.");
        assertTrue(venta.getDetalleVenta().isEmpty(), "La lista de detalles de venta debería estar vacía.");
    }
}
