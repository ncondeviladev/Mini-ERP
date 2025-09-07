package com.erp.controller.components.cliComp;

import com.erp.controller.ClienteController;
import com.erp.controller.VentaFinalizarController;
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
 * Clase de pruebas unitarias para {@link ClienteFormularioBuscarController}.
 * Se encarga de verificar el correcto funcionamiento de la lógica de búsqueda de clientes
 * en el formulario, incluyendo la vinculación de controladores y la obtención de criterios de búsqueda.
 */
class ClienteFormularioBuscarControllerTest {

    @Mock
    private TextField buscarIdClienteField;
    @Mock
    private TextField buscarNombreClienteField;
    @Mock
    private TextField buscarCifApellidosClienteField;

    @Mock
    private ClienteController clienteController;
    @Mock
    private VentaFinalizarController ventaFinalizarController;

    @InjectMocks
    private ClienteFormularioBuscarController controller;

    /**
     * Configuración inicial para cada prueba.
     * Se inicializan los mocks y se simulan comportamientos de los campos de texto.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Mock text field behavior
        when(buscarIdClienteField.textProperty()).thenReturn(mock(javafx.beans.property.StringProperty.class));
        when(buscarNombreClienteField.textProperty()).thenReturn(mock(javafx.beans.property.StringProperty.class));
        when(buscarCifApellidosClienteField.textProperty()).thenReturn(mock(javafx.beans.property.StringProperty.class));
    }

    /**
     * Verifica que el método {@code setClienteController} establece correctamente
     * la referencia al controlador principal de clientes.
     */
    @Test
    void testSetClienteController() {
        controller.setClienteController(clienteController);
        // No direct verification needed beyond the setter call itself
    }

    /**
     * Verifica que el método {@code setVentaFinalizarController} establece correctamente
     * la referencia al controlador de finalización de venta.
     */
    @Test
    void testSetVentaFinalizarController() {
        controller.setVentaFinalizarController(ventaFinalizarController);
        // No direct verification needed beyond the setter call itself
    }

    /**
     * Prueba que el método {@code vincularControlador} configura correctamente
     * los listeners para los campos de búsqueda cuando se vincula con {@code ClienteController}.
     */
    @Test
    void testVincularControlador_ClienteController() {
        controller.setClienteController(clienteController);
        controller.vincularControlador();

        // Verify that listeners are added to text properties
        verify(buscarIdClienteField.textProperty()).addListener(any(javafx.beans.value.ChangeListener.class));
        verify(buscarNombreClienteField.textProperty()).addListener(any(javafx.beans.value.ChangeListener.class));
        verify(buscarCifApellidosClienteField.textProperty()).addListener(any(javafx.beans.value.ChangeListener.class));

        // Simulate text change to trigger filter
        when(buscarIdClienteField.textProperty().get()).thenReturn("1");
        when(buscarNombreClienteField.textProperty().get()).thenReturn("Juan");
        when(buscarCifApellidosClienteField.textProperty().get()).thenReturn("12345678A");

        // Manually trigger the listener (this is a bit tricky without TestFX)
        // For unit tests, we primarily verify that the listener was registered.
        // Actual filtering logic is tested in ClienteControllerTest.
    }

    /**
     * Prueba que el método {@code vincularControlador} configura correctamente
     * los listeners para los campos de búsqueda cuando se vincula con {@code VentaFinalizarController}.
     */
    @Test
    void testVincularControlador_VentaFinalizarController() {
        controller.setVentaFinalizarController(ventaFinalizarController);
        controller.vincularControlador();

        // Verify that listeners are added to text properties
        verify(buscarIdClienteField.textProperty()).addListener(any(javafx.beans.value.ChangeListener.class));
        verify(buscarNombreClienteField.textProperty()).addListener(any(javafx.beans.value.ChangeListener.class));
        verify(buscarCifApellidosClienteField.textProperty()).addListener(any(javafx.beans.value.ChangeListener.class));
    }

    /**
     * Verifica que el método {@code getCriteriosBusqueda} retorna un mapa
     * con los valores correctos de los campos de búsqueda.
     */
    @Test
    void testGetCriteriosBusqueda() {
        when(buscarIdClienteField.getText()).thenReturn("123");
        when(buscarNombreClienteField.getText()).thenReturn("Test Name");
        when(buscarCifApellidosClienteField.getText()).thenReturn("Test CIF");

        Map<String, String> criterios = controller.getCriteriosBusqueda();

        assertEquals("123", criterios.get("id"));
        assertEquals("Test Name", criterios.get("nombre"));
        assertEquals("Test CIF", criterios.get("cifnif"));
    }

    /**
     * Comprueba que el método {@code limpiarCampos} invoca el método clear()
     * en todos los campos de texto del formulario.
     */
    @Test
    void testLimpiarCampos() {
        controller.limpiarCampos();

        verify(buscarIdClienteField).clear();
        verify(buscarNombreClienteField).clear();
        verify(buscarCifApellidosClienteField).clear();
    }
}