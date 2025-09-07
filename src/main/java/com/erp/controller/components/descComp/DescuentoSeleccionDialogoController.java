package com.erp.controller.components.descComp;

import com.erp.model.Descuento;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;

import java.util.List;

public class DescuentoSeleccionDialogoController {

    @FXML
    private DescuentoTablaController descuentoTablaController;

    @FXML
    public void initialize() {
        // Permitir la selección múltiple en la tabla
        descuentoTablaController.getTablaDescuentos().getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    /**
     * Carga la lista de descuentos disponibles en la tabla.
     * @param descuentos La lista de descuentos a mostrar.
     */
    public void setDescuentos(List<Descuento> descuentos) {
        descuentoTablaController.setDescuentos(descuentos);
    }

    /**
     * Devuelve los descuentos que han sido seleccionados en la tabla.
     * @return Una lista de los descuentos seleccionados.
     */
    public List<Descuento> getDescuentosSeleccionados() {
        return descuentoTablaController.getTablaDescuentos().getSelectionModel().getSelectedItems();
    }
}

