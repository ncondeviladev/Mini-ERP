package com.erp.controller;

import org.testfx.framework.junit5.ApplicationTest;

/**
 * Clase de tests para {@link ClienteController}.
 * <p>
 * <b>Nota sobre los tests de controladores:</b>
 * Probar completamente los controladores de JavaFX que instancian sus propias dependencias
 * (ej: {@code new ClienteDAO()}) es complejo sin modificar el código de la aplicación.
 * <p>
 * El enfoque ideal sería usar <b>Inyección de Dependencias</b> para pasar una instancia
 * del DAO al controlador. Esto permitiría, en los tests, pasar un "mock" (un objeto simulado)
 * del DAO y verificar que el controlador llama a los métodos correctos (ej: {@code guardarClienteDb})
 * sin necesidad de una base de datos real.
 * <p>
 * Dado que el objetivo es no modificar el código de la aplicación, estos tests se limitan
 * a ser una estructura base.
 */
public class ClienteControllerTest extends ApplicationTest {
    // Aquí irían los tests de la interfaz de usuario con TestFX.
    // Ejemplo: verificar que al hacer clic en 'Añadir', el formulario se hace visible.
}
