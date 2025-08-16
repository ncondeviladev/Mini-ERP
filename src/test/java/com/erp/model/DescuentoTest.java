package com.erp.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de tests para el modelo {@link Descuento}.
 * Verifica la correcta creación de objetos Descuento y el funcionamiento
 * de sus métodos.
 */
class DescuentoTest {

    /**
     * Test para el constructor principal y los getters.
     * Comprueba que todos los atributos se inicializan correctamente.
     */
    @Test
    void testConstructorYGetters() {
        LocalDate inicio = LocalDate.of(2024, 1, 1);
        LocalDate fin = LocalDate.of(2024, 12, 31);
        Descuento descuento = new Descuento(1, 101, "Descuento Anual", 15.5, inicio, fin, true);

        assertEquals(1, descuento.getId());
        assertEquals(101, descuento.getClienteId());
        assertEquals("Descuento Anual", descuento.getDescripcion());
        assertEquals(15.5, descuento.getPorcentaje());
        assertEquals(inicio, descuento.getFechaInicio());
        assertEquals(fin, descuento.getFechaFin());
        assertTrue(descuento.isActivo());
    }

    /**
     * Test para los métodos que devuelven las fechas formateadas.
     * Comprueba que el formato de fecha es el esperado (dd-MM-yyyy).
     */
    @Test
    void testFechasFormateadas() {
        LocalDate inicio = LocalDate.of(2023, 5, 20);
        LocalDate fin = LocalDate.of(2023, 6, 20);
        Descuento descuento = new Descuento(1, 1, "Test", 10.0, inicio, fin, true);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        assertEquals(inicio.format(formatter), descuento.getFechaInicioFormatted());
        assertEquals(fin.format(formatter), descuento.getFechaFinFormatted());
    }
}
