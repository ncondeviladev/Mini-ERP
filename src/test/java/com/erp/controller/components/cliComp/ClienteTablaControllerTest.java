package com.erp.controller.components.cliComp;

import com.erp.controller.ClienteController;
import com.erp.model.Cliente;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClienteTablaControllerTest {

    @Mock
    private TableView<Cliente> tablaCliente;
    @Mock
    private TableColumn<Cliente, Integer> colIdCliente;
    @Mock
    private TableColumn<Cliente, String> colNombreApellidos;
    @Mock
    private TableColumn<Cliente, String> colRazonContacto;
    @Mock
    private TableColumn<Cliente, String> colTelefonoEmail;
    @Mock
    private TableColumn<Cliente, String> colDireccion;
    @Mock
    private TableColumn<Cliente, String> colCifNif;
    @Mock
    private Button botonModificarCliente;
    @Mock
    private Button botonEliminarCliente;
    @Mock
    private Button botonDescuentoCliente;
    @Mock
    private HBox accionesCliente;

    @Mock
    private ClienteController clienteController;

    @InjectMocks
    private ClienteTablaController controller;

    private ObservableList<Cliente> mockClienteList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock TableView selection model
        TableView.TableViewSelectionModel<Cliente> selectionModel = mock(TableView.TableViewSelectionModel.class);
        when(tablaCliente.getSelectionModel()).thenReturn(selectionModel);
        when(selectionModel.selectedItemProperty()).thenReturn(mock(javafx.beans.property.ObjectProperty.class));

        // Mock initial state of buttons
        when(botonModificarCliente.isDisable()).thenReturn(true);
        when(botonEliminarCliente.isDisable()).thenReturn(true);
        when(botonDescuentoCliente.isDisable()).thenReturn(true);

        // Initialize the controller
        controller.initialize();

        // Set the mock controller
        controller.setClienteController(clienteController);

        // Sample data
        mockClienteList = FXCollections.observableArrayList(
                Cliente.crearParticular(1, "a@a.com", "111", "dir1", "11111111A", LocalDate.now(), "Juan", "Perez"),
                Cliente.crearEmpresa(2, "b@b.com", "222", "dir2", "B22222222", LocalDate.now(), "Empresa SL", "Contacto")
        );
    }

    @Test
    void testSetClienteController() {
        // Already set in @BeforeEach
        // No direct verification needed beyond the setter call itself
    }

    @Test
    void testInitialize_ButtonState() {
        // Verify initial state of buttons
        verify(botonModificarCliente).setDisable(true);
        verify(botonEliminarCliente).setDisable(true);
        verify(botonDescuentoCliente).setDisable(true);
    }

    @Test
    void testInitialize_ColumnSetup() {
        // Verify that PropertyValueFactory is set for basic columns
        verify(colIdCliente).setCellValueFactory(any(javafx.util.Callback.class));
        verify(colDireccion).setCellValueFactory(any(javafx.util.Callback.class));
        verify(colCifNif).setCellValueFactory(any(javafx.util.Callback.class));

        // Verify that custom cell factories are set for complex columns
        verify(colNombreApellidos).setCellValueFactory(any(javafx.util.Callback.class));
        verify(colNombreApellidos).setCellFactory(any(javafx.util.Callback.class));
        verify(colRazonContacto).setCellValueFactory(any(javafx.util.Callback.class));
        verify(colRazonContacto).setCellFactory(any(javafx.util.Callback.class));
        verify(colTelefonoEmail).setCellValueFactory(any(javafx.util.Callback.class));
        verify(colTelefonoEmail).setCellFactory(any(javafx.util.Callback.class));
    }

    @Test
    void testModificarClienteSeleccionado() {
        controller.modificarClienteSeleccionado();
        verify(clienteController).modificarClienteSeleccionado();
    }

    @Test
    void testEliminarClienteSeleccionado() {
        controller.eliminarClienteSeleccionado();
        verify(clienteController).eliminarClienteSeleccionado();
    }

    @Test
    void testVerDescuentosCliente() {
        controller.verDescuentosCliente();
        verify(clienteController).verDescuentos();
    }

    @Test
    void testSetItems() {
        controller.setItems(mockClienteList);
        verify(tablaCliente).setItems(argThat(list -> list.size() == 2 && list.containsAll(mockClienteList)));
    }

    @Test
    void testGetClienteSeleccionado() {
        Cliente selected = mockClienteList.get(0);
        when(tablaCliente.getSelectionModel().getSelectedItem()).thenReturn(selected);
        assertEquals(selected, controller.getClienteSeleccionado());
    }

    @Test
    void testSetDisableBotones() {
        controller.setDisableBotones(false);
        verify(botonModificarCliente).setDisable(false);
        verify(botonEliminarCliente).setDisable(false);
        verify(botonDescuentoCliente).setDisable(false);

        controller.setDisableBotones(true);
        verify(botonModificarCliente, times(2)).setDisable(true); // Once in init, once here
        verify(botonEliminarCliente, times(2)).setDisable(true);
        verify(botonDescuentoCliente, times(2)).setDisable(true);
    }

    @Test
    void testSetAccionesVisible() {
        controller.setAccionesVisible(true);
        verify(accionesCliente).setVisible(true);
        verify(accionesCliente).setManaged(true);

        controller.setAccionesVisible(false);
        verify(accionesCliente).setVisible(false);
        verify(accionesCliente).setManaged(false);
    }
}
