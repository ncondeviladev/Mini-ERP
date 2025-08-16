package com.erp.model;

/**
 * Representa un detalle de línea de una venta, especificando un producto,
 * su cantidad y el precio unitario al momento de la venta.
 */
public class DetalleVenta {
    
    /**
     * Identificador único del detalle de venta.
     */
    private Integer id;
    /**
     * ID de la venta a la que pertenece este detalle.
     */
    private Integer ventaId;
    /**
     * El producto que se vendió en este detalle.
     */
    private Producto producto;
    /**
     * La cantidad del producto vendido.
     */
    private Integer cantidad;
    /**
     * El precio unitario del producto al momento de la venta.
     */
    private double precioUnitario;

    /**
     * Constructor para crear una nueva instancia de DetalleVenta.
     *
     * @param id Identificador único del detalle de venta. Puede ser null para nuevos detalles.
     * @param ventaId ID de la venta a la que pertenece este detalle.
     * @param producto El producto asociado a este detalle.
     * @param cantidad La cantidad del producto vendido.
     * @param precioUnitario El precio unitario del producto al momento de la venta.
     */
    public DetalleVenta(Integer id, Integer ventaId, Producto producto, Integer cantidad, double precioUnitario) {
        this.id = id;
        this.ventaId = ventaId;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    /**
     * Obtiene el identificador único del detalle de venta.
     * @return El ID del detalle de venta.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Establece el identificador único del detalle de venta.
     * @param id El nuevo ID del detalle de venta.
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
     * @return El objeto Producto.
     */
    public Producto getProducto() {
        return producto;
    }

    /**
     * Establece el producto asociado a este detalle de venta.
     * @param producto El nuevo objeto Producto.
     */
    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    /**
     * Obtiene la cantidad del producto vendido en este detalle.
     * @return La cantidad del producto.
     */
    public Integer getCantidad() {
        return cantidad;
    }

    /**
     * Establece la cantidad del producto vendido en este detalle.
     * @param cantidad La nueva cantidad del producto.
     */
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * Obtiene el precio unitario del producto al momento de la venta.
     * @return El precio unitario.
     */
    public double getPrecioUnitario() {
        return precioUnitario;
    }

    /**
     * Establece el precio unitario del producto al momento de la venta.
     * @param precioUnitario El nuevo precio unitario.
     */
    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    /**
     * Calcula y obtiene el subtotal de este detalle de venta (cantidad * precioUnitario).
     * @return El subtotal del detalle de venta.
     */
    public double getSubTotal(){
        return cantidad * precioUnitario;
    }
}

