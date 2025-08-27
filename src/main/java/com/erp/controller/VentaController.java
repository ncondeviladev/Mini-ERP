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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.fxml.FXMLLoader; // Importar FXMLLoader
import javafx.scene.Scene; // Importar Scene
import javafx.stage.Modality; // Importar Modality
import javafx.stage.Stage; // Importar Stage

import java.io.IOException; // Importar IOException
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class VentaController implements Initializable {

    private MainController mainController;
    private ObservableList<DetalleVenta> cestaItems;

    @FXML
    private VBox formularioBuscarProducto; // El VBox que contiene el formulario de búsqueda incluido

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
        formularioBuscarProducto.setVisible(false);
        formularioBuscarProducto.setManaged(false);
        cestaItems = FXCollections.observableArrayList();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        // Pasar el mainController a los controladores hijos si lo necesitan
        if (formularioBuscarProductoController != null) {
            formularioBuscarProductoController.setMainController(mainController);
        }
        if (productoTablaController != null) {
            productoTablaController.setMainController(mainController);
        }
    }

    @FXML
    private void mostrarOcultarFormularioBusqueda() {
        // Alternar la visibilidad del formulario de búsqueda
        boolean estaVisible = formularioBuscarProducto.isVisible();
        formularioBuscarProducto.setVisible(!estaVisible);
        formularioBuscarProducto.setManaged(!estaVisible);
    }

    @FXML
    private void anadirProductoACesta() {
        Producto selectedProduct = productoTablaController.getTablaProductos().getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            Alert alerta = new Alert(AlertType.WARNING);
            alerta.setTitle("Advertencia");
            alerta.setHeaderText(null);
            alerta.setContentText("Por favor, selecciona un producto de la tabla.");
            alerta.showAndWait();
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(campoCantidad.getText());
            if (cantidad <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            Alert alerta = new Alert(AlertType.WARNING);
            alerta.setTitle("Advertencia");
            alerta.setHeaderText(null);
            alerta.setContentText("Por favor, introduce una cantidad válida (número entero positivo).");
            alerta.showAndWait();
            return;
        }

        if (cantidad > selectedProduct.getStock()) {
            Alert alerta = new Alert(AlertType.WARNING);
            alerta.setTitle("Advertencia");
            alerta.setHeaderText(null);
            alerta.setContentText("No hay suficiente stock para el producto seleccionado. Stock disponible: " + selectedProduct.getStock());
            alerta.showAndWait();
            return;
        }

        // Buscar si el producto ya está en la cesta
        Optional<DetalleVenta> existingDetalle = cestaItems.stream()
                .filter(d -> d.getProducto().getId().equals(selectedProduct.getId()))
                .findFirst();

        if (existingDetalle.isPresent()) {
            DetalleVenta detalle = existingDetalle.get();
            detalle.setCantidad(detalle.getCantidad() + cantidad);
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

        Alert alerta = new Alert(AlertType.INFORMATION);
        alerta.setTitle("Éxito");
        alerta.setHeaderText(null);
        alerta.setContentText("Producto añadido a la cesta correctamente.");
        alerta.showAndWait();
    }

    @FXML
    private void verCesta() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/cesta.fxml"));
            VBox root = loader.load();

            CestaController cestaController = loader.getController();
            cestaController.setMainController(mainController); // Pasar el mainController
            cestaController.setCestaItems(cestaItems); // Pasar los items de la cesta

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL); // Bloquear la ventana principal
            stage.setTitle("Cesta de Compra");
            stage.setScene(new Scene(root));
            cestaController.setStage(stage); // Pasar la referencia del Stage al controlador de la cesta
            stage.showAndWait(); // Mostrar y esperar hasta que se cierre
        } catch (IOException e) {
            e.printStackTrace();
            Alert alerta = new Alert(AlertType.ERROR);
            alerta.setTitle("Error");
            alerta.setHeaderText("Error al cargar la cesta");
            alerta.setContentText("No se pudo cargar la ventana de la cesta. " + e.getMessage());
            alerta.showAndWait();
        }
    }
}