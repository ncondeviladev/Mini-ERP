package com.erp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.erp.db.SQLiteConnector;
import com.erp.model.Producto;

/**
 * DAO encargado de gestionar operaciones sobre la tabla 'productos'
 * Compatible con controlador ProductoViewController
 */
public class ProductoDAO {

    private final Connection conexion;

    public ProductoDAO() {
        try {
            this.conexion = SQLiteConnector.connect();
        } catch (SQLException e) {
            throw new RuntimeException("Error al conectar con la base de datos", e);
        }
    }

    /**
     * Guarda un nuevo producto en la base de datos
     * 
     * @param producto objeto Producto con datos
     * @return true si se inserta correctamente
     */
    public boolean guardarProductoDb(Producto producto) {
        String sql = "INSERT INTO productos(nombre, descripcion, categoria, precioUnitario, stock) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, producto.getNombre());
            stmt.setString(2, producto.getDescripcion());
            stmt.setString(3, producto.getCategoria());
            stmt.setDouble(4, producto.getPrecioUnitario());
            stmt.setInt(5, producto.getStock());

            int filas = stmt.executeUpdate();

            if (filas > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    producto.setId(generatedKeys.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Actualiza un producto existente en la base de datos
     * 
     * @param producto objeto Producto con ID y nuevos valores
     * @return true si se actualiza correctamente
     */
    public boolean actualizarProductoEnDb(Producto producto) {
        String sql = "UPDATE productos SET nombre = ?, descripcion = ?, categoria = ?, precioUnitario = ?, stock = ? WHERE id = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, producto.getNombre());
            stmt.setString(2, producto.getDescripcion());
            stmt.setString(3, producto.getCategoria());
            stmt.setDouble(4, producto.getPrecioUnitario());
            stmt.setInt(5, producto.getStock());
            stmt.setInt(6, producto.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina un producto por ID
     * 
     * @param id identificador del producto
     * @return true si se elimina correctamente
     */
    public boolean eliminarProductoPorId(int id) {
        String sql = "DELETE FROM productos WHERE id = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Busca un producto por ID
     * 
     * @param id identificador
     * @return Producto si se encuentra, null si no
     */
    public Producto buscarProductoPorId(int id) {
        String sql = "SELECT * FROM productos WHERE id = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return construirProducto(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Obtiene todos los productos existentes
     * 
     * @return lista de productos
     */
    public List<Producto> listarProductos() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM productos";
        try (Statement stmt = conexion.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                productos.add(construirProducto(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productos;
    }

    /**
     * Utilidad interna para construir objeto Producto desde un ResultSet
     */
    private Producto construirProducto(ResultSet rs) throws SQLException {
        return new Producto(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("descripcion"),
                rs.getString("categoria"),
                rs.getDouble("precioUnitario"),
                rs.getInt("stock"));
    }
}
