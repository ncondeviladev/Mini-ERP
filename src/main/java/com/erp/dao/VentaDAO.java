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
import com.erp.model.Venta;

/**
 * DAO para gestionar la persistencia de las ventas y sus detalles.
 */
public class VentaDAO {

    /**
     * Guarda una venta completa (cabecera, detalles y descuentos) en la base de datos,
     * dentro de una única transacción.
     *
     * <p>Este método realiza los siguientes pasos:</p>
     * <ol>
     *   <li>Inserta la cabecera de la venta en la tabla {@code ventas}.</li>
     *   <li>Recupera el ID generado de la venta y lo asigna al objeto {@link Venta}.</li>
     *   <li>Inserta todos los detalles de la venta en la tabla {@code detalles_venta}.</li>
     *   <li>Inserta los descuentos asociados en la tabla {@code venta_descuentos}.</li>
     *   <li>Confirma la transacción si todo fue correcto o la revierte en caso de error.</li>
     * </ol>
     *
     * @param venta Objeto {@link Venta} a persistir en la base de datos.
     *              Se espera que contenga un {@link Cliente}, una fecha (como {@link java.time.LocalDate}),
     *              el total de la venta, los detalles ({@link DetalleVenta}) y, opcionalmente, descuentos ({@link Descuento}).
     * @return {@code true} si la venta se guardó con éxito, {@code false} en caso de error.
     */
    public boolean guardarVenta(Venta venta) {
        // Sentencias SQL para las inserciones
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
            conn.setAutoCommit(false); // Iniciar transacción

            // 1. Insertar la cabecera de la venta
            pstmtVenta = conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS);
            pstmtVenta.setInt(1, venta.getCliente().getId());

            // ✅ Conversión de LocalDate a java.sql.Date mediante valueOf
            pstmtVenta.setDate(2, Date.valueOf(venta.getFecha()));

            pstmtVenta.setDouble(3, venta.getTotal());
            pstmtVenta.executeUpdate();

            // Obtener el ID generado para la venta
            rs = pstmtVenta.getGeneratedKeys();
            int ventaId = -1;
            if (rs.next()) {
                ventaId = rs.getInt(1);
                venta.setId(ventaId); // Asignar el ID generado al objeto Venta
            } else {
                throw new SQLException("No se pudo obtener el ID de la venta insertada.");
            }

            // 2. Insertar los detalles de la venta
            pstmtDetalle = conn.prepareStatement(sqlDetalle);
            for (DetalleVenta detalle : venta.getDetalleVenta()) {
                pstmtDetalle.setInt(1, ventaId);
                pstmtDetalle.setInt(2, detalle.getProducto().getId());
                pstmtDetalle.setInt(3, detalle.getCantidad());
                pstmtDetalle.setDouble(4, detalle.getPrecioUnitario());
                pstmtDetalle.addBatch();
            }
            pstmtDetalle.executeBatch();

            // 3. Insertar los descuentos asociados a la venta
            if (venta.getDescuentos() != null && !venta.getDescuentos().isEmpty()) {
                pstmtVentaDescuento = conn.prepareStatement(sqlVentaDescuento);
                for (Descuento descuento : venta.getDescuentos()) {
                    pstmtVentaDescuento.setInt(1, ventaId);
                    pstmtVentaDescuento.setInt(2, descuento.getId());
                    pstmtVentaDescuento.addBatch();
                }
                pstmtVentaDescuento.executeBatch();
            }

            conn.commit(); // Confirmar transacción
            exito = true;

        } catch (SQLException e) {
            System.err.println("Error al guardar la venta: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback(); // Deshacer la transacción en caso de error
                    System.err.println("Transacción de venta revertida.");
                } catch (SQLException ex) {
                    System.err.println("Error al revertir la transacción: " + ex.getMessage());
                }
            }
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmtVenta != null) pstmtVenta.close();
                if (pstmtDetalle != null) pstmtDetalle.close();
                if (pstmtVentaDescuento != null) pstmtVentaDescuento.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
        return exito;
    }

    /**
     * Recupera todas las ventas de la base de datos, incluyendo la información del cliente
     * y la lista de descuentos asociados a cada venta.
     *
     * @return Una lista de objetos {@link Venta} completos.
     */
    public List<Venta> obtenerTodasLasVentas() {
        List<Venta> ventas = new ArrayList<>();
        // Consulta principal para obtener datos de la venta y el cliente
        String sqlVentas = "SELECT v.id, v.fecha, v.total, " +
                           "c.id AS cliente_id, c.nombre AS cliente_nombre, c.apellidos AS cliente_apellidos, " +
                           "c.razonSocial AS cliente_razonSocial, c.personaContacto AS cliente_personaContacto, " +
                           "c.tipoCliente AS cliente_tipoCliente, c.telefono AS cliente_telefono, " +
                           "c.email AS cliente_email, c.direccion AS cliente_direccion, c.cifnif AS cliente_cifnif, " +
                           "c.fechaAlta AS cliente_fechaAlta " +
                           "FROM ventas v JOIN clientes c ON v.cliente_id = c.id";

        // Consulta para obtener los descuentos de una venta específica
        String sqlDescuentos = "SELECT d.idDescuento, d.clienteId, d.descripcion, d.porcentaje, " +
                               "d.fechaInicio, d.fechaCaducidad, d.estado " +
                               "FROM descuentos d JOIN venta_descuentos vd ON d.idDescuento = vd.descuento_id " +
                               "WHERE vd.venta_id = ?";

        Connection conn = null;
        PreparedStatement pstmtVentas = null;
        PreparedStatement pstmtDescuentos = null;
        ResultSet rsVentas = null;
        ResultSet rsDescuentos = null;

        try {
            conn = SQLiteConnector.connect();
            pstmtVentas = conn.prepareStatement(sqlVentas);
            rsVentas = pstmtVentas.executeQuery();

            while (rsVentas.next()) {
                // Crear objeto Cliente
                String tipoCliente = rsVentas.getString("cliente_tipoCliente");
                Cliente cliente;

                if ("Particular".equals(tipoCliente)) {
                    cliente = Cliente.crearParticular(
                        rsVentas.getInt("cliente_id"),
                        rsVentas.getString("cliente_email"),
                        rsVentas.getString("cliente_telefono"),
                        rsVentas.getString("cliente_direccion"),
                        rsVentas.getString("cliente_cifnif"), // nif
                        rsVentas.getDate("cliente_fechaAlta") != null ? rsVentas.getDate("cliente_fechaAlta").toLocalDate() : null,
                        rsVentas.getString("cliente_nombre"),
                        rsVentas.getString("cliente_apellidos")
                    );
                } else if ("Empresa".equals(tipoCliente)) {
                    cliente = Cliente.crearEmpresa(
                        rsVentas.getInt("cliente_id"),
                        rsVentas.getString("cliente_email"),
                        rsVentas.getString("cliente_telefono"),
                        rsVentas.getString("cliente_direccion"),
                        rsVentas.getString("cliente_cifnif"), // cif
                        rsVentas.getDate("cliente_fechaAlta") != null ? rsVentas.getDate("cliente_fechaAlta").toLocalDate() : null,
                        rsVentas.getString("cliente_razonSocial"),
                        rsVentas.getString("cliente_personaContacto")
                    );
                } else {
                    // Manejar caso de tipo de cliente desconocido o nulo
                    System.err.println("Tipo de cliente desconocido encontrado: " + tipoCliente + " para ID: " + rsVentas.getInt("cliente_id"));
                    cliente = null; // O lanzar una excepción, dependiendo de la política de errores
                }

                // Crear objeto Venta (sin descuentos ni detalles inicialmente)
                Venta venta = new Venta(
                    rsVentas.getInt("id"),
                    cliente,
                    new ArrayList<>(), // Inicializar lista de descuentos vacía
                    new ArrayList<>(), // Inicializar lista de detalles vacía
                    rsVentas.getDate("fecha").toLocalDate(), // Convertir java.sql.Date a LocalDate
                    rsVentas.getDouble("total")
                );

                // Cargar los descuentos para esta venta
                pstmtDescuentos = conn.prepareStatement(sqlDescuentos);
                pstmtDescuentos.setInt(1, venta.getId());
                rsDescuentos = pstmtDescuentos.executeQuery();
                while (rsDescuentos.next()) {
                    Descuento descuento = new Descuento(
                        rsDescuentos.getInt("idDescuento"),
                        rsDescuentos.getInt("clienteId"), // Assuming client ID is stored in Descuento
                        rsDescuentos.getString("descripcion"),
                        rsDescuentos.getDouble("porcentaje"),
                        rsDescuentos.getDate("fechaInicio") != null ? rsDescuentos.getDate("fechaInicio").toLocalDate() : null,
                        rsDescuentos.getDate("fechaCaducidad") != null ? rsDescuentos.getDate("fechaCaducidad").toLocalDate() : null,
                        rsDescuentos.getBoolean("estado")
                    );
                    venta.getDescuentos().add(descuento);
                }
                // Cerrar rsDescuentos y pstmtDescuentos para cada iteración
                if (rsDescuentos != null) rsDescuentos.close();
                if (pstmtDescuentos != null) pstmtDescuentos.close();


                ventas.add(venta);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todas las ventas: " + e.getMessage());
        } finally {
            try {
                if (rsVentas != null) rsVentas.close();
                if (pstmtVentas != null) pstmtVentas.close();
                // pstmtDescuentos y rsDescuentos ya se cierran en el bucle
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
        return ventas;
    }
}
