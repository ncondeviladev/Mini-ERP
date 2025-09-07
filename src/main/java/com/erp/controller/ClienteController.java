package com.erp.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.erp.controller.components.cliComp.ClienteFormularioAnadirController;
import com.erp.controller.components.cliComp.ClienteFormularioBuscarController;
import com.erp.controller.components.cliComp.ClienteTablaController;
import com.erp.dao.ClienteDAO;
import com.erp.model.Cliente;
import com.erp.utils.Alerta;
import com.erp.utils.AnimationUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Controlador principal para la gestión de Clientes (cliente.fxml).
 * <p>
 * Actúa como un orquestador que coordina los componentes de la interfaz
 * (formularios y tabla) y contiene la lógica de negocio principal para
 * interactuar con la base de datos.
 */
public class ClienteController {

    private MainController mainController;

    // --- DAO ---
    private final ClienteDAO clienteDAO = new ClienteDAO();

    // --- Componentes FXML de la vista principal (cliente.fxml) ---
    @FXML
    private StackPane zonaFormulariosCliente;
    @FXML
    private Label inicioCliente;
    @FXML
    private Button botonAñadirCliente;
    @FXML
    private Button botonBuscarCliente;

    // --- Contenedores de los componentes FXML incluidos ---
    @FXML
    private VBox formAnadir; // Contenedor del FXML 'cliente-formulario-anadir.fxml'
    @FXML
    private VBox formBuscar; // Contenedor del FXML 'cliente-formulario-buscar.fxml'

    // --- Inyección de los CONTROLADORES de los componentes ---
    @FXML
    private ClienteFormularioAnadirController formAnadirController;
    @FXML
    private ClienteFormularioBuscarController formBuscarController;
    @FXML
    private ClienteTablaController tablaClientesController;

    // --- Estado ---
    private List<Cliente> clientesOriginales = new ArrayList<>();

    /**
     * Inyecta el MainController para permitir la navegación a otras vistas.
     * @param mainController El controlador principal de la aplicación.
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * Inicializa el controlador. Conecta los sub-controladores, carga los datos
     * iniciales y configura el estado inicial de la vista.
     */
    @FXML
    public void initialize() {
        // 1. Conectar este controlador principal con sus hijos
        formAnadirController.setClienteController(this);
        formBuscarController.setClienteController(this);
        tablaClientesController.setClienteController(this);
        formBuscarController.vincularControlador();

        // 2. Cargar los datos iniciales en la tabla
        cargarYMostrarClientes();

        // 3. Configurar estado inicial de la UI
        ocultarTodosLosFormularios();

        // 4. Aplicar animaciones a los botones principales
        AnimationUtils.addHoverAnimation(botonAñadirCliente);
        AnimationUtils.addHoverAnimation(botonBuscarCliente);
    }

    /**
     * Muestra el formulario para añadir un nuevo cliente.
     * Delega en el controlador del formulario la tarea de prepararse.
     */
    @FXML
    public void mostrarVistaAñadir() {
        ocultarTodosLosFormularios();
        formAnadirController.prepararParaNuevoCliente();
        formAnadir.setVisible(true);
        formAnadir.setManaged(true);
        inicioCliente.setVisible(false);
        inicioCliente.setManaged(false);
    }

    /**
     * Muestra el formulario de búsqueda de clientes.
     */
    @FXML
    public void mostrarVistaBuscar() {
        ocultarTodosLosFormularios();
        formBuscar.setVisible(true);
        formBuscar.setManaged(true);
        inicioCliente.setVisible(false);
        inicioCliente.setManaged(false);
    }

    /**
     * Guarda o actualiza un cliente. Este método es invocado por el
     * {@link ClienteFormularioAnadirController} después de validar y construir el objeto Cliente.
     * @param cliente El cliente a guardar o actualizar.
     */
    public void guardarOActualizarCliente(Cliente cliente) {
        boolean exito;
        if (cliente.getId() == null || cliente.getId() == 0) { // Es un cliente nuevo
            exito = clienteDAO.guardarClienteDb(cliente);
        } else { // Es una actualización
            exito = clienteDAO.actualizarClienteEnDb(cliente);
        }

        if (exito) {
            Alerta.mostrarAlertaTemporal(AlertType.INFORMATION, "Éxito", null, "Cliente guardado correctamente.");
            cargarYMostrarClientes(); // Recargar y mostrar la lista actualizada
            ocultarTodosLosFormularios();
        } else {
            Alerta.mostrarError("Error de Base de Datos", "No se pudo guardar el cliente.");
        }
    }

    /**
     * Prepara el formulario para modificar el cliente seleccionado.
     * Este método es invocado por el {@link ClienteTablaController}.
     */
    public void modificarClienteSeleccionado() {
        Cliente seleccionado = tablaClientesController.getClienteSeleccionado();
        if (seleccionado != null) {
            ocultarTodosLosFormularios();
            formAnadirController.cargarDatosCliente(seleccionado);
            formAnadir.setVisible(true);
            formAnadir.setManaged(true);
            inicioCliente.setVisible(false);
        } else {
            Alerta.mostrarAdvertencia("Acción no disponible", "Debes seleccionar un cliente para modificar.");
        }
    }

    /**
     * Elimina el cliente seleccionado.
     * Este método es invocado por el {@link ClienteTablaController}.
     */
    public void eliminarClienteSeleccionado() {
        Cliente seleccionado = tablaClientesController.getClienteSeleccionado();
        if (seleccionado == null) {
            Alerta.mostrarAdvertencia("Acción no disponible", "Por favor, selecciona un cliente para eliminar.");
            return;
        }

        String nombre = "Particular".equals(seleccionado.getTipoCliente())
                ? seleccionado.getNombre() + " " + seleccionado.getApellidos()
                : seleccionado.getRazonSocial();

        boolean confirmado = Alerta.mostrarConfirmacion(
                "Confirmar Eliminación",
                "¿Estás seguro de que quieres eliminar a este cliente?",
                nombre);

        if (confirmado) {
            if (clienteDAO.eliminarClientePorId(seleccionado.getId())) {
                Alerta.mostrarAlertaTemporal(AlertType.INFORMATION, "Éxito", null, "Cliente eliminado correctamente.");
                cargarYMostrarClientes();
            } else {
                Alerta.mostrarError("Error de Base de Datos", "No se pudo eliminar el cliente.");
            }
        }
    }

    /**
     * Filtra la lista de clientes.
     * Este método es invocado por el {@link ClienteFormularioBuscarController}.
     */
    public void filtrarClientes() {
        Map<String, String> criterios = formBuscarController.getCriteriosBusqueda();
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

        tablaClientesController.setItems(filtrados);
    }

    /**
     * Actualiza el estado de los botones de la tabla (Modificar/Eliminar/Descuentos).
     * Este método es invocado por el {@link ClienteTablaController}.
     * @param clienteSeleccionado El cliente que ha sido seleccionado, o null.
     */
    public void actualizarEstadoBotones(Cliente clienteSeleccionado) {
        boolean haySeleccion = clienteSeleccionado != null;
        tablaClientesController.setDisableBotones(!haySeleccion);
    }

    /**
     * Navega a la vista de descuentos para el cliente seleccionado.
     * Este método es invocado por el {@link ClienteTablaController}.
     */
    public void verDescuentos() {
        Cliente clienteSeleccionado = tablaClientesController.getClienteSeleccionado();
        if (clienteSeleccionado != null) {
            if (mainController != null) {
                mainController.mostrarDescuentos(clienteSeleccionado);
            } else {
                Alerta.mostrarError("Error de Navegación", "No se pudo mostrar la vista de descuentos. MainController no disponible.");
            }
        } else {
             Alerta.mostrarAdvertencia("Acción no disponible", "Debes seleccionar un cliente para ver sus descuentos.");
        }
    }

    // --- Métodos privados de utilidad ---

    private void cargarYMostrarClientes() {
        clientesOriginales = clienteDAO.listarClientes();
        tablaClientesController.setItems(clientesOriginales);
    }

    private void ocultarTodosLosFormularios() {
        formAnadir.setVisible(false);
        formAnadir.setManaged(false);
        formBuscar.setVisible(false);
        formBuscar.setManaged(false);
        inicioCliente.setVisible(false); // Ocultar también el label de inicio
        inicioCliente.setManaged(false);
    }
}

