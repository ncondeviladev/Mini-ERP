package com.erp.model;

/**
 * Representa una línea de detalle dentro de una {@link Venta}.
 * 
 * <p>Cada instancia de esta clase corresponde a un único producto que ha sido
 * vendido como parte de una transacción más grande. Su propósito es capturar el
 * estado de esa parte de la venta en un momento concreto.</p>
 * 
 * <p><b>Importancia del Diseño:</b></p>
 * <ul>
 *     <li><b>Historial de Precios:</b> Almacena {@code precioUnitario} directamente.
 *     Esto es crucial porque si el precio de un {@link Producto} cambia en el futuro,
 *     los registros de ventas pasadas no se verán alterados, garantizando la 
 *     integridad de los informes históricos.</li>
 *     <li><b>Granularidad:</b> Permite un registro detallado de cada transacción, 
 *     facilitando la gestión de inventario, análisis de ventas por producto, etc.</li>
 *     <li><b>Relación con Venta:</b> Se vincula a una {@code Venta} principal a través 
 *     de {@code ventaId}, formando una relación clásica de maestro-detalle.</li>
 * </ul>
 *
 * @see Venta
 * @see Producto
 * @see com.erp.dao.VentaDAO
 */
public class DetalleVenta {
    
    /**
     * Identificador único autoincremental para este registro de detalle en la base de datos.
     * Es la clave primaria de la tabla de detalles de venta.
     */
    private Integer id;
    
    /**
     * ID de la {@link Venta} a la que pertenece este detalle. 
     * Actúa como clave foránea para establecer la relación con la tabla de ventas.
     */
    private Integer ventaId;
    
    /**
     * El objeto {@link Producto} que se vendió en esta línea de detalle.
     * Contiene toda la información del producto (nombre, descripción, etc.).
     */
    private Producto producto;
    
    /**
     * La cantidad de unidades de este producto que se vendieron en la transacción.
     */
    private Integer cantidad;
    
    /**
     * El precio de una sola unidad del producto en el momento exacto de la venta.
     * Se almacena aquí para preservar el valor histórico y evitar que futuros cambios
     * de precio en la ficha del producto afecten a este registro.
     */
    private double precioUnitario;

    /**
     * Constructor para crear una nueva instancia de DetalleVenta.
     *
     * @param id El identificador único del detalle. Suele ser {@code null} al crear un nuevo detalle antes de persistirlo.
     * @param ventaId El ID de la venta a la que este detalle pertenece.
     * @param producto El producto asociado a esta línea de venta.
     * @param cantidad La cantidad de unidades vendidas.
     * @param precioUnitario El precio unitario del producto en el momento de la transacción.
     */
    public DetalleVenta(Integer id, Integer ventaId, Producto producto, Integer cantidad, double precioUnitario) {
        this.id = id;
        this.ventaId = ventaId;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    // --- GETTERS Y SETTERS ---

    /**
     * Obtiene el identificador único de esta línea de detalle.
     * @return El ID del detalle de venta.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Establece el identificador único de esta línea de detalle.
     * @param id El nuevo ID para el detalle de venta.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Obtiene el ID de la venta principal a la que pertenece este detalle.
     * @return El ID de la venta.
     */
    public Integer getVentaId() {
        return ventaId;
    }

    /**
     * Establece el ID de la venta principal a la que pertenece este detalle.
     * @param ventaId El nuevo ID de la venta.
     */
    public void setVentaId(Integer ventaId) {
        this.ventaId = ventaId;
    }

    /**
     * Obtiene el objeto Producto completo asociado a este detalle de venta.
     * @return El {@link Producto} vendido.
     */
    public Producto getProducto() {
        return producto;
    }

    /**
     * Establece el producto asociado a este detalle de venta.
     * @param producto El nuevo {@link Producto}.
     */
    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    /**
     * Obtiene la cantidad de unidades vendidas de este producto.
     * @return La cantidad del producto.
     */
    public Integer getCantidad() {
        return cantidad;
    }

    /**
     * Establece la cantidad de unidades vendidas de este producto.
     * @param cantidad La nueva cantidad del producto.
     */
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * Obtiene el precio unitario del producto en el momento en que se realizó la venta.
     * @return El precio unitario histórico.
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

    // --- MÉTODOS CALCULADOS ---

    /**
     * Calcula y devuelve el subtotal para esta línea de detalle.
     * El cálculo es siempre {@code cantidad * precioUnitario}.
     * <p>
     * Este valor no se almacena en la base de datos, se calcula dinámicamente
     * para asegurar que siempre sea consistente con la cantidad y el precio, 
     * evitando la redundancia de datos.
     * </p>
     * @return El subtotal del detalle de venta (cantidad * precio).
     */
    public double getSubTotal() {
        return cantidad * precioUnitario;
    }

    /**
     * Método de conveniencia para obtener el nombre del producto directamente.
     * Evita tener que hacer {@code detalle.getProducto().getNombre()} en otras partes del código.
     * @return El nombre del producto, o una cadena vacía si el producto es nulo.
     */
    public String getNombreProducto() {
        return producto != null ? producto.getNombre() : "";
    }
}
