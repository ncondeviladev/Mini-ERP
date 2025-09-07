package com.erp.controller.components.prodComp;

import com.erp.controller.ProductoController;
import com.erp.model.Producto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductoTablaControllerTest {

    @Mock
    private TableView<Producto> tablaProducto;
    @Mock
    private TableColumn<Producto, Integer> colIdProducto;
    @Mock
    private TableColumn<Producto, String> colNombreProducto;
    @Mock
    private TableColumn<Producto, String> colCategoriaProducto;
    @Mock
    private TableColumn<Producto, Double> colPrecioProducto;
    @Mock
    private TableColumn<Producto, Integer> colStockProducto;
    @Mock
    private TableColumn<Producto, String> colDescripcionProducto;
    @Mock
    private Button botonModificarProducto;
    @Mock
    private Button botonEliminarProducto;
    @Mock
    private HBox accionesProducto;

    @Mock
    private ProductoController productoController;

    @InjectMocks
    private ProductoTablaController controller;

    private ObservableList<Producto> mockProductoList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock TableView selection model
        TableView.TableViewSelectionModel<Producto> selectionModel = mock(TableView.TableViewSelectionModel.class);
        when(tablaProducto.getSelectionModel()).thenReturn(selectionModel);
        when(selectionModel.selectedItemProperty()).thenReturn(mock(javafx.beans.property.ObjectProperty.class));

        // Initialize the controller
        controller.initialize();

        // Set the mock controller
        controller.setProductoController(productoController);

        // Sample data
        mockProductoList = FXCollections.observableArrayList(
                new Producto(1, "Laptop", "Gaming Laptop", "Electronics", 1200.0, 5),
                new Producto(2, "Mouse", "Wireless Mouse", "Peripherals", 25.0, 20)
        );
    }

    @Test
    void testSetProductoController() {
        // Already set in @BeforeEach
        // No direct verification needed beyond the setter call itself
    }

    @Test
    void testInitialize_ColumnSetup() {
        // Verify that PropertyValueFactory is set for all columns
        verify(colIdProducto).setCellValueFactory(any());
        verify(colNombreProducto).setCellValueFactory(any());
        verify(colCategoriaProducto).setCellValueFactory(any());
        verify(colPrecioProducto).setCellValueFactory(any());
        verify(colStockProducto).setCellValueFactory(any());
        verify(colDescripcionProducto).setCellValueFactory(any());
    }

    @Test
    void testModificarProductoSeleccionado() {
        controller.modificarProductoSeleccionado();
        verify(productoController).modificarProductoSeleccionado();
    }

    @Test
    void testEliminarProductoSeleccionado() {
        controller.eliminarProductoSeleccionado();
        verify(productoController).eliminarProductoSeleccionado();
    }

    @Test
    void testSetItems() {
        controller.setItems(mockProductoList);
        verify(tablaProducto).setItems(argThat(list -> list.size() == 2 && list.containsAll(mockProductoList)));
    }

    @Test
    void testGetProductoSeleccionado() {
        Producto selected = mockProductoList.get(0);
        when(tablaProducto.getSelectionModel().getSelectedItem()).thenReturn(selected);
        assertEquals(selected, controller.getProductoSeleccionado());
    }

    @Test
    void testSetDisableBotones() {
        controller.setDisableBotones(false);
        verify(botonModificarProducto).setDisable(false);
        verify(botonEliminarProducto).setDisable(false);

        controller.setDisableBotones(true);
        verify(botonModificarProducto).setDisable(true);
        verify(botonEliminarProducto).setDisable(true);
    }

    @Test
    void testSetAccionesProductoVisible() {
        controller.setAccionesProductoVisible(true);
        verify(accionesProducto).setVisible(true);
        verify(accionesProducto).setManaged(true);

        controller.setAccionesProductoVisible(false);
        verify(accionesProducto).setVisible(false);
        verify(accionesProducto).setManaged(false);
    }
}
