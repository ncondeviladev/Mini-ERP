package com.erp.model;

public class DetalleVenta {
    
    private Integer id;
    private Integer ventaId;
    private Producto producto;
    private Integer cantidad;
    private double precioUnitario;

    public DetalleVenta(Integer id, Integer ventaId, Producto producto, Integer cantidad, double precioUnitario) {
        this.id = id;
        this.ventaId = ventaId;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVentaId() {
        return ventaId;
    }

    public void setVentaId(Integer ventaId) {
        this.ventaId = ventaId;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public double getSubTotal(){
        return cantidad * precioUnitario;
    }
}
