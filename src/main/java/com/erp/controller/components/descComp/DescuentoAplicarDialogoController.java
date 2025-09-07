package com.erp.controller.components.descComp;

import com.erp.dao.DescuentoDAO;
import com.erp.model.Descuento;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class DescuentoAplicarDialogoController implements Initializable {

    @FXML
    private TextField campoDescuentoManual;
    @FXML
    private TableView<Descuento> tablaDescuentos;
    @FXML
    private TableColumn<Descuento, String> columnaNombre;
    @FXML
    private TableColumn<Descuento, Double> columnaPorcentaje;
    @FXML
    private Button botonAplicarManual;
    @FXML
    private Button botonAplicarSeleccionado;
    @FXML
    private Button botonCancelar;

    private Stage dialogStage;
    private double appliedDiscount = 0.0; // Stores the applied discount percentage
    private DescuentoDAO descuentoDAO;
    private ObservableList<Descuento> masterData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        descuentoDAO = new DescuentoDAO();

        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        columnaPorcentaje.setCellValueFactory(new PropertyValueFactory<>("porcentaje"));

        masterData.addAll(descuentoDAO.listarDescuentos());
        tablaDescuentos.setItems(masterData);
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public double getAppliedDiscount() {
        return appliedDiscount;
    }

    @FXML
    public void aplicarDescuentoManual() {
        try {
            double manualDiscount = Double.parseDouble(campoDescuentoManual.getText());
            if (manualDiscount < 0 || manualDiscount > 100) {
                throw new NumberFormatException("El descuento debe estar entre 0 y 100.");
            }
            appliedDiscount = manualDiscount;
            dialogStage.close();
        } catch (NumberFormatException e) {
            Alert alerta = new Alert(AlertType.WARNING);
            alerta.setTitle("Advertencia");
            alerta.setHeaderText(null);
            alerta.setContentText("Por favor, introduce un porcentaje de descuento válido (0-100).");
            alerta.showAndWait();
        }
    }

    @FXML
    public void aplicarDescuentoSeleccionado() {
        Descuento selectedDescuento = tablaDescuentos.getSelectionModel().getSelectedItem();
        if (selectedDescuento != null) {
            appliedDiscount = selectedDescuento.getPorcentaje();
            dialogStage.close();
        } else {
            Alert alerta = new Alert(AlertType.WARNING);
            alerta.setTitle("Advertencia");
            alerta.setHeaderText(null);
            alerta.setContentText("Por favor, selecciona un descuento de la tabla.");
            alerta.showAndWait();
        }
    }

    @FXML
    public void cancelar() {
        appliedDiscount = 0.0; // No discount applied
        dialogStage.close();
    }
}