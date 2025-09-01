package com.erp.controller.components.prodComp;

import java.util.HashMap;
import java.util.Map;

import com.erp.controller.ProductoController;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * Controlador para el componente de formulario de búsqueda de producto.
 * Gestiona los campos de filtro y notifica al controlador principal para que
 * actualice la tabla.
 */
public class ProductoFormularioBuscarController {

    @FXML
    private TextField buscarIdProductoField;
    @FXML
    private TextField buscarNombreProductoField;
    @FXML
    private TextField buscarCategoriaProductoField;

    private ProductoController productoController;

    public void setProductoController(ProductoController productoController) {
        this.productoController = productoController;
    }

    @FXML
    public void initialize() {
        // El contenido se mueve a vincularControlador para evitar NullPointerException
    }

    /**
     * Vincula los listeners de los campos de texto al método de filtrado del controlador principal.
     * Debe llamarse después de que el productoController haya sido inyectado.
     */
    public void vincularControlador() {
        buscarIdProductoField.textProperty().addListener((obs, old, val) -> productoController.filtrarProductos());
        buscarNombreProductoField.textProperty().addListener((obs, old, val) -> productoController.filtrarProductos());
        buscarCategoriaProductoField.textProperty().addListener((obs, old, val) -> productoController.filtrarProductos());
    }

    /**
     * Recoge los criterios de búsqueda de los campos de texto.
     * @return Un mapa con los criterios de búsqueda.
     */
    public Map<String, String> getCriteriosBusqueda() {
        Map<String, String> criterios = new HashMap<>();
        criterios.put("id", buscarIdProductoField.getText());
        criterios.put("nombre", buscarNombreProductoField.getText());
        criterios.put("categoria", buscarCategoriaProductoField.getText());
        return criterios;
    }
}