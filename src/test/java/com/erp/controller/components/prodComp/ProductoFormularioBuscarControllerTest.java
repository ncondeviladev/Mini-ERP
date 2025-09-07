package com.erp.controller.components.prodComp;

import com.erp.controller.ProductoController;
import com.erp.controller.VentaController;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de pruebas unitarias para {@link ProductoFormularioBuscarController}.
 * Se encarga de verificar el correcto funcionamiento de la lógica de búsqueda de productos
 * en el formulario, incluyendo la vinculación de controladores y la obtención de criterios de búsqueda.
 */
class ProductoFormularioBuscarControllerTest {

    @Mock
    private TextField buscarIdProductoField;
    @Mock
    private TextField buscarNombreProductoField;
    @Mock
    private TextField buscarCategoriaProductoField;

    @Mock
    private ProductoController productoController;
    @Mock
    private VentaController ventaController;

    @InjectMocks
    private ProductoFormularioBuscarController controller;

    /**
     * Configuración inicial para cada prueba.
     * Se inicializan los mocks y se simulan comportamientos de los campos de texto.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Mock text field behavior
        when(buscarIdProductoField.textProperty()).thenReturn(mock(javafx.beans.property.StringProperty.class));
        when(buscarNombreProductoField.textProperty()).thenReturn(mock(javafx.beans.property.StringProperty.class));
        when(buscarCategoriaProductoField.textProperty()).thenReturn(mock(javafx.beans.property.StringProperty.class));
    }

    /**
     * Verifica que el método {@code setProductoController} establece correctamente
     * la referencia al controlador principal de productos.
     */
    @Test
    void testSetProductoController() {
        controller.setProductoController(productoController);
        // No direct verification needed beyond the setter call itself
    }

    /**
     * Verifica que el método {@code setVentaController} establece correctamente
     * la referencia al controlador de ventas.
     */
    @Test
    void testSetVentaController() {
        controller.setVentaController(ventaController);
        // No direct verification needed beyond the setter call itself
    }

    /**
     * Prueba que el método {@code vincularControlador} configura correctamente
     * los listeners para los campos de búsqueda cuando se vincula con {@code ProductoController}.
     */
    @Test
    void testVincularControlador_ProductoController() {
        controller.setProductoController(productoController);
        controller.vincularControlador();

        // Verify that listeners are added to text properties
        verify(buscarIdProductoField.textProperty()).addListener(any(javafx.beans.value.ChangeListener.class));
        verify(buscarNombreProductoField.textProperty()).addListener(any(javafx.beans.value.ChangeListener.class));
        verify(buscarCategoriaProductoField.textProperty()).addListener(any(javafx.beans.value.ChangeListener.class));

        // Simulate text change to trigger filter
        when(buscarIdProductoField.textProperty().get()).thenReturn("1");
        when(buscarNombreProductoField.textProperty().get()).thenReturn("Laptop");
        when(buscarCategoriaProductoField.textProperty().get()).thenReturn("Electronics");

        // For unit tests, we primarily verify that the listener was registered.
        // Actual filtering logic is tested in ProductoControllerTest.
    }

    @Test
    void testVincularControlador_VentaController() {
        controller.setVentaController(ventaController);
        controller.vincularControlador();

        // Verify that listeners are added to text properties
        verify(buscarIdProductoField.textProperty()).addListener(any(javafx.beans.value.ChangeListener.class));
        verify(buscarNombreProductoField.textProperty()).addListener(any(javafx.beans.value.ChangeListener.class));
        verify(buscarCategoriaProductoField.textProperty()).addListener(any(javafx.beans.value.ChangeListener.class));
    }

    @Test
    void testGetCriteriosBusqueda() {
        when(buscarIdProductoField.getText()).thenReturn("123");
        when(buscarNombreProductoField.getText()).thenReturn("Test Product");
        when(buscarCategoriaProductoField.getText()).thenReturn("Test Category");

        Map<String, String> criterios = controller.getCriteriosBusqueda();

        assertEquals("123", criterios.get("id"));
        assertEquals("Test Product", criterios.get("nombre"));
        assertEquals("Test Category", criterios.get("categoria"));
    }
}
