package com.erp.controller;

import com.erp.dao.DescuentoDAO;
import com.erp.model.Cliente;
import com.erp.model.Descuento;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
    private TableColumn<Descuento, String> columnaFechaInicio;
    @FXML
    private TableColumn<Descuento, String> columnaFechaFin;
    @FXML
    private Button botonAgregarDescuento;
    @FXML
    private Button btnEditarDescuento;
    @FXML
    private Button btnEliminarDescuento;

    private MainController mainController;
    private Cliente clienteSeleccionado;
    private final DescuentoDAO descuentoDAO = new DescuentoDAO();
    private final ObservableList<Descuento> listaDescuentos = FXCollections.observableArrayList();
    private boolean modoEdicion = false;
    private Descuento descuentoAEditar = null;

    @FXML
    public void initialize() {
        configurarColumnasTabla();
        tablaDescuentos.setItems(listaDescuentos);

        tablaDescuentos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean haySeleccion = newSelection != null;
            btnEditarDescuento.setDisable(!haySeleccion);
            btnEliminarDescuento.setDisable(!haySeleccion);
        });

        formularioContenedor.setVisible(false);
        formularioContenedor.setManaged(false);

        // Activar guardado con Enter en los campos del formulario
        configurarGuardadoConEnter(descripcionField);
        configurarGuardadoConEnter(porcentajeField);
        configurarGuardadoConEnter(duracionField);
    }

    private void configurarGuardadoConEnter(TextField textField) {
        textField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                guardarDescuento();
            }
        });
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setClienteSeleccionado(Cliente cliente) {
        this.clienteSeleccionado = cliente;
        cargarDatosDescuentos();
    }

    private void configurarColumnasTabla() {
        columnaDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        columnaPorcentaje.setCellValueFactory(new PropertyValueFactory<>("porcentaje"));
        columnaFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicioFormatted"));
        columnaFechaFin.setCellValueFactory(new PropertyValueFactory<>("fechaFinFormatted"));
    }

    private void cargarDatosDescuentos() {
        if (clienteSeleccionado != null) {
            List<Descuento> descuentosDesdeDB = descuentoDAO.listarDescuentosPorCliente(clienteSeleccionado.getId());
            listaDescuentos.setAll(descuentosDesdeDB);
        } else {
            listaDescuentos.clear();
        }
    }

    @FXML
    private void agregarDescuento() {
        if (clienteSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin Cliente", "No se ha seleccionado un cliente para añadirle un descuento.");
            return;
        }
        modoEdicion = false;
        descuentoAEditar = null;
        limpiarFormulario();
        botonGuardar.setText("Guardar");
        mostrarFormulario();
    }

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
                cargarDatosDescuentos();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo eliminar el descuento de la base de datos.");
            }
        }
    }

    @FXML
    private void guardarDescuento() {
        if (!esFormularioValido()) {
            return;
        }

        if (clienteSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error Crítico", "No hay un cliente seleccionado para asociar el descuento.");
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
                    tablaDescuentos.refresh();
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error de Base de Datos", "No se pudo actualizar el descuento.");
                }
            } else {
                LocalDate fechaInicio = LocalDate.now();
                LocalDate fechaFin = fechaInicio.plusMonths(duracionMeses);
                Descuento nuevoDescuento = new Descuento(clienteSeleccionado.getId(), descripcion, porcentaje, fechaInicio, fechaFin);

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