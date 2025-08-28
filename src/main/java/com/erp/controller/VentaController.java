package com.erp.controller;

import com.erp.controller.components.prodComp.ProductoFormularioBuscarController;
import com.erp.controller.components.prodComp.ProductoTablaController;
import com.erp.model.DetalleVenta;
import com.erp.model.Producto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import com.erp.utils.Alerta;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.fxml.FXMLLoader; // Importar FXMLLoader
import javafx.scene.Scene; // Importar Scene
import javafx.stage.Modality; // Importar Modality
import javafx.stage.Stage; // Importar Stage

import java.io.IOException; // Importar IOException
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import com.erp.dao.ProductoDAO; // Added import

public class VentaController implements Initializable {

    private MainController mainController;
    private ObservableList<DetalleVenta> cestaItems;
    private ProductoDAO productoDAO; // Added instance variable

    @FXML
    private VBox contenedorFormularioBusqueda; // El VBox que contiene el formulario de búsqueda incluido

    @FXML
    private ProductoFormularioBuscarController formularioBuscarProductoController; // Controlador para el formulario de búsqueda de producto incluido

    @FXML
    private ProductoTablaController productoTablaController; // Controlador para la tabla de productos incluida

    @FXML
    private Button botonBuscarProducto; // Botón para mostrar/ocultar el formulario de búsqueda

    @FXML
    private TextField campoCantidad; // Campo para la cantidad del producto

    @FXML
    private Button botonAnadirCesta; // Botón para añadir el producto a la cesta

    @FXML
    private Button botonVerCesta; // Botón para ver la cesta

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Ocultar el formulario de búsqueda al inicio
        contenedorFormularioBusqueda.setVisible(false); // Uncommented
        contenedorFormularioBusqueda.setManaged(false); // Uncommented
        cestaItems = FXCollections.observableArrayList();
        productoDAO = new ProductoDAO(); // Initialized ProductoDAO
        productoTablaController.setItems(productoDAO.listarProductos()); // Load products

        // Add listener to product table for auto-focus on quantity field
        productoTablaController.getTablaProducto().getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                campoCantidad.requestFocus();
            }
        });

        // Add listener to quantity field for Enter key action
        campoCantidad.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                anadirProductoACesta();
            }
        });
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void mostrarOcultarFormularioBusqueda() {
        // Alternar la visibilidad del formulario de búsqueda
        boolean estaVisible = contenedorFormularioBusqueda.isVisible();
        contenedorFormularioBusqueda.setVisible(!estaVisible);
        contenedorFormularioBusqueda.setManaged(!estaVisible);
    }

    @FXML
    private void anadirProductoACesta() {
        Producto selectedProduct = productoTablaController.getTablaProducto().getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            Alerta.mostrarAlertaTemporal(AlertType.WARNING, "Advertencia", null, "Por favor, selecciona un producto de la tabla.");
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(campoCantidad.getText());
            if (cantidad <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            Alerta.mostrarAlertaTemporal(AlertType.WARNING, "Advertencia", null, "Por favor, introduce una cantidad válida (número entero positivo).");
            return;
        }

        if (cantidad > selectedProduct.getStock()) {
            Alerta.mostrarAlertaTemporal(AlertType.WARNING, "Advertencia", null, "No hay suficiente stock para el producto seleccionado. Stock disponible: " + selectedProduct.getStock());
            return;
        }

        // Buscar si el producto ya está en la cesta
        Optional<DetalleVenta> existingDetalle = cestaItems.stream()
                .filter(d -> d.getProducto().getId().equals(selectedProduct.getId()))
                .findFirst();

        if (existingDetalle.isPresent()) {
            DetalleVenta detalle = existingDetalle.get();
            detalle.setCantidad(detalle.getCantidad() + cantidad);
            // Forzar la actualización de la TableView en la cesta
            cestaItems.set(cestaItems.indexOf(detalle), detalle);
            // No es necesario actualizar el precio unitario si ya está en la cesta,
            // ya que se asume que el precio unitario en el detalle de venta es el precio en el momento de la adición.
            // Si el precio del producto cambia, el detalle de venta existente mantiene el precio original.
        } else {
            DetalleVenta newDetalle = new DetalleVenta(
                    null, // ID se asignará al guardar en BD
                    null, // ventaId se asignará al guardar en BD
                    selectedProduct,
                    cantidad,
                    selectedProduct.getPrecioUnitario() // Precio unitario en el momento de la venta
            );
            cestaItems.add(newDetalle);
        }

        Alerta.mostrarAlertaTemporal(AlertType.INFORMATION, "Éxito", null, "Producto añadido a la cesta correctamente.");
    }

    @FXML
    private void verCesta() {
        if (mainController != null) {
            mainController.mostrarCesta(cestaItems);
        }
    }
}
