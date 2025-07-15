package com.erp.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MenuController {

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

    @FXML
    public void abrirFormularioCliente() {
        System.out.println("Formulario de Cliente no implementado aún.");
    }

    @FXML
    public void abrirFormularioVenta() {
        System.out.println("Formulario de Proveedor no implementado aún.");
    }
    @FXML
    public void salirAplicacion() {
        System.exit(0);
    }
}
