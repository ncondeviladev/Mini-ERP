package com.erp.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa una transacción de venta completa en el sistema.
 * <p>
 * Esta clase agrupa toda la información de una venta, incluyendo el cliente,
 * la fecha, el importe total, y las listas de productos vendidos ({@link DetalleVenta})
 * y descuentos aplicados ({@link Descuento}).
 */
public class Venta {
    
    /**
     * Identificador único de la venta en la base de datos.
     */
    private Integer id;
    
    /**
     * El {@link Cliente} que realiza la compra.
     */
    private Cliente cliente;
    
    /**
     * Lista de {@link Descuento}s que se han aplicado a esta venta.
     * Puede estar vacía si no se aplicaron descuentos.
     */
    private List<Descuento> descuentos;
    
    /**
     * Lista de {@link DetalleVenta} que contiene cada uno de los productos vendidos
     * en esta transacción, con su cantidad y precio.
     */
    private List<DetalleVenta> detalleVenta;
    
    /**
     * La fecha en la que se efectuó la venta.
     */
    private LocalDate fecha;
    
    /**
     * El importe final de la venta, calculado tras aplicar los descuentos correspondientes.
     */
    private double total;
    
    /**
     * Constructor completo para crear una instancia de Venta con todos sus atributos.
     *
     * @param id Identificador único de la venta. Usar null para ventas nuevas antes de persistir.
     * @param cliente El cliente asociado a la venta.
     * @param descuentos Lista de descuentos aplicados.
     * @param detalleVenta Lista de los detalles de productos vendidos.
     * @param fecha La fecha de la transacción.
     * @param total El importe total y final de la venta.
     */
    public Venta(Integer id, Cliente cliente, List<Descuento> descuentos, List<DetalleVenta> detalleVenta, LocalDate fecha, double total) {
        this.id = id;
        this.cliente = cliente;
        // Aseguramos que las listas nunca sean nulas para evitar NullPointerException
        this.descuentos = (descuentos != null) ? descuentos : new ArrayList<>();
        this.detalleVenta = (detalleVenta != null) ? detalleVenta : new ArrayList<>();
        this.fecha = fecha;
        this.total = total;
    }

    /**
     * Constructor de conveniencia para crear una Venta sin descuentos iniciales.
     * Internamente, inicializa la lista de descuentos como una lista vacía.
     *
     * @param id Identificador único de la venta.
     * @param cliente El cliente asociado a la venta.
     * @param detalleVenta Lista de los detalles de productos vendidos.
     * @param fecha La fecha de la transacción.
     * @param total El importe total y final de la venta.
     */
    public Venta(Integer id, Cliente cliente, List<DetalleVenta> detalleVenta, LocalDate fecha, double total) {
        // Llama al constructor principal con una lista de descuentos vacía.
        this(id, cliente, new ArrayList<>(), detalleVenta, fecha, total);
    }

    // --- Getters y Setters ---

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
     * Obtiene el cliente de la venta.
     * @return El objeto {@link Cliente}.
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
     * Obtiene la lista de descuentos aplicados.
     * @return Una lista de {@link Descuento}.
     */
    public List<Descuento> getDescuentos() {
        return descuentos;
    }

    /**
     * Establece la lista de descuentos aplicados.
     * @param descuentos La nueva lista de {@link Descuento}.
     */
    public void setDescuentos(List<Descuento> descuentos) {
        this.descuentos = descuentos;
    }

    /**
     * Obtiene la lista de detalles de la venta (productos).
     * @return Una lista de {@link DetalleVenta}.
     */
    public List<DetalleVenta> getDetalleVenta() {
        return detalleVenta;
    }

    /**
     * Establece la lista de detalles de la venta (productos).
     * @param detalleVenta La nueva lista de {@link DetalleVenta}.
     */
    public void setDetalleVenta(List<DetalleVenta> detalleVenta) {
        this.detalleVenta = detalleVenta;
    }

    /**
     * Obtiene la fecha de la venta.
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
     * Obtiene el total de la venta.
     * @return El importe total.
     */
    public double getTotal() {
        return total;
    }

    /**
     * Establece el total de la venta.
     * @param total El nuevo importe total.
     */
    public void setTotal(double total) {
        this.total = total;
    }
}