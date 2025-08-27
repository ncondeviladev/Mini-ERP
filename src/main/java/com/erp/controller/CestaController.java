package com.erp.controller;

import com.erp.controller.components.cliComp.ClienteTablaController;
import com.erp.controller.components.ventaComp.VentaCestaTablaController;
import com.erp.model.DetalleVenta;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.net.URL;
import java.util.ResourceBundle;
import com.erp.dao.VentaDAO;
import com.erp.model.Cliente;
import com.erp.model.Venta;
import java.time.LocalDate;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import java.io.IOException;
import com.erp.controller.components.cliComp.ClienteSeleccionDialogoController;
import com.erp.controller.components.descComp.DescuentoAplicarDialogoController; // New import

import javafx.scene.layout.VBox;

public class CestaController implements Initializable {

    private MainController mainController;
    private Stage stage;
    private VentaDAO ventaDAO;
    private double descuentoAplicado = 0.0; // New field to store the applied discount

    @FXML
    private VentaCestaTablaController cestaTablaComponenteController;

    @FXML
    private ClienteTablaController clienteTablaComponenteController;

    @FXML
    private Label labelTotalCesta;

    @FXML
    private Button botonAplicarDescuentos;

    @FXML
    private Button botonFinalizarVenta;

    @FXML
    private Button botonCancelar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ventaDAO = new VentaDAO();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        if (cestaTablaComponenteController != null) {
            cestaTablaComponenteController.setMainController(mainController);
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setCestaItems(ObservableList<DetalleVenta> items) {
        if (cestaTablaComponenteController != null) {
            cestaTablaComponenteController.setDetallesVenta(items);
            actualizarTotalCesta(cestaTablaComponenteController.calcularTotalCesta());
        }
    }

    @FXML
    private void aplicarDescuentos() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/descComp/descuento-aplicar-dialogo.fxml"));
            VBox root = loader.load();

            DescuentoAplicarDialogoController controller = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Aplicar Descuento");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(stage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            // Get the applied discount from the dialog
            descuentoAplicado = controller.getAppliedDiscount();

            // Recalculate total with discount
            double totalConDescuento = cestaTablaComponenteController.calcularTotalCesta() * (1 - (descuentoAplicado / 100.0));
            actualizarTotalCesta(totalConDescuento);

            Alert alerta = new Alert(AlertType.INFORMATION);
            alerta.setTitle("Descuento Aplicado");
            alerta.setHeaderText(null);
            alerta.setContentText("Descuento del " + String.format("%.2f", descuentoAplicado) + "% aplicado.");
            alerta.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alerta = new Alert(AlertType.ERROR);
            alerta.setTitle("Error");
            alerta.setHeaderText("Error al abrir el diálogo de descuentos");
            alerta.setContentText("No se pudo cargar la ventana de descuentos: " + e.getMessage());
            alerta.showAndWait();
        }
    }

    @FXML
    private void finalizarVenta() {
        if (cestaTablaComponenteController.getDetallesVenta().isEmpty()) {
            Alert alerta = new Alert(AlertType.WARNING);
            alerta.setTitle("Advertencia");
            alerta.setHeaderText(null);
            alerta.setContentText("La cesta está vacía. No se puede finalizar la venta.");
            alerta.showAndWait();
            return;
        }

        Cliente clienteSeleccionado = seleccionarCliente();
        if (clienteSeleccionado == null) {
            Alert alerta = new Alert(AlertType.WARNING);
            alerta.setTitle("Advertencia");
            alerta.setHeaderText(null);
            alerta.setContentText("Debe seleccionar un cliente para finalizar la venta.");
            alerta.showAndWait();
            return;
        }

        try {
            Venta nuevaVenta = new Venta(
                    null,
                    clienteSeleccionado,
                    LocalDate.now(),
                    cestaTablaComponenteController.calcularTotalCesta(), // This will be the total BEFORE discount
                    descuentoAplicado // Use the applied discount
            );

            ventaDAO.guardarVenta(nuevaVenta, cestaTablaComponenteController.getDetallesVenta());

            Alert alerta = new Alert(AlertType.INFORMATION);
            alerta.setTitle("Éxito");
            alerta.setHeaderText(null);
            alerta.setContentText("Venta finalizada y guardada correctamente.");
            alerta.showAndWait();

            if (stage != null) {
                stage.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Alert alerta = new Alert(AlertType.ERROR);
            alerta.setTitle("Error");
            alerta.setHeaderText("Error al finalizar la venta");
            alerta.setContentText("Ocurrió un error al guardar la venta: " + e.getMessage());
            alerta.showAndWait();
        }
    }

    private Cliente seleccionarCliente() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/cliComp/cliente-seleccion-dialogo.fxml"));
            VBox root = loader.load();

            ClienteSeleccionDialogoController controller = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Seleccionar Cliente");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(stage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            return controller.getSelectedClient();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alerta = new Alert(AlertType.ERROR);
            alerta.setTitle("Error");
            alerta.setHeaderText("Error al abrir el diálogo de selección de cliente");
            alerta.setContentText("No se pudo cargar la ventana de selección de cliente: " + e.getMessage());
            alerta.showAndWait();
            return null;
        }
    }

    @FXML
    private void cancelarVenta() {
        if (stage != null) {
            stage.close();
        }
    }

    public void actualizarTotalCesta(double total) {
        labelTotalCesta.setText(String.format("%.2f€", total));
    }
}