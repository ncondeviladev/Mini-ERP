package com.erp.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de tests para el modelo {@link Cliente}.
 * Verifica la correcta creación de clientes de tipo 'Particular' y 'Empresa'
 * a través de sus métodos factory estáticos.
 */
class ClienteTest {

    /**
     * Test para el método factory {@code crearParticular}.
     * Comprueba que todos los atributos se asignan correctamente y que los campos
     * específicos de 'Empresa' son nulos.
     */
    @Test
    void testCrearParticular() {
        LocalDate fechaAlta = LocalDate.now();
        Cliente particular = Cliente.crearParticular(1, "test@test.com", "666555444", "Calle Sol 1", "12345678A", fechaAlta, "Pepe", "Gotera");

        assertEquals(1, particular.getId());
        assertEquals("Particular", particular.getTipoCliente());
        assertEquals("Pepe", particular.getNombre());
        assertEquals("Gotera", particular.getApellidos());
        assertEquals("12345678A", particular.getCifnif());
        assertEquals("test@test.com", particular.getEmail());
        assertEquals(fechaAlta, particular.getFechaAlta());
        assertNull(particular.getRazonSocial(), "La razón social debe ser nula para un particular.");
        assertNull(particular.getPersonaContacto(), "La persona de contacto debe ser nula para un particular.");
    }

    /**
     * Test para el método factory {@code crearEmpresa}.
     * Comprueba que todos los atributos se asignan correctamente y que los campos
     * específicos de 'Particular' son nulos.
     */
    @Test
    void testCrearEmpresa() {
        LocalDate fechaAlta = LocalDate.now();
        Cliente empresa = Cliente.crearEmpresa(2, "info@empresa.com", "912345678", "Av. Principal 2", "B12345678", fechaAlta, "Mi Empresa S.L.", "Ana García");

        assertEquals(2, empresa.getId());
        assertEquals("Empresa", empresa.getTipoCliente());
        assertEquals("Mi Empresa S.L.", empresa.getRazonSocial());
        assertEquals("Ana García", empresa.getPersonaContacto());
        assertEquals("B12345678", empresa.getCifnif());
        assertEquals("info@empresa.com", empresa.getEmail());
        assertEquals(fechaAlta, empresa.getFechaAlta());
        assertNull(empresa.getNombre(), "El nombre debe ser nulo para una empresa.");
        assertNull(empresa.getApellidos(), "Los apellidos deben ser nulos para una empresa.");
    }
}
