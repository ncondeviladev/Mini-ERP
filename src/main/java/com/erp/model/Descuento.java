package com.erp.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Clase modelo que representa un Descuento en el sistema ERP.
 * Utiliza propiedades de JavaFX para facilitar el enlace de datos con la UI (TableView).
 * Autor: Noé
 */
public class Descuento {

    private final ObjectProperty<Integer> id;
    private final ObjectProperty<Integer> clienteId;
    private final StringProperty descripcion;
    private final ObjectProperty<Double> porcentaje;
    private final ObjectProperty<LocalDate> fechaInicio;
    private final ObjectProperty<LocalDate> fechaFin;
    private final ObjectProperty<Boolean> activo; // true para "Activo", false para "Caducado"

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /**
     * Constructor para crear un nuevo Descuento (sin ID, se asignará en la BD).
     */
    public Descuento(Integer clienteId, String descripcion, Double porcentaje, LocalDate fechaInicio, LocalDate fechaFin) {
        this.id = new SimpleObjectProperty<>(null); // ID será asignado por la BD
        this.clienteId = new SimpleObjectProperty<>(clienteId);
        this.descripcion = new SimpleStringProperty(descripcion);
        this.porcentaje = new SimpleObjectProperty<>(porcentaje);
        this.fechaInicio = new SimpleObjectProperty<>(fechaInicio);
        this.fechaFin = new SimpleObjectProperty<>(fechaFin);
        this.activo = new SimpleObjectProperty<>(calcularActivo(fechaFin));
        
    }

    /**
     * Constructor para reconstruir un Descuento existente desde la base de datos.
     */
    public Descuento(Integer id, Integer clienteId, String descripcion, Double porcentaje, LocalDate fechaInicio, LocalDate fechaFin) {
        this.id = new SimpleObjectProperty<>(id);
        this.clienteId = new SimpleObjectProperty<>(clienteId);
        this.descripcion = new SimpleStringProperty(descripcion);
        this.porcentaje = new SimpleObjectProperty<>(porcentaje);
        this.fechaInicio = new SimpleObjectProperty<>(fechaInicio);
        this.fechaFin = new SimpleObjectProperty<>(fechaFin);
        this.activo = new SimpleObjectProperty<>(calcularActivo(fechaFin));
    }
    /**
     * Constructor para reconstruir un Descuento existente desde la base de datos con atributo activo.
     */
    public Descuento(Integer id, Integer clienteId, String descripcion, Double porcentaje, LocalDate fechaInicio, LocalDate fechaFin, boolean activo) {
        this.id = new SimpleObjectProperty<>(id);
        this.clienteId = new SimpleObjectProperty<>(clienteId);
        this.descripcion = new SimpleStringProperty(descripcion);
        this.porcentaje = new SimpleObjectProperty<>(porcentaje);
        this.fechaInicio = new SimpleObjectProperty<>(fechaInicio);
        this.fechaFin = new SimpleObjectProperty<>(fechaFin);
        this.activo = new SimpleObjectProperty<>(activo);
        
    }

    

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

    // --- Métodos de Formato ---

    public String getFechaInicioFormatted() {
        if (getFechaInicio() != null) {
            return DATE_FORMATTER.format(getFechaInicio());
        }
        return "";
    }

    public String getFechaFinFormatted() {
        if (getFechaFin() != null) {
            return DATE_FORMATTER.format(getFechaFin());
        }
        return "";
    }

    /**
     * Calcula si el descuento está activo basado en la fecha de fin.
     */
    private Boolean calcularActivo(LocalDate fechaFin) {
        if (fechaFin != null && fechaFin.isBefore(LocalDate.now())) {
            return false; // Caducado
        }
        return true; // Activo
    }

    /**
     * Actualiza el estado de activo del descuento. Útil para llamar periódicamente o al cargar.
     */
    public void actualizarActivo() {
        this.activo.set(calcularActivo(this.getFechaFin()));
    }
}