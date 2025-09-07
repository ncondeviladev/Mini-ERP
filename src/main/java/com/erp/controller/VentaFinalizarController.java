package com.erp.controller;

import com.erp.controller.components.cliComp.ClienteFormularioBuscarController;
import com.erp.controller.components.cliComp.ClienteTablaController;
import com.erp.controller.components.descComp.DescuentoTablaController;
import com.erp.dao.ClienteDAO;
import com.erp.dao.DescuentoDAO;
import com.erp.dao.VentaDAO;
import com.erp.model.Cliente;
import com.erp.model.Descuento;
import com.erp.model.DetalleVenta;
import com.erp.model.Venta;
import com.erp.utils.Alerta;
import com.erp.utils.FacturaPDFGenerator;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Controlador para la vista de finalización de venta (VentaFinalizar.fxml).
 * Permite seleccionar un cliente, aplicar descuentos y finalizar la venta.
 */
public class VentaFinalizarController implements Initializable {

    private static final double TASA_IVA = 0.21; // 21% de IVA

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
    private DescuentoTablaController descuentoTablaController;
    @FXML
    private Label labelSubtotal;
    @FXML
    private Label labelDescuento;
    @FXML
    private Label labelIva;
    @FXML
    private Label labelTotalFinal;
    @FXML
    private VBox zonaDescuentos;

    private ClienteDAO clienteDAO;
    private List<Cliente> clientesOriginales = new ArrayList<>();

    /**
     * Inicializa el controlador.
     * @param url La ubicación utilizada para resolver rutas relativas para el objeto raíz, o null si la ubicación no es conocida.
     * @param rb Los recursos utilizados para localizar el objeto raíz, o null si el objeto raíz no fue localizado.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.descuentoDAO = new DescuentoDAO();
        this.ventaDAO = new VentaDAO();
        this.clienteDAO = new ClienteDAO();

        // Cargar todos los clientes y mostrarlos inicialmente
        clientesOriginales = this.clienteDAO.listarClientes();
        clienteTablaController.setItems(clientesOriginales);
        clienteTablaController.setAccionesVisible(false);

        // Vincular controladores de componentes
        formularioBuscarClienteController.setVentaFinalizarController(this);
        formularioBuscarClienteController.vincularControlador();
        descuentoTablaController.setOnSelectionChanged(this::recalcularTotales);

        // Listener para seleccion de cliente
        clienteTablaController.getTablaCliente().getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> onClienteSeleccionado(newValue)
        );

        // Ocultar vistas secundarias al inicio
        formularioBuscarClienteController.getPanelRaiz().setVisible(false);
        formularioBuscarClienteController.getPanelRaiz().setManaged(false);
        zonaDescuentos.setVisible(false);
        zonaDescuentos.setManaged(false);
    }

    /**
     * Filtra la lista de clientes según los criterios de búsqueda.
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

    /**
     * Establece el controlador principal.
     * @param mainController El controlador principal.
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * Establece los datos de la cesta y calcula el subtotal.
     * @param cestaItems La lista de detalles de venta.
     */
    public void setData(ObservableList<DetalleVenta> cestaItems) {
        this.cestaItems = cestaItems;
        this.subtotal = cestaItems.stream()
                .mapToDouble(DetalleVenta::getSubTotal)
                .sum();
        recalcularTotales();
    }

    /**
     * Se ejecuta cuando se selecciona un cliente en la tabla.
     * @param cliente El cliente seleccionado.
     */
    public void onClienteSeleccionado(Cliente cliente) {
        if (cliente != null && cliente.getId() != 0) {
            List<Descuento> descuentos = descuentoDAO.listarDescuentosPorCliente(cliente.getId());
            descuentoTablaController.setDescuentos(descuentos);
            
            boolean hayDescuentos = !descuentos.isEmpty();
            descuentoTablaController.getTablaDescuentos().setDisable(!hayDescuentos);
            zonaDescuentos.setVisible(true);
            zonaDescuentos.setManaged(true);
        } else {
            descuentoTablaController.setDescuentos(new ArrayList<>());
            descuentoTablaController.getTablaDescuentos().setDisable(true);
            zonaDescuentos.setVisible(false);
            zonaDescuentos.setManaged(false);
        }
        recalcularTotales();
    }

    /**
     * Recalcula los totales de la venta (subtotal, descuento, IVA y total final).
     */
    public void recalcularTotales() {
        double porcentajeDescuentoTotal = descuentoTablaController.getDescuentosSeleccionados().stream()
                .mapToDouble(Descuento::getPorcentaje)
                .sum();

        double subtotalConDescuento = subtotal * (1 - (porcentajeDescuentoTotal / 100.0));
        double iva = subtotalConDescuento * TASA_IVA;
        double totalFinal = subtotalConDescuento + iva;

        labelSubtotal.setText(String.format("%.2f€", subtotal));
        labelDescuento.setText(String.format("%.2f%%", porcentajeDescuentoTotal));
        labelIva.setText(String.format("%.2f€", iva));
        labelTotalFinal.setText(String.format("%.2f€", totalFinal));
    }

    /**
     * Finaliza la venta, guarda los datos en la base de datos y genera la factura en PDF.
     */
    @FXML
    public void finalizarVenta() {
        Cliente clienteSeleccionado = clienteTablaController.getClienteSeleccionado();
        if (clienteSeleccionado == null) {
            Alerta.mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", null, "Debe seleccionar un cliente.");
            return;
        }

        List<Descuento> descuentosSeleccionados = descuentoTablaController.getDescuentosSeleccionados();
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
            Alerta.mostrarAlertaTemporal(Alert.AlertType.INFORMATION, "Éxito", "Venta guardada correctamente.", null);
            
            // Generar y mostrar factura
            FacturaPDFGenerator.generateInvoicePDF(nuevaVenta, FacturaPDFGenerator.getInvoiceFilePath());
            File pdfFile = new File(FacturaPDFGenerator.getInvoiceFilePath());
            if (pdfFile.exists()) {
                Desktop.getDesktop().open(pdfFile);
            }

            mainController.mostrarVentas();
        } catch (Exception e) {
            e.printStackTrace();
            Alerta.mostrarError("Error al guardar o generar factura", e.getMessage());
        }
    }

    /**
     * Cancela la finalización de la venta y vuelve a la vista de la cesta.
     */
    @FXML
    public void cancelar() {
        if (mainController != null) {
            mainController.mostrarCesta();
        }
    }

    /**
     * Muestra u oculta el formulario de búsqueda de clientes.
     */
    @FXML
    public void mostrarVistaBuscar() {
        boolean isVisible = formularioBuscarClienteController.getPanelRaiz().isVisible();
        formularioBuscarClienteController.getPanelRaiz().setVisible(!isVisible);
        formularioBuscarClienteController.getPanelRaiz().setManaged(!isVisible);
    }
}
