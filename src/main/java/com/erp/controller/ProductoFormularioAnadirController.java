package com.erp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class ProductoFormularioAnadirController {

    @FXML
    private VBox formularioAñadirProducto;
    @FXML
    private Label tituloFormularioProducto;
    @FXML
    private TextField nombreProductoField;
    @FXML
    private TextField descripcionProductoField;
    @FXML
    private TextField categoriaProductoField;
    @FXML
    private TextField precioProductoField;
    @FXML
    private TextField stockProductoField;
    @FXML
    private Button botonGuardarProducto;

    // You can add methods here to set/get product data
    public void setNombreProducto(String nombre) {
        nombreProductoField.setText(nombre);
    }

    public String getNombreProducto() {
        return nombreProductoField.getText();
    }

    public void setDescripcionProducto(String descripcion) {
        descripcionProductoField.setText(descripcion);
    }

    public String getDescripcionProducto() {
        return descripcionProductoField.getText();
    }

    public void setCategoriaProducto(String categoria) {
        categoriaProductoField.setText(categoria);
    }

    public String getCategoriaProducto() {
        return categoriaProductoField.getText();
    }

    public void setPrecioProducto(String precio) {
        precioProductoField.setText(precio);
    }

    public String getPrecioProducto() {
        return precioProductoField.getText();
    }

    public void setStockProducto(String stock) {
        stockProductoField.setText(stock);
    }

    public String getStockProducto() {
        return stockProductoField.getText();
    }

    public void setTituloFormulario(String titulo) {
        tituloFormularioProducto.setText(titulo);
    }

    public void setBotonGuardarTexto(String texto) {
        botonGuardarProducto.setText(texto);
    }

    // Method to clear the input fields
    public void limpiarCampos() {
        nombreProductoField.clear();
        descripcionProductoField.clear();
        categoriaProductoField.clear();
        precioProductoField.clear();
        stockProductoField.clear();
    }

    // Method to handle the save action (will be called by ProductoController)
    // @FXML
    // private void insertarProducto() {
    //     // This method will be handled by the main ProductoController
    // }
}