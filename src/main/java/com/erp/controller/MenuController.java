package com.erp.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MenuController {

    /**
     * Abre el formulario de gestión de productos en una nueva ventana (Stage).
     */
    @FXML
    public void abrirFormularioProducto() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/producto.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root, 600, 450));
            stage.setTitle("Formulario de Producto");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Acción para abrir el formulario de cliente. Actualmente no implementado.
     */
    @FXML
    public void abrirFormularioCliente() {
        System.out.println("Formulario de Cliente no implementado aún.");
    }

    /**
     * Acción para abrir el formulario de venta. Actualmente no implementado.
     */
    @FXML
    public void abrirFormularioVenta() {
        System.out.println("Formulario de Proveedor no implementado aún.");
    }
    /**
     * Cierra la aplicación.
     */
    @FXML
    public void salirAplicacion() {
        System.exit(0);
    }
}
