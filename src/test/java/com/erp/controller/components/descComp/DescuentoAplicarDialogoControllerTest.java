package com.erp.controller.components.descComp;

import com.erp.dao.DescuentoDAO;
import com.erp.model.Descuento;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DescuentoAplicarDialogoControllerTest {

    @Mock
    private TextField campoDescuentoManual;
    @Mock
    private TableView<Descuento> tablaDescuentos;
    @Mock
    private TableColumn<Descuento, String> columnaNombre;
    @Mock
    private TableColumn<Descuento, Double> columnaPorcentaje;
    @Mock
    private Button botonAplicarManual;
    @Mock
    private Button botonAplicarSeleccionado;
    @Mock
    private Button botonCancelar;
    @Mock
    private Stage dialogStage;
    @Mock
    private DescuentoDAO descuentoDAO;

    @InjectMocks
    private DescuentoAplicarDialogoController controller;

    private ObservableList<Descuento> mockDescuentos;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock data
        mockDescuentos = FXCollections.observableArrayList(
                new Descuento(1, 1, "Desc1", 10.0, LocalDate.now(), LocalDate.now().plusDays(10)),
                new Descuento(2, 1, "Desc2", 5.0, LocalDate.now(), LocalDate.now().plusDays(10))
        );
        when(descuentoDAO.listarDescuentos()).thenReturn(mockDescuentos);

        // Mock TableView behavior
        when(tablaDescuentos.getItems()).thenReturn(FXCollections.observableArrayList()); // Return an empty list initially
        when(tablaDescuentos.getSelectionModel()).thenReturn(mock(TableView.TableViewSelectionModel.class));

        // Initialize the controller
        controller.initialize(null, null);
    }

    @Test
    void testInitialize() {
        verify(descuentoDAO).listarDescuentos();
        verify(tablaDescuentos).setItems(mockDescuentos);
    }

    @Test
    void testSetDialogStage() {
        controller.setDialogStage(dialogStage);
        // No direct verification needed beyond the setter call itself
    }

    @Test
    void testGetAppliedDiscount_InitiallyZero() {
        assertEquals(0.0, controller.getAppliedDiscount(), 0.001);
    }

    @Test
    void testAplicarDescuentoManual_Valido() {
        when(campoDescuentoManual.getText()).thenReturn("15.5");
        controller.aplicarDescuentoManual();
        assertEquals(15.5, controller.getAppliedDiscount(), 0.001);
        verify(dialogStage).close();
    }

    @Test
    void testAplicarDescuentoManual_NoNumerico() {
        when(campoDescuentoManual.getText()).thenReturn("abc");
        try (MockedStatic<Alert> mockedAlert = mockStatic(Alert.class)) {
            Alert mockAlertInstance = mock(Alert.class);
            doReturn(mockAlertInstance).when(mockedAlert).when(() -> new Alert(any(Alert.AlertType.class)));

            controller.aplicarDescuentoManual();

            assertEquals(0.0, controller.getAppliedDiscount(), 0.001); // Should remain 0
            mockedAlert.verify(() -> new Alert(Alert.AlertType.WARNING));
            verify(mockAlertInstance).showAndWait();
            verify(dialogStage, never()).close();
        }
    }

    @Test
    void testAplicarDescuentoManual_FueraDeRango() {
        when(campoDescuentoManual.getText()).thenReturn("150");
        try (MockedStatic<Alert> mockedAlert = mockStatic(Alert.class)) {
            Alert mockAlertInstance = mock(Alert.class);
            doReturn(mockAlertInstance).when(mockedAlert).when(() -> new Alert(any(Alert.AlertType.class)));

            controller.aplicarDescuentoManual();

            assertEquals(0.0, controller.getAppliedDiscount(), 0.001); // Should remain 0
            mockedAlert.verify(() -> new Alert(Alert.AlertType.WARNING));
            verify(mockAlertInstance).showAndWait();
            verify(dialogStage, never()).close();
        }
    }

    @Test
    void testAplicarDescuentoSeleccionado_ConSeleccion() {
        Descuento selected = mockDescuentos.get(0);
        when(tablaDescuentos.getSelectionModel().getSelectedItem()).thenReturn(selected);

        controller.aplicarDescuentoSeleccionado();

        assertEquals(10.0, controller.getAppliedDiscount(), 0.001);
        verify(dialogStage).close();
    }

    @Test
    void testAplicarDescuentoSeleccionado_SinSeleccion() {
        when(tablaDescuentos.getSelectionModel().getSelectedItem()).thenReturn(null);

        try (MockedStatic<Alert> mockedAlert = mockStatic(Alert.class)) {
            Alert mockAlertInstance = mock(Alert.class);
            doReturn(mockAlertInstance).when(mockedAlert).when(() -> new Alert(any(Alert.AlertType.class)));

            controller.aplicarDescuentoSeleccionado();

            assertEquals(0.0, controller.getAppliedDiscount(), 0.001); // Should remain 0
            mockedAlert.verify(() -> new Alert(Alert.AlertType.WARNING));
            verify(mockAlertInstance).showAndWait();
            verify(dialogStage, never()).close();
        }
    }

    @Test
    void testCancelar() {
        controller.cancelar();
        assertEquals(0.0, controller.getAppliedDiscount(), 0.001); // Should be 0
        verify(dialogStage).close();
    }
}
