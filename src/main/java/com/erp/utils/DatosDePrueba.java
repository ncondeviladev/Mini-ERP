package com.erp.utils;

import com.erp.dao.ClienteDAO;
import com.erp.dao.DescuentoDAO;
import com.erp.dao.ProductoDAO;
import com.erp.model.Cliente;
import com.erp.model.Descuento;
import com.erp.model.Producto;

import java.time.LocalDate;

/**
 * Clase de utilidad para cargar datos de prueba en la base de datos.
 * Esto es útil para tener un conjunto de datos consistente para desarrollo y pruebas.
 * Autor: Noé
 */
public class DatosDePrueba {

    /**
     * Carga un conjunto de productos, clientes y descuentos en la base de datos
     * si las tablas correspondientes están vacías.
     * <p>
     * Este método está diseñado para ser llamado una única vez al inicio de la aplicación.
     * Una vez que la base de datos está poblada, la llamada a este método puede ser eliminada.
     */
    public static void cargarDatosIniciales() {
        ProductoDAO productoDAO = new ProductoDAO();
        ClienteDAO clienteDAO = new ClienteDAO();
        DescuentoDAO descuentoDAO = new DescuentoDAO();

        // --- Carga de Productos ---
        if (productoDAO.listarProductos().isEmpty()) {
            System.out.println("Base de datos de productos vacía. Cargando datos de prueba...");
            productoDAO.guardarProductoDb(new Producto("Portátil Pro X1", "Portátil de 15 pulgadas, 16GB RAM, 512GB SSD", "Electrónica", 1250.99, 15));
            productoDAO.guardarProductoDb(new Producto("Monitor Curvo 27\"", "Monitor 4K UHD para diseño gráfico", "Electrónica", 450.50, 30));
            productoDAO.guardarProductoDb(new Producto("Teclado Mecánico RGB", "Teclado con switches Cherry MX Red", "Periféricos", 120.00, 50));
            productoDAO.guardarProductoDb(new Producto("Ratón Inalámbrico Ergo", "Ratón ergonómico con 8 botones programables", "Periféricos", 75.99, 80));
            productoDAO.guardarProductoDb(new Producto("Silla de Oficina Premium", "Silla ergonómica con soporte lumbar ajustable", "Mobiliario", 320.00, 25));
            System.out.println("Datos de productos cargados.");
        }

        // --- Carga de Clientes y Descuentos ---
        if (clienteDAO.listarClientes().isEmpty()) {
            System.out.println("Base de datos de clientes vacía. Cargando datos de prueba...");

            // Cliente 1: Particular con un descuento activo.
            Cliente cliente1 = Cliente.crearParticular("ana.perez@email.com", "611223344", "Calle Mayor 1, Madrid", "12345678A", LocalDate.now().minusMonths(6), "Ana", "Pérez García");
            clienteDAO.guardarClienteDb(cliente1); // Se guarda primero para obtener el ID asignado por la BD.

            // Cliente 2: Empresa sin descuentos.
            Cliente cliente2 = Cliente.crearEmpresa("contacto@techsolutions.com", "912345678", "Avenida de la Industria 25, Barcelona", "B87654321", LocalDate.now().minusYears(1), "Tech Solutions S.L.", "Carlos López");
            clienteDAO.guardarClienteDb(cliente2);

            // Cliente 3: Empresa con un descuento caducado y uno activo.
            Cliente cliente3 = Cliente.crearEmpresa("info@innovadesign.es", "934567890", "Paseo de Gracia 100, Barcelona", "A12345678", LocalDate.now().minusMonths(3), "Innova Design Studio", "Laura Martínez");
            clienteDAO.guardarClienteDb(cliente3);
            System.out.println("Datos de clientes cargados.");

            // --- Carga de Descuentos para los clientes ya creados ---
            descuentoDAO.guardarDescuentoDb(new Descuento(cliente1.getId(), "Descuento de Bienvenida", 10.0, LocalDate.now(), LocalDate.now().plusMonths(3)));
            descuentoDAO.guardarDescuentoDb(new Descuento(cliente3.getId(), "Campaña de Invierno (Caducado)", 15.0, LocalDate.now().minusMonths(2), LocalDate.now().minusMonths(1)));
            descuentoDAO.guardarDescuentoDb(new Descuento(cliente3.getId(), "Descuento Anual Cliente VIP", 5.0, LocalDate.now(), LocalDate.now().plusYears(1)));
            System.out.println("Datos de descuentos cargados.");
        }
    }
}
