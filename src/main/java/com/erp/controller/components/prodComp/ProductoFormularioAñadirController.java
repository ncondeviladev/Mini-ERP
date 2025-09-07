package com.erp.controller.components.prodComp;

import com.erp.controller.ProductoController;
import com.erp.model.Producto;
import com.erp.utils.Alerta;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

/**
 * Controlador para el componente de formulario de añadir/modificar producto.
 * Gestiona la entrada de datos del producto y la acción de guardado.
 */
public class ProductoFormularioAñadirController {

    @FXML
    private VBox formularioAñadirProducto;
    @FXML
    private Label tituloFormularioProducto;
    @FXML
    private TextField nombreProductoField;
    @FXML
    private TextField descripcionProductoField;
    @FXML
    private TextField categoriaProductoField;
    @FXML
    private TextField precioProductoField;
    @FXML
    private TextField stockProductoField;
    @FXML
    private Button botonGuardarProducto;

    private ProductoController productoController;
    private Producto productoActual; // Para saber si estamos editando o creando

    /**
     * Inyecta una referencia al controlador principal para poder comunicarse con él.
     * @param productoController El controlador principal de la vista de productos.
     */
    public void setProductoController(ProductoController productoController) {
        this.productoController = productoController;
    }

    @FXML
    public void initialize() {
        // La acción del botón ahora se maneja aquí
        botonGuardarProducto.setOnAction(event -> handleGuardarProducto());

        // Configurar la pulsación de Enter en los campos de texto para que active el botón de guardar
        configurarEnterParaGuardar(nombreProductoField, botonGuardarProducto);
        configurarEnterParaGuardar(descripcionProductoField, botonGuardarProducto);
        configurarEnterParaGuardar(categoriaProductoField, botonGuardarProducto);
        configurarEnterParaGuardar(precioProductoField, botonGuardarProducto);
        configurarEnterParaGuardar(stockProductoField, botonGuardarProducto);
    }

    /**
     * Prepara y muestra el formulario para un producto.
     * Si el producto es nulo, se configura para añadir uno nuevo.
     * Si el producto no es nulo, se configura para modificarlo, rellenando los campos.
     * @param producto El producto a editar, o null para crear uno nuevo.
     */
    public void mostrarFormulario(Producto producto) {
        this.productoActual = producto;
        if (producto == null) {
            tituloFormularioProducto.setText("Añadir Nuevo Producto");
            botonGuardarProducto.setText("Guardar");
            limpiarCampos();
        } else {
            tituloFormularioProducto.setText("Modificar Producto");
            botonGuardarProducto.setText("Actualizar");
            nombreProductoField.setText(producto.getNombre());
            descripcionProductoField.setText(producto.getDescripcion());
            categoriaProductoField.setText(producto.getCategoria());
            precioProductoField.setText(String.valueOf(producto.getPrecioUnitario()));
            stockProductoField.setText(String.valueOf(producto.getStock()));
        }
        formularioAñadirProducto.setVisible(true);
        formularioAñadirProducto.setManaged(true);
    }

    /**
     * Recoge los datos del formulario, crea un objeto Producto y lo pasa
     * al controlador principal para que lo guarde o actualice.
     */
    public void handleGuardarProducto() {
        if (!validarCampos()) {
            Alerta.mostrarAdvertencia("Campos incompletos", "Nombre, precio y stock son obligatorios.");
            return; // Si la validación falla, no continuamos.
        }

        try {
            String nombre = productoController.capitalizar(nombreProductoField.getText());
            String descripcion = descripcionProductoField.getText(); // La descripción no se capitaliza
            String categoria = productoController.capitalizar(categoriaProductoField.getText());
            double precio = Double.parseDouble(precioProductoField.getText().replace(",", "."));
            int stock = Integer.parseInt(stockProductoField.getText());

            if (productoActual == null) { // Creando nuevo producto
                productoActual = new Producto(null, nombre, descripcion, categoria, precio, stock);
            } else { // Actualizando producto existente
                productoActual.setNombre(nombre);
                productoActual.setDescripcion(descripcion);
                productoActual.setCategoria(categoria);
                productoActual.setPrecioUnitario(precio);
                productoActual.setStock(stock);
            }
            // Llamamos al "director" para que haga el trabajo de guardado
            productoController.guardarOActualizarProducto(productoActual);
        } catch (NumberFormatException e) {
            Alerta.mostrarError("Error de formato", "Precio y Stock deben ser números válidos.");
        }
    }

    public boolean validarCampos() {
        return !nombreProductoField.getText().trim().isEmpty() &&
               !precioProductoField.getText().trim().isEmpty() &&
               !stockProductoField.getText().trim().isEmpty();
    }

    public void limpiarCampos() {
        nombreProductoField.clear();
        descripcionProductoField.clear();
        categoriaProductoField.clear();
        precioProductoField.clear();
        stockProductoField.clear();
    }

    /**
     * Configura un campo de texto para que, al presionar la tecla ENTER,
     * se simule un clic en el botón especificado.
     * @param campoTexto El campo de texto al que se le añade el manejador.
     * @param botonGuardar El botón que se debe "clicar" al presionar Enter.
     */
    private void configurarEnterParaGuardar(TextField campoTexto, Button botonGuardar) {
        campoTexto.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                botonGuardar.fire(); // Simula un clic en el botón
            }
        });
    }
}