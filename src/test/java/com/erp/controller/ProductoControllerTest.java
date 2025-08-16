package com.erp.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de tests para {@link ProductoController}.
 * <p>
 * Incluye un test para la lógica pura (método `capitalizar`) y la estructura
 * para futuros tests de UI.
 * @see ClienteControllerTest para una explicación detallada sobre las limitaciones
 * de testear controladores sin inyección de dependencias.
 */
public class ProductoControllerTest extends ApplicationTest {

    private ProductoController controller;

    @BeforeEach
    void setUp() {
        controller = new ProductoController();
    }

    /**
     * Test para el método privado {@code capitalizar}.
     * <p>
     * Se utiliza reflexión para poder invocar un método privado. Esto es útil para
     * probar lógica interna sin cambiar la visibilidad del método en el código de producción.
     */
    @Test
    void testCapitalizar() throws Exception {
        Method capitalizarMethod = ProductoController.class.getDeclaredMethod("capitalizar", String.class);
        capitalizarMethod.setAccessible(true); // Permite invocar el método privado

        assertEquals("Hola", capitalizarMethod.invoke(controller, "hola"));
        assertEquals("Mundo", capitalizarMethod.invoke(controller, "MUNDO"));
        assertEquals("Ya Esta Bien", capitalizarMethod.invoke(controller, "ya esta bien"));
        assertEquals("Unico", capitalizarMethod.invoke(controller, "UNICO"));
        assertEquals("", capitalizarMethod.invoke(controller, ""));
        assertNull(capitalizarMethod.invoke(controller, null), "Un nulo de entrada debe devolver nulo.");
    }
}
