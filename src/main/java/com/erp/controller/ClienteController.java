package com.erp.controller;

import java.time.LocalDate;

import com.erp.dao.ClienteDAO;
import com.erp.model.Cliente;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ClienteController {

    private ClienteDAO clienteDAO = new ClienteDAO();

    // Campos del formulario
    @FXML
    private TextField idField, nombreField, apellidosField, razonSocialField, personaContactoField,
            emailField, telefonoField, direccionField, cifnifField, tipoClienteField;

    // Tabla y columnas
    @FXML
    private TableView<Cliente> tablaClientes;
    @FXML
    private TableColumn<Cliente, Integer> colId;
    @FXML
    private TableColumn<Cliente, String> colNombrePrincipal;
    @FXML
    private TableColumn<Cliente, String> colContacto; // Unifica email y teléfono
    @FXML
    private TableColumn<Cliente, String> colDireccion;
    @FXML
    private TableColumn<Cliente, String> colCifnif;
    @FXML
    private TableColumn<Cliente, LocalDate> colFechaAlta;
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
    private TextField buscarIdField, buscarNombreField, buscarRazonSocialField, buscarEmailField;

}
