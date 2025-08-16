package com.erp.dao;

import com.erp.db.SQLiteConnector;
import com.erp.model.Descuento;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de tests para {@link DescuentoDAO}.
 * Utiliza una base de datos en memoria para asegurar que los tests son independientes
 * y no afectan a la base de datos de producción.
 */
public class DescuentoDAOTest {

    private DescuentoDAO descuentoDAO;
    private Connection connection;
    private int testClienteId = 1;

    /**
     * Configuración inicial para cada test.
     * Crea la tabla de descuentos en una base de datos en memoria.
     * @throws SQLException si hay un error al conectar o crear la tabla.
     */
    @BeforeEach
    void setUp() throws SQLException {
        // Se conecta a la BD en memoria a través del conector de test.
        connection = SQLiteConnector.connect();
        try (Statement stmt = connection.createStatement()) {
            // Crear la tabla de descuentos para cada test
            stmt.execute("CREATE TABLE IF NOT EXISTS descuentos (" +
                    "idDescuento INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "clienteId INTEGER NOT NULL, " +
                    "descripcion TEXT, " +
                    "porcentaje REAL NOT NULL, " +
                    "fechaInicio DATE NOT NULL, " +
                    "fechaCaducidad DATE NOT NULL, " +
                    "estado BOOLEAN NOT NULL)");
        }
        // Se instancia el DAO que operará sobre la BD de test.
        descuentoDAO = new DescuentoDAO();
    }

    /**
     * Limpieza después de cada test.
     * Elimina la tabla de descuentos y cierra la conexión a la base de datos en memoria.
     * @throws SQLException si hay un error al cerrar la conexión o eliminar la tabla.
     */
    @AfterEach
    void tearDown() throws SQLException {
        // Se elimina la tabla para no interferir con el siguiente test.
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS descuentos");
        }
        // Se cierra la conexión a la BD en memoria.
        SQLiteConnector.closeConnection();
    }

    /**
     * Test para verificar el guardado y la búsqueda de un descuento.
     */
    @Test
    void testGuardarYBuscarDescuento() {
        Descuento descuento = new Descuento(testClienteId, "Descuento de bienvenida", 10.0, LocalDate.now(), LocalDate.now().plusMonths(1));
        assertTrue(descuentoDAO.guardarDescuentoDb(descuento), "El descuento debería guardarse correctamente.");
        assertNotEquals(0, descuento.getId(), "El ID del descuento debería haberse actualizado.");

        Descuento encontrado = descuentoDAO.buscarDescuentoPorId(descuento.getId());
        assertNotNull(encontrado, "Se debería encontrar el descuento por su ID.");
        assertEquals("Descuento de bienvenida", encontrado.getDescripcion());
        assertEquals(testClienteId, encontrado.getClienteId());
    }

    /**
     * Test para verificar que se listan todos los descuentos de un cliente específico.
     */
    @Test
    void testListarDescuentosPorCliente() {
        descuentoDAO.guardarDescuentoDb(new Descuento(testClienteId, "Dto 1", 5.0, LocalDate.now(), LocalDate.now().plusDays(10)));
        descuentoDAO.guardarDescuentoDb(new Descuento(testClienteId, "Dto 2", 15.0, LocalDate.now(), LocalDate.now().plusDays(20)));
        descuentoDAO.guardarDescuentoDb(new Descuento(99, "Otro cliente", 10.0, LocalDate.now(), LocalDate.now().plusDays(5))); // Otro cliente

        List<Descuento> descuentos = descuentoDAO.listarDescuentosPorCliente(testClienteId);
        assertEquals(2, descuentos.size(), "La lista debería contener dos descuentos para el cliente especificado.");
    }

    /**
     * Test para verificar que el listado de descuentos devuelve una lista vacía si un cliente no tiene.
     */
    @Test
    void testListarDescuentosPorClienteCuandoNoHay() {
        List<Descuento> descuentos = descuentoDAO.listarDescuentosPorCliente(999); // Un ID de cliente que no existe.
        assertTrue(descuentos.isEmpty(), "La lista de descuentos debería estar vacía para un cliente sin descuentos.");
    }

    /** Test para verificar la correcta actualización de los datos de un descuento. */
    @Test
    void testActualizarDescuento() {
        Descuento descuento = new Descuento(testClienteId, "Dto Original", 20.0, LocalDate.now(), LocalDate.now().plusMonths(2));
        descuentoDAO.guardarDescuentoDb(descuento);

        descuento.setDescripcion("Dto Actualizado");
        descuento.setPorcentaje(25.0);
        assertTrue(descuentoDAO.actualizarDescuentoDb(descuento), "El descuento debería actualizarse correctamente.");

        Descuento actualizado = descuentoDAO.buscarDescuentoPorId(descuento.getId());
        assertEquals("Dto Actualizado", actualizado.getDescripcion());
        assertEquals(25.0, actualizado.getPorcentaje());
    }

    /** Test para verificar la eliminación de un descuento por su ID. */
    @Test
    void testEliminarDescuento() {
        Descuento descuento = new Descuento(testClienteId, "Dto a eliminar", 5.0, LocalDate.now(), LocalDate.now().plusDays(1));
        descuentoDAO.guardarDescuentoDb(descuento);
        int id = descuento.getId();

        assertTrue(descuentoDAO.eliminarDescuentoDb(id), "El descuento debería eliminarse correctamente.");
        assertNull(descuentoDAO.buscarDescuentoPorId(id), "El descuento no debería encontrarse después de ser eliminado.");
    }
}