package com.erp.controller;

import java.net.URL;

import com.erp.model.Cliente;
import com.erp.utils.AnimationUtils;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

/**
 * Controlador principal de la aplicación.
 * <p>
 * Gestiona la navegación entre las diferentes vistas (productos, clientes, etc.),
 * cargándolas en el panel central de la interfaz. Actúa como un coordinador
 * entre los demás controladores.
 */
public class MainController {

    @FXML
    private StackPane contenedorCentral;

    // --- Botones del menú lateral ---
    // NOTA: Asegúrate de que en main.fxml cada botón tenga su fx:id correspondiente.
    // (ej. <Button fx:id="botonProductos" ... />)
    @FXML
    private Button botonProductos;
    @FXML
    private Button botonClientes;
    @FXML
    private Button botonVentas;
    @FXML
    private Button botonSalir;

    /**
     * Método de inicialización que se llama automáticamente al cargar el FXML.
     * Carga la vista de bienvenida por defecto.
     */
    @FXML
    public void initialize() {
        // Al iniciar la aplicación, se muestra la pantalla de bienvenida.
        cargarVista("inicio.fxml");

        // --- Aplicar animaciones a los botones del menú ---
        if (botonProductos != null) AnimationUtils.addHoverAnimation(botonProductos);
        if (botonClientes != null) AnimationUtils.addHoverAnimation(botonClientes);
        if (botonVentas != null) AnimationUtils.addHoverAnimation(botonVentas);
        if (botonSalir != null) AnimationUtils.addHoverAnimation(botonSalir);
    }

    /**
     * Carga y muestra la vista de gestión de productos en el panel central.
     */
    @FXML
    public void mostrarProductos() {
        cargarVista("producto.fxml");
    }

    /**
     * Carga y muestra la vista de gestión de clientes en el panel central.
     * <p>
     * Después de cargar la vista, inyecta una referencia de este `MainController`
     * en el `ClienteController` para permitir la comunicación entre ellos (por ejemplo,
     * para abrir la vista de descuentos desde la de clientes).
     */
    @FXML
    public void mostrarClientes() {
        cargarVista("cliente.fxml");
    }

    @FXML
    public void mostrarDescuentos(Cliente cliente) {
        // Este método se llama desde ClienteController para cambiar a la vista de descuentos.
        // Es un caso especial porque necesita pasar un objeto 'Cliente' al nuevo controlador.
        try {
            // Carga el FXML de la vista de descuentos.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/descuento.fxml"));
            Node vista = loader.load();

            // Obtiene la instancia del controlador de descuentos recién creado.
            DescuentoController controller = loader.getController();
            // Le pasa la referencia de este MainController y el cliente seleccionado.
            controller.setMainController(this);
            controller.setClienteSeleccionado(cliente);

            contenedorCentral.getChildren().setAll(vista);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Carga y muestra la vista de gestión de ventas en el panel central.
     */
    @FXML
    public void mostrarVentas() {
        cargarVista("venta.fxml");
    }

    /**
     * Cierra la aplicación de forma limpia.
     */
    @FXML
    public void salirAplicacion() {
        System.exit(0);
    }

    /**
     * Método de utilidad para cargar una vista FXML en el panel central.
     * <p>
     * Se encarga de localizar el archivo FXML, cargarlo y, si es necesario,
     * inyectar dependencias en el controlador de la vista cargada.
     * @param nombreFXML El nombre del archivo FXML a cargar (ej. "producto.fxml").
     */
    private void cargarVista(String nombreFXML) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + nombreFXML));
            Node vista = loader.load();

            // Comprueba el tipo del controlador cargado para inyectar este MainController si es necesario.
            Object controller = loader.getController();
            if (controller instanceof ClienteController) {
                ((ClienteController) controller).setMainController(this);
            } else if (controller instanceof DescuentoController) {
                ((DescuentoController) controller).setMainController(this);
            }

            contenedorCentral.getChildren().setAll(vista);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
