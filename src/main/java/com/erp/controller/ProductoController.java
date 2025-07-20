package com.erp.controller;

import java.util.ArrayList;
import java.util.List;

import com.erp.dao.ProductoDAO;
import com.erp.model.Producto;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ProductoController {

    private ProductoDAO productoDAO = new ProductoDAO();
    // 📝 Campos del formulario
    @FXML
    private TextField nombreField, descripcionField, categoriaField, precioField, stockField;

    // 🔍 Campos de búsqueda
    @FXML
    private TextField buscarIdField, buscarNombreField, buscarCategoriaField;

    // 📊 Tabla y columnas
    @FXML
    private TableView<Producto> tablaProductos;
    @FXML
    private TableColumn<Producto, Integer> colId, colStock;
    @FXML
    private TableColumn<Producto, String> colNombre, colCategoria, colDescripcion;
    @FXML
    private TableColumn<Producto, Double> colPrecio;

    // 🧩 Vistas dinámicas
    @FXML
    private StackPane zonaContenido;
    @FXML
    private Label inicio;
    @FXML
    private VBox formularioAñadirProducto, formularioBuscarProducto;

    // 🎮 Botones de acción
    @FXML
    private Button botonModificar;
    @FXML
    private Button botonEliminar;
    @FXML
    private Button botonGuardarProducto;
    @FXML
    private Label tituloFormulario;

    // ⚙️ Estado de edición
    private List<Producto> productosOriginales = new ArrayList<>();
    private boolean modoEdicion = false;
    private Producto productoAEditar = null;

    // 🔄 Inicialización
    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        // 🎨 Configurar el ajuste de texto para la columna de descripción
        colDescripcion.setCellFactory(tc -> {
            TableCell<Producto, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(colDescripcion.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell;
        });

        tablaProductos.getItems().addAll(productoDAO.listarProductos());

        mostrarVista(inicio);
        zonaContenido.setVisible(false);
        zonaContenido.setManaged(false);

        activarENterEnCampos();

        tablaProductos.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, nuevoSel) -> {
            boolean haySeleccion = nuevoSel != null;
            botonModificar.setDisable(!haySeleccion);
            botonEliminar.setDisable(!haySeleccion);
        });
    }

    public void activarENterEnCampos() {
        TextField[] campos = {nombreField, descripcionField, categoriaField, precioField, stockField};
        for (TextField campo : campos) {
            campo.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    if (modoEdicion && productoAEditar != null) {
                        actualizarProductoDesdeFormulario(productoAEditar);
                    } else {
                        crearProductoDesdeFormulario();
                    }
                }
            });
        }
    }

    // 🌐 Navegación entre vistas
    private void mostrarVista(Node vista) {
        zonaContenido.getChildren().setAll(vista);
    }

    @FXML
    public void mostrarVistaAñadir() {
        zonaContenido.setVisible(true);
        zonaContenido.setManaged(true);
        mostrarVista(formularioAñadirProducto);

        // 🔁 Reiniciar el estado visual
        tituloFormulario.setText("Formulario añadir Producto");
        botonGuardarProducto.setText("Añadir producto");

        // 🔄 Asegúrate también que NO estás en modo edición
        modoEdicion = false;
        productoAEditar = null;
        limpiarFormulario();
        activarENterEnCampos();
    }

    @FXML
    public void mostrarVistaBuscar() {
        zonaContenido.setVisible(true);
        zonaContenido.setManaged(true);

        productosOriginales = productoDAO.listarProductos();
        tablaProductos.getItems().setAll(productosOriginales);

        buscarIdField.textProperty().addListener((obs, oldVal, newVal) -> filtrarProductos());
        buscarNombreField.textProperty().addListener((obs, oldVal, newVal) -> filtrarProductos());
        buscarCategoriaField.textProperty().addListener((obs, oldVal, newVal) -> filtrarProductos());

        mostrarVista(formularioBuscarProducto);
    }

    // ➕ Añadir o modificar producto
    @FXML
    private void insertarProducto(ActionEvent event) {
        // Si estamos en modo edición, actualizamos el producto seleccionado
        // Si no, creamos un nuevo producto desde el formulario

        if (modoEdicion && productoAEditar != null) {
            actualizarProductoDesdeFormulario(productoAEditar);
            modoEdicion = false;
            productoAEditar = null;
        } else {
            crearProductoDesdeFormulario();
        }
    }

    private void crearProductoDesdeFormulario() {
        try {
            Producto nuevo = new Producto(
                    capitalizar(nombreField.getText()),
                    capitalizar(descripcionField.getText()),
                    capitalizar(categoriaField.getText()),
                    Double.parseDouble(precioField.getText().replace(",", ".")),
                    Integer.parseInt(stockField.getText()));

            if (productoDAO.guardarProductoDb(nuevo)) {
                tablaProductos.getItems().add(nuevo);
                limpiarFormulario();
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Verifica los campos ingresados.\n" + e.getMessage());
        }
    }

    private void actualizarProductoDesdeFormulario(Producto producto) {

        try {
            producto.setNombre(capitalizar(nombreField.getText()));
            producto.setDescripcion(capitalizar(descripcionField.getText()));
            producto.setCategoria(capitalizar(categoriaField.getText()));
            producto.setPrecioUnitario(Double.parseDouble(precioField.getText().replace(",", ".")));
            producto.setStock(Integer.parseInt(stockField.getText()));

            if (productoDAO.actualizarProductoEnDb(producto)) {
                tablaProductos.refresh();
                limpiarFormulario();
                mostrarAlertaTemporal("Éxito", "Producto actualizado");
            } else {
                mostrarAlerta("Error", "No se pudo actualizar el producto");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Verifica los campos \n" + e.getMessage());
        }
    }

    @FXML
    public void modificarProductoSeleccionado() {

        activarENterEnCampos();

        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();

        if (seleccionado != null) {
            tituloFormulario.setText("Modificar Producto");
            botonGuardarProducto.setText("Guardar cambios");
            productoAEditar = seleccionado;
            modoEdicion = true;

            nombreField.setText(seleccionado.getNombre());
            descripcionField.setText(seleccionado.getDescripcion());
            categoriaField.setText(seleccionado.getCategoria());
            precioField.setText(String.valueOf(seleccionado.getPrecioUnitario()));
            stockField.setText(String.valueOf(seleccionado.getStock()));

            zonaContenido.setVisible(true);
            zonaContenido.setManaged(true);
            mostrarVista(formularioAñadirProducto);

        } else {
            mostrarAlerta("Selección inválida", "Por favor, selecciona un producto para modificar.");
        }
    }

    @FXML
    public void eliminarProductoSeleccionado() {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();

        if (seleccionado != null) {
            Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
            alerta.setTitle("Confirmar eliminación");
            alerta.setHeaderText("¿Eliminar producto?");
            alerta.setContentText(
                    "¿Estás seguro de que deseas eliminar el producto: " + seleccionado.getNombre() + "?");

            alerta.showAndWait().ifPresent(respuesta -> {
                if (respuesta == ButtonType.OK) {
                    if (productoDAO.eliminarProductoPorId(seleccionado.getId())) {
                        tablaProductos.getItems().setAll(productoDAO.listarProductos());
                        mostrarAlertaTemporal("Éxito", "Producto eliminado correctamente.");
                    } else {
                        mostrarAlerta("Error", "No se pudo eliminar el producto.");
                    }
                }
            });
        } else {
            mostrarAlerta("Aviso", "Selecciona un producto en la tabla para eliminarlo.");
        }
    }

    // 🧠 Filtro de búsqueda
    private void filtrarProductos() {
        String filtroId = buscarIdField.getText().trim().toLowerCase();
        String filtroNombre = buscarNombreField.getText().trim().toLowerCase();
        String filtroCategoria = buscarCategoriaField.getText().trim().toLowerCase();

        List<Producto> filtrados = new ArrayList<>();

        for (Producto p : productosOriginales) {
            boolean matchId = filtroId.isEmpty() || String.valueOf(p.getId()).toLowerCase().contains(filtroId);
            boolean matchNombre = filtroNombre.isEmpty() || p.getNombre().toLowerCase().contains(filtroNombre);
            boolean matchCategoria = filtroCategoria.isEmpty()
                    || p.getCategoria().toLowerCase().contains(filtroCategoria);

            if (matchId && matchNombre && matchCategoria) {
                filtrados.add(p);
            }
        }

        tablaProductos.getItems().setAll(filtrados);
    }

    // 🧼 Utilidades
    public void limpiarFormulario() {
        nombreField.clear();
        descripcionField.clear();
        categoriaField.clear();
        precioField.clear();
        stockField.clear();
    }

    public void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarAlertaTemporal(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.show();

        javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1));
        delay.setOnFinished(event -> alert.close());
        delay.play(); // 🔥 Aquí está la clave
    }

    private String capitalizar(String texto) {
        if (texto == null || texto.isEmpty())
            return texto;
        return texto.substring(0, 1).toUpperCase() + texto.substring(1).toLowerCase();
    }
}
