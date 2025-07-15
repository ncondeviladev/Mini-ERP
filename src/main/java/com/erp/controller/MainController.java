package com.erp.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

public class MainController {

    @FXML
    private StackPane contenedorCentral;

    @FXML
    public void initialize() {
        cargarVista("inicio.fxml"); // Esto carga el mensaje de bienvenida
    }

    @FXML
    public void mostrarProductos() {
        cargarVista("producto.fxml");
    }

    @FXML
    public void mostrarClientes() {
        cargarVista("cliente.fxml"); // cuando lo tengas
    }

    @FXML
    public void mostrarVentas() {
        cargarVista("venta.fxml"); // cuando lo tengas
    }

    @FXML
    public void salirAplicacion() {
        System.exit(0);
    }

    private void cargarVista(String nombreFXML) {
        try {
            Node vista = FXMLLoader.load(getClass().getResource("/fxml/" + nombreFXML));
            contenedorCentral.getChildren().setAll(vista);
        } catch (Exception e) {
            e.printStackTrace(); // Puedes usar alertas si quieres notificar errores
        }
    }
}