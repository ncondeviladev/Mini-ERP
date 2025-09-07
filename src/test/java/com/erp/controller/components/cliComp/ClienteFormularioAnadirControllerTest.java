package com.erp.controller.components.cliComp;

import com.erp.controller.ClienteController;
import com.erp.model.Cliente;
import com.erp.utils.Alerta;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClienteFormularioAnadirControllerTest {

    @Mock
    private ClienteController clienteController;
    @Mock
    private Label tituloFormularioCliente;
    @Mock
    private ToggleGroup tipoClienteToggleGroup;
    @Mock
    private ToggleButton particularToggle;
    @Mock
    private ToggleButton empresaToggle;
    @Mock
    private StackPane camposDinamicosPane;
    @Mock
    private GridPane camposParticular;
    @Mock
    private GridPane camposEmpresa;
    @Mock
    private TextField nombreClienteField;
    @Mock
    private TextField apellidosClienteField;
    @Mock
    private TextField nifClienteField;
    @Mock
    private TextField razonSocialClienteField;
    @Mock
    private TextField personaContactoClienteField;
    @Mock
    private TextField cifClienteField;
    @Mock
    private TextField direccionClienteField;
    @Mock
    private TextField telefonoClienteField;
    @Mock
    private TextField emailClienteField;
    @Mock
    private Button botonGuardarCliente;

    @InjectMocks
    private ClienteFormularioAnadirController controller;

    // No longer need to mock clienteActual directly, use controller methods

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Default toggle selection
        when(particularToggle.isSelected()).thenReturn(true);
        when(empresaToggle.isSelected()).thenReturn(false);

        // Mock ToggleGroup listener behavior
        doAnswer(invocation -> {
            // Simulate listener being added
            return null;
        }).when(tipoClienteToggleGroup).selectedToggleProperty();

        // Call initialize manually as it's not called by InjectMocks for FXML controllers
        controller.initialize();
    }

    @Test
    void testSetClienteController() {
        controller.setClienteController(clienteController);
        // No direct verification needed beyond the setter call itself
    }

    @Test
    void testConfigurarVisibilidadCampos_Particular() {
        when(particularToggle.isSelected()).thenReturn(true);
        when(empresaToggle.isSelected()).thenReturn(false);

        controller.configurarVisibilidadCampos();

        verify(camposParticular).setVisible(true);
        verify(camposParticular).setManaged(true);
        verify(camposEmpresa).setVisible(false);
        verify(camposEmpresa).setManaged(false);
    }

    @Test
    void testConfigurarVisibilidadCampos_Empresa() {
        when(particularToggle.isSelected()).thenReturn(false);
        when(empresaToggle.isSelected()).thenReturn(true);

        controller.configurarVisibilidadCampos();

        verify(camposParticular).setVisible(false);
        verify(camposParticular).setManaged(false);
        verify(camposEmpresa).setVisible(true);
        verify(camposEmpresa).setManaged(true);
    }

    @Test
    void testGuardarCliente_CamposVaciosParticular() {
        when(particularToggle.isSelected()).thenReturn(true);
        when(nombreClienteField.getText()).thenReturn("");
        when(apellidosClienteField.getText()).thenReturn("Apellidos");
        when(nifClienteField.getText()).thenReturn("12345678A");

        try (MockedStatic<Alerta> mockedAlerta = mockStatic(Alerta.class)) {
            controller.guardarCliente();
            mockedAlerta.verify(() -> Alerta.mostrarError("Error de Validación", "Todos los campos obligatorios deben estar rellenos."));
            verify(clienteController, never()).guardarOActualizarCliente(any(Cliente.class));
        }
    }

    @Test
    void testGuardarCliente_CamposVaciosEmpresa() {
        when(particularToggle.isSelected()).thenReturn(false);
        when(empresaToggle.isSelected()).thenReturn(true);
        when(razonSocialClienteField.getText()).thenReturn("");
        when(cifClienteField.getText()).thenReturn("B12345678");

        try (MockedStatic<Alerta> mockedAlerta = mockStatic(Alerta.class)) {
            controller.guardarCliente();
            mockedAlerta.verify(() -> Alerta.mostrarError("Error de Validación", "Todos los campos obligatorios deben estar rellenos."));
            verify(clienteController, never()).guardarOActualizarCliente(any(Cliente.class));
        }
    }

    @Test
    void testGuardarCliente_NuevoParticular() {
        controller.setClienteController(clienteController);
        controller.prepararParaNuevoCliente(); // Set clienteActual to null
        when(particularToggle.isSelected()).thenReturn(true);
        when(nombreClienteField.getText()).thenReturn("Nuevo");
        when(apellidosClienteField.getText()).thenReturn("Particular");
        when(nifClienteField.getText()).thenReturn("12345678A");
        when(direccionClienteField.getText()).thenReturn("Calle Falsa 123");
        when(telefonoClienteField.getText()).thenReturn("600112233");
        when(emailClienteField.getText()).thenReturn("nuevo@particular.com");

        controller.guardarCliente();

        verify(clienteController).guardarOActualizarCliente(argThat(cliente ->
                cliente.getTipoCliente().equals("Particular") &&
                        cliente.getNombre().equals("Nuevo") &&
                        cliente.getApellidos().equals("Particular") &&
                        cliente.getCifnif().equals("12345678A") &&
                        cliente.getDireccion().equals("Calle Falsa 123") &&
                        cliente.getTelefono().equals("600112233") &&
                        cliente.getEmail().equals("nuevo@particular.com") &&
                        cliente.getId() == null
        ));
    }

    @Test
    void testGuardarCliente_NuevaEmpresa() {
        controller.setClienteController(clienteController);
        controller.prepararParaNuevoCliente(); // Set clienteActual to null
        when(particularToggle.isSelected()).thenReturn(false);
        when(empresaToggle.isSelected()).thenReturn(true);
        when(razonSocialClienteField.getText()).thenReturn("Nueva Empresa SL");
        when(personaContactoClienteField.getText()).thenReturn("Contacto Empresa");
        when(cifClienteField.getText()).thenReturn("B12345678");
        when(direccionClienteField.getText()).thenReturn("Av. Empresa 45");
        when(telefonoClienteField.getText()).thenReturn("900112233");
        when(emailClienteField.getText()).thenReturn("info@empresa.com");

        controller.guardarCliente();

        verify(clienteController).guardarOActualizarCliente(argThat(cliente ->
                cliente.getTipoCliente().equals("Empresa") &&
                        cliente.getRazonSocial().equals("Nueva Empresa SL") &&
                        cliente.getPersonaContacto().equals("Contacto Empresa") &&
                        cliente.getCifnif().equals("B12345678") &&
                        cliente.getDireccion().equals("Av. Empresa 45") &&
                        cliente.getTelefono().equals("900112233") &&
                        cliente.getEmail().equals("info@empresa.com") &&
                        cliente.getId() == null
        ));
    }

    @Test
    void testGuardarCliente_ActualizarParticular() {
        controller.setClienteController(clienteController);
        Cliente clienteExistente = Cliente.crearParticular(1, "old@email.com", "111", "old dir", "11111111B", LocalDate.now(), "Old", "Client");
        controller.cargarDatosCliente(clienteExistente); // Set clienteActual

        when(particularToggle.isSelected()).thenReturn(true);
        when(nombreClienteField.getText()).thenReturn("Updated");
        when(apellidosClienteField.getText()).thenReturn("Client");
        when(nifClienteField.getText()).thenReturn("22222222C");
        when(direccionClienteField.getText()).thenReturn("Updated Dir");
        when(telefonoClienteField.getText()).thenReturn("777888999");
        when(emailClienteField.getText()).thenReturn("updated@email.com");

        controller.guardarCliente();

        verify(clienteController).guardarOActualizarCliente(argThat(cliente ->
                cliente.getId().equals(1) && // ID should be preserved
                        cliente.getTipoCliente().equals("Particular") &&
                        cliente.getNombre().equals("Updated") &&
                        cliente.getCifnif().equals("22222222C")
        ));
    }

    @Test
    void testCargarDatosCliente_Particular() {
        Cliente cliente = Cliente.crearParticular(1, "test@test.com", "123", "dir", "12345678A", LocalDate.now(), "Juan", "Perez");
        controller.cargarDatosCliente(cliente);

        verify(tituloFormularioCliente).setText("Formulario modificar Cliente");
        verify(botonGuardarCliente).setText("Guardar cambios");
        verify(particularToggle).setSelected(true);
        verify(nombreClienteField).setText("Juan");
        verify(apellidosClienteField).setText("Perez");
        verify(nifClienteField).setText("12345678A");
        verify(direccionClienteField).setText("dir");
        verify(telefonoClienteField).setText("123");
        verify(emailClienteField).setText("test@test.com");

        // Ensure empresa fields are not set
        verify(razonSocialClienteField, never()).setText(anyString());
        verify(personaContactoClienteField, never()).setText(anyString());
        verify(cifClienteField, never()).setText(anyString()); // This one is tricky as it's also for NIF
    }

    @Test
    void testCargarDatosCliente_Empresa() {
        Cliente cliente = Cliente.crearEmpresa(2, "info@empresa.com", "456", "dirEmpresa", "B87654321", LocalDate.now(), "Empresa Test SL", "Contacto Test");
        controller.cargarDatosCliente(cliente);

        verify(tituloFormularioCliente).setText("Formulario modificar Cliente");
        verify(botonGuardarCliente).setText("Guardar cambios");
        verify(empresaToggle).setSelected(true);
        verify(razonSocialClienteField).setText("Empresa Test SL");
        verify(personaContactoClienteField).setText("Contacto Test");
        verify(cifClienteField).setText("B87654321");
        verify(direccionClienteField).setText("dirEmpresa");
        verify(telefonoClienteField).setText("456");
        verify(emailClienteField).setText("info@empresa.com");

        // Ensure particular fields are not set
        verify(nombreClienteField, never()).setText(anyString());
        verify(apellidosClienteField, never()).setText(anyString());
        verify(nifClienteField, never()).setText(anyString());
    }

    @Test
    void testPrepararParaNuevoCliente() {
        controller.prepararParaNuevoCliente();

        verify(tituloFormularioCliente).setText("Formulario añadir Cliente");
        verify(botonGuardarCliente).setText("Añadir cliente");
        verify(particularToggle).setSelected(true);
        verify(nombreClienteField).clear();
        verify(apellidosClienteField).clear();
        verify(nifClienteField).clear();
        verify(razonSocialClienteField).clear();
        verify(personaContactoClienteField).clear();
        verify(cifClienteField).clear();
        verify(direccionClienteField).clear();
        verify(telefonoClienteField).clear();
        verify(emailClienteField).clear();
        // No longer checking controller.clienteActual directly
    }

    @Test
    void testIsCamposVacios_Particular_True() {
        when(particularToggle.isSelected()).thenReturn(true);
        when(nombreClienteField.getText()).thenReturn("");
        when(apellidosClienteField.getText()).thenReturn("Apellidos");
        when(nifClienteField.getText()).thenReturn("12345678A");

        assertTrue(controller.isCamposVacios());
    }

    @Test
    void testIsCamposVacios_Particular_False() {
        when(particularToggle.isSelected()).thenReturn(true);
        when(nombreClienteField.getText()).thenReturn("Nombre");
        when(apellidosClienteField.getText()).thenReturn("Apellidos");
        when(nifClienteField.getText()).thenReturn("12345678A");

        assertFalse(controller.isCamposVacios());
    }

    @Test
    void testIsCamposVacios_Empresa_True() {
        when(particularToggle.isSelected()).thenReturn(false);
        when(empresaToggle.isSelected()).thenReturn(true);
        when(razonSocialClienteField.getText()).thenReturn("");
        when(cifClienteField.getText()).thenReturn("B12345678");

        assertTrue(controller.isCamposVacios());
    }

    @Test
    void testIsCamposVacios_Empresa_False() {
        when(particularToggle.isSelected()).thenReturn(false);
        when(empresaToggle.isSelected()).thenReturn(true);
        when(razonSocialClienteField.getText()).thenReturn("Razon Social");
        when(cifClienteField.getText()).thenReturn("B12345678");

        assertFalse(controller.isCamposVacios());
    }
}