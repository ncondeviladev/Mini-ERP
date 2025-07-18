package com.erp.controller;

// Este controlador gestiona la lógica directa de producto: insertar y listar en base de datos

import java.sql.PreparedStatement; // Modelo que representa un producto
import java.sql.ResultSet; // Clase de conexión a la base de datos SQLite
import java.sql.SQLException;
import java.sql.Statement; // Anotaciones para vincular con FXML
import java.util.ArrayList;
import java.util.List;

import com.erp.db.SQLiteConnector;
import com.erp.model.Producto;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ProductoController {

    // 📥 Campos del formulario FXML para introducir datos
    @FXML
    private TextField nombreField;
    @FXML
    private TextField descripcionField;
    @FXML
    private TextField categoriaField;
    @FXML
    private TextField precioField;
    @FXML
    private TextField stockField;

    // 📊 Tabla y columnas que se ven en la interfaz FXML
    @FXML
    private TableView<Producto> tablaProductos;
    @FXML
    private TableColumn<Producto, Integer> colId;
    @FXML
    private TableColumn<Producto, String> colNombre;
    @FXML
    private TableColumn<Producto, String> colCategoria;
    @FXML
    private TableColumn<Producto, Double> colPrecio;
    @FXML
    private TableColumn<Producto, Integer> colStock;

    @FXML
    private StackPane contenedorProducto; // Contenedor principal de la vista
    
    @FXML private Label mensajeInicio; // Mensaje de inicio en la vista
    @FXML private VBox formularioAñadirProducto; // Formulario para añadir productos
    /**
     * Método que se ejecuta automáticamente al cargar el FXML.
     * Configura las columnas de la tabla y carga productos desde la base de datos.
     */
    @FXML
    public void initialize() {

        mensajeInicio.setVisible(true); // Muestra mensaje de inicio
        mensajeInicio.setManaged(true);
        formularioAñadirProducto.setVisible(false); // Oculta el formulario de añadir producto
        formularioAñadirProducto.setManaged(false);
        
        // Asocia cada columna con una propiedad del objeto Producto
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

        // Carga los productos existentes en la tabla
        tablaProductos.getItems().addAll(listarProductos());
    }

    /**
     * Método ejecutado cuando se pulsa el botón "Añadir producto" en la UI.
     * Toma datos del formulario, los convierte, crea un objeto Producto, lo inserta
     * en BD.
     */
    @FXML
    private void insertarProducto(ActionEvent event) {
        try {
            // 📝 Obtiene valores de los campos del formulario
            Producto nuevo = new Producto(
                    nombreField.getText(),
                    descripcionField.getText(),
                    categoriaField.getText(),
                    Double.parseDouble(precioField.getText()),
                    Integer.parseInt(stockField.getText()));

            // 🚀 Intenta insertar el nuevo producto en la base de datos
            if (insertarProducto(nuevo)) {
                tablaProductos.getItems().add(nuevo); // Añade el producto a la tabla
                // Limpia los campos del formulario
                nombreField.clear();
                descripcionField.clear();
                categoriaField.clear();
                precioField.clear();
                stockField.clear();
            }

        } catch (Exception e) {
            // ⚠️ Captura cualquier error y muestra alerta
            mostrarAlerta("Error", "Verifica los campos ingresados.\n" + e.getMessage());
        }
    }

    /**
     * Muestra una alerta modal de tipo ERROR.
     * 
     * @param titulo  Título de la alerta
     * @param mensaje Contenido descriptivo del error
     */
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait(); // Bloquea hasta que el usuario cierre la ventana
    }

    /**
     * Inserta un objeto Producto en la base de datos.
     * 
     * @param producto El objeto Producto a insertar
     * @return true si se insertó correctamente, false si hubo error
     */
    public boolean insertarProducto(Producto producto) {
        String sql = "INSERT INTO productos (nombre, descripcion, categoria, precioUnitario, stock) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = SQLiteConnector.connect().prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
            // Asigna parámetros a la consulta
            stmt.setString(1, producto.getNombre());
            stmt.setString(2, producto.getDescripcion());
            stmt.setString(3, producto.getCategoria());
            stmt.setDouble(4, producto.getPrecioUnitario());
            stmt.setInt(5, producto.getStock());

            // Ejecuta la inserción
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                // Obtiene el ID generado por la base de datos y lo asigna al objeto
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    producto.setId(rs.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            // Muestra alerta si hay error en la base de datos
            mostrarAlerta("Base de Datos", "Error al insertar producto:\n" + e.getMessage());
        }

        return false;
    }

    /**
     * Recupera todos los productos almacenados en la base de datos.
     * 
     * @return Lista de productos encontrados
     */
    public List<Producto> listarProductos() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM productos";

        try (Statement stmt = SQLiteConnector.connect().createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            // Itera sobre cada fila de resultados y crea objetos Producto
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
            // Muestra alerta si falla la lectura
            mostrarAlerta("Base de Datos", "Error al listar productos:\n" + e.getMessage());
        }

        return lista;
    }

    @FXML
    public void mostrarVistaAñadir() {
        mensajeInicio.setVisible(false); // Oculta mensaje de inicio
        mensajeInicio.setManaged(false);
        formularioAñadirProducto.setVisible(true); // Muestra formulario de añadir producto
        formularioAñadirProducto.setManaged(true);
    }

    @FXML
    public void mostrarVistaBuscar() {
        // por ahora puede estar vacío, solo debe existir
    }

    @FXML
    public void mostrarVistaModificar() {
        // Aquí iría otro formulario para edición
    }

    @FXML
    public void mostrarVistaEliminar() {
        // Aquí otra vista para confirmación y borrado
    }

}
