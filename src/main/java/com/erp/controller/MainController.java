package com.erp.controller;

import java.net.URL;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

public class MainController {

    @FXML
    private StackPane contenedorCentral;

    /**
     * Se ejecuta al iniciar la aplicación. Carga la vista de bienvenida por
     * defecto.
     * También verifica la existencia de una imagen de fondo (actualmente solo
     * imprime en consola).
     */
    @FXML
    public void initialize() {
        cargarVista("inicio.fxml"); // Esto carga el mensaje de bienvenida

        URL url = getClass().getResource("/images/colorPattern.jpg");

        if (url != null) {
            System.out.println("✅ Imagen encontrada: " + url);
        } else {
            System.out.println("❌ Imagen NO encontrada. Revisa la ruta.");
        }

    }

    /**
     * Muestra la vista de gestión de productos en el contenedor central.
     */
    @FXML
    public void mostrarProductos() {
        cargarVista("producto.fxml");
    }

    /**
     * Muestra la vista de gestión de clientes en el contenedor central.
     */
    @FXML
    public void mostrarClientes() {
        cargarVista("cliente.fxml"); // cuando lo tengas
    }

    @FXML
    public void mostrarDescuentos() {
        cargarVista("descuento.fxml");
    }

    /**
     * Muestra la vista de gestión de ventas en el contenedor central.
     */
    @FXML
    public void mostrarVentas() {
        cargarVista("venta.fxml"); // cuando lo tengas
    }

    /**
     * Cierra la aplicación.
     */
    @FXML
    public void salirAplicacion() {
        System.exit(0);
    }

    /**
     * Carga una vista FXML en el panel central de la aplicación.
     * 
     * @param nombreFXML El nombre del archivo FXML a cargar (p. ej.,
     *                   "producto.fxml").
     */
    private void cargarVista(String nombreFXML) {
        try {
            Node vista = FXMLLoader.load(getClass().getResource("/fxml/" + nombreFXML));
            contenedorCentral.getChildren().setAll(vista);
        } catch (Exception e) {
            e.printStackTrace(); // Puedes usar alertas si quieres notificar errores
        }
    }
}