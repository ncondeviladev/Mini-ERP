package com.erp.controller;

import com.erp.controller.components.ventaComp.VentaCestaTablaController;
import com.erp.model.DetalleVenta;
import com.erp.model.Producto;
import com.erp.utils.Alerta;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CestaControllerTest {

    @Mock
    private MainController mainController;
    @Mock
    private VentaCestaTablaController cestaTablaComponenteController;
    @Mock
    private javafx.scene.control.Label labelTotalCesta; // Mock the FXML Label
    @Mock
    private javafx.scene.control.Button botonLimpiarCesta; // Mock the FXML Button
    @Mock
    private javafx.scene.control.Button botonVolver; // Mock the FXML Button
    @Mock
    private javafx.scene.control.Button botonConfirmarCesta; // Mock the FXML Button

    @InjectMocks
    private CestaController cestaController;

    private ObservableList<DetalleVenta> mockCestaItems;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks

        mockCestaItems = FXCollections.observableArrayList();
        // Simulate FXML injection for the VBox and its children
        when(cestaTablaComponenteController.calcularTotalCesta()).thenReturn(0.0); // Default mock behavior
    }

    @Test
    void testSetMainController() {
        cestaController.setMainController(mainController);
        verify(cestaTablaComponenteController).setMainController(mainController);
    }

    @Test
    void testSetCestaItems() {
        Producto p1 = new Producto(1, "Prod1", "", "", 10.0, 10);
        DetalleVenta d1 = new DetalleVenta(null, null, p1, 2, 10.0);
        mockCestaItems.add(d1);

        cestaController.setCestaItems(mockCestaItems);
        verify(cestaTablaComponenteController).setDetallesVenta(mockCestaItems);
        verify(cestaTablaComponenteController).calcularTotalCesta(); // Should be called to update total
        verify(labelTotalCesta).setText("20.00€"); // Assuming initial total is 20.0
    }

    @Test
    void testActualizarTotalCesta() {
        when(cestaTablaComponenteController.calcularTotalCesta()).thenReturn(50.0);
        cestaController.actualizarTotalCesta();
        verify(labelTotalCesta).setText("50.00€");
    }

    @Test
    void testLimpiarCesta() {
        Producto p1 = new Producto(1, "Prod1", "", "", 10.0, 10);
        DetalleVenta d1 = new DetalleVenta(null, null, p1, 2, 10.0);
        mockCestaItems.add(d1);
        cestaController.setCestaItems(mockCestaItems); // Set items first

        try (MockedStatic<Alerta> mockedAlerta = mockStatic(Alerta.class)) {
            cestaController.limpiarCesta();
            assertTrue(mockCestaItems.isEmpty());
            verify(cestaTablaComponenteController).calcularTotalCesta();
            verify(labelTotalCesta).setText("0.00€");
            mockedAlerta.verify(() -> Alerta.mostrarAlertaTemporal(Alert.AlertType.INFORMATION, "Cesta Limpiada", null, "La cesta ha sido vaciada."));
        }
    }

    @Test
    void testVolverAVentas() {
        cestaController.setMainController(mainController); // Ensure mainController is set
        cestaController.volverAVentas();
        verify(mainController).mostrarVentas();
    }

    @Test
    void testProcederConCesta_CestaVacia() {
        try (MockedStatic<Alerta> mockedAlerta = mockStatic(Alerta.class)) {
            cestaController.setCestaItems(FXCollections.observableArrayList()); // Ensure empty cesta
            cestaController.procederConCesta();
            mockedAlerta.verify(() -> Alerta.mostrarAlertaTemporal(Alert.AlertType.WARNING, "Advertencia", "Cesta vacía", "No puedes continuar con una cesta vacía."));
            verify(mainController, never()).mostrarSeleccionClienteParaVenta(any());
        }
    }

    @Test
    void testProcederConCesta_CestaConItems() {
        Producto p1 = new Producto(1, "Prod1", "", "", 10.0, 10);
        DetalleVenta d1 = new DetalleVenta(null, null, p1, 2, 10.0);
        mockCestaItems.add(d1);
        cestaController.setCestaItems(mockCestaItems); // Set items

        cestaController.setMainController(mainController); // Ensure mainController is set
        cestaController.procederConCesta();
        verify(mainController).mostrarSeleccionClienteParaVenta(mockCestaItems);
    }
}
