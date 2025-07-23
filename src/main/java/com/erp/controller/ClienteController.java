package com.erp.controller;

import java.time.LocalDate;

import com.erp.dao.ClienteDAO;
import com.erp.model.Cliente;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class ClienteController {

    private ClienteDAO clienteDAO = new ClienteDAO();

    // Campos del formulario
    @FXML
    private TextField idClienteField, nombreClienteField, apellidosClienteField, razonSocialClienteField,
            personaContactoClienteField,
            emailClienteField, telefonoClienteField, direccionClienteField, cifnifClienteField, tipoClienteField;

    // Tabla y columnas
    @FXML
    private TableView<Cliente> tablaCliente;
    @FXML
    private TableColumn<Cliente, Integer> colIdCliente;
    @FXML
    private TableColumn<Cliente, String> colNombrePrincipalCliente;
    @FXML
    private TableColumn<Cliente, String> colContactoCliente; // Unifica email y teléfono
    @FXML
    private TableColumn<Cliente, String> colDireccionCliente;
    @FXML
    private TableColumn<Cliente, String> colCifnifCliente;
    @FXML
    private TableColumn<Cliente, LocalDate> colFechaAltaCliente;
    @FXML
    private TableColumn<Cliente, String> colTipoCliente;

    // Botones de acción
    @FXML
    private Button añadirClienteButton;
    @FXML
    private Button modificarClienteButton;
    @FXML
    private Button eliminarClienteButton;
    @FXML
    private Button buscarClienteButton;
    @FXML
    private Button limpiarClienteButton;
    @FXML
    private Button verDescuentosClienteButton;

    // Campos de búsqueda (si los usas)
    @FXML
    private TextField buscarIdClienteField, buscarNombreField, buscarRazonSocialField, buscarEmailField;

    @FXML
    void initialize() {
        colIdCliente.setCellValueFactory(new PropertyValueFactory<>("id"));

        colNombrePrincipalCliente.setCellValueFactory(cell -> {
            Cliente c = cell.getValue();
            if ("Particular".equals(c.getTipoCliente())) {
                return new SimpleStringProperty(
                        c.getNombre() + "\n" + c.getApellidos());
            } else {
                return new SimpleStringProperty(
                        c.getRazonSocial() + "\n (" + c.getPersonaContacto() + ")");
            }
        });

        colTipoCliente.setCellValueFactory(new PropertyValueFactory<>("tipoCliente"));
        colContactoCliente.setCellValueFactory
        (cell -> {
            Cliente c = cell.getValue();
            return new SimpleStringProperty(
                c.getTelefono() + "\n" + c.getEmail()
            )
        });

        colDireccionCliente.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colCifnifCliente.setCellValueFactory(new PropertyValueFactory<>("cifnif"));
        colFechaAltaCliente.setCellValueFactory(new PropertyValueFactory<>("fechaAlta"));


        tablaCliente.getItems().addAll(clienteDAO.listarClientes());
    }

}
