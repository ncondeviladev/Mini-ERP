package com.erp.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de tests para el modelo {@link Producto}.
 * Verifica la correcta creación de objetos Producto y el funcionamiento
 * de sus métodos.
 */
class ProductoTest {

    /**
     * Test para el constructor principal y los getters.
     * Comprueba que todos los atributos se inicializan correctamente.
     */
    @Test
    void testConstructorYGetters() {
        Producto producto = new Producto(1, "Laptop", "Laptop de 15 pulgadas", "Electrónica", 999.99, 50);

        assertEquals(1, producto.getId());
        assertEquals("Laptop", producto.getNombre());
        assertEquals("Laptop de 15 pulgadas", producto.getDescripcion());
        assertEquals("Electrónica", producto.getCategoria());
        assertEquals(999.99, producto.getPrecioUnitario());
        assertEquals(50, producto.getStock());
    }
}
