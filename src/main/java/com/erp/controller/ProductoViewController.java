package com.erp.controller;

// Controlador vinculado a la vista FXML, responsable de gestionar interacción entre la UI y los datos

import com.erp.dao.ProductoDAO; // Modelo de datos: clase Producto
import com.erp.model.Producto; // DAO para acceso a datos de productos

import javafx.collections.FXCollections; // Controlador lógico que gestiona operaciones con Productos
import javafx.collections.ObservableList; // Anotaciones FXML para vincular campos con la vista
import javafx.fxml.FXML; // Controles JavaFX como TextField, TableView, etc.
import javafx.scene.control.Alert; // Para inicializar ObservableList
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn; // Lista observable para mantener tabla sincronizada
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

public class ProductoViewController {

    // 📦 Campos de entrada del formulario, vinculados por fx:id desde FXML
    @FXML private TextField nombreProductoField;
    @FXML private TextField descripcionProductoField;
    @FXML private TextField categoriaProductoField;
    @FXML private TextField precioProductoField;
    @FXML private TextField stockProductoField;

    // 🗃️ Tabla y columnas para mostrar productos existentes
    @FXML private TableView<Producto> tablaProducto;
    @FXML private TableColumn<Producto, Integer> colIdProducto;
    @FXML private TableColumn<Producto, String> colNombreProducto;
    @FXML private TableColumn<Producto, String> colCategoriaProducto;
    @FXML private TableColumn<Producto, Double> colPrecioProducto;
    @FXML private TableColumn<Producto, Integer> colStockProducto;

    @FXML private ComboBox<String> selectorAccion;
    @FXML private StackPane contenedorFormulario;

    private final ProductoDAO productoDAO = new ProductoDAO(); // DAO para acceso a datos
    

    // 👨‍💼 Controlador que maneja lógica de inserción y recuperación
    private final ProductoController controller = new ProductoController();

    // 📌 Lista observable que mantiene sincronización con la tabla
    private final ObservableList<Producto> listaProductos = FXCollections.observableArrayList();

    /**
     * Método que se ejecuta automáticamente cuando se carga la vista FXML.
     * Configura las columnas de la tabla y carga productos desde la base de datos.
     */
    @FXML
    public void initialize() {
        // Vincula columna de ID con propiedad 'id' del producto
        colIdProducto.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(
            cell.getValue().getId()).asObject());

        // Vincula columna 'Nombre' con propiedad 'nombre'
        colNombreProducto.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
            cell.getValue().getNombre()));

        // Vincula columna 'Categoría' con propiedad 'categoria'
        colCategoriaProducto.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
            cell.getValue().getCategoria()));

        // Vincula columna 'Precio' con propiedad 'precioUnitario'
        colPrecioProducto.setCellValueFactory(cell -> new javafx.beans.property.SimpleDoubleProperty(
            cell.getValue().getPrecioUnitario()).asObject());

        // Vincula columna 'Stock' con propiedad 'stock'
        colStockProducto.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(
            cell.getValue().getStock()).asObject());

        // Asocia lista observable a la tabla
        tablaProducto.setItems(listaProductos);

        // Recupera productos existentes desde el controlador y los muestra
        listaProductos.setAll(productoDAO.listarProductos());
    }

    /**
     * Acción ejecutada al presionar el botón 'Añadir producto'.
     * Extrae datos del formulario, crea un Producto y lo inserta si es válido.
     */
    @FXML
    public void insertarProducto() {
        try {
            // 🟢 Recolecta datos desde los campos del formulario
            String nombre = nombreProductoField.getText();
            String descripcion = descripcionProductoField.getText();
            String categoria = categoriaProductoField.getText();
            double precio = Double.parseDouble(precioProductoField.getText());
            int stock = Integer.parseInt(stockProductoField.getText());

            // 🆕 Crea un nuevo objeto Producto (id = 0 se asignará en DB)
            Producto producto = new Producto(0, nombre, descripcion, categoria, precio, stock);

            // ✅ Inserta producto usando el controlador y actualiza tabla si tiene éxito
            if (productoDAO.guardarProductoDb(producto)) {
                listaProductos.add(producto); // Añade a la lista visual
                limpiarCampos(); // Limpia formulario para nuevos datos
            }
        } catch (Exception e) {
            // ⚠️ Muestra alerta en caso de error de conversión (precio/stock no numéricos)
            e.printStackTrace();
            mostrarAlerta("Datos inválidos", "Revisa los campos: precio y stock deben ser numéricos.");
        }
    }

    /**
     * Limpia los campos del formulario para permitir una nueva entrada.
     */
    private void limpiarCampos() {
        nombreProductoField.clear();
        descripcionProductoField.clear();
        categoriaProductoField.clear();
        precioProductoField.clear();
        stockProductoField.clear();
    }

    /**
     * Muestra una alerta modal de tipo ERROR con mensaje personalizado.
     * @param titulo Título de la ventana emergente
     * @param mensaje Texto descriptivo del problema
     */
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR); // Tipo de alerta: error
        alerta.setTitle(titulo);
        alerta.setHeaderText(null); // Sin encabezado adicional
        alerta.setContentText(mensaje); // Mensaje principal
        alerta.showAndWait(); // Bloquea hasta que se cierre
    }

    @FXML public void cambiarAccion() {
        String seleccion = selectorAccion.getValue();

        if(seleccion.equals("Añadir producto")){
            
        }
    }

}
