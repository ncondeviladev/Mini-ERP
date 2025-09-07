package com.erp.controller;

import com.erp.controller.components.prodComp.ProductoFormularioBuscarController;
import com.erp.controller.components.prodComp.ProductoTablaController;
import com.erp.dao.ProductoDAO;
import com.erp.model.DetalleVenta;
import com.erp.model.Producto;
import com.erp.utils.Alerta;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VentaControllerTest {

    @Mock
    private MainController mainController;
    @Mock
    private ProductoFormularioBuscarController formularioBuscarProductoController;
    @Mock
    private ProductoTablaController productoTablaController;
    @Mock
    private ProductoDAO productoDAO;
    @Mock
    private VBox contenedorFormularioBusqueda;
    @Mock
    private TextField campoCantidad;
    @Mock
    private TableView<Producto> mockProductoTableView; // Mock the TableView directly

    @InjectMocks
    private VentaController ventaController;

    private ObservableList<DetalleVenta> cestaItems;
    private List<Producto> mockProductosOriginales;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize cestaItems as an actual ObservableList
        cestaItems = FXCollections.observableArrayList();
        ventaController.setCestaItems(cestaItems); // Inject into controller

        // Mock behavior for productDAO
        mockProductosOriginales = Arrays.asList(
                new Producto(1, "Laptop", "Gaming Laptop", "Electronics", 1200.0, 5),
                new Producto(2, "Mouse", "Wireless Mouse", "Peripherals", 25.0, 20)
        );
        when(productoDAO.listarProductos()).thenReturn(mockProductosOriginales);

        // Mock behavior for productoTablaController.getTablaProducto()
        when(productoTablaController.getTablaProducto()).thenReturn(mockProductoTableView);
        when(mockProductoTableView.getSelectionModel()).thenReturn(mock(TableView.TableViewSelectionModel.class));
    }

    @Test
    void testInitialize() {
        ventaController.initialize(null, null);

        verify(productoDAO).listarProductos();
        verify(productoTablaController).setItems(mockProductosOriginales);
        verify(formularioBuscarProductoController).setVentaController(ventaController);
        verify(formularioBuscarProductoController).vincularControlador();
        verify(productoTablaController).setAccionesProductoVisible(false);
    }

    @Test
    void testSetMainController() {
        ventaController.setMainController(mainController);
        // No direct verification needed beyond the setter call itself
    }

    @Test
    void testSetCestaItems() {
        ObservableList<DetalleVenta> newCesta = FXCollections.observableArrayList();
        ventaController.setCestaItems(newCesta);
        assertEquals(newCesta, cestaItems);
    }

    @Test
    void testFiltrarProductos() {
        Map<String, String> criterios = new HashMap<>();
        criterios.put("id", "");
        criterios.put("nombre", "lap");
        criterios.put("categoria", "");
        when(formularioBuscarProductoController.getCriteriosBusqueda()).thenReturn(criterios);

        ventaController.filtrarProductos();

        verify(productoTablaController).setItems(argThat(list ->
                list.size() == 1 && list.get(0).getNombre().equals("Laptop")
        ));
    }

    @Test
    void testMostrarOcultarFormularioBusqueda() {
        when(contenedorFormularioBusqueda.isVisible()).thenReturn(false);
        ventaController.mostrarOcultarFormularioBusqueda();
        verify(contenedorFormularioBusqueda).setVisible(true);
        verify(contenedorFormularioBusqueda).setManaged(true);

        when(contenedorFormularioBusqueda.isVisible()).thenReturn(true);
        ventaController.mostrarOcultarFormularioBusqueda();
        verify(contenedorFormularioBusqueda).setVisible(false);
        verify(contenedorFormularioBusqueda).setManaged(false);
    }

    @Test
    void testAnadirProductoACesta_NoProductoSeleccionado() {
        when(mockProductoTableView.getSelectionModel().getSelectedItem()).thenReturn(null);

        try (MockedStatic<Alerta> mockedAlerta = mockStatic(Alerta.class)) {
            ventaController.anadirProductoACesta();
            mockedAlerta.verify(() -> Alerta.mostrarAlertaTemporal(Alert.AlertType.WARNING, "Advertencia", null, "Por favor, selecciona un producto de la tabla."));
            assertTrue(cestaItems.isEmpty());
        }
    }

    @Test
    void testAnadirProductoACesta_CantidadInvalida() {
        Producto selectedProduct = mockProductosOriginales.get(0);
        when(mockProductoTableView.getSelectionModel().getSelectedItem()).thenReturn(selectedProduct);
        when(campoCantidad.getText()).thenReturn("-5");

        try (MockedStatic<Alerta> mockedAlerta = mockStatic(Alerta.class)) {
            ventaController.anadirProductoACesta();
            mockedAlerta.verify(() -> Alerta.mostrarAlertaTemporal(Alert.AlertType.WARNING, "Advertencia", null, "Por favor, introduce una cantidad válida (número entero positivo)."));
            assertTrue(cestaItems.isEmpty());
        }
    }

    @Test
    void testAnadirProductoACesta_StockInsuficiente() {
        Producto selectedProduct = mockProductosOriginales.get(0); // Laptop, stock 5
        when(mockProductoTableView.getSelectionModel().getSelectedItem()).thenReturn(selectedProduct);
        when(campoCantidad.getText()).thenReturn("10");

        try (MockedStatic<Alerta> mockedAlerta = mockStatic(Alerta.class)) {
            ventaController.anadirProductoACesta();
            mockedAlerta.verify(() -> Alerta.mostrarAlertaTemporal(Alert.AlertType.WARNING, "Advertencia", null, "No hay suficiente stock para el producto seleccionado. Stock disponible: 5"));
            assertTrue(cestaItems.isEmpty());
        }
    }

    @Test
    void testAnadirProductoACesta_NuevoProducto() {
        Producto selectedProduct = mockProductosOriginales.get(0); // Laptop
        when(mockProductoTableView.getSelectionModel().getSelectedItem()).thenReturn(selectedProduct);
        when(campoCantidad.getText()).thenReturn("2");

        try (MockedStatic<Alerta> mockedAlerta = mockStatic(Alerta.class)) {
            ventaController.anadirProductoACesta();
            assertEquals(1, cestaItems.size());
            assertEquals(selectedProduct, cestaItems.get(0).getProducto());
            assertEquals(2, cestaItems.get(0).getCantidad());
            mockedAlerta.verify(() -> Alerta.mostrarAlertaTemporal(Alert.AlertType.INFORMATION, "Éxito", null, "Producto añadido a la cesta correctamente."));
        }
    }

    @Test
    void testAnadirProductoACesta_ProductoExistente() {
        Producto selectedProduct = mockProductosOriginales.get(0); // Laptop
        DetalleVenta existingDetalle = new DetalleVenta(null, null, selectedProduct, 2, selectedProduct.getPrecioUnitario());
        cestaItems.add(existingDetalle);

        when(mockProductoTableView.getSelectionModel().getSelectedItem()).thenReturn(selectedProduct);
        when(campoCantidad.getText()).thenReturn("3");

        try (MockedStatic<Alerta> mockedAlerta = mockStatic(Alerta.class)) {
            ventaController.anadirProductoACesta();
            assertEquals(1, cestaItems.size()); // Still one item
            assertEquals(5, cestaItems.get(0).getCantidad()); // Quantity updated
            mockedAlerta.verify(() -> Alerta.mostrarAlertaTemporal(Alert.AlertType.INFORMATION, "Éxito", null, "Producto añadido a la cesta correctamente."));
        }
    }

    @Test
    void testVerCesta() {
        ventaController.setMainController(mainController);
        ventaController.verCesta();
        verify(mainController).mostrarCesta();
    }
}
