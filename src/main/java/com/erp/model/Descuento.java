package com.erp.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Clase modelo que representa un Descuento aplicable a un cliente en el sistema ERP.
 * 
 * <p>Un descuento tiene un período de validez (fecha de inicio y fin) y un porcentaje.
 * Está asociado a un cliente específico a través de {@code clienteId}.</p>
 * 
 * <p><b>Características Clave:</b></p>
 * <ul>
 *     <li><b>Propiedades JavaFX:</b> Al igual que otras clases del modelo, utiliza propiedades
 *     JavaFX para un enlace de datos sencillo y reactivo con la interfaz de usuario.</li>
 *     <li><b>Estado Calculado:</b> El estado del descuento (activo o caducado) se calcula
 *     automáticamente a partir de la fecha de fin, simplificando la lógica de negocio.</li>
 *     <li><b>Propiedad para UI:</b> Incluye una propiedad {@code seleccionado} específica para
 *     controlar su estado en la interfaz (ej. en una celda con CheckBox) sin afectar
 *     al modelo de datos persistente.</li>
 * </ul>
 *
 * @see com.erp.dao.DescuentoDAO
 * @see com.erp.controller.DescuentoController
 * @author Noé
 */
public class Descuento {

    // --- PROPIEDADES OBSERVABLES PARA JAVA FX ---

    /**
     * Identificador único del descuento en la base de datos.
     */
    private final ObjectProperty<Integer> id;

    /**
     * ID del {@link Cliente} al que pertenece este descuento. Es la clave foránea en la relación.
     */
    private final ObjectProperty<Integer> clienteId;

    /**
     * Descripción textual del descuento (ej. "Descuento de verano", "Promoción de lanzamiento").
     */
    private final StringProperty descripcion;

    /**
     * El valor del descuento expresado en porcentaje (ej. 10.0 para un 10%).
     */
    private final ObjectProperty<Double> porcentaje;

    /**
     * Fecha a partir de la cual el descuento es válido.
     */
    private final ObjectProperty<LocalDate> fechaInicio;

    /**
     * Fecha hasta la cual el descuento es válido. El descuento caduca al final de este día.
     */
    private final ObjectProperty<LocalDate> fechaFin;

    /**
     * Estado del descuento, calculado a partir de la fecha de fin. 
     * {@code true} si está vigente, {@code false} si ha caducado.
     */
    private final ObjectProperty<Boolean> activo;

    /**
     * Propiedad utilizada exclusivamente por la interfaz de usuario para gestionar la selección
     * en tablas o listas (ej. mediante un {@code CheckBoxTableCell}). No se persiste en la base de datos.
     */
    private final BooleanProperty seleccionado;

    /**
     * Formateador de fecha estándar para presentar las fechas en un formato legible (dd-MM-yyyy).
     */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    // --- CONSTRUCTORES ---

    /**
     * Constructor para crear un nuevo Descuento que aún no ha sido guardado en la base de datos.
     * El ID se inicializa a {@code null} y el estado 'activo' se calcula automáticamente.
     *
     * @param clienteId El ID del cliente al que se asocia el descuento.
     * @param descripcion La descripción del descuento.
     * @param porcentaje El porcentaje a aplicar.
     * @param fechaInicio La fecha de inicio de validez.
     * @param fechaFin La fecha de fin de validez.
     */
    public Descuento(Integer clienteId, String descripcion, Double porcentaje, LocalDate fechaInicio, LocalDate fechaFin) {
        this.id = new SimpleObjectProperty<>(null); // El ID será asignado por la BD al guardar.
        this.clienteId = new SimpleObjectProperty<>(clienteId);
        this.descripcion = new SimpleStringProperty(descripcion);
        this.porcentaje = new SimpleObjectProperty<>(porcentaje);
        this.fechaInicio = new SimpleObjectProperty<>(fechaInicio);
        this.fechaFin = new SimpleObjectProperty<>(fechaFin);
        this.activo = new SimpleObjectProperty<>(calcularActivo(fechaFin)); // El estado se autocalcula.
        this.seleccionado = new SimpleBooleanProperty(false); // Por defecto, no está seleccionado en la UI.
    }

    /**
     * Constructor para reconstruir un Descuento a partir de los datos obtenidos de la base de datos.
     * Se utiliza para crear el objeto en memoria una vez que ya existe un registro persistido.
     *
     * @param id El ID único del descuento.
     * @param clienteId El ID del cliente asociado.
     * @param descripcion La descripción del descuento.
     * @param porcentaje El porcentaje del descuento.
     * @param fechaInicio La fecha de inicio de validez.
     * @param fechaFin La fecha de fin de validez.
     */
    public Descuento(Integer id, Integer clienteId, String descripcion, Double porcentaje, LocalDate fechaInicio, LocalDate fechaFin) {
        this.id = new SimpleObjectProperty<>(id);
        this.clienteId = new SimpleObjectProperty<>(clienteId);
        this.descripcion = new SimpleStringProperty(descripcion);
        this.porcentaje = new SimpleObjectProperty<>(porcentaje);
        this.fechaInicio = new SimpleObjectProperty<>(fechaInicio);
        this.fechaFin = new SimpleObjectProperty<>(fechaFin);
        this.activo = new SimpleObjectProperty<>(calcularActivo(fechaFin));
        this.seleccionado = new SimpleBooleanProperty(false);
    }
    
    /**
     * Constructor para reconstruir un Descuento desde la base de datos, incluyendo el estado 'activo'.
     * Este constructor es útil si el estado se almacena explícitamente en la BD, aunque el modelo
     * también permite calcularlo dinámicamente.
     *
     * @param id El ID único del descuento.
     * @param clienteId El ID del cliente asociado.
     * @param descripcion La descripción del descuento.
     * @param porcentaje El porcentaje del descuento.
     * @param fechaInicio La fecha de inicio de validez.
     * @param fechaFin La fecha de fin de validez.
     * @param activo El estado pre-calculado o almacenado del descuento.
     */
    public Descuento(Integer id, Integer clienteId, String descripcion, Double porcentaje, LocalDate fechaInicio, LocalDate fechaFin, boolean activo) {
        this.id = new SimpleObjectProperty<>(id);
        this.clienteId = new SimpleObjectProperty<>(clienteId);
        this.descripcion = new SimpleStringProperty(descripcion);
        this.porcentaje = new SimpleObjectProperty<>(porcentaje);
        this.fechaInicio = new SimpleObjectProperty<>(fechaInicio);
        this.fechaFin = new SimpleObjectProperty<>(fechaFin);
        this.activo = new SimpleObjectProperty<>(activo);
        this.seleccionado = new SimpleBooleanProperty(false);
    }

    // --- GETTERS, SETTERS Y MÉTODOS DE PROPIEDAD ---

    public Integer getId() { return id.get(); }
    public void setId(Integer id) { this.id.set(id); }
    public ObjectProperty<Integer> idProperty() { return id; }

    public Integer getClienteId() { return clienteId.get(); }
    public void setClienteId(Integer clienteId) { this.clienteId.set(clienteId); }
    public ObjectProperty<Integer> clienteIdProperty() { return clienteId; }

    public String getDescripcion() { return descripcion.get(); }
    public void setDescripcion(String descripcion) { this.descripcion.set(descripcion); }
    public StringProperty descripcionProperty() { return descripcion; }

    public Double getPorcentaje() { return porcentaje.get(); }
    public void setPorcentaje(Double porcentaje) { this.porcentaje.set(porcentaje); }
    public ObjectProperty<Double> porcentajeProperty() { return porcentaje; }

    public LocalDate getFechaInicio() { return fechaInicio.get(); }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio.set(fechaInicio); }
    public ObjectProperty<LocalDate> fechaInicioProperty() { return fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin.get(); }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin.set(fechaFin); }
    public ObjectProperty<LocalDate> fechaFinProperty() { return fechaFin; }

    public Boolean isActivo() { return activo.get(); }
    public void setActivo(Boolean activo) { this.activo.set(activo); }
    public ObjectProperty<Boolean> activoProperty() { return activo; }

    public boolean isSeleccionado() { return seleccionado.get(); }
    public void setSeleccionado(boolean seleccionado) { this.seleccionado.set(seleccionado); }
    public BooleanProperty seleccionadoProperty() { return seleccionado; }

    // --- MÉTODOS DE FORMATO PARA LA UI ---

    /**
     * Devuelve la fecha de inicio formateada como un String (dd-MM-yyyy).
     * @return La fecha formateada o un string vacío si la fecha es nula.
     */
    public String getFechaInicioFormatted() {
        if (getFechaInicio() != null) {
            return DATE_FORMATTER.format(getFechaInicio());
        }
        return "";
    }

    /**
     * Devuelve la fecha de fin formateada como un String (dd-MM-yyyy).
     * @return La fecha formateada o un string vacío si la fecha es nula.
     */
    public String getFechaFinFormatted() {
        if (getFechaFin() != null) {
            return DATE_FORMATTER.format(getFechaFin());
        }
        return "";
    }

    // --- LÓGICA DE NEGOCIO ---

    /**
     * Calcula si el descuento está activo comparando su fecha de fin con la fecha actual.
     * Un descuento es inactivo si su fecha de fin es anterior a hoy.
     * @param fechaFin La fecha de fin del descuento.
     * @return {@code false} si la fecha ya ha pasado, {@code true} en caso contrario.
     */
    private Boolean calcularActivo(LocalDate fechaFin) {
        // Si la fecha de fin no es nula y es anterior a la fecha de hoy, el descuento está caducado.
        if (fechaFin != null && fechaFin.isBefore(LocalDate.now())) {
            return false; // Caducado
        }
        return true; // Activo
    }

    /**
     * Permite recalcular y actualizar el estado 'activo' del descuento en cualquier momento.
     * Útil si la aplicación se mantiene abierta durante largos periodos y un descuento puede caducar
     * mientras el programa está en ejecución.
     */
    public void actualizarActivo() {
        this.activo.set(calcularActivo(this.getFechaFin()));
    }
    
    /**
     * Proporciona una representación textual del descuento, útil para debugging o logs.
     * @return Un string con la descripción y el porcentaje del descuento.
     */
    @Override
    public String toString() {
        return String.format("%s (%.2f%%)", getDescripcion(), getPorcentaje());
    }
}