package com.erp.controller.components.ventaComp;

import com.erp.controller.MainController;
import com.erp.model.DetalleVenta; // Assuming DetalleVenta will represent items in the cart
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.ObservableList; // Added this import

public class VentaCestaTablaController implements Initializable {

    private MainController mainController;

    @FXML
    private TableView<DetalleVenta> tablaCesta;
    @FXML
    private TableColumn<DetalleVenta, String> columnaProducto;
    @FXML
    private TableColumn<DetalleVenta, Integer> columnaCantidad;
    @FXML
    private TableColumn<DetalleVenta, Double> columnaPrecioUnitario;
    @FXML
    private TableColumn<DetalleVenta, Double> columnaSubtotal;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        columnaProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto")); // Assuming DetalleVenta has getNombreProducto()
        columnaCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        columnaPrecioUnitario.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        columnaSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public TableView<DetalleVenta> getTablaCesta() {
        return tablaCesta;
    }

    // Método para establecer los elementos en la tabla de la cesta
    public void setDetallesVenta(ObservableList<DetalleVenta> detalles) { // Modified signature
        tablaCesta.setItems(detalles); // Modified body
    }

    // Método para añadir un detalle de venta a la cesta
    public void anadirDetalleVenta(DetalleVenta detalle) {
        tablaCesta.getItems().add(detalle);
    }

    // Método para obtener todos los detalles de venta en la cesta
    public List<DetalleVenta> getDetallesVenta() {
        return tablaCesta.getItems();
    }

    // Método para calcular el total de la cesta
    public double calcularTotalCesta() {
        return tablaCesta.getItems().stream()
                .mapToDouble(DetalleVenta::getSubtotal)
                .sum();
    }
}