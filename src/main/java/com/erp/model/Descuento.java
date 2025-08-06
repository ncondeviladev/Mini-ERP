package com.erp.model;

import java.time.LocalDate;

public class Descuento {
    
    private Integer idDescuento;
    private Integer clienteId;
    private String descripcion;
    private double porcentaje;
    private LocalDate fechaInicio;
    private LocalDate fechaCaducidad;
    private Boolean estado;

    // Constructor vacío
    public Descuento() {
    }

    // Constructor con todos los parámetros
    public Descuento(Integer idDescuento, Integer clienteId, String descripcion, double porcentaje, LocalDate fechaInicio, LocalDate fechaCaducidad, Boolean estado) {
        this.idDescuento = idDescuento;
        this.clienteId = clienteId;
        this.descripcion = descripcion;
        this.porcentaje = porcentaje;
        this.fechaInicio = fechaInicio;
        this.fechaCaducidad = fechaCaducidad;
        this.estado = estado;
    }
    public Descuento( Integer clienteId, String descripcion, double porcentaje, LocalDate fechaInicio, LocalDate fechaCaducidad, Boolean estado) {
        this.clienteId = clienteId;
        this.descripcion = descripcion;
        this.porcentaje = porcentaje;
        this.fechaInicio = fechaInicio;
        this.fechaCaducidad = fechaCaducidad;
        this.estado = estado;
    }

    // Getters y Setters
    public Integer getIdDescuento() {
        return idDescuento;
    }

    public void setIdDescuento(Integer idDescuento) {
        this.idDescuento = idDescuento;
    }

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(double porcentaje) {
        this.porcentaje = porcentaje;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaCaducidad() {
        return fechaCaducidad;
    }

    public void setFechaCaducidad(LocalDate fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Descuento - " +
                "\n ID Descuento - " + idDescuento +
                "\n  ID Cliente - " + clienteId +
                "\n  Descripcion - " + descripcion +
                "\n  Porcentaje - " + porcentaje +
                "\n  Fecha Inicio - " + fechaInicio +
                "\n  Fecha Caducidad - " + fechaCaducidad +
                "\n  Estado - " + (estado ? "Activo" : "Inactivo");
    }

    
}
