package com.erp.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.erp.db.SQLiteConnector;
import com.erp.model.Cliente;
import com.erp.model.Descuento;
import com.erp.model.DetalleVenta;
import com.erp.model.Producto;
import com.erp.model.Venta;

/**
 * Data Access Object (DAO) para gestionar la persistencia de las entidades {@link Venta}.
 * <p>
 * Proporciona métodos para realizar operaciones CRUD (Crear, Leer, Actualizar, Borrar)
 * sobre las ventas, sus detalles y las tablas relacionadas en la base de datos.
 * Maneja la lógica de transacciones para garantizar la integridad de los datos.
 */
public class VentaDAO {

    /**
     * Guarda una venta completa en la base de datos dentro de una única transacción.
     * <p>
     * Este método realiza los siguientes pasos de forma atómica:
     * <ol>
     *   <li>Inicia una transacción (desactiva el auto-commit).</li>
     *   <li>Inserta la cabecera de la venta en la tabla `ventas`.</li>
     *   <li>Recupera el ID autogenerado de la nueva venta.</li>
     *   <li>Inserta todos los detalles de la venta (productos) en `detalles_venta` usando el ID anterior.</li>
     *   <li>Inserta las referencias a los descuentos aplicados en `venta_descuentos`.</li>
     *   <li>Si todo tiene éxito, confirma la transacción (commit).</li>
     *   <li>Si ocurre cualquier error, revierte todos los cambios (rollback).</li>
     * </ol>
     *
     * @param venta El objeto {@link Venta} a persistir. Debe contener un cliente, fecha, total,
     *              una lista de detalles y, opcionalmente, una lista de descuentos.
     * @return {@code true} si la transacción se completó con éxito, {@code false} en caso de error.
     */
    public boolean guardarVenta(Venta venta) {
        String sqlVenta = "INSERT INTO ventas(cliente_id, fecha, total) VALUES(?, ?, ?)";
        String sqlDetalle = "INSERT INTO detalles_venta(venta_id, producto_id, cantidad, precio_unitario) VALUES(?, ?, ?, ?)";
        String sqlVentaDescuento = "INSERT INTO venta_descuentos(venta_id, descuento_id) VALUES(?, ?)";

        Connection conn = null;
        PreparedStatement pstmtVenta = null;
        PreparedStatement pstmtDetalle = null;
        PreparedStatement pstmtVentaDescuento = null;
        ResultSet rs = null;
        boolean exito = false;

        try {
            conn = SQLiteConnector.connect();
            // 1. Iniciar transacción
            conn.setAutoCommit(false);

            // 2. Insertar la cabecera de la venta
            pstmtVenta = conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS);
            pstmtVenta.setInt(1, venta.getCliente().getId());
            pstmtVenta.setDate(2, Date.valueOf(venta.getFecha())); // Conversión de LocalDate a sql.Date
            pstmtVenta.setDouble(3, venta.getTotal());
            pstmtVenta.executeUpdate();

            // 3. Obtener el ID generado para la venta
            rs = pstmtVenta.getGeneratedKeys();
            int ventaId = -1;
            if (rs.next()) {
                ventaId = rs.getInt(1);
                venta.setId(ventaId); // Actualizar el objeto Venta con su nuevo ID
            } else {
                // Si no obtenemos un ID, la inserción falló y no podemos continuar.
                throw new SQLException("No se pudo obtener el ID de la venta insertada.");
            }

            // 4. Insertar los detalles de la venta en un lote (batch)
            pstmtDetalle = conn.prepareStatement(sqlDetalle);
            for (DetalleVenta detalle : venta.getDetalleVenta()) {
                pstmtDetalle.setInt(1, ventaId);
                pstmtDetalle.setInt(2, detalle.getProducto().getId());
                pstmtDetalle.setInt(3, detalle.getCantidad());
                pstmtDetalle.setDouble(4, detalle.getPrecioUnitario());
                pstmtDetalle.addBatch(); // Añadir la sentencia al lote
            }
            pstmtDetalle.executeBatch(); // Ejecutar todas las sentencias del lote

            // 5. Insertar los descuentos asociados (si los hay) en otro lote
            if (venta.getDescuentos() != null && !venta.getDescuentos().isEmpty()) {
                pstmtVentaDescuento = conn.prepareStatement(sqlVentaDescuento);
                for (Descuento descuento : venta.getDescuentos()) {
                    pstmtVentaDescuento.setInt(1, ventaId);
                    pstmtVentaDescuento.setInt(2, descuento.getId());
                    pstmtVentaDescuento.addBatch();
                }
                pstmtVentaDescuento.executeBatch();
            }

            // 6. Si todo fue bien, confirmar la transacción
            conn.commit();
            exito = true;

        } catch (SQLException e) {
            System.err.println("Error al guardar la venta: " + e.getMessage());
            if (conn != null) {
                try {
                    // 7. Si algo falló, revertir todos los cambios
                    conn.rollback();
                    System.err.println("Transacción de venta revertida.");
                } catch (SQLException ex) {
                    System.err.println("Error al revertir la transacción: " + ex.getMessage());
                }
            }
        } finally {
            // 8. Cerrar todos los recursos en el orden inverso a su apertura
            try {
                if (rs != null) rs.close();
                if (pstmtVenta != null) pstmtVenta.close();
                if (pstmtDetalle != null) pstmtDetalle.close();
                if (pstmtVentaDescuento != null) pstmtVentaDescuento.close();
                if (conn != null) {
                    conn.setAutoCommit(true); // Restaurar el modo auto-commit
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
        return exito;
    }

    /**
     * Recupera todas las ventas de la base de datos.
     * <p>
     * Para cada venta, este método carga de forma anidada:
     * <ul>
     *   <li>La información del {@link Cliente} asociado.</li>
     *   <li>La lista de {@link Descuento}s aplicados.</li>
     *   <li>La lista completa de {@link DetalleVenta} con sus respectivos {@link Producto}s.</li>
     * </ul>
     * Este método puede ser intensivo en consultas si hay un gran número de ventas.
     *
     * @return Una lista de objetos {@link Venta} completamente inicializados.
     */
    public List<Venta> obtenerTodasLasVentas() {
        List<Venta> ventas = new ArrayList<>();
        String sqlVentas = "SELECT v.id, v.fecha, v.total, " +
                           "c.id AS cliente_id, c.nombre AS cliente_nombre, c.apellidos AS cliente_apellidos, " +
                           "c.razonSocial AS cliente_razonSocial, c.personaContacto AS cliente_personaContacto, " +
                           "c.tipoCliente AS cliente_tipoCliente, c.telefono AS cliente_telefono, " +
                           "c.email AS cliente_email, c.direccion AS cliente_direccion, c.cifnif AS cliente_cifnif, " +
                           "c.fechaAlta AS cliente_fechaAlta " +
                           "FROM ventas v JOIN clientes c ON v.cliente_id = c.id";

        String sqlDescuentos = "SELECT d.idDescuento, d.clienteId, d.descripcion, d.porcentaje, " +
                               "d.fechaInicio, d.fechaCaducidad, d.estado " +
                               "FROM descuentos d JOIN venta_descuentos vd ON d.idDescuento = vd.descuento_id " +
                               "WHERE vd.venta_id = ?";

        // CORRECCIÓN: Añadido p.categoria a la consulta
        String sqlDetalles = "SELECT dv.id, dv.cantidad, dv.precio_unitario, " +
                             "p.id AS producto_id, p.nombre AS producto_nombre, p.descripcion AS producto_descripcion, " +
                             "p.categoria AS producto_categoria, p.precioUnitario AS producto_precio, p.stock AS producto_stock " +
                             "FROM detalles_venta dv JOIN productos p ON dv.producto_id = p.id " +
                             "WHERE dv.venta_id = ?";

        Connection conn = null;
        PreparedStatement pstmtVentas = null, pstmtDescuentos = null, pstmtDetalles = null;
        ResultSet rsVentas = null, rsDescuentos = null, rsDetalles = null;

        try {
            conn = SQLiteConnector.connect();
            pstmtVentas = conn.prepareStatement(sqlVentas);
            rsVentas = pstmtVentas.executeQuery();

            // Iterar sobre cada registro de venta encontrado
            while (rsVentas.next()) {
                // 1. Reconstruir el objeto Cliente a partir de los datos de la venta
                Cliente cliente = construirClienteDesdeResultSet(rsVentas);

                // 2. Crear el objeto Venta principal (aún sin listas de detalles/descuentos)
                Venta venta = new Venta(
                    rsVentas.getInt("id"),
                    cliente,
                    new ArrayList<>(), // Inicializar lista de descuentos
                    new ArrayList<>(), // Inicializar lista de detalles
                    rsVentas.getDate("fecha").toLocalDate(),
                    rsVentas.getDouble("total")
                );

                // 3. Cargar los descuentos para esta venta específica
                pstmtDescuentos = conn.prepareStatement(sqlDescuentos);
                pstmtDescuentos.setInt(1, venta.getId());
                rsDescuentos = pstmtDescuentos.executeQuery();
                while (rsDescuentos.next()) {
                    Descuento descuento = construirDescuentoDesdeResultSet(rsDescuentos);
                    venta.getDescuentos().add(descuento);
                }
                rsDescuentos.close();
                pstmtDescuentos.close();

                // 4. Cargar los detalles de la venta (productos)
                pstmtDetalles = conn.prepareStatement(sqlDetalles);
                pstmtDetalles.setInt(1, venta.getId());
                rsDetalles = pstmtDetalles.executeQuery();
                while (rsDetalles.next()) {
                    DetalleVenta detalle = construirDetalleVentaDesdeResultSet(rsDetalles, venta.getId());
                    venta.getDetalleVenta().add(detalle);
                }
                rsDetalles.close();
                pstmtDetalles.close();

                // 5. Añadir la venta completamente construida a la lista
                ventas.add(venta);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todas las ventas: " + e.getMessage());
            e.printStackTrace(); // Imprimir el stack trace para más detalles
        } finally {
            // 6. Cerrar los recursos restantes
            try {
                if (rsVentas != null) rsVentas.close();
                if (rsDescuentos != null) rsDescuentos.close();
                if (rsDetalles != null) rsDetalles.close();
                if (pstmtVentas != null) pstmtVentas.close();
                if (pstmtDescuentos != null) pstmtDescuentos.close();
                if (pstmtDetalles != null) pstmtDetalles.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
        return ventas;
    }

    /**
     * Método de utilidad para construir un objeto {@link Cliente} desde un {@link ResultSet}.
     * @param rs El ResultSet posicionado en una fila que contiene datos de cliente.
     * @return Un nuevo objeto Cliente.
     * @throws SQLException Si hay un error al acceder a los datos del ResultSet.
     */
    private Cliente construirClienteDesdeResultSet(ResultSet rs) throws SQLException {
        String tipoCliente = rs.getString("cliente_tipoCliente");
        if ("Particular".equals(tipoCliente)) {
            return Cliente.crearParticular(
                rs.getInt("cliente_id"), rs.getString("cliente_email"), rs.getString("cliente_telefono"),
                rs.getString("cliente_direccion"), rs.getString("cliente_cifnif"),
                rs.getDate("cliente_fechaAlta") != null ? rs.getDate("cliente_fechaAlta").toLocalDate() : null,
                rs.getString("cliente_nombre"), rs.getString("cliente_apellidos")
            );
        } else if ("Empresa".equals(tipoCliente)) {
            return Cliente.crearEmpresa(
                rs.getInt("cliente_id"), rs.getString("cliente_email"), rs.getString("cliente_telefono"),
                rs.getString("cliente_direccion"), rs.getString("cliente_cifnif"),
                rs.getDate("cliente_fechaAlta") != null ? rs.getDate("cliente_fechaAlta").toLocalDate() : null,
                rs.getString("cliente_razonSocial"), rs.getString("cliente_personaContacto")
            );
        } else {
            System.err.println("Tipo de cliente desconocido: " + tipoCliente);
            return null; // O lanzar una excepción
        }
    }

    /**
     * Método de utilidad para construir un objeto {@link Descuento} desde un {@link ResultSet}.
     * @param rs El ResultSet posicionado en una fila que contiene datos de descuento.
     * @return Un nuevo objeto Descuento.
     * @throws SQLException Si hay un error al acceder a los datos del ResultSet.
     */
    private Descuento construirDescuentoDesdeResultSet(ResultSet rs) throws SQLException {
        return new Descuento(
            rs.getInt("idDescuento"), rs.getInt("clienteId"), rs.getString("descripcion"),
            rs.getDouble("porcentaje"),
            rs.getDate("fechaInicio") != null ? rs.getDate("fechaInicio").toLocalDate() : null,
            rs.getDate("fechaCaducidad") != null ? rs.getDate("fechaCaducidad").toLocalDate() : null,
            rs.getBoolean("estado")
        );
    }

    /**
     * Método de utilidad para construir un objeto {@link DetalleVenta} desde un {@link ResultSet}.
     * @param rs El ResultSet posicionado en una fila que contiene datos de detalle y producto.
     * @param ventaId El ID de la venta a la que pertenece este detalle.
     * @return Un nuevo objeto DetalleVenta.
     * @throws SQLException Si hay un error al acceder a los datos del ResultSet.
     */
    private DetalleVenta construirDetalleVentaDesdeResultSet(ResultSet rs, int ventaId) throws SQLException {
        // CORRECCIÓN: Se utiliza el constructor correcto de Producto, incluyendo la categoría.
        Producto producto = new Producto(
            rs.getInt("producto_id"), 
            rs.getString("producto_nombre"),
            rs.getString("producto_descripcion"), 
            rs.getString("producto_categoria"), // Campo que faltaba
            rs.getDouble("producto_precio"),
            rs.getInt("producto_stock")
        );
        return new DetalleVenta(
            rs.getInt("id"), ventaId, producto,
            rs.getInt("cantidad"), rs.getDouble("precio_unitario")
        );
    }
}
