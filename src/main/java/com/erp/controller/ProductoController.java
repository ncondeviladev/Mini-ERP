package com.erp.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.erp.controller.components.prodComp.ProductoFormularioAñadirController;
import com.erp.controller.components.prodComp.ProductoFormularioBuscarController;
import com.erp.controller.components.prodComp.ProductoTablaController;
import com.erp.dao.ProductoDAO;
import com.erp.model.Producto;
import com.erp.utils.Alerta;
import com.erp.utils.AnimationUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Controlador para la vista de gestión de productos.
 * <p>
 * Maneja la lógica de la interfaz de usuario para añadir, modificar, eliminar y
 * buscar productos, así como la presentación de datos en la tabla.
 */
public class ProductoController {

    // --- DAO ---
    private final ProductoDAO productoDAO = new ProductoDAO();

    @FXML
    private StackPane zonaFormulariosProducto;
    @FXML
    private Label inicioProducto;
    @FXML
    private Button botonAñadirProducto;
    @FXML
    private Button botonBuscarProducto;

    // --- Contenedores de los componentes incluidos ---
    @FXML
    private VBox formAnadir; // Contenedor del formulario de añadir
    @FXML
    private VBox formBuscar; // Contenedor del formulario de buscar

    // --- Inyección de los CONTROLADORES de los componentes ---
    @FXML
    private ProductoFormularioAñadirController formAnadirController; // Inyecta el controlador del FXML incluido
    @FXML
    private ProductoFormularioBuscarController formBuscarController; // Inyecta el controlador del FXML incluido
    @FXML
    private ProductoTablaController tablaProductosComponentController; // Inyecta el controlador del FXML incluido

    // --- Estado ---
    private List<Producto> productosOriginales = new ArrayList<>(); // Lista completa para filtrar

    /**
     * Inicializa el controlador principal. Conecta los sub-controladores,
     * carga los datos iniciales y configura el estado inicial de la vista.
     */
    @FXML
    public void initialize() {
        // 1. Conectar este controlador principal con sus hijos
        formAnadirController.setProductoController(this);
        formBuscarController.setProductoController(this);
        tablaProductosComponentController.setProductoController(this);

        // 2. Cargar los datos iniciales en la tabla
        cargarYMostrarProductos();

        // 3. Configurar estado inicial de la UI
        ocultarTodosLosFormularios();

        // 4. Aplicar animaciones a los botones principales
        AnimationUtils.addHoverAnimation(botonAñadirProducto);
        AnimationUtils.addHoverAnimation(botonBuscarProducto);
    }

    /**
     * Muestra el formulario para añadir un nuevo producto.
     */
    @FXML
    public void mostrarVistaAñadir() {
        ocultarTodosLosFormularios();
        // Delega la preparación y visualización al controlador del formulario
        formAnadirController.mostrarFormulario(null); // null indica que es un producto nuevo
        inicioProducto.setVisible(false);
    }

    /**
     * Muestra el formulario de búsqueda de productos.
     */
    @FXML
    public void mostrarVistaBuscar() {
        ocultarTodosLosFormularios();
        formBuscar.setVisible(true);
        formBuscar.setManaged(true);
        inicioProducto.setVisible(false);
    }

    /**
     * Guarda o actualiza un producto en la base de datos.
     * Este método es invocado por el `ProductoFormularioAñadirController`.
     * @param producto El producto a guardar o actualizar.
     */
    public void guardarOActualizarProducto(Producto producto) {
        boolean exito;
        if (producto.getId() == null) { // Es un producto nuevo
            exito = productoDAO.guardarProductoDb(producto);
        } else {
            exito = productoDAO.actualizarProductoEnDb(producto);
        }

        if (exito) {
            Alerta.mostrarAlertaTemporal( AlertType.INFORMATION, "Éxito", null, "Producto guardado correctamente.");
            cargarYMostrarProductos();
            ocultarTodosLosFormularios();
        } else {
            Alerta.mostrarError("Error de Base de Datos", "No se pudo guardar el producto.");
        }
    }

    /**
     * Prepara el formulario para modificar el producto seleccionado en la tabla.
     * Este método es invocado por el `ProductoTablaController`.
     */
    public void modificarProductoSeleccionado() {
        Producto seleccionado = tablaProductosComponentController.getProductoSeleccionado();
        if (seleccionado != null) {
            ocultarTodosLosFormularios();
            // Delega la preparación y visualización al controlador del formulario
            formAnadirController.mostrarFormulario(seleccionado);
            inicioProducto.setVisible(false);
        } else {
            Alerta.mostrarAdvertencia("Acción no disponible", "Debes seleccionar un producto para modificar.");
        }
    }

    /**
     * Elimina el producto seleccionado de la tabla.
     * Este método es invocado por el `ProductoTablaController`.
     */
    public void eliminarProductoSeleccionado() {
        Producto seleccionado = tablaProductosComponentController.getProductoSeleccionado();
        if (seleccionado == null) {
            Alerta.mostrarAdvertencia("Acción no disponible", "Por favor, selecciona un producto para eliminar.");
            return;
        }

        boolean confirmado = Alerta.mostrarConfirmacion(
                "Confirmar Eliminación",
                "¿Estás seguro de que quieres eliminar este producto?",
                seleccionado.getNombre());

        if (confirmado) {
            if (productoDAO.eliminarProductoPorId(seleccionado.getId())) {
                Alerta.mostrarAlertaTemporal(AlertType.INFORMATION, "Éxito", null, "Producto eliminado correctamente.");
                cargarYMostrarProductos();
            } else {
                Alerta.mostrarError("Error de Base de Datos", "No se pudo eliminar el producto.");
            }
        }
    }

    /**
     * Filtra la lista de productos en la tabla.
     * Este método es invocado por el `ProductoFormularioBuscarController`.
     */
    public void filtrarProductos() {
        Map<String, String> criterios = formBuscarController.getCriteriosBusqueda();
        String filtroId = criterios.get("id").toLowerCase();
        String filtroNombre = criterios.get("nombre").toLowerCase();
        String filtroCategoria = criterios.get("categoria").toLowerCase();

        List<Producto> filtrados = productosOriginales.stream()
                .filter(p -> filtroId.isEmpty() || String.valueOf(p.getId()).contains(filtroId))
                .filter(p -> filtroNombre.isEmpty() || p.getNombre().toLowerCase().contains(filtroNombre))
                .filter(p -> filtroCategoria.isEmpty() || p.getCategoria().toLowerCase().contains(filtroCategoria))
                .collect(Collectors.toList());

        tablaProductosComponentController.setItems(filtrados);
    }

    /**
     * Actualiza el estado de los botones de la tabla (Modificar/Eliminar).
     * Este método es invocado por el `ProductoTablaController`.
     * @param productoSeleccionado El producto que ha sido seleccionado, o null.
     */
    public void actualizarEstadoBotones(Producto productoSeleccionado) {
        boolean haySeleccion = productoSeleccionado != null;
        tablaProductosComponentController.setDisableBotones(!haySeleccion);
    }

    // --- Métodos de utilidad ---

    private void cargarYMostrarProductos() {
        productosOriginales = productoDAO.listarProductos();
        tablaProductosComponentController.setItems(productosOriginales);
    }

    private void ocultarTodosLosFormularios() {
        formAnadir.setVisible(false);
        formAnadir.setManaged(false);
        formBuscar.setVisible(false);
        formBuscar.setManaged(false);

                inicioProducto.setVisible(false);
        inicioProducto.setManaged(false);
    }

   

    /**
     * Convierte la primera letra de una cadena a mayúsculas y el resto a minúsculas.
     * @param texto La cadena de texto a capitalizar.
     * @return El texto capitalizado, o el texto original si es nulo o vacío.
     */
    public String capitalizar(String texto) {
        if (texto == null || texto.isEmpty())
            return texto;
        return texto.substring(0, 1).toUpperCase() + texto.substring(1).toLowerCase();
    }
}



