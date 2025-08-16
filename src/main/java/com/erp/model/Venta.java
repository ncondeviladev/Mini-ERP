package com.erp.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


/**
 * Representa una venta realizada en el sistema.
 * Contiene información sobre el cliente, los descuentos aplicados, los productos vendidos (detalles de venta),
 * la fecha de la venta y el total.
 */
public class Venta {
    
    /**
     * Identificador único de la venta.
     */
    private Integer id;
    /**
     * Cliente asociado a esta venta.
     */
    private Cliente cliente;
    /**
     * Lista de descuentos aplicados a esta venta. Permite múltiples descuentos acumulativos.
     */
    private List<Descuento> descuentos;
    /**
     * Lista de productos y sus cantidades vendidas en esta venta.
     */
    private List<DetalleVenta> detalleVenta;
    /**
     * Fecha en la que se realizó la venta.
     */
    private LocalDate fecha;
    /**
     * Importe total de la venta después de aplicar todos los descuentos.
     */
    private double total;
    
    /**
     * Constructor completo para crear una nueva instancia de Venta.
     *
     * @param id Identificador único de la venta. Puede ser null para nuevas ventas.
     * @param cliente El cliente que realizó la venta.
     * @param descuentos Lista de descuentos aplicados a la venta.
     * @param detalleVenta Lista de detalles de los productos vendidos.
     * @param fecha La fecha en que se realizó la venta.
     * @param total El importe total de la venta.
     */
    public Venta(Integer id, Cliente cliente, List<Descuento> descuentos, List<DetalleVenta> detalleVenta, LocalDate fecha, double total) {
        this.id = id;
        this.cliente = cliente;
        this.descuentos = descuentos != null ? descuentos : new ArrayList<>(); // Initialize list
        this.detalleVenta = detalleVenta != null ? detalleVenta : new ArrayList<>();
        this.fecha = fecha;
        this.total = total;
    }

    /**
     * Constructor de conveniencia para crear una nueva instancia de Venta sin descuentos iniciales.
     * Los descuentos se inicializan como una lista vacía.
     *
     * @param id Identificador único de la venta. Puede ser null para nuevas ventas.
     * @param cliente El cliente que realizó la venta.
     * @param detalleVenta Lista de detalles de los productos vendidos.
     * @param fecha La fecha en que se realizó la venta.
     * @param total El importe total de la venta.
     */
    public Venta(Integer id, Cliente cliente, List<DetalleVenta> detalleVenta, LocalDate fecha, double total) {
        this(id, cliente, new ArrayList<>(), detalleVenta, fecha, total); // Call main constructor with empty list
    }

    /**
     * Obtiene el identificador único de la venta.
     * @return El ID de la venta.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Establece el identificador único de la venta.
     * @param id El nuevo ID de la venta.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Obtiene el cliente asociado a esta venta.
     * @return El objeto Cliente de la venta.
     */
    public Cliente getCliente() {
        return cliente;
    }

    /**
     * Establece el cliente asociado a esta venta.
     * @param cliente El nuevo objeto Cliente para la venta.
     */
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    /**
     * Obtiene la lista de descuentos aplicados a esta venta.
     * @return Una lista de objetos Descuento.
     */
    public List<Descuento> getDescuentos() {
        return descuentos;
    }

    /**
     * Establece la lista de descuentos aplicados a esta venta.
     * @param descuentos La nueva lista de objetos Descuento para la venta.
     */
    public void setDescuentos(List<Descuento> descuentos) {
        this.descuentos = descuentos;
    }

    /**
     * Obtiene la lista de detalles de los productos vendidos en esta venta.
     * @return Una lista de objetos DetalleVenta.
     */
    public List<DetalleVenta> getDetalleVenta() {
        return detalleVenta;
    }

    /**
     * Establece la lista de detalles de los productos vendidos en esta venta.
     * @param detalleVenta La nueva lista de objetos DetalleVenta para la venta.
     */
    public void setDetalleVenta(List<DetalleVenta> detalleVenta) {
        this.detalleVenta = detalleVenta;
    }

    /**
     * Obtiene la fecha en la que se realizó la venta.
     * @return La fecha de la venta.
     */
    public LocalDate getFecha() {
        return fecha;
    }

    /**
     * Establece la fecha en la que se realizó la venta.
     * @param fecha La nueva fecha de la venta.
     */
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    /**
     * Obtiene el importe total de la venta.
     * @return El total de la venta.
     */
    public double getTotal() {
        return total;
    }

    /**
     * Establece el importe total de la venta.
     * @param total El nuevo total de la venta.
     */
    public void setTotal(double total) {
        this.total = total;
    }
}

