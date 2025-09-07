package com.erp.controller.components.descComp;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.erp.controller.MainController;
import com.erp.model.Descuento;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;

public class DescuentoTablaController implements Initializable {

    private MainController mainController;

    @FXML
    private TableView<Descuento> tablaDescuentos;
    @FXML
    private TableColumn<Descuento, Boolean> columnaSeleccion;
    @FXML
    private TableColumn<Descuento, String> columnaDescripcion;
    @FXML
    private TableColumn<Descuento, Double> columnaPorcentaje;
    @FXML
    private TableColumn<Descuento, String> columnaFechaInicio;
    @FXML
    private TableColumn<Descuento, String> columnaFechaFin;
    @FXML
    private TableColumn<Descuento, Boolean> columnaEstado;

    private ChangeListener<Boolean> selectionChangeListener;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // --- Configuración de las columnas ---
        columnaDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        columnaPorcentaje.setCellValueFactory(new PropertyValueFactory<>("porcentaje"));
        columnaFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicioFormatted"));
        columnaFechaFin.setCellValueFactory(new PropertyValueFactory<>("fechaFinFormatted"));
        columnaEstado.setCellValueFactory(new PropertyValueFactory<>("activo"));

        // --- Columna de CheckBox con deshabilitado condicional ---
        columnaSeleccion.setCellValueFactory(new PropertyValueFactory<>("seleccionado"));
        columnaSeleccion.setCellFactory(col -> new CheckBoxTableCell<Descuento, Boolean>() {
            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setDisable(true);
                    setStyle("");
                } else {
                    Descuento descuento = getTableRow().getItem();
                    if (!descuento.isActivo()) {
                        // Si el descuento está caducado, deshabilitar la celda
                        setDisable(true);
                        
                    } else {
                        // Si está activo, habilitar la celda
                        setDisable(false);
                        setStyle("");
                    }
                }
            }
        });
        columnaSeleccion.setEditable(true);
        tablaDescuentos.setEditable(true);

        // --- Renderizado de la columna Estado ---
        columnaEstado.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    if (item) {
                        setText("Activo");
                        setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;");
                    } else {
                        setText("Caducado");
                        setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setDescuentos(List<Descuento> descuentos) {
        // Limpiar listeners antiguos para evitar fugas de memoria
        for (Descuento d : tablaDescuentos.getItems()) {
            if (selectionChangeListener != null) {
                d.seleccionadoProperty().removeListener(selectionChangeListener);
            }
        }
        // Añadir nuevos items
        tablaDescuentos.getItems().setAll(descuentos);
        // Añadir nuevos listeners
        for (Descuento d : descuentos) {
             if (selectionChangeListener != null) {
                d.seleccionadoProperty().addListener(selectionChangeListener);
            }
        }
    }

    public List<Descuento> getDescuentosSeleccionados() {
        return tablaDescuentos.getItems().stream()
                .filter(Descuento::isSeleccionado)
                .collect(Collectors.toList());
    }
    
    public void setOnSelectionChanged(Runnable action) {
        this.selectionChangeListener = (obs, oldVal, newVal) -> {
            if (action != null) {
                action.run();
            }
        };
        // Aplicar el listener a los items ya existentes
        for (Descuento d : tablaDescuentos.getItems()) {
            d.seleccionadoProperty().removeListener(this.selectionChangeListener); // Evitar duplicados
            d.seleccionadoProperty().addListener(this.selectionChangeListener);
        }
    }
    
    public TableView<Descuento> getTablaDescuentos() {
        return tablaDescuentos;
    }

    public Descuento getSelectedDescuento() {
        return tablaDescuentos.getSelectionModel().getSelectedItem();
    }
}