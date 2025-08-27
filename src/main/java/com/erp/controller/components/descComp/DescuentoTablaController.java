package com.erp.controller.components.descComp;

import com.erp.controller.MainController;
import com.erp.model.Descuento;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableCell; // Importar TableCell

import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;

public class DescuentoTablaController implements Initializable {

    private MainController mainController;

    @FXML
    private TableView<Descuento> tablaDescuentos;
    @FXML
    private TableColumn<Descuento, String> columnaDescripcion;
    @FXML
    private TableColumn<Descuento, Double> columnaPorcentaje;
    @FXML
    private TableColumn<Descuento, String> columnaFechaInicio; // Asumiendo String por simplicidad, podría ser LocalDate
    @FXML
    private TableColumn<Descuento, String> columnaFechaFin; // Asumiendo String por simplicidad, podría ser LocalDate
    @FXML
    private TableColumn<Descuento, Boolean> columnaEstado; // Cambiado a Boolean para reflejar la propiedad 'activo'

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        columnaDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        columnaPorcentaje.setCellValueFactory(new PropertyValueFactory<>("porcentaje"));
        columnaFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicioFormatted")); // Usar el getter formateado
        columnaFechaFin.setCellValueFactory(new PropertyValueFactory<>("fechaFinFormatted")); // Usar el getter formateado
        columnaEstado.setCellValueFactory(new PropertyValueFactory<>("activo")); // Corregido a 'activo'

        // Personaliza la celda de la columna "Estado" para mostrar texto y color
        // en lugar de un simple "true" o "false".
        columnaEstado.setCellFactory(columna -> new TableCell<Descuento, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    // Si la fila está vacía, limpiamos la celda.
                    setText(null);
                    setStyle("");
                } else {
                    // Si el valor es 'true', mostramos "Activo" en verde.
                    if (item) {
                        setText("Activo");
                        setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;");
                    } else { // Si el valor es 'false'
                        // Si no, mostramos "Caducado" en rojo.
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

    public TableView<Descuento> getTablaDescuentos() {
        return tablaDescuentos;
    }

    // Método para establecer los elementos en la tabla, a ser llamado por el controlador padre
    public void setDescuentos(List<Descuento> descuentos) {
        tablaDescuentos.getItems().setAll(descuentos);
    }

    // Método para obtener el descuento seleccionado, a ser llamado por el controlador padre
    public Descuento getSelectedDescuento() {
        return tablaDescuentos.getSelectionModel().getSelectedItem();
    }
}