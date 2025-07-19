package com.erp.controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.erp.db.SQLiteConnector;
import com.erp.model.Producto;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ProductoController {

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

        tablaProductos.getItems().addAll(listarProductos());

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
        TextField[] campos = { nombreField, descripcionField, categoriaField, precioField, stockField };
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

        productosOriginales = listarProductos();
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

            if (guardarProductoDb(nuevo)) {
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

            if (actualizarProductoEnDb(producto)) {
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
    public void borrarProductoSeleccionado() {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();

        if (seleccionado != null) {
            Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
            alerta.setTitle("Confirmar eliminación");
            alerta.setHeaderText("¿Eliminar producto?");
            alerta.setContentText(
                    "¿Estás seguro de que deseas eliminar el producto: " + seleccionado.getNombre() + "?");

            alerta.showAndWait().ifPresent(respuesta -> {
                if (respuesta == ButtonType.OK) {
                    if (eliminarProductoPorId(seleccionado.getId())) {
                        tablaProductos.getItems().remove(seleccionado);
                    } else {
                        mostrarAlerta("Error", "No se pudo eliminar el producto.");
                    }
                }
            });
        } else {
            mostrarAlerta("Aviso", "Selecciona un producto en la tabla para eliminarlo.");
        }
    }

    // 💾 Acceso a base de datos
    public boolean guardarProductoDb(Producto producto) {
        String sql = "INSERT INTO productos (nombre, descripcion, categoria, precioUnitario, stock) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = SQLiteConnector.connect().prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, producto.getNombre());
            stmt.setString(2, producto.getDescripcion());
            stmt.setString(3, producto.getCategoria());
            stmt.setDouble(4, producto.getPrecioUnitario());
            stmt.setInt(5, producto.getStock());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next())
                    producto.setId(rs.getInt(1));
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Base de Datos", "Error al insertar producto:\n" + e.getMessage());
        }

        return false;
    }

    public boolean actualizarProductoEnDb(Producto producto) {
        String sql = "UPDATE productos SET nombre = ?, descripcion = ?, categoria = ?, precioUnitario = ?, stock = ? WHERE id = ?";

        try (PreparedStatement stmt = SQLiteConnector.connect().prepareStatement(sql)) {
            stmt.setString(1, producto.getNombre());
            stmt.setString(2, producto.getDescripcion());
            stmt.setString(3, producto.getCategoria());
            stmt.setDouble(4, producto.getPrecioUnitario());
            stmt.setInt(5, producto.getStock());
            stmt.setInt(6, producto.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Base de datos", "Error al actualizar en base de datos \n" + e.getMessage());
            return false;
        }
    }

    public boolean eliminarProductoPorId(Integer id) {
        String sql = "DELETE FROM productos WHERE id = ?";

        try (PreparedStatement stmt = SQLiteConnector.connect().prepareStatement(sql)) {
            stmt.setInt(1, id);
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Base de Datos", "Error al eliminar producto:\n" + e.getMessage());
            return false;
        }
    }

    public List<Producto> listarProductos() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM productos";

        try (Statement stmt = SQLiteConnector.connect().createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Producto p = new Producto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getString("categoria"),
                        rs.getDouble("precioUnitario"),
                        rs.getInt("stock"));
                lista.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Base de Datos", "Error al listar productos:\n" + e.getMessage());
        }

        return lista;
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

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarAlertaTemporal(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.setHeaderText(null);

        // ⚡ Cierra tras 2 segundos
        new javafx.animation.PauseTransition(javafx.util.Duration.seconds(2))
                .setOnFinished(event -> alert.close());

        alert.show();
    }

    private String capitalizar(String texto) {
        if (texto == null || texto.isEmpty())
            return texto;
        return texto.substring(0, 1).toUpperCase() + texto.substring(1).toLowerCase();
    }
}
