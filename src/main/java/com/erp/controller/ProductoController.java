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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ProductoController {

    @FXML private TextField nombreField, descripcionField, categoriaField, precioField, stockField;
    @FXML private TextField buscarIdField, buscarNombreField, buscarCategoriaField;

    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, Integer> colId, colStock;
    @FXML private TableColumn<Producto, String> colNombre, colCategoria;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, String> colDescripcion;

    @FXML private StackPane zonaContenido;
    @FXML private Label mensajeInicio;
    @FXML private VBox formularioAñadirProducto, formularioBuscarProducto;

    private List<Producto> productosOriginales = new ArrayList<>();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        tablaProductos.getItems().addAll(listarProductos());
        mostrarVista(mensajeInicio);

        // Listener de tecla Enter en campos del formulario de añadir
        TextField[] campos = {nombreField, descripcionField, categoriaField, precioField, stockField};
        for (TextField campo : campos) {
            campo.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    procesarInsertarProducto();
                }
            });
        }
    }

    private void mostrarVista(Node vista) {
        zonaContenido.getChildren().setAll(vista);
    }

    @FXML public void mostrarVistaAñadir() {
        mostrarVista(formularioAñadirProducto);
    }

    @FXML public void mostrarVistaBuscar() {
        productosOriginales = listarProductos();
        tablaProductos.getItems().setAll(productosOriginales);

        buscarIdField.textProperty().addListener((obs, oldVal, newVal) -> filtrarProductos());
        buscarNombreField.textProperty().addListener((obs, oldVal, newVal) -> filtrarProductos());
        buscarCategoriaField.textProperty().addListener((obs, oldVal, newVal) -> filtrarProductos());

        mostrarVista(formularioBuscarProducto);
    }

    @FXML public void mostrarVistaModificar() {
        mostrarVista(new Label("Vista de modificar aún no disponible"));
    }

    @FXML public void mostrarVistaEliminar() {
        mostrarVista(new Label("Vista de eliminar aún no disponible"));
    }

    @FXML private void insertarProducto(ActionEvent event) {
        procesarInsertarProducto();
    }

    private void procesarInsertarProducto() {
        try {
            Producto nuevo = new Producto(
                capitalizar(nombreField.getText()),
                capitalizar(descripcionField.getText()),
                capitalizar(categoriaField.getText()),
                Double.parseDouble(precioField.getText().replace(",", ".")),
                Integer.parseInt(stockField.getText()));

            if (insertarProducto(nuevo)) {
                tablaProductos.getItems().add(nuevo);
                nombreField.clear(); descripcionField.clear();
                categoriaField.clear(); precioField.clear(); stockField.clear();
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Verifica los campos ingresados.\n" + e.getMessage());
        }
    }

    public boolean insertarProducto(Producto producto) {
        String sql = "INSERT INTO productos (nombre, descripcion, categoria, precioUnitario, stock) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = SQLiteConnector.connect().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, producto.getNombre());
            stmt.setString(2, producto.getDescripcion());
            stmt.setString(3, producto.getCategoria());
            stmt.setDouble(4, producto.getPrecioUnitario());
            stmt.setInt(5, producto.getStock());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) producto.setId(rs.getInt(1));
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Base de Datos", "Error al insertar producto:\n" + e.getMessage());
        }

        return false;
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

    private void filtrarProductos() {
        String filtroId = buscarIdField.getText().trim().toLowerCase();
        String filtroNombre = buscarNombreField.getText().trim().toLowerCase();
        String filtroCategoria = buscarCategoriaField.getText().trim().toLowerCase();

        List<Producto> filtrados = new ArrayList<>();

        for (Producto p : productosOriginales) {
            boolean matchId = filtroId.isEmpty() || String.valueOf(p.getId()).toLowerCase().contains(filtroId);
            boolean matchNombre = filtroNombre.isEmpty() || p.getNombre().toLowerCase().contains(filtroNombre);
            boolean matchCategoria = filtroCategoria.isEmpty() || p.getCategoria().toLowerCase().contains(filtroCategoria);

            if (matchId && matchNombre && matchCategoria) {
                filtrados.add(p);
            }
        }

        tablaProductos.getItems().setAll(filtrados);
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        return texto.substring(0, 1).toUpperCase() + texto.substring(1).toLowerCase();
    }

}
