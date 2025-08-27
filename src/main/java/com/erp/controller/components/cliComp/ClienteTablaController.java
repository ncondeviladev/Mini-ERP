package com.erp.controller.components.cliComp;

import java.util.List;

import com.erp.controller.ClienteController;
import com.erp.model.Cliente;
import com.erp.utils.AnimationUtils;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

public class ClienteTablaController {

    @FXML
    private TableView<Cliente> tablaCliente;
    @FXML
    private TableColumn<Cliente, Integer> colIdCliente;
    @FXML
    private TableColumn<Cliente, String> colNombreApellidos;
    @FXML
    private TableColumn<Cliente, String> colRazonContacto;
    @FXML
    private TableColumn<Cliente, String> colTelefonoEmail;
    @FXML
    private TableColumn<Cliente, String> colDireccion;
    @FXML
    private TableColumn<Cliente, String> colCifNif;
    @FXML
    private Button botonModificarCliente;
    @FXML
    private Button botonEliminarCliente;
    @FXML
    private Button botonDescuentoCliente;

    private ClienteController clienteController;

    public void setClienteController(ClienteController clienteController) {
        this.clienteController = clienteController;
    }

    @FXML
    public void initialize() {
        configurarColumnasTabla();

        // Listener para notificar al controlador principal sobre cambios en la selección
        tablaCliente.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (clienteController != null) {
                clienteController.actualizarEstadoBotones(newSelection);
            }
        });

        // Estado inicial de los botones
        setDisableBotones(true);

        // Animaciones
        AnimationUtils.addHoverAnimation(botonModificarCliente);
        AnimationUtils.addHoverAnimation(botonEliminarCliente);
        AnimationUtils.addHoverAnimation(botonDescuentoCliente);
    }

    private void configurarColumnasTabla() {
        colIdCliente.setCellValueFactory(new PropertyValueFactory<>("id"));

        colNombreApellidos.setCellValueFactory(cell -> {
            Cliente c = cell.getValue();
            return new SimpleStringProperty("Particular".equals(c.getTipoCliente()) ? c.getNombre() + "\n" + c.getApellidos() : "");
        });
        setWrappingCellFactory(colNombreApellidos);

        colRazonContacto.setCellValueFactory(cell -> {
            Cliente c = cell.getValue();
            return new SimpleStringProperty("Empresa".equals(c.getTipoCliente()) ? c.getRazonSocial() + "\n" + c.getPersonaContacto() : "");
        });
        setWrappingCellFactory(colRazonContacto);

        colTelefonoEmail.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTelefono() + "\n" + cell.getValue().getEmail()));
        setWrappingCellFactory(colTelefonoEmail);

        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        setWrappingCellFactory(colDireccion);

        colCifNif.setCellValueFactory(new PropertyValueFactory<>("cifnif"));
    }

    private void setWrappingCellFactory(TableColumn<Cliente, String> column) {
        column.setCellFactory(tc -> {
            TableCell<Cliente, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(column.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell;
        });
    }

    @FXML
    private void modificarClienteSeleccionado() {
        clienteController.modificarClienteSeleccionado();
    }

    @FXML
    private void eliminarClienteSeleccionado() {
        clienteController.eliminarClienteSeleccionado();
    }

    @FXML
    private void verDescuentosCliente() {
        clienteController.verDescuentos();
    }

    public void setItems(List<Cliente> clientes) {
        tablaCliente.setItems(FXCollections.observableArrayList(clientes));
    }

    public Cliente getClienteSeleccionado() {
        return tablaCliente.getSelectionModel().getSelectedItem();
    }

    public void setDisableBotones(boolean disable) {
        botonModificarCliente.setDisable(disable);
        botonEliminarCliente.setDisable(disable);
        botonDescuentoCliente.setDisable(disable);
    }
}
