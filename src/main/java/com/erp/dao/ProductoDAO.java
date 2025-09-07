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
 * DAO (Data Access Object) para la entidad {@link Producto}.
 * 
 * <p>Esta clase encapsula toda la lógica de acceso a datos para la tabla `productos`
 * en la base de datos. Proporciona métodos para realizar las operaciones CRUD 
 * (Crear, Leer, Actualizar, Eliminar) de manera que el resto de la aplicación
 * no necesita conocer los detalles de las sentencias SQL ni de la gestión de la
 * conexión con la base de datos.</p>
 *
 * @author Noé
 * @see Producto
 * @see com.erp.db.SQLiteConnector
 */
public class ProductoDAO {

    /**
     * La conexión a la base de datos. Se mantiene como un campo de instancia para ser
     * reutilizada por todos los métodos del DAO.
     */
    private final Connection conexion;

    /**
     * Constructor del DAO.
     * Se encarga de obtener una conexión a la base de datos a través de la clase
     * {@link SQLiteConnector} en el momento de su instanciación.
     * Si la conexión falla, lanza una {@code RuntimeException} para detener la ejecución,
     * ya que el DAO no puede funcionar sin una conexión válida.
     */
    public ProductoDAO() {
        try {
            this.conexion = SQLiteConnector.connect();
        } catch (SQLException e) {
            throw new RuntimeException("Error fatal: no se pudo conectar con la base de datos.", e);
        }
    }

    /**
     * Guarda un nuevo producto en la base de datos.
     * <p>
     * Utiliza un {@link PreparedStatement} para insertar los datos del producto
     * y recupera el ID autogenerado por la base de datos, asignándolo de nuevo al objeto.
     * </p>
     * 
     * @param producto El objeto {@link Producto} con los datos a guardar. Su ID debe ser nulo.
     * @return {@code true} si el producto se guardó correctamente, {@code false} en caso contrario.
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
            System.err.println("Error al guardar el producto en la base de datos.");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Actualiza los datos de un producto existente en la base de datos.
     * <p>
     * El producto es identificado por su ID. Todos los campos del objeto {@link Producto}
     * se utilizan para actualizar el registro correspondiente en la tabla `productos`.
     * </p>
     * 
     * @param producto El objeto {@link Producto} con los datos actualizados. Debe tener un ID válido.
     * @return {@code true} si la actualización fue exitosa, {@code false} en caso contrario.
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
            System.err.println("Error al actualizar el producto en la base de datos.");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina un producto de la base de datos utilizando su ID.
     * 
     * @param id El ID del producto a eliminar.
     * @return {@code true} si el producto se eliminó correctamente (se afectó una o más filas), {@code false} en caso contrario.
     */
    public boolean eliminarProductoPorId(int id) {
        String sql = "DELETE FROM productos WHERE id = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar el producto de la base de datos.");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Busca y devuelve un producto por su ID.
     * 
     * @param id El ID del producto a buscar.
     * @return Un objeto {@link Producto} si se encuentra, o {@code null} si no existe un producto con ese ID.
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
            System.err.println("Error al buscar el producto por ID.");
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



