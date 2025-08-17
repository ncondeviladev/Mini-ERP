package com.erp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class ProductoFormularioBuscarController {

    @FXML
    private TextField buscarIdProductoField;
    @FXML
    private TextField buscarNombreProductoField;
    @FXML
    private TextField buscarCategoriaProductoField;

    // You can add methods here to get the search criteria
    public String getBuscarIdProducto() {
        return buscarIdProductoField.getText();
    }

    public String getBuscarNombreProducto() {
        return buscarNombreProductoField.getText();
    }

    public String getBuscarCategoriaProducto() {
        return buscarCategoriaProductoField.getText();
    }

    // Method to clear the search fields
    public void limpiarCampos() {
        buscarIdProductoField.clear();
        buscarNombreProductoField.clear();
        buscarCategoriaProductoField.clear();
    }
}