package com.erp.utils;

/**
 * Clase de utilidad que proporciona métodos estáticos para la validación de
 * formatos de datos comunes.
 * <p>
 * Esta clase no es instanciable y todos sus métodos son estáticos para un fácil
 * acceso.
 */
public final class ValidationUtils {

    /**
     * Constructor privado para prevenir la instanciación de la clase de utilidad.
     */
    private ValidationUtils() {
    }

    /**
     * Valida si una cadena de texto tiene un formato de email estándar.
     * 
     * @param email La dirección de email a validar.
     * @return {@code true} si el email tiene un formato válido, {@code false} en
     *         caso contrario.
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        // Expresión regular para un formato de email estándar.
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }

    /**
     * Valida si una cadena de texto corresponde a un número de teléfono español (9
     * dígitos).
     * 
     * @param tlf El número de teléfono a validar.
     * @return {@code true} si el teléfono tiene un formato válido, {@code false} en
     *         caso contrario.
     */
    public static boolean isValidTlf(String tlf) {
        if (tlf == null || tlf.trim().isEmpty()) {
            return false;
        }
        // Expresión regular que busca exactamente 9 caracteres numéricos.
        String tlfRegex = "^[0-9]{9}$";
        return tlf.matches(tlfRegex);
    }

    /**
     * Valida si una cadena de texto tiene el formato estructural de un NIF, CIF o NIE español.
     * - NIF: 8 números y 1 letra.
     * - CIF/NIE: 1 letra, 7 números y 1 carácter de control (letra o número).
     * @param nifCif El identificador fiscal a validar.
     * @return {@code true} si el formato es válido, {@code false} en caso contrario.
     */
    public static boolean isValidNifCif(String nifCif) {
        if (nifCif == null || nifCif.trim().isEmpty()) {
            return false;
        }
        // Expresión regular que valida los formatos más comunes de NIF, CIF y NIE.
        String nifCifRegex = "^([A-Z]{1}[0-9]{7}[A-Z0-9]{1}|[0-9]{8}[A-Z]{1})$";

        // Comprueba si la cadena (en mayúsculas para ser case-insensitive) coincide con el patrón.
        return nifCif.trim().toUpperCase().matches(nifCifRegex);
    }
}
