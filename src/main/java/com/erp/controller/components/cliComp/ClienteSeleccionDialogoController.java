package com.erp.controller.components.cliComp;

import com.erp.dao.ClienteDAO;
import com.erp.model.Cliente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.net.URL;
import java.util.ResourceBundle;

public class ClienteSeleccionDialogoController implements Initializable {

    @FXML
    private TextField campoBusquedaCliente;
    @FXML
    private TableView<Cliente> tablaClientes;
    @FXML
    private TableColumn<Cliente, String> columnaNombre;
    @FXML
    private TableColumn<Cliente, String> columnaNIF;
    @FXML
    private TableColumn<Cliente, String> columnaEmail;
    @FXML
    private TableColumn<Cliente, String> columnaTelefono;
    @FXML
    private Button botonSeleccionar;
    @FXML
    private Button botonCancelar;

    private Stage dialogStage;
    private Cliente selectedClient;
    private ClienteDAO clienteDAO;
    private ObservableList<Cliente> masterData = FXCollections.observableArrayList();
    private FilteredList<Cliente> filteredData;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        clienteDAO = new ClienteDAO();

        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnaNIF.setCellValueFactory(new PropertyValueFactory<>("cifnif"));
        columnaEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        columnaTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));

        // Load all clients
        masterData.addAll(clienteDAO.listarClientes());
        filteredData = new FilteredList<>(masterData, p -> true); // Initially display all clients
        tablaClientes.setItems(filteredData);

        // Add listener for search field
        campoBusquedaCliente.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(cliente -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true; // Display all clients if search field is empty
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (cliente.getNombre().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (cliente.getCifnif().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false; // No match
            });
        });
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public Cliente getSelectedClient() {
        return selectedClient;
    }

    @FXML
    public void buscarCliente() {
        // The filtering is already handled by the listener on campoBusquedaCliente.textProperty()
        // This method can be empty or trigger a refresh if needed for other reasons.
    }

    @FXML
    public void seleccionar() {
        selectedClient = tablaClientes.getSelectionModel().getSelectedItem();
        if (selectedClient != null) {
            dialogStage.close();
        } else {
            Alert alerta = new Alert(AlertType.WARNING);
            alerta.setTitle("Advertencia");
            alerta.setHeaderText(null);
            alerta.setContentText("Por favor, selecciona un cliente de la tabla.");
            alerta.showAndWait();
        }
    }

    @FXML
    public void cancelar() {
        selectedClient = null; // No client selected
        dialogStage.close();
    }
}