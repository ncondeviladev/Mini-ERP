package com.erp.dao;

import com.erp.model.Cliente;
import org.junit.jupiter.api.*;
import java.time.LocalDate;
import java.util.List;

class ClienteDAOTest {

    private ClienteDAO clienteDAO;

    @BeforeEach
    void setUp() {
        clienteDAO = new ClienteDAO();
    }

    @Test
    void testGuardarYBuscarClienteParticular() throws Exception {
        Cliente nuevo = Cliente.crearParticular(
            null, "test@email.com", "123456789", "Calle Falsa 123", "12345678A",
            LocalDate.now(), "Juan", "Pérez"
        );
        Assertions.assertTrue(clienteDAO.guardarClienteDb(nuevo));
        Cliente buscado = clienteDAO.buscarClientePorId(nuevo.getId());
        Assertions.assertNotNull(buscado);
        Assertions.assertEquals("Juan", buscado.getNombre());
    }

    @Test
    void testListarClientes() {
        List<Cliente> lista = clienteDAO.listarClientes();
        Assertions.assertNotNull(lista);
        Assertions.assertTrue(lista.size() >= 0);
    }

    @Test
    void testActualizarYEliminarCliente() throws Exception {
        Cliente nuevo = Cliente.crearEmpresa(
            null, "empresa@email.com", "987654321", "Av. Empresa 1", "B12345678",
            LocalDate.now(), "Empresa S.A.", "Contacto"
        );
        Assertions.assertTrue(clienteDAO.guardarClienteDb(nuevo));
        nuevo.setEmail("nuevo@email.com");
        Assertions.assertTrue(clienteDAO.actualizarClienteEnDb(nuevo));
        Assertions.assertTrue(clienteDAO.eliminarClientePorId(nuevo.getId()));
    }
}
