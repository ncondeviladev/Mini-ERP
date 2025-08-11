package com.erp.controller;

import java.util.ArrayList;
import java.util.List;

import com.erp.dao.ProductoDAO;
import com.erp.model.Producto;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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

/**
 * Controlador para la vista de gestión de productos.
 * <p>
 * Maneja la lógica de la interfaz de usuario para añadir, modificar, eliminar y
 * buscar productos, así como la presentación de datos en la tabla.
 */
public class ProductoController {

    // --- Inyección de componentes FXML ---

    // DAO para interactuar con la base de datos de productos.
    private ProductoDAO productoDAO = new ProductoDAO(); // Acceso a datos de producto

    @FXML
    private TextField nombreProductoField, descripcionProductoField, categoriaProductoField, precioProductoField, stockProductoField;

    @FXML
    private TextField buscarIdProductoField, buscarNombreProductoField, buscarCategoriaProductoField;

    @FXML
    private TableView<Producto> tablaProducto;
    @FXML
    private TableColumn<Producto, Integer> colIdProducto, colStockProducto;
    @FXML
    private TableColumn<Producto, String> colNombreProducto, colCategoriaProducto, colDescripcionProducto;
    @FXML
    private TableColumn<Producto, Double> colPrecioProducto;

    @FXML
    private StackPane zonaFormulariosProducto;
    @FXML
    private Label inicioProducto;
    @FXML
    private VBox formularioAñadirProducto, formularioBuscarProducto;

    @FXML
    private Button botonModificarProducto;
    @FXML
    private Button botonEliminar;
    @FXML
    private Button guardarProductoButton;
    @FXML
    private Label tituloFormularioProducto;

    // --- Lógica de negocio y estado ---
    private List<Producto> productosOriginales = new ArrayList<>(); // Lista completa de productos para filtrar
    private boolean modoEdicion = false; // Indica si se está editando un producto
    private Producto productoAEditar = null; // Producto que se está editando

    /**
     * Inicializa el controlador. Configura las columnas de la tabla de productos,
     * carga los datos iniciales, y establece listeners para la selección de la
     * tabla y eventos de teclado.
     */
    @FXML
    public void initialize() {
        // Configura las columnas de la tabla con las propiedades del modelo
        colIdProducto.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombreProducto.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoriaProducto.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colPrecioProducto.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colStockProducto.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colDescripcionProducto.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        // Personaliza la celda de descripción para que el texto se ajuste automáticamente
        // si es demasiado largo para el ancho de la columna.
        colDescripcionProducto.setCellFactory(tc -> {
            TableCell<Producto, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(colDescripcionProducto.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell;
        });

        // Carga los productos iniciales desde la base de datos a la tabla.
        tablaProducto.getItems().addAll(productoDAO.listarProductos());

        // Oculta el contenedor de formularios al inicio para que no ocupe espacio.
        zonaFormulariosProducto.setVisible(false);
        zonaFormulariosProducto.setManaged(false);

        // Asigna el evento de la tecla Enter a los campos del formulario de añadir/editar.
        activarENterEnCampos();

        // Añade un listener a la selección de la tabla para habilitar/deshabilitar los botones de acción.
        tablaProducto.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, nuevoSel) -> {
            boolean haySeleccion = nuevoSel != null;
            botonModificarProducto.setDisable(!haySeleccion);
            botonEliminar.setDisable(!haySeleccion);
        });
    }

    /**
     * Asigna un manejador de eventos de teclado a los campos del formulario de producto.
     * <p>
     * Al presionar la tecla ENTER, se intenta guardar (crear o actualizar) el
     * producto.
     */
    public void activarENterEnCampos() {
        TextField[] campos = {nombreProductoField, descripcionProductoField, categoriaProductoField, precioProductoField, stockProductoField};
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

    /**
     * Muestra el formulario para añadir un nuevo producto.
     * <p>
     * Configura la interfaz para el modo de creación, limpiando campos y ajustando
     * textos de botones y títulos.
     */
    @FXML
    public void mostrarVistaAñadir() {
        zonaFormulariosProducto.setVisible(true);
        zonaFormulariosProducto.setManaged(true);

        // Se asegura de que el formulario de búsqueda esté oculto.
        formularioBuscarProducto.setVisible(false);
        formularioBuscarProducto.setManaged(false);

        // Muestra el formulario de añadir/editar.
        formularioAñadirProducto.setVisible(true);
        formularioAñadirProducto.setManaged(true);
        
        tituloFormularioProducto.setText("Formulario añadir Producto");
        guardarProductoButton.setText("Añadir producto");

        modoEdicion = false;
        productoAEditar = null;
        limpiarFormulario();
        activarENterEnCampos();
    }

    /**
     * Muestra el formulario de búsqueda de productos.
     * <p>
     * Carga la lista completa de productos y añade listeners a los campos de
     * búsqueda para filtrar en tiempo real.
     */
    @FXML
    public void mostrarVistaBuscar() {
        zonaFormulariosProducto.setVisible(true);
        zonaFormulariosProducto.setManaged(true);

        // Se asegura de que el formulario de añadir esté oculto.
        formularioAñadirProducto.setVisible(false);
        formularioAñadirProducto.setManaged(false);

        // Muestra el formulario de búsqueda.
        formularioBuscarProducto.setVisible(true);
        formularioBuscarProducto.setManaged(true);
        
        // Carga la lista completa de productos para poder filtrar sobre ella.
        productosOriginales = productoDAO.listarProductos();
        tablaProducto.getItems().setAll(productosOriginales);

        // Añade listeners a los campos de búsqueda para que el filtrado sea en tiempo real.
        buscarIdProductoField.textProperty().addListener((obs, oldVal, newVal) -> filtrarProductos());
        buscarNombreProductoField.textProperty().addListener((obs, oldVal, newVal) -> filtrarProductos());
        buscarCategoriaProductoField.textProperty().addListener((obs, oldVal, newVal) -> filtrarProductos());
    }

    /**
     * Maneja el evento del botón de guardar.
     * <p>
     * Llama al método de actualización si está en modo edición, o al de creación si
     * no lo está.
     */
    @FXML
    private void insertarProducto(ActionEvent event) {
        // Decide si crear o actualizar basándose en el estado 'modoEdicion'.
        if (modoEdicion && productoAEditar != null) {
            actualizarProductoDesdeFormulario(productoAEditar);
            modoEdicion = false;
            productoAEditar = null;
        } else {
            crearProductoDesdeFormulario();
        }
    }

    /**
     * Recoge los datos del formulario, crea un nuevo objeto `Producto` y lo persiste en la base de datos.
     * <p>
     * Si tiene éxito, actualiza la tabla, limpia el formulario y capitaliza los
     * campos de texto.
     */
    private void crearProductoDesdeFormulario() {
        try {
            // Crea un nuevo objeto Producto con los datos del formulario.
            Producto nuevo = new Producto(
                    capitalizar(nombreProductoField.getText()),
                    capitalizar(descripcionProductoField.getText()),
                    capitalizar(categoriaProductoField.getText()),
                    Double.parseDouble(precioProductoField.getText().replace(",", ".")),
                    Integer.parseInt(stockProductoField.getText()));

            // Intenta guardar en la base de datos.
            if (productoDAO.guardarProductoDb(nuevo)) {
                tablaProducto.getItems().add(nuevo);
                limpiarFormulario();
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Verifica los campos ingresados.\n" + e.getMessage());
        }
    }

    /**
     * Actualiza los datos de un producto existente (`productoAEditar`) con los valores actuales del formulario.
     * 
     * @param producto El producto a actualizar.
     */
    private void actualizarProductoDesdeFormulario(Producto producto) {
        try {
            // Actualiza las propiedades del objeto Producto existente.
            producto.setNombre(capitalizar(nombreProductoField.getText()));
            producto.setDescripcion(capitalizar(descripcionProductoField.getText()));
            producto.setCategoria(capitalizar(categoriaProductoField.getText()));
            producto.setPrecioUnitario(Double.parseDouble(precioProductoField.getText().replace(",", ".")));
            producto.setStock(Integer.parseInt(stockProductoField.getText()));

            if (productoDAO.actualizarProductoEnDb(producto)) {
                // Refresca la tabla para mostrar los cambios y limpia el formulario.
                tablaProducto.refresh();
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

    /**
     * Prepara el formulario para modificar el producto seleccionado en la tabla.
     * <p>
     * Rellena los campos con los datos del producto y activa el modo de edición,
     * mostrando el formulario correspondiente.
     */
    @FXML
    public void modificarProductoSeleccionado() {
        Producto seleccionado = tablaProducto.getSelectionModel().getSelectedItem();

        if (seleccionado != null) {
            // Configura la UI para el modo de edición.
            tituloFormularioProducto.setText("Modificar Producto");
            guardarProductoButton.setText("Guardar cambios");
            productoAEditar = seleccionado;
            modoEdicion = true;

            nombreProductoField.setText(seleccionado.getNombre());
            descripcionProductoField.setText(seleccionado.getDescripcion());
            categoriaProductoField.setText(seleccionado.getCategoria());
            precioProductoField.setText(String.valueOf(seleccionado.getPrecioUnitario()));
            stockProductoField.setText(String.valueOf(seleccionado.getStock()));

            // Muestra el formulario de edición.
            zonaFormulariosProducto.setVisible(true);
            zonaFormulariosProducto.setManaged(true);

            // Oculta el formulario de búsqueda.
            formularioBuscarProducto.setVisible(false);
            formularioBuscarProducto.setManaged(false);

            // Muestra el formulario de añadir/editar.
            formularioAñadirProducto.setVisible(true);
            formularioAñadirProducto.setManaged(true);
            
        } else {
            mostrarAlerta("Selección inválida", "Por favor, selecciona un producto para modificar.");
        }
    }

    /**
     * Elimina el producto seleccionado de la tabla.
     * <p>
     * Pide confirmación al usuario antes de proceder con la eliminación en la base
     * de datos y la actualización de la tabla.
     */
    @FXML
    public void eliminarProductoSeleccionado() {
        Producto seleccionado = tablaProducto.getSelectionModel().getSelectedItem();

        if (seleccionado != null) {
            // Muestra un diálogo de confirmación antes de eliminar.
            Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
            alerta.setTitle("Confirmar eliminación");
            alerta.setHeaderText("¿Eliminar producto?");
            alerta.setContentText(
                    "¿Estás seguro de que deseas eliminar el producto: " + seleccionado.getNombre() + "?");

            alerta.showAndWait().ifPresent(respuesta -> {
                if (respuesta == ButtonType.OK) {
                    // Si el usuario confirma, procede a eliminar de la BD y actualizar la tabla.
                    if (productoDAO.eliminarProductoPorId(seleccionado.getId())) {
                        tablaProducto.getItems().setAll(productoDAO.listarProductos());
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

    /**
     * Filtra la lista de productos en la tabla basándose en los criterios de los campos de búsqueda.
     * <p>
     * La búsqueda es insensible a mayúsculas y minúsculas y busca por ID, nombre y categoría.
     */
    private void filtrarProductos() {
        String filtroId = buscarIdProductoField.getText().trim().toLowerCase();
        String filtroNombre = buscarNombreProductoField.getText().trim().toLowerCase();
        String filtroCategoria = buscarCategoriaProductoField.getText().trim().toLowerCase();

        List<Producto> filtrados = new ArrayList<>();

        // Recorre la lista original y añade a una lista temporal los que coinciden con los filtros.
        for (Producto p : productosOriginales) {
            boolean matchId = filtroId.isEmpty() || String.valueOf(p.getId()).toLowerCase().contains(filtroId);
            boolean matchNombre = filtroNombre.isEmpty() || p.getNombre().toLowerCase().contains(filtroNombre);
            boolean matchCategoria = filtroCategoria.isEmpty()
                    || p.getCategoria().toLowerCase().contains(filtroCategoria);

            if (matchId && matchNombre && matchCategoria) {
                filtrados.add(p);
            }
        }

        // Actualiza la tabla con la lista filtrada.
        tablaProducto.getItems().setAll(filtrados);
    }

    /**
     * Limpia todos los campos de texto del formulario de producto.
     */
    public void limpiarFormulario() {
        nombreProductoField.clear();
        descripcionProductoField.clear();
        categoriaProductoField.clear();
        precioProductoField.clear();
        stockProductoField.clear();
    }

    /**
     * Muestra una alerta de error estándar.
     * 
     * @param titulo El título de la ventana de alerta.
     * @param mensaje El mensaje de contenido de la alerta.
     */
    public void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra una alerta de información que se cierra automáticamente tras un breve período.
     * @param titulo El título de la ventana de alerta.
     * @param mensaje El mensaje de contenido de la alerta.
     */
    private void mostrarAlertaTemporal(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.show();

        javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1));
        delay.setOnFinished(event -> alert.close());
        delay.play();
    }

    /**
     * Convierte la primera letra de una cadena a mayúsculas y el resto a minúsculas.
     * @param texto La cadena de texto a capitalizar.
     * @return El texto capitalizado, o el texto original si es nulo o vacío.
     */
    private String capitalizar(String texto) {
        if (texto == null || texto.isEmpty())
            return texto;
        return texto.substring(0, 1).toUpperCase() + texto.substring(1).toLowerCase();
    }
}
