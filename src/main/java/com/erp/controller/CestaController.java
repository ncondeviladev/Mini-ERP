package com.erp.controller;

import com.erp.controller.components.ventaComp.VentaCestaTablaController;
import com.erp.model.DetalleVenta;
import com.erp.utils.AnimationUtils;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Node;
import com.erp.utils.Alerta;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador para la vista de la cesta de la compra (cesta.fxml).
 * Gestiona los productos añadidos a la cesta, calcula el total y permite
 * al usuario proceder a finalizar la venta, volver a la tienda o limpiar la cesta.
 */
public class CestaController implements Initializable {

    private MainController mainController;
    private ObservableList<DetalleVenta> cestaItems;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private VentaCestaTablaController cestaTablaComponenteController;

    @FXML
    private Label labelTotalCesta;

    @FXML
    private Button botonLimpiarCesta;

    @FXML
    private Button botonVolver;

    @FXML
    private Button botonConfirmarCesta;

    /**
     * Inicializa el controlador después de que su elemento raíz haya sido completamente procesado.
     * Añade animaciones a los botones.
     *
     * @param url La ubicación utilizada para resolver rutas relativas para el objeto raíz, o null si la ubicación no es conocida.
     * @param rb  Los recursos utilizados para localizar el objeto raíz, o null si el objeto raíz no fue localizado.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        AnimationUtils.addHoverAnimation(botonLimpiarCesta);
        AnimationUtils.addHoverAnimation(botonVolver);
        AnimationUtils.addHoverAnimation(botonConfirmarCesta);
    }

    /**
     * Establece el controlador principal para permitir la navegación entre vistas.
     *
     * @param mainController La instancia del controlador principal.
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        if (cestaTablaComponenteController != null) {
            cestaTablaComponenteController.setMainController(mainController);
        }
    }

    /**
     * Establece los artículos (productos) en la cesta y actualiza la tabla y el total.
     *
     * @param items La lista observable de detalles de venta que representan los productos en la cesta.
     */
    public void setCestaItems(ObservableList<DetalleVenta> items) {
        this.cestaItems = items;
        if (cestaTablaComponenteController != null) {
            cestaTablaComponenteController.setDetallesVenta(items);
            actualizarTotalCesta();
        }
    }

    /**
     * Devuelve el nodo raíz de la vista para ser mostrado en el panel principal.
     *
     * @return El nodo raíz (AnchorPane) de esta vista.
     */
    public Node getVista() {
        return rootPane;
    }

    /**
     * Calcula y actualiza el label que muestra el coste total de los productos en la cesta.
     */
    public void actualizarTotalCesta() {
        if (cestaTablaComponenteController != null) {
            double total = cestaTablaComponenteController.calcularTotalCesta();
            labelTotalCesta.setText(String.format("%.2f€", total));
        }
    }

    /**
     * Maneja el evento de clic en el botón "Limpiar Cesta".
     * Vacía la lista de artículos de la cesta y actualiza la UI.
     */
    @FXML
    public void limpiarCesta() {
        if (cestaItems != null) {
            cestaItems.clear();
            actualizarTotalCesta();
            Alerta.mostrarAlertaTemporal(javafx.scene.control.Alert.AlertType.INFORMATION, "Cesta Limpiada", null, "La cesta ha sido vaciada.");
        }
    }

    /**
     * Maneja el evento de clic en el botón "Volver".
     * Navega de vuelta a la vista principal de ventas.
     */
    @FXML
    public void volverAVentas() {
        if (mainController != null) {
            mainController.mostrarVentas();
        }
    }

    /**
     * Maneja el evento de clic en el botón "Confirmar Cesta".
     * Si la cesta no está vacía, procede a la siguiente fase de la venta (selección de cliente).
     * Muestra una advertencia si la cesta está vacía.
     */
    @FXML
    public void procederConCesta() {
        if (cestaItems == null || cestaItems.isEmpty()) {
            Alerta.mostrarAlertaTemporal(javafx.scene.control.Alert.AlertType.WARNING, "Advertencia", "Cesta vacía", "No puedes continuar con una cesta vacía.");
            return;
        }

        if (mainController != null) {
            mainController.mostrarSeleccionClienteParaVenta(cestaItems);
        }
    }
}