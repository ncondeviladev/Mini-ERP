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
 * DAO (Data Access Object) para la entidad {@link Descuento}.
 * 
 * <p>Esta clase se encarga de todas las operaciones de base de datos relacionadas con
 * los descuentos, como guardar, actualizar, eliminar y consultar información en la
 * tabla `descuentos`. Abstrae la lógica de acceso a datos del resto de la aplicación.</p>
 *
 * @author Noé
 * @see Descuento
 * @see com.erp.db.SQLiteConnector
 */
public class DescuentoDAO {

    /**
     * La conexión a la base de datos, obtenida al instanciar el DAO.
     */
    private final Connection conexion;

    /**
     * Constructor que inicializa el DAO y establece la conexión con la base de datos.
     * Lanza una {@code RuntimeException} si la conexión no puede ser establecida.
     */
    public DescuentoDAO() {
        try {
            this.conexion = SQLiteConnector.connect();
        } catch (SQLException e) {
            throw new RuntimeException("Error fatal: no se pudo conectar con la base de datos.", e);
        }
    }

    /**
     * Guarda un nuevo descuento en la base de datos.
     *
     * @param descuento El objeto {@code Descuento} a persistir. Su ID debe ser nulo.
     * @return {@code true} si el descuento se guardó con éxito, {@code false} en caso contrario.
     */
    public boolean guardarDescuentoDb(Descuento descuento) {
        // La columna de estado en la BD se llama 'estado', y la de fecha fin 'fechaCaducidad'.
        String sql = "INSERT INTO descuentos(clienteId, descripcion, porcentaje, fechaInicio, fechaCaducidad, estado) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, descuento.getClienteId());
            stmt.setString(2, descuento.getDescripcion());
            stmt.setDouble(3, descuento.getPorcentaje());
            // Se convierte de java.time.LocalDate a java.sql.Date para compatibilidad con JDBC.
            stmt.setDate(4, java.sql.Date.valueOf(descuento.getFechaInicio()));
            stmt.setDate(5, java.sql.Date.valueOf(descuento.getFechaFin()));
            stmt.setBoolean(6, descuento.isActivo());

            int filas = stmt.executeUpdate();

            // Si la inserción fue exitosa, se recupera el ID generado y se asigna al objeto.
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
            System.err.println("Error al guardar el descuento en la base de datos.");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Actualiza un descuento existente en la base de datos.
     *
     * @param descuento El objeto {@code Descuento} con los datos actualizados. Debe tener un ID válido.
     * @return {@code true} si la actualización fue exitosa, {@code false} en caso contrario.
     */
    public boolean actualizarDescuentoDb(Descuento descuento) {
        // El ID en la tabla de descuentos se llama 'idDescuento'.
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
            System.err.println("Error al actualizar el descuento en la base de datos.");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina un descuento de la base de datos por su ID.
     *
     * @param idDescuento El ID del descuento a eliminar.
     * @return {@code true} si se eliminó con éxito, {@code false} en caso contrario.
     */
    public boolean eliminarDescuentoDb(int idDescuento) {
        String sql = "DELETE FROM descuentos WHERE idDescuento = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idDescuento);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar el descuento de la base de datos.");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Busca y devuelve un descuento específico por su ID.
     *
     * @param idDescuento El ID del descuento a buscar.
     * @return Un objeto {@code Descuento} si se encuentra, o {@code null} si no.
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
            System.err.println("Error al buscar el descuento por ID.");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Devuelve una lista con todos los descuentos de la base de datos.
     *
     * @return Una {@code List<Descuento>} con todos los descuentos. Lista vacía si no hay ninguno.
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
            System.err.println("Error al listar los descuentos.");
            e.printStackTrace();
        }
        return descuentos;
    }

    /**
     * Devuelve una lista de todos los descuentos asociados a un cliente específico.
     *
     * @param clienteId El ID del cliente cuyos descuentos se quieren obtener.
     * @return Una {@code List<Descuento>} con los descuentos del cliente. Lista vacía si no tiene.
     */
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
            System.err.println("Error al listar los descuentos por cliente.");
            e.printStackTrace();
        }
        return descuentos;
    }

    /**
     * Método de ayuda para construir un objeto {@link Descuento} a partir de un {@link ResultSet}.
     * Centraliza la lógica de mapeo de la fila de la base de datos al objeto Java.
     *
     * @param rs El ResultSet posicionado en la fila a procesar.
     * @return Un objeto {@code Descuento} poblado con los datos de la fila.
     * @throws SQLException Si hay un error al acceder a las columnas del ResultSet.
     */
    private Descuento construirDescuento(ResultSet rs) throws SQLException {
        // Se convierten las fechas de java.sql.Date a java.time.LocalDate.
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
