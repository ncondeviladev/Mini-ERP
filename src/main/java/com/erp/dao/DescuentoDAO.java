package com.erp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.erp.db.SQLiteConnector;
import com.erp.model.Descuento;

public class DescuentoDAO {

    private final Connection conexion;

    public DescuentoDAO() {
        try {
            this.conexion = SQLiteConnector.connect();
        } catch (SQLException e) {
            throw new RuntimeException("Error al conectar con la base de datos", e);
        }
    }

    public boolean guardarDescuentoDb(Descuento descuento) {
        String sql = "INSERT INTO descuentos(clienteId, descripcion, porcentaje, fechaInicio, fechaCaducidad, estado) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, descuento.getClienteId());
            stmt.setString(2, descuento.getDescripcion());
            stmt.setDouble(3, descuento.getPorcentaje());
            stmt.setDate(4, java.sql.Date.valueOf(descuento.getFechaInicio()));
            stmt.setDate(5, java.sql.Date.valueOf(descuento.getFechaFin()));
            stmt.setBoolean(6, descuento.isActivo());

            int filas = stmt.executeUpdate();

            if (filas > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        descuento.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarDescuentoDb(Descuento descuento) {
        String sql = "UPDATE descuentos SET clienteId = ?, descripcion = ?, porcentaje = ?, fechaInicio = ?, fechaCaducidad = ?, estado = ? WHERE idDescuento = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, descuento.getClienteId());
            stmt.setString(2, descuento.getDescripcion());
            stmt.setDouble(3, descuento.getPorcentaje());
            stmt.setDate(4, java.sql.Date.valueOf(descuento.getFechaInicio()));
            stmt.setDate(5, java.sql.Date.valueOf(descuento.getFechaFin()));
            stmt.setBoolean(6, descuento.isActivo());
            stmt.setInt(7, descuento.getId());

            int filas = stmt.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminarDescuentoDb(int idDescuento) {
        String sql = "DELETE FROM descuentos WHERE idDescuento = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idDescuento);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Descuento buscarDescuentoPorId(int idDescuento) {
        String sql = "SELECT * FROM descuentos WHERE idDescuento = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idDescuento);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return construirDescuento(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Descuento> listarDescuentos() {
        List<Descuento> descuentos = new ArrayList<>();
        String sql = "SELECT * FROM descuentos";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                descuentos.add(construirDescuento(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return descuentos;
    }

    public List<Descuento> listarDescuentosPorCliente(int clienteId) {
        List<Descuento> descuentos = new ArrayList<>();
        String sql = "SELECT * FROM descuentos WHERE clienteId = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                descuentos.add(construirDescuento(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return descuentos;
    }

    private Descuento construirDescuento(ResultSet rs) throws SQLException {
        return new Descuento(
                rs.getInt("idDescuento"),
                rs.getInt("clienteId"),
                rs.getString("descripcion"),
                rs.getDouble("porcentaje"),
                rs.getDate("fechaInicio").toLocalDate(),
                rs.getDate("fechaCaducidad").toLocalDate(),
                rs.getBoolean("estado")
        );
    }
}