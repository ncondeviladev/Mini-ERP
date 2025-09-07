package com.erp.controller.components.cliComp;

import com.erp.dao.ClienteDAO;
import com.erp.model.Cliente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Alert;
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

class ClienteSeleccionDialogoControllerTest {

    @Mock
    private TextField campoBusquedaCliente;
    @Mock
    private TableView<Cliente> tablaClientes;
    @Mock
    private TableColumn<Cliente, String> columnaNombre;
    @Mock
    private TableColumn<Cliente, String> columnaNIF;
    @Mock
    private TableColumn<Cliente, String> columnaEmail;
    @Mock
    private TableColumn<Cliente, String> columnaTelefono;
    @Mock
    private Stage dialogStage;
    @Mock
    private ClienteDAO clienteDAO;

    @InjectMocks
    private ClienteSeleccionDialogoController controller;

    private ObservableList<Cliente> masterData;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock data
        masterData = FXCollections.observableArrayList(
                Cliente.crearParticular(1, "test1@test.com", "111", "dir1", "11111111A", LocalDate.now(), "Juan", "Perez"),
                Cliente.crearEmpresa(2, "test2@test.com", "222", "dir2", "B22222222", LocalDate.now(), "Empresa SL", "Contacto")
        );
        when(clienteDAO.listarClientes()).thenReturn(masterData);

        // Mock TableView behavior
        when(tablaClientes.getItems()).thenReturn(FXCollections.observableArrayList()); // Return an empty list initially
        when(tablaClientes.getSelectionModel()).thenReturn(mock(TableView.TableViewSelectionModel.class));

        // Mock TextField textProperty
        when(campoBusquedaCliente.textProperty()).thenReturn(mock(javafx.beans.property.StringProperty.class));

        // Initialize the controller
        controller.initialize(null, null);
    }

    @Test
    void testInitialize() {
        verify(clienteDAO).listarClientes();
        verify(tablaClientes).setItems(any(FilteredList.class)); // Verify FilteredList is set
        verify(campoBusquedaCliente.textProperty()).addListener(any(javafx.beans.value.ChangeListener.class)); // Verify listener is added
    }

    @Test
    void testSetDialogStage() {
        controller.setDialogStage(dialogStage);
        // No direct verification needed beyond the setter call itself
    }

    @Test
    void testGetSelectedClient_InitiallyNull() {
        assertNull(controller.getSelectedClient());
    }

    @Test
    void testBuscarCliente() {
        // This method is empty in the actual code, so just call it and ensure no exceptions
        controller.buscarCliente();
    }

    @Test
    void testSeleccionar_ClienteSeleccionado() {
        Cliente selected = masterData.get(0);
        when(tablaClientes.getSelectionModel().getSelectedItem()).thenReturn(selected);

        controller.seleccionar();

        assertEquals(selected, controller.getSelectedClient());
        verify(dialogStage).close();
    }

    @Test
    void testSeleccionar_NoClienteSeleccionado() {
        when(tablaClientes.getSelectionModel().getSelectedItem()).thenReturn(null);

        try (MockedStatic<Alert> mockedAlert = mockStatic(Alert.class)) {
            Alert mockAlertInstance = mock(Alert.class);
            doReturn(mockAlertInstance).when(mockedAlert).when(() -> new Alert(any(Alert.AlertType.class)));

            controller.seleccionar();

            assertNull(controller.getSelectedClient());
            mockedAlert.verify(() -> new Alert(Alert.AlertType.WARNING));
            verify(mockAlertInstance).showAndWait();
            verify(dialogStage, never()).close(); // Dialog should not close
        }
    }

    @Test
    void testCancelar() {
        controller.cancelar();

        assertNull(controller.getSelectedClient());
        verify(dialogStage).close();
    }
}
