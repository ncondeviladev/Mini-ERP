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

public class CestaController implements Initializable {

    private MainController mainController;
    private ObservableList<DetalleVenta> cestaItems;

    @FXML
    private AnchorPane rootPane; // Inyectar el AnchorPane raíz

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        AnimationUtils.addHoverAnimation(botonLimpiarCesta);
        AnimationUtils.addHoverAnimation(botonVolver);
        AnimationUtils.addHoverAnimation(botonConfirmarCesta);
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

    public Node getVista() {
        return rootPane;
    }

    public void actualizarTotalCesta() {
        if (cestaTablaComponenteController != null) {
            double total = cestaTablaComponenteController.calcularTotalCesta();
            labelTotalCesta.setText(String.format("%.2f€", total));
        }
    }

    @FXML
    private void limpiarCesta() {
        if (cestaItems != null) {
            cestaItems.clear();
            actualizarTotalCesta();
            Alerta.mostrarAlertaTemporal(javafx.scene.control.Alert.AlertType.INFORMATION, "Cesta Limpiada", null, "La cesta ha sido vaciada.");
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
            Alerta.mostrarAlertaTemporal(javafx.scene.control.Alert.AlertType.WARNING, "Advertencia", "Cesta vacía", "No puedes continuar con una cesta vacía.");
            return;
        }

        if (mainController != null) {
            mainController.mostrarSeleccionClienteParaVenta(cestaItems);
        }
    }
}
