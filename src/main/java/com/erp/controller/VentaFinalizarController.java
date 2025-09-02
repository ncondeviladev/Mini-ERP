package com.erp.controller;

import com.erp.controller.components.cliComp.ClienteFormularioBuscarController;
import com.erp.controller.components.cliComp.ClienteTablaController;
import com.erp.dao.ClienteDAO;
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
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import com.erp.utils.Alerta;

// import com.erp.utils.FacturaPDFGenerator;

public class VentaFinalizarController implements Initializable {

    private MainController mainController;
    private ObservableList<DetalleVenta> cestaItems;
    private DescuentoDAO descuentoDAO;
    private VentaDAO ventaDAO;
    private double subtotal = 0.0;

    @FXML
    private StackPane zonaFormulariosCliente;
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
    @FXML
    private Button botonBuscarCliente;

    private ClienteDAO clienteDAO;
    private List<Cliente> clientesOriginales = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.descuentoDAO = new DescuentoDAO();
        this.ventaDAO = new VentaDAO();
        this.clienteDAO = new ClienteDAO();

        // Cargar todos los clientes y mostrarlos inicialmente
        clientesOriginales = this.clienteDAO.listarClientes();
        clienteTablaController.setItems(clientesOriginales);
        clienteTablaController.setAccionesVisible(false);

        // Vincular el controlador de búsqueda para que los listeners funcionen
        formularioBuscarClienteController.setVentaFinalizarController(this);
        formularioBuscarClienteController.vincularControlador();

        listaDescuentos.setCellFactory(CheckBoxListCell.forListView(Descuento::seleccionadoProperty));

        clienteTablaController.getTablaCliente().getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> onClienteSeleccionado(newValue)
        );

        listaDescuentos.setDisable(true);

        // Ocultar el formulario de búsqueda al inicio
        formularioBuscarClienteController.getPanelRaiz().setVisible(false);
        formularioBuscarClienteController.getPanelRaiz().setManaged(false);
    }

    /**
     * Filtra la lista de clientes mostrada en la tabla.
     * Este método es invocado por el {@link ClienteFormularioBuscarController}.
     */
    public void filtrarClientes() {
        Map<String, String> criterios = formularioBuscarClienteController.getCriteriosBusqueda();
        String filtroId = criterios.get("id").toLowerCase();
        String filtroNombre = criterios.get("nombre").toLowerCase();
        String filtroCifNif = criterios.get("cifnif").toLowerCase();

        List<Cliente> filtrados = clientesOriginales.stream()
            .filter(c -> filtroId.isEmpty() || String.valueOf(c.getId()).contains(filtroId))
            .filter(c -> filtroCifNif.isEmpty() || (c.getCifnif() != null && c.getCifnif().toLowerCase().contains(filtroCifNif)))
            .filter(c -> {
                if (filtroNombre.isEmpty()) return true;
                if ("Particular".equals(c.getTipoCliente())) {
                    return (c.getNombre() + " " + c.getApellidos()).toLowerCase().contains(filtroNombre);
                } else { // Empresa
                    return c.getRazonSocial().toLowerCase().contains(filtroNombre);
                }
            })
            .collect(Collectors.toList());

        clienteTablaController.setItems(filtrados);
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
        System.out.println("onClienteSeleccionado llamado. Cliente: " + (cliente != null ? cliente.getNombre() : "null"));
        listaDescuentos.getItems().clear();
        if (cliente != null) {
            ObservableList<Descuento> descuentos = FXCollections.observableArrayList(descuentoDAO.listarDescuentosPorCliente(cliente.getId()));
            System.out.println("Descuentos encontrados para el cliente: " + descuentos.size());
            descuentos.forEach(d -> d.seleccionadoProperty().addListener((obs, oldVal, newVal) -> recalcularTotales()));
            listaDescuentos.setItems(descuentos);
            listaDescuentos.setDisable(descuentos.isEmpty());
            System.out.println("Lista de descuentos deshabilitada: " + listaDescuentos.isDisabled());
        } else {
            listaDescuentos.setDisable(true);
            System.out.println("Cliente nulo, lista de descuentos deshabilitada.");
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
            Alerta.mostrarAlertaTemporal(Alert.AlertType.WARNING, "Advertencia", null, "Debe seleccionar un cliente.");
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
            // FacturaPDFGenerator.generateInvoicePDF(nuevaVenta);
            Alerta.mostrarAlertaTemporal(Alert.AlertType.INFORMATION, "Éxito", "Venta guardada correctamente.", "Se ha generado la factura en: " /*+ FacturaPDFGenerator.getInvoiceFilePath()*/);

            // Abrir el PDF generado
            /*
            if (Desktop.isDesktopSupported()) {
                new Thread(() -> {
                    try {
                        File myFile = new File(FacturaPDFGenerator.getInvoiceFilePath());
                        Desktop.getDesktop().open(myFile);
                    } catch (IOException ex) {
                        System.err.println("Error al abrir el PDF: " + ex.getMessage());
                    }
                }).start();
            }
            */

            mainController.mostrarVentas();
        } catch (Exception e) {
            e.printStackTrace();
            Alerta.mostrarAlertaTemporal(Alert.AlertType.ERROR, "Error", "Error al guardar la venta", e.getMessage());
        }
    }

    @FXML
    private void cancelar() {
        if (mainController != null) {
            mainController.mostrarCesta();
        }
    }

    @FXML
    private void mostrarVistaBuscar() {
        boolean isVisible = formularioBuscarClienteController.getPanelRaiz().isVisible();
        formularioBuscarClienteController.getPanelRaiz().setVisible(!isVisible);
        formularioBuscarClienteController.getPanelRaiz().setManaged(!isVisible);
    }
}