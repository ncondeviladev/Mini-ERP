package com.erp.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class Venta {
    
    private Integer id;
    private Cliente cliente;
    private Descuento descuento;
    private List<DetalleVenta> detalleVenta;
    private LocalDate fecha;
    private double total;
    
    public Venta(Integer id, Cliente cliente, Descuento descuento, List<DetalleVenta> detalleVenta, LocalDate fecha, double total) {
        this.id = id;
        this.cliente = cliente;
        this.descuento = descuento;
        this.detalleVenta = detalleVenta != null ? detalleVenta : new ArrayList<>();
        this.fecha = fecha;
        this.total = total;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Descuento getDescuento() {
        return descuento;
    }

    public void setDescuento(Descuento descuento) {
        this.descuento = descuento;
    }

    public List<DetalleVenta> getDetalleVenta() {
        return detalleVenta;
    }

    public void setDetalleVenta(List<DetalleVenta> detalleVenta) {
        this.detalleVenta = detalleVenta;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
