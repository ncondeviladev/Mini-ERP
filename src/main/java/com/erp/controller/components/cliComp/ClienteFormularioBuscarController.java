package com.erp.controller.components.cliComp;

import java.util.HashMap;
import java.util.Map;

import com.erp.controller.ClienteController;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class ClienteFormularioBuscarController {

    @FXML
    private TextField buscarIdClienteField;
    @FXML
    private TextField buscarNombreClienteField;
    @FXML
    private TextField buscarCifApellidosClienteField; // En el FXML este campo es para CIF/NIF

    private ClienteController clienteController;

    public void setClienteController(ClienteController clienteController) {
        this.clienteController = clienteController;
    }

    @FXML
    public void initialize() {
        // El contenido se mueve a vincularControlador para evitar NullPointerException
    }

    /**
     * Vincula los listeners de los campos de texto al método de filtrado del controlador principal.
     * Debe llamarse después de que el clienteController haya sido inyectado.
     */
    public void vincularControlador() {
        buscarIdClienteField.textProperty().addListener((obs, old, val) -> clienteController.filtrarClientes());
        buscarNombreClienteField.textProperty().addListener((obs, old, val) -> clienteController.filtrarClientes());
        buscarCifApellidosClienteField.textProperty().addListener((obs, old, val) -> clienteController.filtrarClientes());
    }

    /**
     * Recopila los criterios de búsqueda de los campos de texto.
     * @return Un mapa con los criterios de búsqueda.
     */
    public Map<String, String> getCriteriosBusqueda() {
        Map<String, String> criterios = new HashMap<>();
        criterios.put("id", buscarIdClienteField.getText().trim());
        criterios.put("nombre", buscarNombreClienteField.getText().trim());
        criterios.put("cifnif", buscarCifApellidosClienteField.getText().trim());
        return criterios;
    }

    /**
     * Limpia todos los campos del formulario de búsqueda.
     */
    public void limpiarCampos() {
        buscarIdClienteField.clear();
        buscarNombreClienteField.clear();
        buscarCifApellidosClienteField.clear();
    }
}
