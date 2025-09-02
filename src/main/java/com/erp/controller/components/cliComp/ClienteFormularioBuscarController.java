package com.erp.controller.components.cliComp;

import java.util.HashMap;
import java.util.Map;

import com.erp.controller.ClienteController;
import com.erp.controller.VentaFinalizarController;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class ClienteFormularioBuscarController {

    @FXML
    private VBox panelRaiz;
    @FXML
    private TextField buscarIdClienteField;
    @FXML
    private TextField buscarNombreClienteField;
    @FXML
    private TextField buscarCifApellidosClienteField; // En el FXML este campo es para CIF/NIF

    private ClienteController clienteController;
    private VentaFinalizarController ventaFinalizarController;

    public void setClienteController(ClienteController clienteController) {
        this.clienteController = clienteController;
    }

    public void setVentaFinalizarController(VentaFinalizarController ventaFinalizarController) {
        this.ventaFinalizarController = ventaFinalizarController;
    }

    @FXML
    public void initialize() {
        // El contenido se mueve a vincularControlador para evitar NullPointerException
    }

    public VBox getPanelRaiz() {
        return panelRaiz;
    }

    /**
     * Vincula los listeners de los campos de texto al método de filtrado del controlador principal.
     * Debe llamarse después de que el controlador padre haya sido inyectado.
     */
    public void vincularControlador() {
        if (clienteController != null) {
            buscarIdClienteField.textProperty().addListener((obs, old, val) -> clienteController.filtrarClientes());
            buscarNombreClienteField.textProperty().addListener((obs, old, val) -> clienteController.filtrarClientes());
            buscarCifApellidosClienteField.textProperty().addListener((obs, old, val) -> clienteController.filtrarClientes());
        } else if (ventaFinalizarController != null) {
            buscarIdClienteField.textProperty().addListener((obs, old, val) -> ventaFinalizarController.filtrarClientes());
            buscarNombreClienteField.textProperty().addListener((obs, old, val) -> ventaFinalizarController.filtrarClientes());
            buscarCifApellidosClienteField.textProperty().addListener((obs, old, val) -> ventaFinalizarController.filtrarClientes());
        }
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