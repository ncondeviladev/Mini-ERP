package com.erp.dao;

import com.erp.db.SQLiteConnector;
import com.erp.model.Cliente;
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
 * Clase de tests para {@link ClienteDAO}.
 * Utiliza una base de datos en memoria para asegurar que los tests son independientes
 * y no afectan a la base de datos de producción.
 */
public class ClienteDAOTest {

    private ClienteDAO clienteDAO;
    private Connection connection;

    /**
     * Configuración inicial para cada test.
     * Crea la tabla de clientes en una base de datos en memoria.
     * @throws SQLException si hay un error al conectar o crear la tabla.
     */
    @BeforeEach
    void setUp() throws SQLException {
        // Se conecta a la BD en memoria a través del conector de test.
        connection = SQLiteConnector.connect();
        try (Statement stmt = connection.createStatement()) {
            // Se crea la estructura de la tabla antes de cada test para asegurar un entorno limpio.
            stmt.execute("CREATE TABLE IF NOT EXISTS clientes (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "tipoCliente TEXT NOT NULL, " +
                    "email TEXT, " +
                    "telefono TEXT, " +
                    "direccion TEXT, " +
                    "cifnif TEXT UNIQUE, " +
                    "fechaAlta TEXT, " +
                    "nombre TEXT, " +
                    "apellidos TEXT, " +
                    "razonSocial TEXT, " +
                    "personaContacto TEXT)");
        }
        // Se instancia el DAO que operará sobre la BD de test.
        clienteDAO = new ClienteDAO();
    }

    /**
     * Limpieza después de cada test.
     * Elimina la tabla de clientes y cierra la conexión a la base de datos en memoria.
     * @throws SQLException si hay un error al cerrar la conexión o eliminar la tabla.
     */
    @AfterEach
    void tearDown() throws SQLException {
        // Se elimina la tabla para no interferir con el siguiente test.
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS clientes");
        }
        // Se cierra la conexión a la BD en memoria.
        SQLiteConnector.closeConnection();
    }

    /** Test para verificar el guardado y la búsqueda de un cliente de tipo 'Particular'. */
    @Test
    void testGuardarYBuscarClienteParticular() {
        Cliente particular = Cliente.crearParticular(0, "test@particular.com", "600111222", "Calle Falsa 123", "12345678Z", LocalDate.now(), "Juan", "Pérez");
        assertTrue(clienteDAO.guardarClienteDb(particular), "El cliente particular debería guardarse correctamente.");
        assertNotEquals(0, particular.getId(), "El ID del cliente debería haberse actualizado.");

        Cliente encontrado = clienteDAO.buscarClientePorId(particular.getId());
        assertNotNull(encontrado, "Se debería encontrar el cliente por su ID.");
        assertEquals("Juan", encontrado.getNombre());
        assertEquals("Particular", encontrado.getTipoCliente());
    }

    /** Test para verificar el guardado y la búsqueda de un cliente de tipo 'Empresa'. */
    @Test
    void testGuardarYBuscarClienteEmpresa() {
        Cliente empresa = Cliente.crearEmpresa(0, "contacto@empresa.com", "912345678", "Av. Industria 45", "B87654321", LocalDate.now(), "Tech Solutions SL", "Ana López");
        assertTrue(clienteDAO.guardarClienteDb(empresa), "El cliente empresa debería guardarse correctamente.");
        assertNotEquals(0, empresa.getId(), "El ID del cliente debería haberse actualizado.");

        Cliente encontrado = clienteDAO.buscarClientePorId(empresa.getId());
        assertNotNull(encontrado, "Se debería encontrar el cliente por su ID.");
        assertEquals("Tech Solutions SL", encontrado.getRazonSocial());
        assertEquals("Empresa", encontrado.getTipoCliente());
    }

    /** Test para verificar que se listan todos los clientes existentes en la base de datos. */
    @Test
    void testListarClientes() {
        Cliente particular = Cliente.crearParticular(0, "test@particular.com", "600111222", "Calle Falsa 123", "12345678Z", LocalDate.now(), "Juan", "Pérez");
        Cliente empresa = Cliente.crearEmpresa(0, "contacto@empresa.com", "912345678", "Av. Industria 45", "B87654321", LocalDate.now(), "Tech Solutions SL", "Ana López");
        clienteDAO.guardarClienteDb(particular);
        clienteDAO.guardarClienteDb(empresa);

        List<Cliente> clientes = clienteDAO.listarClientes();
        assertEquals(2, clientes.size(), "La lista debería contener dos clientes.");
    }

    /** Test para verificar que el listado de clientes devuelve una lista vacía si no hay ninguno. */
    @Test
    void testListarClientesCuandoEstaVacio() {
        List<Cliente> clientes = clienteDAO.listarClientes();
        assertTrue(clientes.isEmpty(), "La lista de clientes debería estar vacía si no se ha añadido ninguno.");
    }

    /** Test para verificar la correcta actualización de los datos de un cliente. */
    @Test
    void testActualizarCliente() {
        Cliente particular = Cliente.crearParticular(0, "test@particular.com", "600111222", "Calle Falsa 123", "12345678Z", LocalDate.now(), "Juan", "Pérez");
        clienteDAO.guardarClienteDb(particular);

        particular.setNombre("Juanito");
        particular.setEmail("nuevo@email.com");
        assertTrue(clienteDAO.actualizarClienteEnDb(particular), "El cliente debería actualizarse correctamente.");

        Cliente actualizado = clienteDAO.buscarClientePorId(particular.getId());
        assertEquals("Juanito", actualizado.getNombre());
        assertEquals("nuevo@email.com", actualizado.getEmail());
    }

    /** Test para verificar la eliminación de un cliente por su ID. */
    @Test
    void testEliminarCliente() {
        Cliente particular = Cliente.crearParticular(0, "test@particular.com", "600111222", "Calle Falsa 123", "12345678Z", LocalDate.now(), "Juan", "Pérez");
        clienteDAO.guardarClienteDb(particular);
        int id = particular.getId();

        assertTrue(clienteDAO.eliminarClientePorId(id), "El cliente debería eliminarse correctamente.");
        assertNull(clienteDAO.buscarClientePorId(id), "El cliente no debería encontrarse después de ser eliminado.");
    }
}