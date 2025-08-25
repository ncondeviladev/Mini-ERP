package com.erp.controller.components;

import java.util.List;

import com.erp.controller.ProductoController;
import com.erp.model.Producto;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controlador para el componente de la tabla de productos.
 * Gestiona la visualizacion de los datos y las acciones sobre la tabla.
 */
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
    private Button botonModificarProducto;
    @FXML
    private Button botonEliminarProducto;

    private ProductoController productoController;

    public void setProductoController(ProductoController productoController) {
        this.productoController = productoController;
    }

    @FXML
    private void initialize() {
        // Configurar las columnas de la tabla
        colIdProducto.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombreProducto.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoriaProducto.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colPrecioProducto.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colStockProducto.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colDescripcionProducto.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        // Listener para habilitar/deshabilitar botones
        tablaProducto.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> productoController.actualizarEstadoBotones(newSelection));

        // Las acciones de los botones ahora se gestionan mediante onAction en el FXML.
    }

    /**
     * Método invocado por el onAction del botón 'Modificar'.
     * Notifica al controlador principal para que inicie el proceso de modificación.
     */
    @FXML
    private void modificarProductoSeleccionado() {
        if (productoController != null) {
            productoController.modificarProductoSeleccionado();
        }
    }

    /**
     * Método invocado por el onAction del botón 'Eliminar'.
     * Notifica al controlador principal para que inicie el proceso de eliminación.
     */
    @FXML
    private void eliminarProductoSeleccionado() {
        if (productoController != null) {
            productoController.eliminarProductoSeleccionado();
        }
    }

    public void setItems(List<Producto> productos) {
        tablaProducto.setItems(FXCollections.observableArrayList(productos));
    }

    public Producto getProductoSeleccionado() {
        return tablaProducto.getSelectionModel().getSelectedItem();
    }

    public void setDisableBotones(boolean disable) {
        botonModificarProducto.setDisable(disable);
        botonEliminarProducto.setDisable(disable);
    }
}