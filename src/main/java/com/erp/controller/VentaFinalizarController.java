package com.erp.controller;

import com.erp.controller.components.cliComp.ClienteFormularioBuscarController;
import com.erp.controller.components.cliComp.ClienteTablaController;
import com.erp.dao.DescuentoDAO;
import com.erp.dao.VentaDAO;
import com.erp.model.Cliente;
import com.erp.model.Descuento;
import com.erp.model.DetalleVenta;
import com.erp.model.Venta;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class VentaFinalizarController implements Initializable {

    private MainController mainController;
    private ObservableList<DetalleVenta> cestaItems;
    private DescuentoDAO descuentoDAO;
    private VentaDAO ventaDAO;
    private double subtotal = 0.0;

    @FXML
    private ClienteFormularioBuscarController formularioBuscarClienteController;
    @FXML
    private ClienteTablaController clienteTablaController;
    @FXML
    private ListView<Descuento> listaDescuentos;
    @FXML
    private Label labelSubtotal;
    @FXML
    private Label labelDescuento;
    @FXML
    private Label labelTotalFinal;
    @FXML
    private Button botonCancelar;
    @FXML
    private Button botonFinalizarVenta;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.descuentoDAO = new DescuentoDAO();
        this.ventaDAO = new VentaDAO();

        listaDescuentos.setCellFactory(CheckBoxListCell.forListView(Descuento::seleccionadoProperty));

        clienteTablaController.getTablaCliente().getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> onClienteSeleccionado(newValue)
        );

        listaDescuentos.setDisable(true);
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setData(ObservableList<DetalleVenta> cestaItems) {
        this.cestaItems = cestaItems;
        this.subtotal = cestaItems.stream()
                .mapToDouble(DetalleVenta::getSubTotal)
                .sum();
        recalcularTotales();
    }

    private void onClienteSeleccionado(Cliente cliente) {
        listaDescuentos.getItems().clear();
        if (cliente != null) {
            ObservableList<Descuento> descuentos = FXCollections.observableArrayList(descuentoDAO.listarDescuentosPorCliente(cliente.getId()));
            descuentos.forEach(d -> d.seleccionadoProperty().addListener((obs, oldVal, newVal) -> recalcularTotales()));
            listaDescuentos.setItems(descuentos);
            listaDescuentos.setDisable(descuentos.isEmpty());
        } else {
            listaDescuentos.setDisable(true);
        }
        recalcularTotales();
    }

    private void recalcularTotales() {
        double porcentajeDescuentoTotal = listaDescuentos.getItems().stream()
                .filter(Descuento::isSeleccionado)
                .mapToDouble(Descuento::getPorcentaje)
                .sum();

        double totalFinal = subtotal * (1 - (porcentajeDescuentoTotal / 100.0));

        labelSubtotal.setText(String.format("%.2f€", subtotal));
        labelDescuento.setText(String.format("%.2f%%", porcentajeDescuentoTotal));
        labelTotalFinal.setText(String.format("%.2f€", totalFinal));
    }

    @FXML
    private void finalizarVenta() {
        Cliente clienteSeleccionado = clienteTablaController.getClienteSeleccionado();
        if (clienteSeleccionado == null) {
            new Alert(Alert.AlertType.WARNING, "Debe seleccionar un cliente.").showAndWait();
            return;
        }

        List<Descuento> descuentosSeleccionados = listaDescuentos.getItems().stream()
                .filter(Descuento::isSeleccionado)
                .collect(Collectors.toList());

        double totalFinal = Double.parseDouble(labelTotalFinal.getText().replace("€", "").replace(",", "."));

        Venta nuevaVenta = new Venta(
                null,
                clienteSeleccionado,
                descuentosSeleccionados,
                new ArrayList<>(cestaItems),
                LocalDate.now(),
                totalFinal
        );

        try {
            ventaDAO.guardarVenta(nuevaVenta);
            new Alert(Alert.AlertType.INFORMATION, "Venta guardada correctamente.").showAndWait();
            mainController.mostrarVentas();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error al guardar la venta: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void cancelar() {
        if (mainController != null) {
            mainController.mostrarCesta(this.cestaItems);
        }
    }
}