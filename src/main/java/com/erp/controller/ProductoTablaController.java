package com.erp.controller;

import com.erp.model.Producto;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

public class ProductoTablaController {

    @FXML
    private TableView<Producto> tablaProducto;
    @FXML
    private TableColumn<Producto, Integer> colIdProducto;
    @FXML
    private TableColumn<Producto, String> colNombreProducto;
    @FXML
    private TableColumn<Producto, String> colCategoriaProducto;
    @FXML
    private TableColumn<Producto, Double> colPrecioProducto;
    @FXML
    private TableColumn<Producto, Integer> colStockProducto;
    @FXML
    private TableColumn<Producto, String> colDescripcionProducto;
    @FXML
    private HBox accionesProducto;
    @FXML
    private Button botonModificarProducto;
    @FXML
    private Button botonEliminarProducto;

    // Initialize method for the table columns
    @FXML
    public void initialize() {
        colIdProducto.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombreProducto.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoriaProducto.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colPrecioProducto.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStockProducto.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colDescripcionProducto.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        // Listener for table selection to enable/disable buttons
        tablaProducto.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean disableButtons = newSelection == null;
            botonModificarProducto.setDisable(disableButtons);
            botonEliminarProducto.setDisable(disableButtons);
        });
    }

    // Method to set items in the table
    public void setProductos(Iterable<Producto> productos) {
        tablaProducto.getItems().setAll(productos);
    }

    // Method to get the selected product
    public Producto getSelectedProducto() {
        return tablaProducto.getSelectionModel().getSelectedItem();
    }

    // Methods to handle button actions (will be called by ProductoController)
    // @FXML
    // private void modificarProductoSeleccionado() {
    //     // This will be handled by the main ProductoController
    // }

    // @FXML
    // private void eliminarProductoSeleccionado() {
    //     // This will be handled by the main ProductoController
    // }
}