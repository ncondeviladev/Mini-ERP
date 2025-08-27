package com.erp.controller;

import java.time.LocalDate;
import java.util.List;

import com.erp.dao.DescuentoDAO;
import com.erp.model.Cliente;
import com.erp.model.Descuento;
import com.erp.utils.Alerta;
import com.erp.utils.AnimationUtils;
import com.erp.controller.components.descComp.DescuentoTablaController; // Importa el nuevo controlador del componente de tabla de descuentos

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

/**
 * Controlador para la vista de gestión de descuentos (descuento.fxml).
 * <p>
 * Se encarga de mostrar, añadir, editar y eliminar los descuentos asociados a un
 * cliente específico.
 */
public class DescuentoController {

    @FXML
    private VBox formularioContenedor;
    @FXML
    private TextField descripcionField;
    @FXML
    private TextField porcentajeField;
    @FXML
    private TextField duracionField;
    @FXML
    private Button botonGuardarDescuento;
    @FXML
    private Button botonCancelarDescuento;

    // New injection for the component controller
    @FXML
    private DescuentoTablaController tablaDescuentosComponenteController;

    @FXML
    private Button botonAgregarDescuento;
    @FXML
    private Button botonEditarDescuento;
    @FXML
    private Button botonEliminarDescuento;

    private MainController mainController;
    private Cliente clienteSeleccionado;
    private final DescuentoDAO descuentoDAO = new DescuentoDAO();
    // The ObservableList is still useful for managing data before passing to the component
    private final ObservableList<Descuento> listaDescuentos = FXCollections.observableArrayList();
    private boolean modoEdicion = false;
    private Descuento descuentoAEditar = null;

    /**
     * Método de inicialización llamado por JavaFX después de cargar el FXML.
     * Configura la tabla, los listeners y el estado inicial de la UI.
     */
    @FXML
    public void initialize() {
        // The table column configuration is now handled by DescuentoTablaController
        // The table items are set via the component controller

        // Listener for table selection
        tablaDescuentosComponenteController.getTablaDescuentos().getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean haySeleccion = newSelection != null;
            botonEditarDescuento.setDisable(!haySeleccion);
            botonEliminarDescuento.setDisable(!haySeleccion);
        });

        // Oculta el formulario al inicio.
        formularioContenedor.setVisible(false);
        formularioContenedor.setManaged(false);

        // Activar guardado con Enter en los campos del formulario
        configurarGuardadoConEnter(descripcionField);
        configurarGuardadoConEnter(porcentajeField);
        configurarGuardadoConEnter(duracionField);

        // --- Aplicar animaciones a los botones ---
        AnimationUtils.addHoverAnimation(botonGuardarDescuento);
        AnimationUtils.addHoverAnimation(botonCancelarDescuento);
        AnimationUtils.addHoverAnimation(botonAgregarDescuento);
        AnimationUtils.addHoverAnimation(botonEditarDescuento);
        AnimationUtils.addHoverAnimation(botonEliminarDescuento);
    }

    /**
     * Configura un campo de texto para que, al presionar la tecla ENTER,
     * se intente guardar el descuento.
     * @param textField El campo de texto al que se le añadirá el listener.
     */
    private void configurarGuardadoConEnter(TextField textField) {
        textField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                guardarDescuento();
            }
        });
    }

    /**
     * Inyecta el controlador principal para permitir la comunicación entre vistas.
     * @param mainController La instancia del controlador principal.
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        // Pass mainController to the child component controller
        if (tablaDescuentosComponenteController != null) {
            tablaDescuentosComponenteController.setMainController(mainController);
        }
    }

    /**
     * Establece el cliente para el cual se gestionarán los descuentos y carga sus datos.
     * Este método es el punto de entrada a esta vista.
     * @param cliente El cliente seleccionado en la vista anterior.
     */
    public void setClienteSeleccionado(Cliente cliente) {
        this.clienteSeleccionado = cliente;
        cargarDatosDescuentos();
    }

    /**
     * Carga los descuentos del cliente seleccionado desde la base de datos
     * y los muestra en la tabla.
     */
    private void cargarDatosDescuentos() {
        if (clienteSeleccionado != null) {
            List<Descuento> descuentosDesdeDB = descuentoDAO.listarDescuentosPorCliente(clienteSeleccionado.getId());
            tablaDescuentosComponenteController.setDescuentos(descuentosDesdeDB);
        } else {
            tablaDescuentosComponenteController.setDescuentos(new java.util.ArrayList<>());
        }
    }

    /**
     * Prepara y muestra el formulario para añadir un nuevo descuento.
     */
    @FXML
    private void agregarDescuento() {
        if (clienteSeleccionado == null) {
            Alerta.mostrarAdvertencia("Sin Cliente", "No se ha seleccionado un cliente para añadirle un descuento.");
            return;
        }
        modoEdicion = false;
        descuentoAEditar = null;
        limpiarFormulario();
        botonGuardarDescuento.setText("Guardar");
        mostrarFormulario();
    }

    /**
     * Prepara y muestra el formulario para editar el descuento seleccionado en la tabla.
     */
    @FXML
    private void editarDescuento() {
        descuentoAEditar = tablaDescuentosComponenteController.getSelectedDescuento();
        if (descuentoAEditar == null) {
            Alerta.mostrarAdvertencia("Sin Selección", "Debes seleccionar un descuento para editar.");
            return;
        }
        modoEdicion = true;
        poblarFormulario(descuentoAEditar);
        botonGuardarDescuento.setText("Guardar Cambios");
        mostrarFormulario();
    }

    /**
     * Elimina el descuento seleccionado de la tabla y de la base de datos,
     * pidiendo confirmación al usuario previamente.
     */
    @FXML
    private void eliminarDescuento() {
        Descuento descuentoAEliminar = tablaDescuentosComponenteController.getSelectedDescuento();
        if (descuentoAEliminar == null) {
            Alerta.mostrarAdvertencia("Sin Selección", "Por favor, selecciona un descuento para eliminar.");
            return;
        }

        boolean confirmado = Alerta.mostrarConfirmacion(
                "Confirmar Eliminación",
                "¿Seguro que quieres eliminar el descuento?",
                descuentoAEliminar.getDescripcion());

        if (confirmado) {
            if (descuentoDAO.eliminarDescuentoDb(descuentoAEliminar.getId())) {
                cargarDatosDescuentos();
            } else {
                Alerta.mostrarError("Error", "No se pudo eliminar el descuento de la base de datos.");
            }
        }
    }

    @FXML
    private void guardarDescuento() {
        if (!esFormularioValido()) {
            return;
        }

        if (clienteSeleccionado == null) {
            Alerta.mostrarError("Error Crítico", "No hay un cliente seleccionado para asociar el descuento.");
            return;
        }

        try {
            String descripcion = descripcionField.getText();
            double porcentaje = Double.parseDouble(porcentajeField.getText());
            int duracionMeses = Integer.parseInt(duracionField.getText());

            if (modoEdicion) {
                descuentoAEditar.setDescripcion(descripcion);
                descuentoAEditar.setPorcentaje(porcentaje);
                descuentoAEditar.setFechaFin(descuentoAEditar.getFechaInicio().plusMonths(duracionMeses));
                descuentoAEditar.actualizarActivo();

                if (descuentoDAO.actualizarDescuentoDb(descuentoAEditar)) {
                    tablaDescuentosComponenteController.getTablaDescuentos().refresh();
                } else {
                    Alerta.mostrarError("Error de Base de Datos", "No se pudo actualizar el descuento.");
                }
            } else {
                LocalDate fechaInicio = LocalDate.now();
                LocalDate fechaFin = fechaInicio.plusMonths(duracionMeses);
                Descuento nuevoDescuento = new Descuento(clienteSeleccionado.getId(), descripcion, porcentaje, fechaInicio, fechaFin);

                if (descuentoDAO.guardarDescuentoDb(nuevoDescuento)) {
                    tablaDescuentosComponenteController.getTablaDescuentos().getItems().add(nuevoDescuento);
                } else {
                    Alerta.mostrarError("Error de Base de Datos", "No se pudo guardar el descuento.");
                }
            }
            ocultarFormulario();

        } catch (NumberFormatException e) {
            Alerta.mostrarError("Error de Formato", "El porcentaje y la duración deben ser números válidos.");
        } catch (Exception e) {
            Alerta.mostrarError("Error Inesperado", "Ocurrió un error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean esFormularioValido() {
        if (descripcionField.getText().isEmpty() || porcentajeField.getText().isEmpty() || duracionField.getText().isEmpty()) {
            Alerta.mostrarError("Error de Validación", "Todos los campos son obligatorios.");
            return false;
        }
        try {
            Double.parseDouble(porcentajeField.getText());
            Integer.parseInt(duracionField.getText());
        } catch (NumberFormatException e) {
            Alerta.mostrarError("Error de Validación", "Porcentaje y duración deben ser números.");
            return false;
        }
        return true;
    }

    private void mostrarFormulario() {
        formularioContenedor.setVisible(true);
        formularioContenedor.setManaged(true);
    }

    @FXML
    private void ocultarFormulario() {
        formularioContenedor.setVisible(false);
        formularioContenedor.setManaged(false);
    }

    private void limpiarFormulario() {
        descripcionField.clear();
        porcentajeField.clear();
        duracionField.clear();
    }

    private void poblarFormulario(Descuento descuento) {
        descripcionField.setText(descuento.getDescripcion());
        porcentajeField.setText(String.valueOf(descuento.getPorcentaje()));
        duracionField.clear();
        duracionField.setPromptText("Introduce nueva duración en meses");
    }

}

