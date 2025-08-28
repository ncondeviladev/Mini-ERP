package com.erp.controller;

import com.erp.controller.components.ventaComp.VentaCestaTablaController;
import com.erp.model.DetalleVenta;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.net.URL;
import java.util.ResourceBundle;

public class CestaController implements Initializable {

    private MainController mainController;
    private ObservableList<DetalleVenta> cestaItems;

    @FXML
    private VentaCestaTablaController cestaTablaComponenteController;

    @FXML
    private Label labelTotalCesta;

    @FXML
    private Button botonVolver;

    @FXML
    private Button botonConfirmarCesta;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Lógica de inicialización futura si es necesaria
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        if (cestaTablaComponenteController != null) {
            cestaTablaComponenteController.setMainController(mainController);
        }
    }

    public void setCestaItems(ObservableList<DetalleVenta> items) {
        this.cestaItems = items;
        if (cestaTablaComponenteController != null) {
            cestaTablaComponenteController.setDetallesVenta(items);
            actualizarTotalCesta();
        }
    }

    public void actualizarTotalCesta() {
        if (cestaTablaComponenteController != null) {
            double total = cestaTablaComponenteController.calcularTotalCesta();
            labelTotalCesta.setText(String.format("%.2f€", total));
        }
    }

    @FXML
    private void volverAVentas() {
        if (mainController != null) {
            mainController.mostrarVentas();
        }
    }

    @FXML
    private void procederConCesta() {
        if (cestaItems == null || cestaItems.isEmpty()) {
            Alert alerta = new Alert(AlertType.WARNING);
            alerta.setTitle("Advertencia");
            alerta.setHeaderText("Cesta vacía");
            alerta.setContentText("No puedes continuar con una cesta vacía.");
            alerta.showAndWait();
            return;
        }

        if (mainController != null) {
            mainController.mostrarSeleccionClienteParaVenta(cestaItems);
        }
    }
}
