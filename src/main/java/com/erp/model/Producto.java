package com.erp.model;

/**
 * Clase modelo que representa un Producto en el inventario del sistema ERP.
 * 
 * <p>Esta clase es un POJO (Plain Old Java Object) que encapsula todos los 
 * atributos de un producto, como su nombre, descripción, precio y stock disponible.
 * A diferencia de otros modelos en este proyecto, no utiliza propiedades de JavaFX,
 * ya que su información se considera más estática en el contexto de las vistas
 * donde se presenta.</p>
 *
 * @see com.erp.dao.ProductoDAO
 * @see com.erp.controller.ProductoController
 * @author Noé
 */
public class Producto {

    /**
     * Identificador único del producto en la base de datos.
     * Es la clave primaria y se genera automáticamente al insertar un nuevo producto.
     */
    private Integer id;

    /**
     * El nombre comercial del producto.
     */
    private String nombre;

    /**
     * Un texto detallado que describe las características del producto.
     */
    private String descripcion;

    /**
     * La categoría a la que pertenece el producto (ej. "Electrónica", "Ropa", "Alimentación").
     * Útil para agrupar y filtrar productos.
     */
    private String categoria;

    /**
     * El precio de venta de una sola unidad del producto.
     */
    private double precioUnitario;

    /**
     * La cantidad de unidades de este producto que se encuentran disponibles en el inventario.
     */
    private Integer stock;

    /**
     * Constructor principal utilizado para reconstruir un objeto Producto a partir de
     * datos existentes, típicamente al recuperarlo de la base de datos.
     *
     * @param id El ID único del producto.
     * @param nombre El nombre del producto.
     * @param descripcion La descripción detallada del producto.
     * @param categoria La categoría a la que pertenece.
     * @param precioUnitario El precio de venta por unidad.
     * @param stock La cantidad de unidades disponibles en inventario.
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
     * Constructor secundario para crear una nueva instancia de Producto desde la aplicación
     * (por ejemplo, a través de un formulario de "nuevo producto").
     * El ID se establece explícitamente en {@code null}, indicando que es un objeto nuevo
     * que aún no ha sido persistido en la base de datos.
     *
     * @param nombre El nombre del nuevo producto.
     * @param descripcion La descripción del nuevo producto.
     * @param categoria La categoría del nuevo producto.
     * @param precioUnitario El precio del nuevo producto.
     * @param stock El stock inicial del nuevo producto.
     */
    public Producto(String nombre, String descripcion, String categoria, double precioUnitario, int stock) {
        this.id = null; // Se asigna null porque la BD generará el ID.
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.precioUnitario = precioUnitario;
        this.stock = stock;
    }


    // === MÉTODOS GETTERS Y SETTERS ===

    /**
     * Obtiene el ID del producto.
     * @return El identificador único del producto.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Establece el ID del producto.
     * @param id El nuevo identificador para el producto.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre del producto.
     * @return El nombre del producto.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del producto.
     * @param nombre El nuevo nombre para el producto.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la descripción del producto.
     * @return La descripción detallada del producto.
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripción del producto.
     * @param descripcion La nueva descripción para el producto.
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Obtiene la categoría del producto.
     * @return La categoría del producto.
     */
    public String getCategoria() {
        return categoria;
    }

    /**
     * Establece la categoría del producto.
     * @param categoria La nueva categoría para el producto.
     */
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    /**
     * Obtiene el precio unitario del producto.
     * @return El precio de venta por unidad.
     */
    public double getPrecioUnitario() {
        return precioUnitario;
    }

    /**
     * Establece el precio unitario del producto.
     * @param precioUnitario El nuevo precio de venta por unidad.
     */
    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    /**
     * Obtiene el stock disponible del producto.
     * @return La cantidad de unidades en inventario.
     */
    public Integer getStock() {
        return stock;
    }

    /**
     * Establece el stock disponible del producto.
     * @param stock La nueva cantidad de unidades en inventario.
     */
    public void setStock(Integer stock) {
        this.stock = stock;
    }
}