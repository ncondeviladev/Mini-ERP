package com.erp.dao;

import com.erp.db.SQLiteConnector;
import com.erp.model.DetalleVenta;
import com.erp.model.Venta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DAO para gestionar la persistencia de las ventas y sus detalles.
 */
public class VentaDAO {

    /**
     * Guarda una venta completa (cabecera y detalles) en la base de datos
     * dentro de una única transacción.
     *
     * @param venta El objeto Venta a persistir.
     * @return true si la venta se guardó con éxito, false en caso contrario.
     */
    public boolean guardarVenta(Venta venta) {
        // Sentencias SQL para las inserciones
        String sqlVenta = "INSERT INTO ventas(cliente_id, descuento_id, fecha, total) VALUES(?, ?, ?, ?)";
        String sqlDetalle = "INSERT INTO detalles_venta(venta_id, producto_id, cantidad, precio_unitario) VALUES(?, ?, ?, ?)";
        
        Connection conn = null;
        try {
            conn = SQLiteConnector.connect();
            if (conn == null) return false;

            // Iniciar transacción: Desactivamos el auto-commit
            conn.setAutoCommit(false);

            // --- 1. Insertar la cabecera de la venta ---
            PreparedStatement pstmtVenta = conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS);
            pstmtVenta.setInt(1, venta.getCliente().getId());
            
            if (venta.getDescuento() != null) {
                pstmtVenta.setInt(2, venta.getDescuento().getId());
            } else {
                pstmtVenta.setNull(2, java.sql.Types.INTEGER);
            }
            
            pstmtVenta.setString(3, venta.getFecha().toString());
            pstmtVenta.setDouble(4, venta.getTotal());
            
            int affectedRows = pstmtVenta.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No se pudo guardar la venta, no se afectaron filas.");
            }

            // --- 2. Obtener el ID de la venta recién insertada ---
            ResultSet generatedKeys = pstmtVenta.getGeneratedKeys();
            int ventaId;
            if (generatedKeys.next()) {
                ventaId = generatedKeys.getInt(1);
            } else {
                throw new SQLException("No se pudo obtener el ID para la venta guardada.");
            }
            pstmtVenta.close();

            // --- 3. Insertar cada detalle de la venta ---
            PreparedStatement pstmtDetalle = conn.prepareStatement(sqlDetalle);
            for (DetalleVenta detalle : venta.getDetalleVenta()) {
                pstmtDetalle.setInt(1, ventaId);
                pstmtDetalle.setInt(2, detalle.getProducto().getId());
                pstmtDetalle.setInt(3, detalle.getCantidad());
                pstmtDetalle.setDouble(4, detalle.getPrecioUnitario());
                pstmtDetalle.addBatch(); // Agrupamos las inserciones de detalles
            }
            
            pstmtDetalle.executeBatch(); // Ejecutamos todas las inserciones de detalles de golpe
            pstmtDetalle.close();

            // --- 4. Si todo fue bien, confirmamos la transacción ---
            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al guardar la venta: " + e.getMessage());
            // Si algo falla, revertimos la transacción
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                System.err.println("Error al hacer rollback: " + ex.getMessage());
            }
            return false;
        } finally {
            // Devolvemos la conexión a su estado normal
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException ex) {
                System.err.println("Error al cerrar la conexión: " + ex.getMessage());
            }
        }
    }
}