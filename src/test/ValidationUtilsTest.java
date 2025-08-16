package com.erp.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de tests para la clase de utilidades {@link ValidationUtils}.
 * Verifica que los métodos de validación de NIF/CIF, email y teléfono
 * funcionan correctamente para casos válidos e inválidos.
 */
class ValidationUtilsTest {

    /**
     * Tests para el método {@code isValidNifCif} con valores válidos.
     */
    @Test
    void testNifCifValidos() {
        assertTrue(ValidationUtils.isValidNifCif("12345678Z"), "NIF válido debería retornar true.");
        assertTrue(ValidationUtils.isValidNifCif("A12345678"), "CIF válido debería retornar true.");
        assertTrue(ValidationUtils.isValidNifCif("X12345678Z"), "NIE válido debería retornar true.");
    }

    /**
     * Tests para el método {@code isValidNifCif} con valores invál
    @Test
    void testNifCifInvalidos() {
        assertFalse(ValidationUtils.isValidNifCif("12345"), "NIF/CIF demasiado corto debería ser inválido.");
        assertFalse(ValidationUtils.isValidNifCif("123456789"), "NIF sin letra debería ser inválido.");
        assertFalse(ValidationUtils.isValidNifCif("A1234567Z"), "CIF con letra incorrecta debería ser inválido.");
        assertFalse(ValidationUtils.isValidNifCif(null), "NIF/CIF nulo debería ser inválido.");
        assertFalse(ValidationUtils.isValidNifCif(""), "NIF/CIF vacío debería ser inválido.");
    }

    // --- Tests para isValidEmail ---
    @Test
    void testEmailValidos() {
        assertTrue(ValidationUtils.isValidEmail("test@example.com"), "Email estándar válido.");
        assertTrue(ValidationUtils.isValidEmail("test.name@example.co.uk"), "Email con subdominio válido.");
    }

    @Test
    void testEmailInvalidos() {
        assertFalse(ValidationUtils.isValidEmail("test@.com"), "Email inválido (sin dominio).");
        assertFalse(ValidationUtils.isValidEmail("test@example"), "Email inválido (sin .tld).");
        assertFalse(ValidationUtils.isValidEmail("testexample.com"), "Email inválido (sin @).");
        assertFalse(ValidationUtils.isValidEmail(null), "Email nulo debería ser inválido.");
    }

    // --- Tests para isValidTlf ---
    @Test
    void testTelefonoValidos() {
        assertTrue(ValidationUtils.isValidTlf("612345678"), "Móvil válido.");
        assertTrue(ValidationUtils.isValidTlf("912345678"), "Fijo válido.");
    }

    @Test
    void testTelefonoInvalidos() {
        assertFalse(ValidationUtils.isValidTlf("123456789"), "Teléfono que no empieza por 6,7,8,9 inválido.");
        assertFalse(ValidationUtils.isValidTlf("61234567"), "Teléfono demasiado corto inválido.");
        assertFalse(ValidationUtils.isValidTlf(null), "Teléfono nulo debería ser inválido.");
    }
}