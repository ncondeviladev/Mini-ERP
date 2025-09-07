package com.erp.dao;

import com.erp.db.SQLiteConnector;
import com.erp.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de tests para el DAO de Venta ({@link VentaDAO}).
 * Utiliza Mockito para simular la interacción con la base de datos y verificar
 * el comportamiento de los métodos de persistencia de ventas.
 */
class VentaDAOTest {

    private VentaDAO ventaDAO;
    private Connection mockConnection;
    private PreparedStatement mockPstmtVenta;
    private PreparedStatement mockPstmtDetalle;
    private PreparedStatement mockPstmtVentaDescuento;
    private ResultSet mockRs;

    @BeforeEach
    void setUp() throws SQLException {
        // Resetear mocks antes de cada test
        mockConnection = mock(Connection.class);
        mockPstmtVenta = mock(PreparedStatement.class);
        mockPstmtDetalle = mock(PreparedStatement.class);
        mockPstmtVentaDescuento = mock(PreparedStatement.class);
        mockRs = mock(ResultSet.class);

        // Simular la conexión a la base de datos
        try (MockedStatic<SQLiteConnector> mockedStatic = mockStatic(SQLiteConnector.class)) {
            mockedStatic.when(SQLiteConnector::connect).thenReturn(mockConnection);
            ventaDAO = new VentaDAO(); // Esto llamará a SQLiteConnector.connect()
        } catch (RuntimeException e) {
            // Si la conexión falla en el constructor, el test fallará aquí.
            // Esto es para manejar el caso en que el mockStatic no funcione como se espera.
            fail("Failed to mock SQLiteConnector.connect()", e);
        }

        // Configurar comportamiento por defecto de la conexión
        when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockPstmtVenta);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPstmtDetalle, mockPstmtVentaDescuento);
        when(mockPstmtVenta.getGeneratedKeys()).thenReturn(mockRs);
    }

    /**
     * Test para el método {@code guardarVenta()} en un escenario exitoso.
     * Verifica que se realizan las llamadas correctas a la base de datos y que la transacción se confirma.
     */
    @Test
    void testGuardarVenta_Exito() throws SQLException {
        // Datos de prueba
        Cliente cliente = Cliente.crearParticular(1, "test@test.com", "123", "dir", "nif", LocalDate.now(), "Nombre", "Apellido");
        Producto producto = new Producto(1, "Prod1", "Desc1", "Cat1", 10.0, 10);
        DetalleVenta detalle1 = new DetalleVenta(null, null, producto, 2, 10.0);
        List<DetalleVenta> detalles = Arrays.asList(detalle1);
        Descuento descuento1 = new Descuento(1, 1, "Desc1", 5.0, LocalDate.now(), LocalDate.now().plusDays(10));
        List<Descuento> descuentos = Arrays.asList(descuento1);
        Venta venta = new Venta(null, cliente, descuentos, detalles, LocalDate.now(), 20.0);

        // Comportamiento de los mocks
        when(mockPstmtVenta.executeUpdate()).thenReturn(1); // 1 fila afectada por la inserción de venta
        when(mockRs.next()).thenReturn(true); // Hay una clave generada
        when(mockRs.getInt(1)).thenReturn(100); // El ID generado es 100
        when(mockPstmtDetalle.executeBatch()).thenReturn(new int[]{1}); // 1 fila afectada por el detalle
        when(mockPstmtVentaDescuento.executeBatch()).thenReturn(new int[]{1}); // 1 fila afectada por el descuento

        // Ejecutar el método a probar
        boolean resultado = ventaDAO.guardarVenta(venta);

        // Verificaciones
        assertTrue(resultado, "La venta debería guardarse con éxito.");
        assertEquals(100, venta.getId(), "El ID de la venta debería actualizarse.");

        // Verificar llamadas a la conexión y statements
        verify(mockConnection).setAutoCommit(false);
        verify(mockConnection).prepareStatement(eq("INSERT INTO ventas(cliente_id, fecha, total) VALUES(?, ?, ?)"), eq(Statement.RETURN_GENERATED_KEYS));
        verify(mockPstmtVenta).setInt(1, cliente.getId());
        verify(mockPstmtVenta).setDate(eq(2), any(Date.class));
        verify(mockPstmtVenta).setDouble(3, venta.getTotal());
        verify(mockPstmtVenta).executeUpdate();
        verify(mockRs).next();
        verify(mockRs).getInt(1);

        verify(mockConnection).prepareStatement(eq("INSERT INTO detalles_venta(venta_id, producto_id, cantidad, precio_unitario) VALUES(?, ?, ?, ?)"));
        verify(mockPstmtDetalle).setInt(1, venta.getId());
        verify(mockPstmtDetalle).setInt(2, detalle1.getProducto().getId());
        verify(mockPstmtDetalle).setInt(3, detalle1.getCantidad());
        verify(mockPstmtDetalle).setDouble(4, detalle1.getPrecioUnitario());
        verify(mockPstmtDetalle).addBatch();
        verify(mockPstmtDetalle).executeBatch();

        verify(mockConnection).prepareStatement(eq("INSERT INTO venta_descuentos(venta_id, descuento_id) VALUES(?, ?)"));
        verify(mockPstmtVentaDescuento).setInt(1, venta.getId());
        verify(mockPstmtVentaDescuento).setInt(2, descuento1.getId());
        verify(mockPstmtVentaDescuento).addBatch();
        verify(mockPstmtVentaDescuento).executeBatch();

        verify(mockConnection).commit();
        verify(mockConnection, never()).rollback(); // No debería haber rollback en caso de éxito
        verify(mockConnection).setAutoCommit(true); // Auto-commit restaurado
        verify(mockConnection).close();
    }

    /**
     * Test para el método {@code guardarVenta()} en un escenario de fallo (rollback).
     * Verifica que se llama a rollback si ocurre una excepción.
     */
    @Test
    void testGuardarVenta_FalloRollback() throws SQLException {
        // Datos de prueba
        Cliente cliente = Cliente.crearParticular(1, "test@test.com", "123", "dir", "nif", LocalDate.now(), "Nombre", "Apellido");
        Producto producto = new Producto(1, "Prod1", "Desc1", "Cat1", 10.0, 10);
        DetalleVenta detalle1 = new DetalleVenta(null, null, producto, 2, 10.0);
        List<DetalleVenta> detalles = Arrays.asList(detalle1);
        Venta venta = new Venta(null, cliente, new ArrayList<>(), detalles, LocalDate.now(), 20.0);

        // Simular que la inserción de la venta falla
        when(mockPstmtVenta.executeUpdate()).thenThrow(new SQLException("Error de inserción simulado"));

        // Ejecutar el método a probar
        boolean resultado = ventaDAO.guardarVenta(venta);

        // Verificaciones
        assertFalse(resultado, "La venta no debería guardarse debido al error.");
        verify(mockConnection).setAutoCommit(false);
        verify(mockPstmtVenta).executeUpdate(); // Se intenta ejecutar la inserción
        verify(mockConnection).rollback(); // Se llama a rollback
        verify(mockConnection, never()).commit(); // No debería haber commit
        verify(mockConnection).setAutoCommit(true); // Auto-commit restaurado
        verify(mockConnection).close();
    }

    /**
     * Test para el método {@code obtenerTodasLasVentas()}.
     * Simula la recuperación de una venta con cliente, detalles y descuentos.
     */
    @Test
    void testObtenerTodasLasVentas() throws SQLException {
        // Mocks para la consulta principal de ventas
        PreparedStatement mockPstmtVentas = mock(PreparedStatement.class);
        ResultSet mockRsVentas = mock(ResultSet.class);
        when(mockConnection.prepareStatement(startsWith("SELECT v.id, v.fecha, v.total"))).thenReturn(mockPstmtVentas);
        when(mockPstmtVentas.executeQuery()).thenReturn(mockRsVentas);

        // Simular una fila de venta
        when(mockRsVentas.next()).thenReturn(true, false); // Una venta, luego fin
        when(mockRsVentas.getInt("id")).thenReturn(1);
        when(mockRsVentas.getDate("fecha")).thenReturn(Date.valueOf(LocalDate.now()));
        when(mockRsVentas.getDouble("total")).thenReturn(100.0);
        // Datos del cliente
        when(mockRsVentas.getString("cliente_tipoCliente")).thenReturn("Particular");
        when(mockRsVentas.getInt("cliente_id")).thenReturn(10);
        when(mockRsVentas.getString("cliente_email")).thenReturn("cliente@test.com");
        when(mockRsVentas.getString("cliente_telefono")).thenReturn("123456789");
        when(mockRsVentas.getString("cliente_direccion")).thenReturn("Dir Cliente");
        when(mockRsVentas.getString("cliente_cifnif")).thenReturn("12345678X");
        when(mockRsVentas.getDate("cliente_fechaAlta")).thenReturn(Date.valueOf(LocalDate.now()));
        when(mockRsVentas.getString("cliente_nombre")).thenReturn("Juan");
        when(mockRsVentas.getString("cliente_apellidos")).thenReturn("Perez");

        // Mocks para la consulta de descuentos
        PreparedStatement mockPstmtDescuentos = mock(PreparedStatement.class);
        ResultSet mockRsDescuentos = mock(ResultSet.class);
        when(mockConnection.prepareStatement(startsWith("SELECT d.idDescuento"))).thenReturn(mockPstmtDescuentos);
        when(mockPstmtDescuentos.executeQuery()).thenReturn(mockRsDescuentos);
        // Simular una fila de descuento
        when(mockRsDescuentos.next()).thenReturn(true, false); // Un descuento, luego fin
        when(mockRsDescuentos.getInt("idDescuento")).thenReturn(101);
        when(mockRsDescuentos.getInt("clienteId")).thenReturn(10);
        when(mockRsDescuentos.getString("descripcion")).thenReturn("Desc Test");
        when(mockRsDescuentos.getDouble("porcentaje")).thenReturn(10.0);
        when(mockRsDescuentos.getDate("fechaInicio")).thenReturn(Date.valueOf(LocalDate.now()));
        when(mockRsDescuentos.getDate("fechaCaducidad")).thenReturn(Date.valueOf(LocalDate.now().plusDays(30)));
        when(mockRsDescuentos.getBoolean("estado")).thenReturn(true);

        // Mocks para la consulta de detalles de venta
        PreparedStatement mockPstmtDetalles = mock(PreparedStatement.class);
        ResultSet mockRsDetalles = mock(ResultSet.class);
        when(mockConnection.prepareStatement(startsWith("SELECT dv.id, dv.cantidad"))).thenReturn(mockPstmtDetalles);
        when(mockPstmtDetalles.executeQuery()).thenReturn(mockRsDetalles);
        // Simular una fila de detalle de venta
        when(mockRsDetalles.next()).thenReturn(true, false); // Un detalle, luego fin
        when(mockRsDetalles.getInt("id")).thenReturn(201);
        when(mockRsDetalles.getInt("cantidad")).thenReturn(2);
        when(mockRsDetalles.getDouble("precio_unitario")).thenReturn(50.0);
        // Datos del producto
        when(mockRsDetalles.getInt("producto_id")).thenReturn(1001);
        when(mockRsDetalles.getString("producto_nombre")).thenReturn("Producto Venta");
        when(mockRsDetalles.getString("producto_descripcion")).thenReturn("Desc Prod Venta");
        when(mockRsDetalles.getString("producto_categoria")).thenReturn("Cat Venta");
        when(mockRsDetalles.getDouble("producto_precio")).thenReturn(50.0);
        when(mockRsDetalles.getInt("producto_stock")).thenReturn(10);


        // Ejecutar el método a probar
        List<Venta> ventas = ventaDAO.obtenerTodasLasVentas();

        // Verificaciones
        assertNotNull(ventas);
        assertFalse(ventas.isEmpty());
        assertEquals(1, ventas.size());

        Venta venta = ventas.get(0);
        assertEquals(1, venta.getId());
        assertEquals(100.0, venta.getTotal(), 0.001);
        assertNotNull(venta.getCliente());
        assertEquals("Juan", venta.getCliente().getNombre());

        assertFalse(venta.getDescuentos().isEmpty());
        assertEquals(1, venta.getDescuentos().size());
        assertEquals("Desc Test", venta.getDescuentos().get(0).getDescripcion());

        assertFalse(venta.getDetalleVenta().isEmpty());
        assertEquals(1, venta.getDetalleVenta().size());
        assertEquals("Producto Venta", venta.getDetalleVenta().get(0).getProducto().getNombre());

        verify(mockConnection).close(); // La conexión debería cerrarse al final
    }
}
