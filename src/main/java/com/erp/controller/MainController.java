package com.erp.controller;

import java.net.URL;

import com.erp.model.Cliente;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

public class MainController {

    @FXML
    private StackPane contenedorCentral;

    @FXML
    public void initialize() {
        cargarVista("inicio.fxml");

        URL url = getClass().getResource("/images/colorPattern.jpg");
        if (url != null) {
            System.out.println("✅ Imagen encontrada: " + url);
        } else {
            System.out.println("❌ Imagen NO encontrada. Revisa la ruta.");
        }
    }

    @FXML
    public void mostrarProductos() {
        cargarVista("producto.fxml");
    }

    @FXML
    public void mostrarClientes() {
        cargarVista("cliente.fxml");
    }

    @FXML
    public void mostrarDescuentos(Cliente cliente) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/descuento.fxml"));
            Node vista = loader.load();

            DescuentoController controller = loader.getController();
            controller.setMainController(this);
            controller.setClienteSeleccionado(cliente);

            contenedorCentral.getChildren().setAll(vista);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void mostrarVentas() {
        cargarVista("venta.fxml");
    }

    @FXML
    public void salirAplicacion() {
        System.exit(0);
    }

    private void cargarVista(String nombreFXML) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + nombreFXML));
            Node vista = loader.load();

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
