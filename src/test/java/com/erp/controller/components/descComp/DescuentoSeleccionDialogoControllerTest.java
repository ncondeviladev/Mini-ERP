package com.erp.controller.components.descComp;

import com.erp.model.Descuento;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
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

class DescuentoSeleccionDialogoControllerTest {

    @Mock
    private DescuentoTablaController descuentoTablaController;
    @Mock
    private TableView<Descuento> mockTableView; // Mock the TableView from DescuentoTablaController

    @InjectMocks
    private DescuentoSeleccionDialogoController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(descuentoTablaController.getTablaDescuentos()).thenReturn(mockTableView);
        when(mockTableView.getSelectionModel()).thenReturn(mock(TableView.TableViewSelectionModel.class));

        controller.initialize();
    }

    @Test
    void testInitialize() {
        verify(mockTableView.getSelectionModel()).setSelectionMode(SelectionMode.MULTIPLE);
    }

    @Test
    void testSetDescuentos() {
        List<Descuento> descuentos = Arrays.asList(
                new Descuento(1, 1, "Desc1", 10.0, LocalDate.now(), LocalDate.now().plusDays(10)),
                new Descuento(2, 1, "Desc2", 5.0, LocalDate.now(), LocalDate.now().plusDays(10))
        );
        controller.setDescuentos(descuentos);
        verify(descuentoTablaController).setDescuentos(descuentos);
    }

    @Test
    void testGetDescuentosSeleccionados() {
        ObservableList<Descuento> selectedItems = FXCollections.observableArrayList(
                new Descuento(1, 1, "Desc1", 10.0, LocalDate.now(), LocalDate.now().plusDays(10))
        );
        when(mockTableView.getSelectionModel().getSelectedItems()).thenReturn(selectedItems);

        List<Descuento> result = controller.getDescuentosSeleccionados();
        assertEquals(1, result.size());
        assertEquals("Desc1", result.get(0).getDescripcion());
    }
}
