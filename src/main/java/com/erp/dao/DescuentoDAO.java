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

/**
 * DAO (Data Access Object) para la entidad Descuento.
 * Esta clase se encarga de todas las operaciones de base de datos (CRUD) relacionadas con los descuentos.
 */
public class DescuentoDAO {

    private final Connection conexion;

    /**
     * Constructor que inicializa el DAO y establece la conexión con la base de datos.
     * Lanza una RuntimeException si la conexión no puede ser establecida.
     */
    public DescuentoDAO() {
        try {
            this.conexion = SQLiteConnector.connect();
        } catch (SQLException e) {
            throw new RuntimeException("Error al conectar con la base de datos", e);
        }
    }

    /**
     * Guarda un nuevo descuento en la base de datos.
     * Después de guardar, recupera el ID autogenerado y lo asigna al objeto Descuento.
     *
     * @param descuento El objeto Descuento a guardar. Su ID será establecido por este método.
     * @return true si el descuento se guardó correctamente, false en caso contrario.
     */
    public boolean guardarDescuentoDb(Descuento descuento) {
        String sql = "INSERT INTO descuentos(clienteId, descripcion, porcentaje, fechaInicio, fechaCaducidad, estado) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, descuento.getClienteId());
            stmt.setString(2, descuento.getDescripcion());
            stmt.setDouble(3, descuento.getPorcentaje());
            stmt.setDate(4, java.sql.Date.valueOf(descuento.getFechaInicio()));
            stmt.setDate(5, java.sql.Date.valueOf(descuento.getFechaCaducidad()));
            stmt.setBoolean(6, descuento.getEstado());

            int filas = stmt.executeUpdate();

            if (filas > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        descuento.setIdDescuento(generatedKeys.getInt(1));
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

    /**
     * Actualiza un descuento existente en la base de datos.
     *
     * @param descuento El objeto Descuento con los datos a actualizar. Se utiliza el ID para encontrar el registro.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public boolean actualizarDescuentoDb(Descuento descuento) {
        String sql = "UPDATE descuentos SET clienteId = ?, descripcion = ?, porcentaje = ?, fechaInicio = ?, fechaCaducidad = ?, estado = ? WHERE idDescuento = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, descuento.getClienteId());
            stmt.setString(2, descuento.getDescripcion());
            stmt.setDouble(3, descuento.getPorcentaje());
            stmt.setDate(4, java.sql.Date.valueOf(descuento.getFechaInicio()));
            stmt.setDate(5, java.sql.Date.valueOf(descuento.getFechaCaducidad()));
            stmt.setBoolean(6, descuento.getEstado());
            stmt.setInt(7, descuento.getIdDescuento());

            int filas = stmt.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina un descuento de la base de datos utilizando su ID.
     *
     * @param idDescuento El ID del descuento a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     */
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

    /**
     * Busca y devuelve un descuento específico por su ID.
     *
     * @param idDescuento El ID del descuento a buscar.
     * @return Un objeto Descuento si se encuentra, o null si no existe.
     */
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

    /**
     * Devuelve una lista con todos los descuentos registrados en la base de datos.
     *
     * @return Una lista de objetos Descuento. La lista estará vacía si no hay descuentos.
     */
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

    /**
     * Método de utilidad interna para construir un objeto Descuento a partir de un ResultSet.
     *
     * @param rs El ResultSet de la consulta a la base de datos.
     * @return Un objeto Descuento completamente populado.
     * @throws SQLException si ocurre un error al acceder a los datos del ResultSet.
     */
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
