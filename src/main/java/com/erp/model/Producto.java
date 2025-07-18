package com.erp.model;

/**
 * Clase modelo que representa un producto del sistema ERP.
 * Contiene propiedades básicas del producto y métodos de acceso.
 * Autor: Noé
 */
public class Producto {

    // 🆔 Identificador único del producto (autogenerado en BD)
    private Integer id;

    // 📝 Propiedades del producto
    private String nombre;
    private String descripcion;
    private String categoria;
    private double precioUnitario;
    private Integer stock;

    /**
     * Constructor principal usado para crear productos completos desde la base de datos.
     * @param id ID único del producto
     * @param nombre Nombre del producto
     * @param descripcion Descripción detallada
     * @param categoria Categoría asignada
     * @param precioUnitario Precio de venta por unidad
     * @param stock Cantidad disponible en inventario
     */
    public Producto(Integer id, String nombre, String descripcion, String categoria, double precioUnitario, Integer stock){
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.precioUnitario = precioUnitario;
        this.stock = stock;
    }

    /**
     * 
     */
    public Producto(String nombre, String descripcion, String categoria, double precioUnitario, int stock) {
    this.id = null; 
    this.nombre = nombre;
    this.descripcion = descripcion;
    this.categoria = categoria;
    this.precioUnitario = precioUnitario;
    this.stock = stock;
}


    // === MÉTODOS GETTERS Y SETTERS ===

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
