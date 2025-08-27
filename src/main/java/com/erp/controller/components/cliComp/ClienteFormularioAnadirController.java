package com.erp.controller.components.cliComp;

import com.erp.controller.ClienteController;
import com.erp.model.Cliente;
import com.erp.utils.Alerta;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class ClienteFormularioAnadirController {

    @FXML
    private Label tituloFormularioCliente;
    @FXML
    private ToggleGroup tipoClienteToggleGroup;
    @FXML
    private ToggleButton particularToggle;
    @FXML
    private ToggleButton empresaToggle;
    @FXML
    private StackPane camposDinamicosPane;
    @FXML
    private GridPane camposParticular;
    @FXML
    private GridPane camposEmpresa;
    @FXML
    private TextField nombreClienteField;
    @FXML
    private TextField apellidosClienteField;
    @FXML
    private TextField nifClienteField;
    @FXML
    private TextField razonSocialClienteField;
    @FXML
    private TextField personaContactoClienteField;
    @FXML
    private TextField cifClienteField;
    @FXML
    private TextField direccionClienteField;
    @FXML
    private TextField telefonoClienteField;
    @FXML
    private TextField emailClienteField;
    @FXML
    private Button botonGuardarCliente;

    private ClienteController clienteController;
    private Cliente clienteActual; // Para saber si estamos editando o creando

    @FXML
    public void initialize() {
        configurarVisibilidadCampos();
        tipoClienteToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            configurarVisibilidadCampos();
        });
    }

    public void setClienteController(ClienteController clienteController) {
        this.clienteController = clienteController;
    }

    private void configurarVisibilidadCampos() {
        boolean isParticular = particularToggle.isSelected();
        camposParticular.setVisible(isParticular);
        camposParticular.setManaged(isParticular);
        camposEmpresa.setVisible(!isParticular);
        camposEmpresa.setManaged(!isParticular);
    }

    @FXML
    private void guardarCliente() {
        // 1. Validar campos (simplificado, se puede mejorar con ValidationUtils)
        if (isCamposVacios()) {
            Alerta.mostrarError("Error de Validación", "Todos los campos obligatorios deben estar rellenos.");
            return;
        }

        // 2. Construir el objeto Cliente a partir del formulario
        Cliente cliente = new Cliente();
        if (clienteActual != null) {
            cliente.setId(clienteActual.getId()); // Mantener el ID si es una actualización
        }

        if (particularToggle.isSelected()) {
            cliente.setTipoCliente("Particular");
            cliente.setNombre(nombreClienteField.getText());
            cliente.setApellidos(apellidosClienteField.getText());
            cliente.setCifnif(nifClienteField.getText());
        } else {
            cliente.setTipoCliente("Empresa");
            cliente.setRazonSocial(razonSocialClienteField.getText());
            cliente.setPersonaContacto(personaContactoClienteField.getText());
            cliente.setCifnif(cifClienteField.getText());
        }
        cliente.setDireccion(direccionClienteField.getText());
        cliente.setTelefono(telefonoClienteField.getText());
        cliente.setEmail(emailClienteField.getText());

        // 3. Delegar la acción de guardado/actualización al controlador principal
        if (clienteController != null) {
            clienteController.guardarOActualizarCliente(cliente);
        }
    }

    /**
     * Carga los datos de un cliente existente en el formulario para su modificación.
     * @param cliente El cliente a editar.
     */
    public void cargarDatosCliente(Cliente cliente) {
        this.clienteActual = cliente;
        tituloFormularioCliente.setText("Formulario modificar Cliente");
        botonGuardarCliente.setText("Guardar cambios");

        if ("Empresa".equals(cliente.getTipoCliente())) {
            empresaToggle.setSelected(true);
            razonSocialClienteField.setText(cliente.getRazonSocial());
            personaContactoClienteField.setText(cliente.getPersonaContacto());
            cifClienteField.setText(cliente.getCifnif());
        } else {
            particularToggle.setSelected(true);
            nombreClienteField.setText(cliente.getNombre());
            apellidosClienteField.setText(cliente.getApellidos());
            nifClienteField.setText(cliente.getCifnif());
        }
        direccionClienteField.setText(cliente.getDireccion());
        telefonoClienteField.setText(cliente.getTelefono());
        emailClienteField.setText(cliente.getEmail());
    }

    /**
     * Prepara el formulario para la creación de un nuevo cliente, limpiando los campos.
     */
    public void prepararParaNuevoCliente() {
        this.clienteActual = null;
        tituloFormularioCliente.setText("Formulario añadir Cliente");
        botonGuardarCliente.setText("Añadir cliente");
        particularToggle.setSelected(true);
        nombreClienteField.clear();
        apellidosClienteField.clear();
        nifClienteField.clear();
        razonSocialClienteField.clear();
        personaContactoClienteField.clear();
        cifClienteField.clear();
        direccionClienteField.clear();
        telefonoClienteField.clear();
        emailClienteField.clear();
    }

    private boolean isCamposVacios() {
        if (particularToggle.isSelected()) {
            return nombreClienteField.getText().isBlank() ||
                   apellidosClienteField.getText().isBlank() ||
                   nifClienteField.getText().isBlank();
        } else {
            return razonSocialClienteField.getText().isBlank() ||
                   cifClienteField.getText().isBlank();
        }
    }
}
