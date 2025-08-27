package com.erp.model;

import com.erp.model.Producto;

/**
 * Representa una línea de detalle dentro de una Venta.
 * <p>
 * Cada instancia de esta clase corresponde a un producto específico que ha sido
 * vendido, incluyendo la cantidad y el precio unitario en el momento de la transacción.
 * Esto permite mantener un registro histórico preciso de los precios.
 */
public class DetalleVenta {
    
    /**
     * Identificador único autoincremental para el registro del detalle de venta en la base de datos.
     */
    private Integer id;
    
    /**
     * ID de la venta (de la tabla `ventas`) a la que pertenece este detalle.
     * Sirve como clave foránea para relacionar el detalle con su venta principal.
     */
    private Integer ventaId;
    
    /**
     * El objeto {@link Producto} que se vendió.
     * Contiene toda la información del producto.
     */
    private Producto producto;
    
    /**
     * La cantidad de unidades del producto que se vendieron.
     */
    private Integer cantidad;
    
    /**
     * El precio de una sola unidad del producto en el momento exacto de la venta.
     * Se almacena aquí para evitar que futuros cambios de precio en el producto afecten
     * a los registros de ventas pasadas.
     */
    private double precioUnitario;

    /**
     * Constructor para crear una nueva instancia de DetalleVenta.
     *
     * @param id Identificador único del detalle. Generalmente es null al crear un nuevo detalle antes de guardarlo en la BD.
     * @param ventaId ID de la venta a la que pertenece.
     * @param producto El producto asociado a este detalle.
     * @param cantidad La cantidad de producto vendido.
     * @param precioUnitario El precio unitario del producto en el momento de la venta.
     */
    public DetalleVenta(Integer id, Integer ventaId, Producto producto, Integer cantidad, double precioUnitario) {
        this.id = id;
        this.ventaId = ventaId;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    // --- Getters y Setters ---

    /**
     * Obtiene el identificador único del detalle de venta.
     * @return El ID del detalle de venta.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Establece el identificador único del detalle de venta.
     * @param id El nuevo ID para el detalle de venta.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Obtiene el ID de la venta a la que pertenece este detalle.
     * @return El ID de la venta.
     */
    public Integer getVentaId() {
        return ventaId;
    }

    /**
     * Establece el ID de la venta a la que pertenece este detalle.
     * @param ventaId El nuevo ID de la venta.
     */
    public void setVentaId(Integer ventaId) {
        this.ventaId = ventaId;
    }

    /**
     * Obtiene el producto asociado a este detalle de venta.
     * @return El objeto {@link Producto}.
     */
    public Producto getProducto() {
        return producto;
    }

    /**
     * Establece el producto asociado a este detalle de venta.
     * @param producto El nuevo objeto {@link Producto}.
     */
    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    /**
     * Obtiene la cantidad del producto vendido.
     * @return La cantidad del producto.
     */
    public Integer getCantidad() {
        return cantidad;
    }

    /**
     * Establece la cantidad del producto vendido.
     * @param cantidad La nueva cantidad del producto.
     */
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * Obtiene el precio unitario del producto en el momento de la venta.
     * @return El precio unitario.
     */
    public double getPrecioUnitario() {
        return precioUnitario;
    }

    /**
     * Establece el precio unitario del producto.
     * @param precioUnitario El nuevo precio unitario.
     */
    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    /**
     * Calcula y devuelve el subtotal para esta línea de detalle.
     * El cálculo es {@code cantidad * precioUnitario}.
     * 
     * @return El subtotal del detalle de venta.
     */
    public double getSubTotal() {
        // El subtotal se calcula dinámicamente y no se almacena en la BD
        // para asegurar que siempre sea consistente con la cantidad y el precio.
        return cantidad * precioUnitario;
    }

    /**
     * Obtiene el nombre del producto asociado a este detalle de venta.
     * @return El nombre del producto.
     */
    public String getNombreProducto() {
        return producto != null ? producto.getNombre() : "";
    }
}