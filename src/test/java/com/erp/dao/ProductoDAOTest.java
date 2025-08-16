package com.erp.dao;

import com.erp.db.SQLiteConnector;
import com.erp.model.Producto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de tests para {@link ProductoDAO}.
 * Utiliza una base de datos en memoria para asegurar que los tests son independientes
 * y no afectan a la base de datos de producción.
 */
public class ProductoDAOTest {

    private ProductoDAO productoDAO;
    private Connection connection;

    /**
     * Configuración inicial para cada test.
     * Crea la tabla de productos en una base de datos en memoria.
     * @throws SQLException si hay un error al conectar o crear la tabla.
     */
    @BeforeEach
    void setUp() throws SQLException {
        // Se conecta a la BD en memoria a través del conector de test.
        connection = SQLiteConnector.connect();
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS productos (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nombre TEXT NOT NULL, " +
                    "descripcion TEXT, " +
                    "categoria TEXT, " +
                    "precioUnitario REAL NOT NULL, " +
                    "stock INTEGER NOT NULL)");
        }
        // Se instancia el DAO que operará sobre la BD de test.
        productoDAO = new ProductoDAO();
    }

    /**
     * Limpieza después de cada test.
     * Elimina la tabla de productos y cierra la conexión a la base de datos en memoria.
     * @throws SQLException si hay un error al cerrar la conexión o eliminar la tabla.
     */
    @AfterEach
    void tearDown() throws SQLException {
        // Se elimina la tabla para no interferir con el siguiente test.
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS productos");
        }
        // Se cierra la conexión a la BD en memoria.
        SQLiteConnector.closeConnection();
    }

    /** Test para verificar el guardado y la búsqueda de un producto. */
    @Test
    void testGuardarYBuscarProducto() {
        Producto producto = new Producto("Laptop Pro", "Potente laptop", "Electrónica", 1200.50, 10);
        assertTrue(productoDAO.guardarProductoDb(producto), "El producto debería guardarse correctamente.");
        assertNotEquals(0, producto.getId(), "El ID del producto debería haberse actualizado.");

        Producto encontrado = productoDAO.buscarProductoPorId(producto.getId());
        assertNotNull(encontrado, "Se debería encontrar el producto por su ID.");
        assertEquals("Laptop Pro", encontrado.getNombre());
    }

    /** Test para verificar que se listan todos los productos existentes en la base de datos. */
    @Test
    void testListarProductos() {
        productoDAO.guardarProductoDb(new Producto("Producto A", "Desc A", "Cat A", 10.0, 100));
        productoDAO.guardarProductoDb(new Producto("Producto B", "Desc B", "Cat B", 20.0, 200));

        List<Producto> productos = productoDAO.listarProductos();
        assertEquals(2, productos.size(), "La lista debería contener dos productos.");
    }

    /** Test para verificar que el listado de productos devuelve una lista vacía si no hay ninguno. */
    @Test
    void testListarProductosCuandoEstaVacio() {
        List<Producto> productos = productoDAO.listarProductos();
        assertTrue(productos.isEmpty(), "La lista de productos debería estar vacía si no se ha añadido ninguno.");
    }

    /** Test para verificar la correcta actualización de los datos de un producto. */
    @Test
    void testActualizarProducto() {
        Producto producto = new Producto("Teclado", "Mecánico", "Periféricos", 80.0, 50);
        productoDAO.guardarProductoDb(producto);

        producto.setPrecioUnitario(75.50);
        producto.setStock(45);
        assertTrue(productoDAO.actualizarProductoEnDb(producto), "El producto debería actualizarse correctamente.");

        Producto actualizado = productoDAO.buscarProductoPorId(producto.getId());
        assertEquals(75.50, actualizado.getPrecioUnitario());
        assertEquals(45, actualizado.getStock());
    }

    /** Test para verificar la eliminación de un producto por su ID. */
    @Test
    void testEliminarProducto() {
        Producto producto = new Producto("Mouse", "Inalámbrico", "Periféricos", 25.0, 150);
        productoDAO.guardarProductoDb(producto);
        int id = producto.getId();

        assertTrue(productoDAO.eliminarProductoPorId(id), "El producto debería eliminarse correctamente.");
        assertNull(productoDAO.buscarProductoPorId(id), "El producto no debería encontrarse después de ser eliminado.");
    }
}