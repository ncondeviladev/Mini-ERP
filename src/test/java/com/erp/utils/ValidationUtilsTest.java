package com.erp.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de tests para la utilidad {@link ValidationUtils}.
 * Verifica la correcta validación de formatos de email, teléfono y NIF/CIF.
 */
class ValidationUtilsTest {

    /**
     * Test para el método {@code isValidEmail()}.
     * Comprueba la validación de direcciones de correo electrónico.
     */
    @Test
    void testIsValidEmail() {
        // Casos válidos
        assertTrue(ValidationUtils.isValidEmail("test@example.com"));
        assertTrue(ValidationUtils.isValidEmail("john.doe123@sub.domain.co.uk"));
        assertTrue(ValidationUtils.isValidEmail("user+tag@domain.net"));
        assertTrue(ValidationUtils.isValidEmail("a@b.c"));

        // Casos inválidos
        assertFalse(ValidationUtils.isValidEmail("invalid-email"));
        assertFalse(ValidationUtils.isValidEmail("test@.com"));
        assertFalse(ValidationUtils.isValidEmail("@example.com"));
        assertFalse(ValidationUtils.isValidEmail("test@example"));
        assertFalse(ValidationUtils.isValidEmail("test example.com"));
        assertFalse(ValidationUtils.isValidEmail(null));
        assertFalse(ValidationUtils.isValidEmail(""));
        assertFalse(ValidationUtils.isValidEmail("   "));
    }

    /**
     * Test para el método {@code isValidTlf()}.
     * Comprueba la validación de números de teléfono españoles (9 dígitos).
     */
    @Test
    void testIsValidTlf() {
        // Casos válidos
        assertTrue(ValidationUtils.isValidTlf("600112233"));
        assertTrue(ValidationUtils.isValidTlf("912345678"));
        assertTrue(ValidationUtils.isValidTlf("789012345"));

        // Casos inválidos
        assertFalse(ValidationUtils.isValidTlf("12345678"));   // Menos de 9 dígitos
        assertFalse(ValidationUtils.isValidTlf("1234567890")); // Más de 9 dígitos
        assertFalse(ValidationUtils.isValidTlf("60011223A"));  // Caracter no numérico
        assertFalse(ValidationUtils.isValidTlf(" 600112233")); // Espacios
        assertFalse(ValidationUtils.isValidTlf(null));
        assertFalse(ValidationUtils.isValidTlf(""));
        assertFalse(ValidationUtils.isValidTlf("   "));
    }

    /**
     * Test para el método {@code isValidNifCif()}.
     * Comprueba la validación de formatos de NIF, CIF y NIE españoles.
     */
    @Test
    void testIsValidNifCif() {
        // Casos válidos (ejemplos de formato, no validación de letra de control real)
        // NIF
        assertTrue(ValidationUtils.isValidNifCif("12345678A"));
        assertTrue(ValidationUtils.isValidNifCif("87654321Z"));
        // CIF
        assertTrue(ValidationUtils.isValidNifCif("A12345678"));
        assertTrue(ValidationUtils.isValidNifCif("B87654321"));
        // NIE (formato X/Y/Z + 7 dígitos + letra)
        assertTrue(ValidationUtils.isValidNifCif("X1234567A"));
        assertTrue(ValidationUtils.isValidNifCif("Y8765432B"));
        assertTrue(ValidationUtils.isValidNifCif("Z1122334C"));

        // Casos inválidos
        assertFalse(ValidationUtils.isValidNifCif("1234567A"));   // NIF corto
        assertFalse(ValidationUtils.isValidNifCif("123456789A")); // NIF largo
        assertFalse(ValidationUtils.isValidNifCif("123456789"));  // NIF sin letra
        assertFalse(ValidationUtils.isValidNifCif("A1234567"));   // CIF corto
        assertFalse(ValidationUtils.isValidNifCif("A123456789")); // CIF largo
        assertFalse(ValidationUtils.isValidNifCif("12345678"));   // Solo números
        assertFalse(ValidationUtils.isValidNifCif("ABCDEFGHI"));  // Solo letras
        assertFalse(ValidationUtils.isValidNifCif(null));
        assertFalse(ValidationUtils.isValidNifCif(""));
        assertFalse(ValidationUtils.isValidNifCif("   "));
    }
}
