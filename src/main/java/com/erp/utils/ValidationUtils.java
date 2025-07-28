package com.erp.utils;

/**
 * Clase de utilidad que proporciona métodos estáticos para validar el formato
 * de diferentes tipos de datos, como email, teléfono, CIF y NIF.
 * Autor: Noé
 */
public class ValidationUtils {
    
    /**
     * Valida si una cadena de texto tiene el formato de una dirección de correo electrónico.
     * @param email La dirección de email a validar.
     * @return {@code true} si el formato del email es válido, {@code false} en caso contrario.
     */
    public static boolean isValidEMail(String email) {
        // Expresión regular para un formato de email estándar.
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        
        // Comprueba si la cadena coincide con el patrón de la expresión regular.
        if(email.matches(emailRegex)) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Valida si una cadena de texto corresponde a un número de teléfono español (9 dígitos).
     * @param tlf El número de teléfono a validar.
     * @return {@code true} si el formato del teléfono es válido, {@code false} en caso contrario.
     */
    public static boolean isValidTlf(String tlf) {
        // Expresión regular que busca exactamente 9 caracteres numéricos.
        String tlfRegex = "^[0-9]{9}$";

        // Comprueba si la cadena coincide con el patrón.
        if(tlf.matches(tlfRegex)) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Valida si una cadena de texto tiene el formato estructural de un CIF español.
     * El formato esperado es: una letra mayúscula, seguida de 7 números y un carácter final (letra mayúscula o número).
     * @param cif El CIF a validar.
     * @return {@code true} si el formato del CIF es válido, {@code false} en caso contrario.
     */
    public static boolean isValidCIF(String cif) {
        // Expresión regular para el formato: 1 letra mayúscula, 7 números, 1 carácter alfanumérico mayúsculo.
        String cifRegex = "^[A-Z]{1}[0-9]{7}[A-Z0-9]{1}$";

        // Comprueba si la cadena coincide con el patrón.
        if(cif.matches(cifRegex)) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Valida si una cadena de texto tiene el formato estructural de un NIF español.
     * El formato esperado es: 8 números seguidos de una letra mayúscula.
     * @param nif El NIF a validar.
     * @return {@code true} si el formato del NIF es válido, {@code false} en caso contrario.
     */
    public static boolean isValidNIF(String nif) {
        
        String nifRegex = "^[0-9]{8}[A-Z]{1}$";

        if(nif.matches(nifRegex)) {
            return true;
        } else {
            return false;
        }
    }
}
