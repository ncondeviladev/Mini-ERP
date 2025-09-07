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
import com.erp.model.Producto;
import com.erp.utils.Alerta;
import com.erp.utils.FacturaPDFGenerator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VentaFinalizarControllerTest {

    @Mock
    private MainController mainController;
    @Mock
    private ClienteFormularioBuscarController formularioBuscarClienteController;
    @Mock
    private ClienteTablaController clienteTablaController;
    @Mock
    private DescuentoTablaController descuentoTablaController;
    @Mock
    private ClienteDAO clienteDAO;
    @Mock
    private DescuentoDAO descuentoDAO;
    @Mock
    private VentaDAO ventaDAO;
    @Mock
    private Label labelSubtotal;
    @Mock
    private Label labelDescuento;
    @Mock
    private Label labelIva;
    @Mock
    private Label labelTotalFinal;
    @Mock
    private VBox zonaDescuentos;
    @Mock
    private javafx.scene.layout.VBox mockPanelRaiz; // Mock for getPanelRaiz()

    @InjectMocks
    private VentaFinalizarController ventaFinalizarController;

    private ObservableList<DetalleVenta> mockCestaItems;
    private List<Cliente> mockClientesOriginales;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockCestaItems = FXCollections.observableArrayList();
        mockClientesOriginales = Arrays.asList(
                Cliente.crearParticular(1, "a@a.com", "111", "dir1", "11111111A", LocalDate.now(), "Juan", "Perez"),
                Cliente.crearEmpresa(2, "b@b.com", "222", "dir2", "B22222222", LocalDate.now(), "Empresa SL", "Contacto")
        );

        when(clienteDAO.listarClientes()).thenReturn(mockClientesOriginales);
        when(formularioBuscarClienteController.getPanelRaiz()).thenReturn(mockPanelRaiz);
        when(mockPanelRaiz.isVisible()).thenReturn(false); // Default for initial state
    }

    @Test
    void testInitialize() {
        ventaFinalizarController.initialize(null, null);

        verify(clienteDAO).listarClientes();
        verify(clienteTablaController).setItems(mockClientesOriginales);
        verify(clienteTablaController).setAccionesVisible(false);
        verify(formularioBuscarClienteController).setVentaFinalizarController(ventaFinalizarController);
        verify(formularioBuscarClienteController).vincularControlador();
        verify(descuentoTablaController).setOnSelectionChanged(any());

        verify(mockPanelRaiz).setVisible(false);
        verify(mockPanelRaiz).setManaged(false);
        verify(zonaDescuentos).setVisible(false);
        verify(zonaDescuentos).setManaged(false);
    }

    @Test
    void testFiltrarClientes() {
        Map<String, String> criterios = new HashMap<>();
        criterios.put("id", "");
        criterios.put("nombre", "juan");
        criterios.put("cifnif", "");
        when(formularioBuscarClienteController.getCriteriosBusqueda()).thenReturn(criterios);

        ventaFinalizarController.filtrarClientes();

        verify(clienteTablaController).setItems(argThat(list ->
                list.size() == 1 && list.get(0).getNombre().equals("Juan")
        ));
    }

    @Test
    void testSetData() {
        Producto p1 = new Producto(1, "Prod1", "", "", 10.0, 10);
        mockCestaItems.add(new DetalleVenta(null, null, p1, 2, 10.0)); // Subtotal 20.0
        mockCestaItems.add(new DetalleVenta(null, null, p1, 3, 5.0));  // Subtotal 15.0
        // Total subtotal = 35.0

        ventaFinalizarController.setData(mockCestaItems);

        // Verify subtotal calculation and label update
        verify(labelSubtotal).setText("35.00€");
        // Recalcular totales is called, so other labels should be updated too
        verify(labelDescuento).setText("0.00%"); // No discounts selected initially
        verify(labelIva).setText("7.35€"); // 35.0 * 0.21
        verify(labelTotalFinal).setText("42.35€"); // 35.0 + 7.35
    }

    @Test
    void testOnClienteSeleccionado_ConDescuentos() {
        Cliente cliente = mockClientesOriginales.get(0); // Juan Perez
        List<Descuento> mockDescuentos = Arrays.asList(
                new Descuento(1, cliente.getId(), "Desc1", 10.0, LocalDate.now(), LocalDate.now().plusDays(10))
        );
        when(descuentoDAO.listarDescuentosPorCliente(cliente.getId())).thenReturn(mockDescuentos);

        ventaFinalizarController.onClienteSeleccionado(cliente);

        verify(descuentoTablaController).setDescuentos(mockDescuentos);
        verify(descuentoTablaController.getTablaDescuentos()).setDisable(false);
        verify(zonaDescuentos).setVisible(true);
        verify(zonaDescuentos).setManaged(true);
        verify(labelSubtotal).setText(anyString()); // Recalcular totales is called
    }

    @Test
    void testOnClienteSeleccionado_SinDescuentos() {
        Cliente cliente = mockClientesOriginales.get(0); // Juan Perez
        when(descuentoDAO.listarDescuentosPorCliente(cliente.getId())).thenReturn(new ArrayList<>());

        ventaFinalizarController.onClienteSeleccionado(cliente);

        verify(descuentoTablaController).setDescuentos(argThat(List::isEmpty));
        verify(descuentoTablaController.getTablaDescuentos()).setDisable(true);
        verify(zonaDescuentos).setVisible(false);
        verify(zonaDescuentos).setManaged(false);
        verify(labelSubtotal).setText(anyString()); // Recalcular totales is called
    }

    @Test
    void testRecalcularTotales() {
        // Setup initial subtotal
        Producto p1 = new Producto(1, "Prod1", "", "", 10.0, 10);
        mockCestaItems.add(new DetalleVenta(null, null, p1, 10, 10.0)); // Subtotal 100.0
        ventaFinalizarController.setData(mockCestaItems); // This calls recalcularTotales once

        // Simulate selected discounts
        List<Descuento> selectedDescuentos = Arrays.asList(
                new Descuento(1, 1, "Desc1", 10.0, LocalDate.now(), LocalDate.now().plusDays(10)),
                new Descuento(2, 1, "Desc2", 5.0, LocalDate.now(), LocalDate.now().plusDays(10))
        ); // Total 15% discount
        when(descuentoTablaController.getDescuentosSeleccionados()).thenReturn(selectedDescuentos);

        // Call recalcularTotales again
        ventaFinalizarController.recalcularTotales();

        // Expected values:
        // Subtotal: 100.00€
        // Descuento: 15.00% (15.00€)
        // Base Imponible: 85.00€ (100 - 15)
        // IVA: 17.85€ (85 * 0.21)
        // Total Final: 102.85€ (85 + 17.85)

        verify(labelSubtotal, times(2)).setText("100.00€"); // Called twice (setData and recalcularTotales)
        verify(labelDescuento).setText("15.00%");
        verify(labelIva).setText("17.85€");
        verify(labelTotalFinal).setText("102.85€");
    }

    @Test
    void testFinalizarVenta_NoClienteSeleccionado() {
        when(clienteTablaController.getClienteSeleccionado()).thenReturn(null);

        try (MockedStatic<Alerta> mockedAlerta = mockStatic(Alerta.class)) {
            ventaFinalizarController.finalizarVenta();
            mockedAlerta.verify(() -> Alerta.mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", null, "Debe seleccionar un cliente."));
            verify(ventaDAO, never()).guardarVenta(any());
        }
    }

    @Test
    void testFinalizarVenta_Exito() throws Exception {
        Cliente cliente = mockClientesOriginales.get(0);
        when(clienteTablaController.getClienteSeleccionado()).thenReturn(cliente);
        when(descuentoTablaController.getDescuentosSeleccionados()).thenReturn(new ArrayList<>());
        when(labelTotalFinal.getText()).thenReturn("100.00€"); // Simulate UI value

        // Mock FacturaPDFGenerator and Desktop
        try (MockedStatic<FacturaPDFGenerator> mockedPdfGenerator = mockStatic(FacturaPDFGenerator.class);
             MockedStatic<Desktop> mockedDesktop = mockStatic(Desktop.class)) {

            Desktop mockDesktopInstance = mock(Desktop.class);
            when(Desktop.getDesktop()).thenReturn(mockDesktopInstance);
            when(FacturaPDFGenerator.getInvoiceFilePath()).thenReturn("facturas/test_invoice.pdf");

            ventaFinalizarController.setData(mockCestaItems); // Set some data for total calculation
            ventaFinalizarController.finalizarVenta();

            verify(ventaDAO).guardarVenta(any());
            mockedPdfGenerator.verify(() -> FacturaPDFGenerator.generateInvoicePDF(any(com.erp.model.Venta.class), any(String.class)));
            mockedDesktop.verify(() -> Desktop.getDesktop());
            verify(mockDesktopInstance).open(any(File.class));
            verify(mainController).mostrarVentas();
        }
    }

    @Test
    void testCancelar() {
        ventaFinalizarController.setMainController(mainController);
        ventaFinalizarController.cancelar();
        verify(mainController).mostrarCesta();
    }

    @Test
    void testMostrarVistaBuscar() {
        when(mockPanelRaiz.isVisible()).thenReturn(false);
        ventaFinalizarController.mostrarVistaBuscar();
        verify(mockPanelRaiz).setVisible(true);
        verify(mockPanelRaiz).setManaged(true);

        when(mockPanelRaiz.isVisible()).thenReturn(true);
        ventaFinalizarController.mostrarVistaBuscar();
        verify(mockPanelRaiz).setVisible(false);
        verify(mockPanelRaiz).setManaged(false);
    }
}
