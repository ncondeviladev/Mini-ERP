package com.erp.controller.components.descComp;

import com.erp.controller.MainController;
import com.erp.model.Descuento;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DescuentoTablaControllerTest {

    @Mock
    private MainController mainController;
    @Mock
    private TableView<Descuento> tablaDescuentos;
    @Mock
    private TableColumn<Descuento, Boolean> columnaSeleccion;
    @Mock
    private TableColumn<Descuento, String> columnaDescripcion;
    @Mock
    private TableColumn<Descuento, Double> columnaPorcentaje;
    @Mock
    private TableColumn<Descuento, String> columnaFechaInicio;
    @Mock
    private TableColumn<Descuento, String> columnaFechaFin;
    @Mock
    private TableColumn<Descuento, Boolean> columnaEstado;

    @InjectMocks
    private DescuentoTablaController controller;

    private ObservableList<Descuento> mockDescuentos;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockDescuentos = FXCollections.observableArrayList(
                new Descuento(1, 1, "Desc Activo", 10.0, LocalDate.now(), LocalDate.now().plusDays(10)),
                new Descuento(2, 1, "Desc Caducado", 5.0, LocalDate.now().minusMonths(2), LocalDate.now().minusDays(1))
        );

        // Mock TableView items
        when(tablaDescuentos.getItems()).thenReturn(FXCollections.observableArrayList());
        when(tablaDescuentos.getSelectionModel()).thenReturn(mock(TableView.TableViewSelectionModel.class));

        // Initialize the controller
        controller.initialize(null, null);
    }

    @Test
    void testInitialize_ColumnSetup() {
        verify(columnaDescripcion).setCellValueFactory(any());
        verify(columnaPorcentaje).setCellValueFactory(any());
        verify(columnaFechaInicio).setCellValueFactory(any());
        verify(columnaFechaFin).setCellValueFactory(any());
        verify(columnaEstado).setCellValueFactory(any());

        verify(columnaSeleccion).setCellValueFactory(any());
        verify(columnaSeleccion).setCellFactory(any());
        verify(tablaDescuentos).setEditable(true);
    }

    @Test
    void testSetMainController() {
        controller.setMainController(mainController);
        // No direct verification needed beyond the setter call itself
    }

    @Test
    void testSetDescuentos() {
        controller.setDescuentos(mockDescuentos);
        assertEquals(mockDescuentos, tablaDescuentos.getItems());

        // Verify listeners are added (indirectly, by checking if property is accessed)
        // This is hard to test directly without a custom listener mock.
        // We'll rely on the fact that the code calls addListener.
    }

    @Test
    void testGetDescuentosSeleccionados() {
        Descuento d1 = mockDescuentos.get(0);
        Descuento d2 = mockDescuentos.get(1);
        d1.setSeleccionado(true); // Manually set selected state

        tablaDescuentos.getItems().addAll(d1, d2); // Add to the mock table's items

        List<Descuento> selected = controller.getDescuentosSeleccionados();
        assertEquals(1, selected.size());
        assertTrue(selected.contains(d1));
        assertFalse(selected.contains(d2));
    }

    @Test
    void testSetOnSelectionChanged() {
        Runnable mockAction = mock(Runnable.class);
        controller.setOnSelectionChanged(mockAction);

        // Simulate a selection change on a discount
        Descuento d1 = mockDescuentos.get(0);
        BooleanProperty selectedProperty = new SimpleBooleanProperty(false);
        d1.seleccionadoProperty().bindBidirectional(selectedProperty);

        tablaDescuentos.getItems().add(d1); // Add to the mock table's items

        selectedProperty.set(true);
        verify(mockAction).run();

        selectedProperty.set(false);
        verify(mockAction, times(2)).run();
    }

    @Test
    void testGetTablaDescuentos() {
        assertEquals(tablaDescuentos, controller.getTablaDescuentos());
    }

    @Test
    void testGetSelectedDescuento() {
        Descuento selected = mockDescuentos.get(0);
        when(tablaDescuentos.getSelectionModel().getSelectedItem()).thenReturn(selected);
        assertEquals(selected, controller.getSelectedDescuento());
    }
}
