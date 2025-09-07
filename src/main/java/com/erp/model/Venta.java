package com.erp.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa una transacción de venta completa en el sistema.
 * 
 * <p>Esta clase es un objeto central que agrupa toda la información relacionada con una
 * única transacción de venta. Actúa como un contenedor para el cliente que realiza la compra,
 * la fecha de la transacción, el importe total final y, lo más importante, las listas
 * de productos vendidos y los descuentos que se aplicaron.</p>
 * 
 * <p>Es la entidad principal que se persiste en la base de datos para registrar una venta
 * y sus detalles asociados.</p>
 *
 * @see DetalleVenta
 * @see Cliente
 * @see Descuento
 * @see com.erp.dao.VentaDAO
 */
public class Venta {
    
    /**
     * Identificador único de la venta en la base de datos (clave primaria).
     */
    private Integer id;
    
    /**
     * El objeto {@link Cliente} que realizó la compra.
     */
    private Cliente cliente;
    
    /**
     * Una lista de los objetos {@link Descuento} que se aplicaron en esta venta.
     * La lista puede estar vacía si no se aplicó ningún descuento.
     */
    private List<Descuento> descuentos;
    
    /**
     * Una lista de {@link DetalleVenta} que contiene cada una de las líneas de producto
     * de esta transacción, incluyendo la cantidad y el precio de cada una.
     */
    private List<DetalleVenta> detalleVenta;
    
    /**
     * La fecha en la que se registró la transacción de venta.
     */
    private LocalDate fecha;
    
    /**
     * El importe final y total de la venta, después de haber aplicado los descuentos
     * y calculado los impuestos correspondientes.
     */
    private double total;
    
    /**
     * Constructor completo para crear una instancia de Venta con todos sus atributos.
     * Este constructor es ideal para reconstruir un objeto Venta desde la base de datos.
     * <p>
     * Realiza una comprobación para asegurar que las listas de descuentos y detalles
     * nunca sean nulas, inicializándolas como listas vacías si es necesario para
     * prevenir {@code NullPointerException}.
     * </p>
     *
     * @param id Identificador único de la venta. Usar {@code null} para ventas nuevas antes de persistir.
     * @param cliente El cliente asociado a la venta.
     * @param descuentos La lista de descuentos aplicados.
     * @param detalleVenta La lista de los detalles de productos vendidos.
     * @param fecha La fecha de la transacción.
     * @param total El importe total y final de la venta.
     */
    public Venta(Integer id, Cliente cliente, List<Descuento> descuentos, List<DetalleVenta> detalleVenta, LocalDate fecha, double total) {
        this.id = id;
        this.cliente = cliente;
        // Se asegura que las listas nunca sean nulas para evitar NullPointerExceptions en el futuro.
        this.descuentos = (descuentos != null) ? descuentos : new ArrayList<>();
        this.detalleVenta = (detalleVenta != null) ? detalleVenta : new ArrayList<>();
        this.fecha = fecha;
        this.total = total;
    }

    /**
     * Constructor de conveniencia para crear una Venta sin descuentos iniciales.
     * Delega en el constructor principal, pasándole una lista de descuentos vacía.
     * Es útil para simplificar la creación de ventas donde no se aplican descuentos.
     *
     * @param id Identificador único de la venta.
     * @param cliente El cliente asociado a la venta.
     * @param detalleVenta La lista de los detalles de productos vendidos.
     * @param fecha La fecha de la transacción.
     * @param total El importe total y final de la venta.
     */
    public Venta(Integer id, Cliente cliente, List<DetalleVenta> detalleVenta, LocalDate fecha, double total) {
        // Llama al constructor principal con una lista de descuentos vacía por defecto.
        this(id, cliente, new ArrayList<>(), detalleVenta, fecha, total);
    }

    // --- GETTERS Y SETTERS ---

    /**
     * Obtiene el ID de la venta.
     * @return El ID de la venta.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Establece el ID de la venta.
     * @param id El nuevo ID de la venta.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Obtiene el cliente que realizó la venta.
     * @return El objeto {@link Cliente} asociado.
     */
    public Cliente getCliente() {
        return cliente;
    }

    /**
     * Establece el cliente de la venta.
     * @param cliente El nuevo objeto {@link Cliente}.
     */
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    /**
     * Obtiene la lista de descuentos que se aplicaron en la venta.
     * @return Una {@code List} de {@link Descuento}. No será nula.
     */
    public List<Descuento> getDescuentos() {
        return descuentos;
    }

    /**
     * Establece la lista de descuentos aplicados en la venta.
     * @param descuentos La nueva lista de {@link Descuento}.
     */
    public void setDescuentos(List<Descuento> descuentos) {
        this.descuentos = descuentos;
    }

    /**
     * Obtiene la lista de detalles (líneas de producto) de la venta.
     * @return Una {@code List} de {@link DetalleVenta}. No será nula.
     */
    public List<DetalleVenta> getDetalleVenta() {
        return detalleVenta;
    }

    /**
     * Establece la lista de detalles de la venta.
     * @param detalleVenta La nueva lista de {@link DetalleVenta}.
     */
    public void setDetalleVenta(List<DetalleVenta> detalleVenta) {
        this.detalleVenta = detalleVenta;
    }

    /**
     * Obtiene la fecha en que se realizó la venta.
     * @return La fecha de la venta.
     */
    public LocalDate getFecha() {
        return fecha;
    }

    /**
     * Establece la fecha de la venta.
     * @param fecha La nueva fecha de la venta.
     */
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    /**
     * Obtiene el importe total final de la venta.
     * @return El importe total.
     */
    public double getTotal() {
        return total;
    }

    /**
     * Establece el importe total final de la venta.
     * @param total El nuevo importe total.
     */
    public void setTotal(double total) {
        this.total = total;
    }
}
