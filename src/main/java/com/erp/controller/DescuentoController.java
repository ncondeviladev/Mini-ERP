package com.erp.controller;

import com.erp.dao.DescuentoDAO;
import com.erp.model.Descuento;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controlador para la vista de gestión de descuentos.
 * Maneja la lógica para añadir, modificar y eliminar descuentos.
 */
public class DescuentoController {

    // --- Componentes FXML ---
    @FXML
    private VBox formularioContenedor;
    @FXML
    private TextField descripcionField;
    @FXML
    private TextField porcentajeField;
    @FXML
    private TextField duracionField;
    @FXML
    private Button botonGuardar;
    @FXML
    private Button botonCancelar;
    @FXML
    private TableView<Descuento> tablaDescuentos;
    @FXML
    private TableColumn<Descuento, String> columnaDescripcion;
    @FXML
    private TableColumn<Descuento, Double> columnaPorcentaje;
    @FXML
    private TableColumn<Descuento, LocalDate> columnaFechaInicio;
    @FXML
    private TableColumn<Descuento, LocalDate> columnaFechaFin;
    @FXML
    private Button botonAgregarDescuento;
    @FXML
    private Button btnEditarDescuento;
    @FXML
    private Button btnEliminarDescuento;

    // --- Lógica de Negocio ---
    private final DescuentoDAO descuentoDAO = new DescuentoDAO();
    private final ObservableList<Descuento> listaDescuentos = FXCollections.observableArrayList();
    private boolean modoEdicion = false;
    private Descuento descuentoAEditar = null;

    /**
     * Inicializa el controlador.
     * Configura la tabla, carga los datos iniciales y define los listeners.
     */
    @FXML
    public void initialize() {
        configurarColumnasTabla();
        tablaDescuentos.setItems(listaDescuentos);
        cargarDatosDescuentos();

        // Listener para habilitar/deshabilitar botones según la selección en la tabla
        tablaDescuentos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean haySeleccion = newSelection != null;
            btnEditarDescuento.setDisable(!haySeleccion);
            btnEliminarDescuento.setDisable(!haySeleccion);
        });

        // Ocultar el formulario al inicio
        formularioContenedor.setVisible(false);
        formularioContenedor.setManaged(false);
    }

    /**
     * Configura las columnas de la TableView para que muestren las propiedades del modelo Descuento.
     */
    private void configurarColumnasTabla() {
        columnaDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        columnaPorcentaje.setCellValueFactory(new PropertyValueFactory<>("porcentaje"));
        columnaFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        columnaFechaFin.setCellValueFactory(new PropertyValueFactory<>("fechaFin"));
    }

    /**
     * Carga todos los descuentos desde la base de datos y los añade a la lista observable de la tabla.
     */
    private void cargarDatosDescuentos() {
        List<Descuento> descuentosDesdeDB = descuentoDAO.listarDescuentos();
        listaDescuentos.setAll(descuentosDesdeDB);
    }

    /**
     * Prepara y muestra el formulario para añadir un nuevo descuento.
     */
    @FXML
    private void agregarDescuento() {
        modoEdicion = false;
        descuentoAEditar = null;
        limpiarFormulario();
        botonGuardar.setText("Guardar");
        mostrarFormulario();
    }

    /**
     * Prepara el formulario para modificar un descuento existente.
     * Rellena los campos con los datos del descuento seleccionado.
     */
    @FXML
    private void editarDescuento() {
        descuentoAEditar = tablaDescuentos.getSelectionModel().getSelectedItem();
        if (descuentoAEditar == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin Selección", "Debes seleccionar un descuento para editar.");
            return;
        }
        modoEdicion = true;
        poblarFormulario(descuentoAEditar);
        botonGuardar.setText("Guardar Cambios");
        mostrarFormulario();
    }

    /**
     * Elimina el descuento seleccionado tras pedir confirmación.
     */
    @FXML
    private void eliminarDescuento() {
        Descuento descuentoAEliminar = tablaDescuentos.getSelectionModel().getSelectedItem();
        if (descuentoAEliminar == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin Selección", "Por favor, selecciona un descuento para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Seguro que quieres eliminar el descuento?");
        confirmacion.setContentText(descuentoAEliminar.getDescripcion());

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            if (descuentoDAO.eliminarDescuentoDb(descuentoAEliminar.getId())) {
                cargarDatosDescuentos(); // Recargar la tabla
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo eliminar el descuento de la base de datos.");
            }
        }
    }

    /**
     * Maneja la acción del botón Guardar/Guardar Cambios.
     * Valida los datos y llama al DAO para crear o actualizar el descuento.
     */
    @FXML
    private void guardarDescuento() {
        if (!esFormularioValido()) {
            return;
        }

        try {
            String descripcion = descripcionField.getText();
            double porcentaje = Double.parseDouble(porcentajeField.getText());
            int duracionMeses = Integer.parseInt(duracionField.getText());

            if (modoEdicion) {
                // Actualizar descuento existente
                descuentoAEditar.setDescripcion(descripcion);
                descuentoAEditar.setPorcentaje(porcentaje);
                descuentoAEditar.setFechaFin(descuentoAEditar.getFechaInicio().plusMonths(duracionMeses));
                descuentoAEditar.actualizarActivo(); // Re-evalúa si está activo

                if (descuentoDAO.actualizarDescuentoDb(descuentoAEditar)) {
                    tablaDescuentos.refresh();
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error de Base de Datos", "No se pudo actualizar el descuento.");
                }
            } else {
                // Crear nuevo descuento
                LocalDate fechaInicio = LocalDate.now();
                LocalDate fechaFin = fechaInicio.plusMonths(duracionMeses);
                // NOTA: Se pasa 'null' para clienteId, ya que este formulario no lo gestiona.
                Descuento nuevoDescuento = new Descuento(null, descripcion, porcentaje, fechaInicio, fechaFin);

                if (descuentoDAO.guardarDescuentoDb(nuevoDescuento)) {
                    listaDescuentos.add(nuevoDescuento);
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error de Base de Datos", "No se pudo guardar el descuento.");
                }
            }
            ocultarFormulario();

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Formato", "El porcentaje y la duración deben ser números válidos.");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error Inesperado", "Ocurrió un error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Valida que los campos del formulario no estén vacíos y tengan el formato correcto.
     * @return true si es válido, false en caso contrario.
     */
    private boolean esFormularioValido() {
        if (descripcionField.getText().isEmpty() || porcentajeField.getText().isEmpty() || duracionField.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Validación", "Todos los campos son obligatorios.");
            return false;
        }
        try {
            Double.parseDouble(porcentajeField.getText());
            Integer.parseInt(duracionField.getText());
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Validación", "Porcentaje y duración deben ser números.");
            return false;
        }
        return true;
    }

    // --- Métodos de ayuda para la UI ---

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

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
