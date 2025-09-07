package com.erp.controller.components.ventaComp;

import com.erp.controller.MainController;
import com.erp.model.DetalleVenta;
import com.erp.model.Producto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VentaCestaTablaControllerTest {

    @Mock
    private MainController mainController;
    @Mock
    private TableView<DetalleVenta> tablaCesta;
    @Mock
    private TableColumn<DetalleVenta, String> columnaProducto;
    @Mock
    private TableColumn<DetalleVenta, Integer> columnaCantidad;
    @Mock
    private TableColumn<DetalleVenta, Double> columnaPrecioUnitario;
    @Mock
    private TableColumn<DetalleVenta, Double> columnaSubtotal;

    @InjectMocks
    private VentaCestaTablaController controller;

    private ObservableList<DetalleVenta> mockDetallesVenta;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock TableView items
        when(tablaCesta.getItems()).thenReturn(FXCollections.observableArrayList());

        // Initialize the controller
        controller.initialize(null, null);

        // Sample data
        Producto p1 = new Producto(1, "Prod A", "Desc A", "Cat A", 10.0, 100);
        Producto p2 = new Producto(2, "Prod B", "Desc B", "Cat B", 20.0, 50);
        mockDetallesVenta = FXCollections.observableArrayList(
                new DetalleVenta(null, null, p1, 2, 10.0), // Subtotal 20.0
                new DetalleVenta(null, null, p2, 3, 20.0)  // Subtotal 60.0
        );
    }

    @Test
    void testInitialize_ColumnSetup() {
        verify(columnaProducto).setCellValueFactory(any());
        verify(columnaCantidad).setCellValueFactory(any());
        verify(columnaPrecioUnitario).setCellValueFactory(any());
        verify(columnaSubtotal).setCellValueFactory(any());
    }

    @Test
    void testSetMainController() {
        controller.setMainController(mainController);
        // No direct verification needed beyond the setter call itself
    }

    @Test
    void testGetTablaCesta() {
        assertEquals(tablaCesta, controller.getTablaCesta());
    }

    @Test
    void testSetDetallesVenta() {
        controller.setDetallesVenta(mockDetallesVenta);
        verify(tablaCesta).setItems(mockDetallesVenta);
    }

    @Test
    void testAnadirDetalleVenta() {
        Producto p3 = new Producto(3, "Prod C", "Desc C", "Cat C", 5.0, 200);
        DetalleVenta newDetalle = new DetalleVenta(null, null, p3, 1, 5.0);

        controller.anadirDetalleVenta(newDetalle);
        assertTrue(tablaCesta.getItems().contains(newDetalle));
    }

    @Test
    void testGetDetallesVenta() {
        tablaCesta.setItems(mockDetallesVenta);
        List<DetalleVenta> retrievedDetails = controller.getDetallesVenta();
        assertEquals(mockDetallesVenta.size(), retrievedDetails.size());
        assertTrue(retrievedDetails.containsAll(mockDetallesVenta));
    }

    @Test
    void testCalcularTotalCesta() {
        tablaCesta.setItems(mockDetallesVenta);
        double total = controller.calcularTotalCesta();
        assertEquals(80.0, total, 0.001);
    }

    @Test
    void testCalcularTotalCesta_Empty() {
        tablaCesta.setItems(FXCollections.observableArrayList());
        double total = controller.calcularTotalCesta();
        assertEquals(0.0, total, 0.001);
    }
}
