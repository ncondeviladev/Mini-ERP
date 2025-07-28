package com.erp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.erp.db.SQLiteConnector;
import com.erp.model.Cliente;

/**
 * DAO encargado de gestionar operaciones sobre la tabla 'clientes'
 * Compatible con el controlador de Clientes.
 * Autor: Noé
 */
public class ClienteDAO {

    private final Connection conexion;

    public ClienteDAO() {
        try {
            this.conexion = SQLiteConnector.connect();
        } catch (SQLException e) {
            throw new RuntimeException("Error al conectar con la base de datos", e);
        }
    }

    /**
     * Guarda un nuevo cliente en la base de datos.
     * El método determina si es un Particular o una Empresa y construye la consulta
     * SQL apropiada.
     * 
     * @param cliente objeto Cliente con datos.
     * @return true si el cliente se guardó correctamente, false en caso contrario.
     */
    public boolean guardarClienteDb(Cliente cliente) {
        String sql;
        // 1. Determinar la sentencia SQL basada en el tipo de cliente
        if ("Particular".equals(cliente.getTipoCliente())) {
            sql = "INSERT INTO clientes(tipoCliente, email, telefono, direccion, cifnif, fechaAlta, nombre, apellidos) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        } else if ("Empresa".equals(cliente.getTipoCliente())) {
            sql = "INSERT INTO clientes(tipoCliente, email, telefono, direccion, cifnif, fechaAlta, razonSocial, personaContacto) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        } else {
            // Tipo de cliente no reconocido, no se puede guardar.
            return false;
        }

        try (PreparedStatement stmt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // 2. Asignar los valores a los parámetros del PreparedStatement
            stmt.setString(1, cliente.getTipoCliente());
            stmt.setString(2, cliente.getEmail());
            stmt.setString(3, cliente.getTelefono());
            stmt.setString(4, cliente.getDireccion());
            stmt.setString(5, cliente.getCifnif());
            stmt.setString(6, cliente.getFechaAlta().toString()); // Guardamos la fecha como texto

            if ("Particular".equals(cliente.getTipoCliente())) {
                stmt.setString(7, cliente.getNombre());
                stmt.setString(8, cliente.getApellidos());
            } else { // Es una Empresa
                stmt.setString(7, cliente.getRazonSocial());
                stmt.setString(8, cliente.getPersonaContacto());
            }

            int filasAfectadas = stmt.executeUpdate();

            // 3. Recuperar el ID autogenerado y asignarlo al objeto cliente
            if (filasAfectadas > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        cliente.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean actualizarClienteEnDb(Cliente cliente) {

        if (cliente.getId() == null) {
            return false;
        }

        StringBuilder sql = new StringBuilder(
                "UPDATE clientes SET email = ?, telefono = ?, direccion = ?, cifnif = ?, fechaAlta = ?");
        if ("Particular".equals(cliente.getTipoCliente())) {
            sql.append(
                    ", nombre = ?, apellidos = ?, razonSocial = NULL, personaContacto = NULL, tipoCliente = 'Particular'");
        } else if ("Empresa".equals(cliente.getTipoCliente())) {
            sql.append(
                    ", razonSocial = ?, personaContacto = ?, nombre = NULL, apellidos = NULL, tipoCliente = 'Empresa'");
        } else {
            return false; // Tipo de cliente no reconocido
        }
        sql.append(" WHERE id = ?");

        try (PreparedStatement stmt = conexion.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            // Asignar los valores a los parámetros del PreparedStatement
            stmt.setString(paramIndex++, cliente.getEmail());
            stmt.setString(paramIndex++, cliente.getTelefono());
            stmt.setString(paramIndex++, cliente.getDireccion());
            stmt.setString(paramIndex++, cliente.getCifnif());
            stmt.setString(paramIndex++, cliente.getFechaAlta().toString());

            if ("Particular".equals(cliente.getTipoCliente())) {
                stmt.setString(paramIndex++, cliente.getNombre());
                stmt.setString(paramIndex++, cliente.getApellidos());
            } else if ("Empresa".equals(cliente.getTipoCliente())) {
                stmt.setString(paramIndex++, cliente.getRazonSocial());
                stmt.setString(paramIndex++, cliente.getPersonaContacto());
            }
            stmt.setInt(paramIndex++, cliente.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminarClientePorId(Integer id) {

        String sql = "DELETE FROM clientes WHERE id = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Cliente buscarClientePorId(Integer id) throws Exception {

        String sql = "SELECT * FROM clientes WHERE id = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return construirCliente(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Cliente> listarClientes() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes";
        try (PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                clientes.add(construirCliente(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return clientes;
    }

    private Cliente construirCliente(ResultSet rs) throws Exception {
        if ("Particular".equals(rs.getString("tipoCliente"))) {
            return Cliente.crearParticular(
                    rs.getInt("id"),
                    rs.getString("email"),
                    rs.getString("telefono"),
                    rs.getString("direccion"),
                    rs.getString("cifnif"),
                    LocalDate.parse(rs.getString("fechaAlta")),
                    rs.getString("nombre"),
                    rs.getString("apellidos"));
        } else if ("Empresa".equals(rs.getString("tipoCliente"))) {
            return Cliente.crearEmpresa(
                    rs.getInt("id"),
                    rs.getString("email"),
                    rs.getString("telefono"),
                    rs.getString("direccion"),
                    rs.getString("cifnif"),
                    LocalDate.parse(rs.getString("fechaAlta")),
                    rs.getString("razonSocial"),
                    rs.getString("personaContacto"));
        }
        return null;
    }
}
