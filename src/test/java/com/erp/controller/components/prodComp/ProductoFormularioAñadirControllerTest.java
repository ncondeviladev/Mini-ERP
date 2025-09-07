package com.erp.controller.components.prodComp;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.erp.controller.ProductoController;
import com.erp.model.Producto;
import com.erp.utils.Alerta;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * Clase de pruebas unitarias para {@link ProductoFormularioAñadirController}.
 * Se encarga de verificar el correcto funcionamiento de la lógica de añadir y modificar productos
 * en el formulario, incluyendo validaciones y la interacción con el controlador principal de productos.
 */
class ProductoFormularioAñadirControllerTest {

    @Mock
    private VBox formularioAñadirProducto;
    @Mock
    private Label tituloFormularioProducto;
    @Mock
    private TextField nombreProductoField;
    @Mock
    private TextField descripcionProductoField;
    @Mock
    private TextField categoriaProductoField;
    @Mock
    private TextField precioProductoField;
    @Mock
    private TextField stockProductoField;
    @Mock
    private Button botonGuardarProducto;

    @Mock
    private ProductoController productoController;

    @InjectMocks
    private ProductoFormularioAñadirController controller;

    /**
     * Configuración inicial para cada prueba.
     * Se inicializan los mocks y se simulan comportamientos de los componentes de UI
     * y se llama al método initialize del controlador.
     */
    @BeforeEach
    void configurar() {
        MockitoAnnotations.openMocks(this);

        // Simular las propiedades de texto de los TextField y los métodos clear
        when(nombreProductoField.getText()).thenReturn("");
        when(descripcionProductoField.getText()).thenReturn("");
        when(categoriaProductoField.getText()).thenReturn("");
        when(precioProductoField.getText()).thenReturn("");
        when(stockProductoField.getText()).thenReturn("");

        // Simular el manejo de eventos de teclado para los campos de texto
        doAnswer(invocation -> {
            // Simular que se añade un listener
            return null;
        }).when(nombreProductoField).setOnKeyPressed(any());
        doAnswer(invocation -> {
            // Simular que se añade un listener
            return null;
        }).when(descripcionProductoField).setOnKeyPressed(any());
        doAnswer(invocation -> {
            // Simular que se añade un listener
            return null;
        }).when(categoriaProductoField).setOnKeyPressed(any());
        doAnswer(invocation -> {
            // Simular que se añade un listener
            return null;
        }).when(precioProductoField).setOnKeyPressed(any());
        doAnswer(invocation -> {
            // Simular que se añade un listener
            return null;
        }).when(stockProductoField).setOnKeyPressed(any());

        // Llamar a initialize manualmente ya que InjectMocks no lo hace para los controladores FXML
        controller.initialize();
    }

    /**
     * Verifica que el método {@code setProductoController} establece correctamente
     * la referencia al controlador principal de productos.
     */
    @Test
    void testEstablecerProductoController() {
        controller.setProductoController(productoController);
        // No se necesita verificación directa más allá de la llamada al setter
    }

    /**
     * Comprueba que el método {@code initialize} configura correctamente los listeners
     * de teclado para los campos de texto.
     */
    @Test
    void testInicializar_ListenersConfigurados() {
        // Verificar que setOnKeyPressed fue llamado para cada TextField
        verify(nombreProductoField).setOnKeyPressed(any());
        verify(descripcionProductoField).setOnKeyPressed(any());
        verify(categoriaProductoField).setOnKeyPressed(any());
        verify(precioProductoField).setOnKeyPressed(any());
        verify(stockProductoField).setOnKeyPressed(any());
    }

    /**
     * Prueba que el formulario se muestra correctamente para añadir un nuevo producto.
     * Verifica que los campos se limpian y los textos de título/botón son los esperados.
     */
    @Test
    void testMostrarFormulario_ProductoNuevo() {
        controller.mostrarFormulario(null);

        verify(tituloFormularioProducto).setText("Añadir Nuevo Producto");
        verify(botonGuardarProducto).setText("Guardar");
        verify(nombreProductoField).clear();
        verify(descripcionProductoField).clear();
        verify(categoriaProductoField).clear();
        verify(precioProductoField).clear();
        verify(stockProductoField).clear();
        verify(formularioAñadirProducto).setVisible(true);
        verify(formularioAñadirProducto).setManaged(true);
    }

    @Test
    void testMostrarFormulario_ProductoExistente() {
        Producto producto = new Producto(1, "Test Prod", "Desc", "Cat", 10.0, 5);
        controller.mostrarFormulario(producto);

        verify(tituloFormularioProducto).setText("Modificar Producto");
        verify(botonGuardarProducto).setText("Actualizar");
        verify(nombreProductoField).setText("Test Prod");
        verify(descripcionProductoField).setText("Desc");
        verify(categoriaProductoField).setText("Cat");
        verify(precioProductoField).setText("10.0");
        verify(stockProductoField).setText("5");
        verify(formularioAñadirProducto).setVisible(true);
        verify(formularioAñadirProducto).setManaged(true);
    }

        @Test
    void testManejarGuardarProducto_CamposVacios() {
        when(nombreProductoField.getText()).thenReturn(""); // Campo vacío
        when(precioProductoField.getText()).thenReturn("10.0");
        when(stockProductoField.getText()).thenReturn("5");

        try (MockedStatic<Alerta> mockedAlerta = mockStatic(Alerta.class)) {
            controller.handleGuardarProducto();
            mockedAlerta.verify(() -> Alerta.mostrarAdvertencia("Campos incompletos", "Nombre, precio y stock son obligatorios."));
            verify(productoController, never()).guardarOActualizarProducto(any(Producto.class));
        }
    }

    @Test
    void testHandleGuardarProducto_PrecioNoNumerico() {
        when(nombreProductoField.getText()).thenReturn("Producto");
        when(precioProductoField.getText()).thenReturn("abc"); // No es un número
        when(stockProductoField.getText()).thenReturn("5");

        try (MockedStatic<Alerta> mockedAlerta = mockStatic(Alerta.class)) {
            controller.handleGuardarProducto();
            mockedAlerta.verify(() -> Alerta.mostrarError("Error de formato", "Precio y Stock deben ser números válidos."));
            verify(productoController, never()).guardarOActualizarProducto(any(Producto.class));
        }
    }

    /**
     * Comprueba que {@code handleGuardarProducto} muestra un error
     * cuando el campo de stock no es numérico.
     */
    @Test
    void testHandleGuardarProducto_StockNoNumerico() {
        when(nombreProductoField.getText()).thenReturn("Producto");
        when(precioProductoField.getText()).thenReturn("10.0");
        when(stockProductoField.getText()).thenReturn("xyz"); // No es un número

        try (MockedStatic<Alerta> mockedAlerta = mockStatic(Alerta.class)) {
            controller.handleGuardarProducto();
            mockedAlerta.verify(() -> Alerta.mostrarError("Error de formato", "Precio y Stock deben ser números válidos."));
            verify(productoController, never()).guardarOActualizarProducto(any(Producto.class));
        }
    }

    /**
     * Verifica que {@code handleGuardarProducto} guarda un nuevo producto
     * cuando todos los campos son válidos y el producto es nuevo.
     */
    @Test
    void testHandleGuardarProducto_NuevoProducto() {
        controller.setProductoController(productoController);
        when(nombreProductoField.getText()).thenReturn("Nuevo Producto");
        when(descripcionProductoField.getText()).thenReturn("Nueva Desc");
        when(categoriaProductoField.getText()).thenReturn("Nueva Cat");
        when(precioProductoField.getText()).thenReturn("25.50");
        when(stockProductoField.getText()).thenReturn("100");
        when(productoController.capitalizar(anyString())).thenAnswer(invocation -> invocation.getArgument(0)); // Mock capitalizar

        controller.handleGuardarProducto();

        verify(productoController).guardarOActualizarProducto(argThat(p ->
                p.getNombre().equals("Nuevo Producto") &&
                        p.getDescripcion().equals("Nueva Desc") &&
                        p.getCategoria().equals("Nueva Cat") &&
                        p.getPrecioUnitario() == 25.50 &&
                        p.getStock() == 100 &&
                        p.getId() == null
        ));
    }

    /**
     * Prueba que {@code handleGuardarProducto} actualiza un producto existente
     * cuando todos los campos son válidos y se ha cargado un producto previamente.
     */
    @Test
    void testHandleGuardarProducto_ActualizarProducto() {
        Producto productoExistente = new Producto(1, "Old Name", "Old Desc", "Old Cat", 50.0, 50);
        controller.mostrarFormulario(productoExistente);
        controller.setProductoController(productoController);

        when(nombreProductoField.getText()).thenReturn("Updated Name");
        when(descripcionProductoField.getText()).thenReturn("Updated Desc");
        when(categoriaProductoField.getText()).thenReturn("Updated Cat");
        when(precioProductoField.getText()).thenReturn("75.00");
        when(stockProductoField.getText()).thenReturn("200");
        when(productoController.capitalizar(anyString())).thenAnswer(invocation -> invocation.getArgument(0)); // Mock capitalizar

        controller.handleGuardarProducto();

        verify(productoController).guardarOActualizarProducto(argThat(p ->
                p.getId().equals(1) && // ID should be preserved
                        p.getNombre().equals("Updated Name") &&
                        p.getDescripcion().equals("Updated Desc") &&
                        p.getCategoria().equals("Updated Cat") &&
                        p.getPrecioUnitario() == 75.00 &&
                        p.getStock() == 200
        ));
    }

    /**
     * Verifica que {@code validarCampos} retorna true cuando todos los campos obligatorios están llenos.
     */
    @Test
    void testValidarCampos_True() {
        when(nombreProductoField.getText()).thenReturn("Nombre");
        when(precioProductoField.getText()).thenReturn("10.0");
        when(stockProductoField.getText()).thenReturn("5");
        assertTrue(controller.validarCampos());
    }

    /**
     * Verifica que {@code validarCampos} retorna false cuando algún campo obligatorio está vacío.
     */
    @Test
    void testValidarCampos_False() {
        when(nombreProductoField.getText()).thenReturn("");
        when(precioProductoField.getText()).thenReturn("10.0");
        when(stockProductoField.getText()).thenReturn("5");
        assertFalse(controller.validarCampos());
    }

    /**
     * Comprueba que el método {@code limpiarCampos} invoca el método clear()
     * en todos los campos de texto del formulario.
     */
    @Test
    void testLimpiarCampos() {
        controller.limpiarCampos();
        verify(nombreProductoField).clear();
        verify(descripcionProductoField).clear();
        verify(categoriaProductoField).clear();
        verify(precioProductoField).clear();
        verify(stockProductoField).clear();
    }
}

