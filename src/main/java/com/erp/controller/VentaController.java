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
import javafx.scene.layout.AnchorPane;
import javafx.scene.Node;
import javafx.scene.layout.Region; // Importar Region

import java.io.IOException; // Importar IOException
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.erp.dao.ProductoDAO; // Added import

/**
 * Controlador para la vista de ventas (venta.fxml).
 * Permite buscar productos, añadirlos a la cesta de la compra y navegar a la vista de la cesta.
 */
public class VentaController implements Initializable {

    private MainController mainController;
    private ObservableList<DetalleVenta> cestaItems;
    private ProductoDAO productoDAO; // Added instance variable
    private List<Producto> productosOriginales = new ArrayList<>();

    @FXML
    private AnchorPane rootPane; // Inyectar el AnchorPane raíz

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

    /**
     * Inicializa el controlador.
     * @param url La ubicación utilizada para resolver rutas relativas para el objeto raíz, o null si la ubicación no es conocida.
     * @param rb Los recursos utilizados para localizar el objeto raíz, o null si el objeto raíz no fue localizado.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Forzar la renderización inicial y luego ocultar
        contenedorFormularioBusqueda.setVisible(true);
        contenedorFormularioBusqueda.setManaged(true);
        contenedorFormularioBusqueda.setVisible(false);
        contenedorFormularioBusqueda.setManaged(false);
        
        productoDAO = new ProductoDAO();
        this.productosOriginales = productoDAO.listarProductos();
        productoTablaController.setItems(this.productosOriginales);

        // Vincular controladores para el filtro
        formularioBuscarProductoController.setVentaController(this);
        formularioBuscarProductoController.vincularControlador();

        // Hide the action buttons (Modify/Delete) from the product table in the sales view
        if (productoTablaController != null) {
            productoTablaController.setAccionesProductoVisible(false);
        }

        // Add listener to product table for auto-focus on quantity field
        productoTablaController.getTablaProducto().getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                campoCantidad.requestFocus();
            }
        });

        // Add double-click listener to product table for auto-focus on quantity field
        productoTablaController.getTablaProducto().setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                campoCantidad.requestFocus();
            }
        });

        // Add listener to product table for Enter key action to focus quantity field
        productoTablaController.getTablaProducto().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
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

    /**
     * Establece el controlador principal.
     * @param mainController El controlador principal.
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * Establece los items de la cesta.
     * @param cestaItems La lista de detalles de venta.
     */
    public void setCestaItems(ObservableList<DetalleVenta> cestaItems) {
        this.cestaItems = cestaItems;
    }

    /**
     * Obtiene la vista raíz.
     * @return El nodo raíz.
     */
    public Node getVista() {
        return rootPane;
    }

    /**
     * Filtra la lista de productos según los criterios de búsqueda.
     */
    public void filtrarProductos() {
        Map<String, String> criterios = formularioBuscarProductoController.getCriteriosBusqueda();
        String filtroId = criterios.get("id").toLowerCase();
        String filtroNombre = criterios.get("nombre").toLowerCase();
        String filtroCategoria = criterios.get("categoria").toLowerCase();

        List<Producto> filtrados = productosOriginales.stream()
            .filter(p -> filtroId.isEmpty() || String.valueOf(p.getId()).contains(filtroId))
            .filter(p -> filtroNombre.isEmpty() || p.getNombre().toLowerCase().contains(filtroNombre))
            .filter(p -> filtroCategoria.isEmpty() || (p.getCategoria() != null && p.getCategoria().toLowerCase().contains(filtroCategoria)))
            .collect(Collectors.toList());

        productoTablaController.setItems(filtrados);
    }

    /**
     * Muestra u oculta el formulario de búsqueda.
     */
    @FXML
    public void mostrarOcultarFormularioBusqueda() {
        // Alternar la visibilidad del formulario de búsqueda
        boolean estaVisible = contenedorFormularioBusqueda.isVisible();
        if (estaVisible) {
            contenedorFormularioBusqueda.setVisible(false);
            contenedorFormularioBusqueda.setMinHeight(0);
            contenedorFormularioBusqueda.setManaged(false);
        } else {
            contenedorFormularioBusqueda.setVisible(true);
            contenedorFormularioBusqueda.setMinHeight(Region.USE_COMPUTED_SIZE);
            contenedorFormularioBusqueda.setManaged(true);
        }
    }

    /**
     * Añade el producto seleccionado a la cesta.
     */
    @FXML
    public void anadirProductoACesta() {
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
    public void verCesta() {
        if (mainController != null) {
            mainController.mostrarCesta();
        }
    }
}
