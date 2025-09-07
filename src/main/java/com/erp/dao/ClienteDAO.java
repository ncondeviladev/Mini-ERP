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
 * DAO (Data Access Object) para la entidad {@link Cliente}.
 * 
 * <p>Esta clase encapsula toda la lógica de acceso a datos para la tabla `clientes`
 * en la base de datos. Proporciona métodos para realizar las operaciones CRUD 
 * (Crear, Leer, Actualizar, Eliminar) de manera que el resto de la aplicación
 * no necesita conocer los detalles de las sentencias SQL ni de la gestión de la
 * conexión con la base de datos.</p>
 *
 * @author Noé
 * @see Cliente
 * @see com.erp.db.SQLiteConnector
 */
public class ClienteDAO {

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
    public ClienteDAO() {
        try {
            this.conexion = SQLiteConnector.connect();
        } catch (SQLException e) {
            // Si no se puede conectar, es un error crítico. Se lanza una excepción no comprobada.
            throw new RuntimeException("Error fatal: no se pudo conectar con la base de datos.", e);
        }
    }

    /**
     * Guarda un nuevo cliente en la base de datos.
     * <p>
     * El método inspecciona el {@code tipoCliente} del objeto {@link Cliente} para
     * construir la sentencia SQL de inserción adecuada, ya sea para un "Particular" o una "Empresa".
     * Utiliza un {@link PreparedStatement} para prevenir inyección SQL y gestiona la
     * recuperación del ID autogenerado por la base de datos, asignándolo de nuevo al objeto.
     * </p>
     * 
     * @param cliente El objeto {@code Cliente} con los datos a guardar. Su ID debe ser nulo.
     * @return {@code true} si el cliente se guardó correctamente, {@code false} en caso contrario.
     */
    public boolean guardarClienteDb(Cliente cliente) {
        String sql;
        
        // Paso 1: Determinar la sentencia SQL correcta basada en el tipo de cliente.
        // Esto asegura que solo se insertan los campos relevantes para cada tipo.
        if ("Particular".equals(cliente.getTipoCliente())) {
            sql = "INSERT INTO clientes(tipoCliente, email, telefono, direccion, cifnif, fechaAlta, nombre, apellidos) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        } else if ("Empresa".equals(cliente.getTipoCliente())) {
            sql = "INSERT INTO clientes(tipoCliente, email, telefono, direccion, cifnif, fechaAlta, razonSocial, personaContacto) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        } else {
            // Si el tipo de cliente no es reconocido, se aborta la operación.
            System.err.println("Error: Tipo de cliente no reconocido: " + cliente.getTipoCliente());
            return false;
        }

        // Se utiliza un try-with-resources para asegurar que el PreparedStatement se cierre automáticamente.
        try (PreparedStatement stmt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // Paso 2: Asignar los valores a los parámetros del PreparedStatement.
            stmt.setString(1, cliente.getTipoCliente());
            stmt.setString(2, cliente.getEmail());
            stmt.setString(3, cliente.getTelefono());
            stmt.setString(4, cliente.getDireccion());
            stmt.setString(5, cliente.getCifnif());
            stmt.setString(6, cliente.getFechaAlta().toString()); // SQLite no tiene un tipo DATE nativo, se guarda como TEXT.

            // Asignar los campos específicos del tipo de cliente.
            if ("Particular".equals(cliente.getTipoCliente())) {
                stmt.setString(7, cliente.getNombre());
                stmt.setString(8, cliente.getApellidos());
            } else { // Es una Empresa
                stmt.setString(7, cliente.getRazonSocial());
                stmt.setString(8, cliente.getPersonaContacto());
            }

            int filasAfectadas = stmt.executeUpdate();

            // Paso 3: Si la inserción fue exitosa, recuperar el ID autogenerado.
            if (filasAfectadas > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        // Se asigna el ID generado al objeto cliente para que esté sincronizado con la BD.
                        cliente.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar el cliente en la base de datos.");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Actualiza los datos de un cliente existente en la base de datos.
     * <p>
     * Construye dinámicamente la sentencia SQL de actualización. Si se cambia el tipo de cliente
     * (de Particular a Empresa o viceversa), los campos que no correspondan al nuevo tipo
     * se establecen a {@code NULL} en la base de datos para mantener la consistencia.
     * </p>
     *
     * @param cliente El objeto {@code Cliente} con los datos actualizados. Debe tener un ID válido.
     * @return {@code true} si la actualización fue exitosa, {@code false} en caso contrario.
     */
    public boolean actualizarClienteEnDb(Cliente cliente) {
        // Un cliente sin ID no puede ser actualizado porque no se puede identificar en la BD.
        if (cliente.getId() == null) {
            return false;
        }

        // Se usa StringBuilder para construir la consulta dinámicamente.
        StringBuilder sql = new StringBuilder("UPDATE clientes SET email = ?, telefono = ?, direccion = ?, cifnif = ?, fechaAlta = ?");
        
        // Añadir los campos específicos y limpiar los que no correspondan.
        if ("Particular".equals(cliente.getTipoCliente())) {
            sql.append(", nombre = ?, apellidos = ?, razonSocial = NULL, personaContacto = NULL, tipoCliente = 'Particular'");
        } else if ("Empresa".equals(cliente.getTipoCliente())) {
            sql.append(", razonSocial = ?, personaContacto = ?, nombre = NULL, apellidos = NULL, tipoCliente = 'Empresa'");
        } else {
            return false; // Tipo de cliente no reconocido.
        }
        sql.append(" WHERE id = ?");

        try (PreparedStatement stmt = conexion.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            // Asignar los valores a los parámetros comunes.
            stmt.setString(paramIndex++, cliente.getEmail());
            stmt.setString(paramIndex++, cliente.getTelefono());
            stmt.setString(paramIndex++, cliente.getDireccion());
            stmt.setString(paramIndex++, cliente.getCifnif());
            stmt.setString(paramIndex++, cliente.getFechaAlta().toString());

            // Asignar los valores a los parámetros específicos del tipo.
            if ("Particular".equals(cliente.getTipoCliente())) {
                stmt.setString(paramIndex++, cliente.getNombre());
                stmt.setString(paramIndex++, cliente.getApellidos());
            } else if ("Empresa".equals(cliente.getTipoCliente())) {
                stmt.setString(paramIndex++, cliente.getRazonSocial());
                stmt.setString(paramIndex++, cliente.getPersonaContacto());
            }
            // Finalmente, asignar el ID para la cláusula WHERE.
            stmt.setInt(paramIndex, cliente.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar el cliente en la base de datos.");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina un cliente de la base de datos usando su ID.
     *
     * @param id El ID del cliente a eliminar.
     * @return {@code true} si se eliminó correctamente (se afectó una o más filas), {@code false} en caso contrario.
     */
    public boolean eliminarClientePorId(Integer id) {
        String sql = "DELETE FROM clientes WHERE id = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar el cliente de la base de datos.");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Busca y devuelve un cliente por su ID.
     *
     * @param id El ID del cliente a buscar.
     * @return Un objeto {@code Cliente} si se encuentra, o {@code null} si no existe un cliente con ese ID.
     */
    public Cliente buscarClientePorId(Integer id) {
        String sql = "SELECT * FROM clientes WHERE id = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            // Si el ResultSet tiene al menos una fila, construimos el objeto Cliente.
            if (rs.next()) {
                return construirCliente(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar el cliente por ID.");
            e.printStackTrace();
        }
        return null; // No se encontró el cliente.
    }

    /**
     * Devuelve una lista con todos los clientes registrados en la base de datos.
     *
     * @return Una {@code List<Cliente>} con todos los clientes. Si no hay clientes, la lista estará vacía.
     */
    public List<Cliente> listarClientes() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes ORDER BY id";

        // Se usa un try-with-resources para Statement y ResultSet.
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            // Se itera sobre cada fila del resultado.
            while (rs.next()) {
                // Se construye un objeto Cliente por cada fila y se añade a la lista.
                clientes.add(construirCliente(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar los clientes.");
            e.printStackTrace();
        }
        return clientes;
    }

    /**
     * Método de ayuda (helper method) para construir un objeto {@link Cliente} a partir de un {@link ResultSet}.
     * <p>
     * Este método centraliza la lógica de creación de objetos Cliente desde los datos de la BD.
     * Lee la columna {@code tipoCliente} y utiliza el método de fábrica estático apropiado
     * ({@code Cliente.crearParticular} o {@code Cliente.crearEmpresa}) para instanciar el objeto.
     * </p>
     *
     * @param rs El ResultSet posicionado en la fila que contiene los datos del cliente.
     * @return Un objeto {@code Cliente} completamente poblado.
     * @throws SQLException Si ocurre un error al acceder a los datos del ResultSet.
     */
    private Cliente construirCliente(ResultSet rs) throws SQLException {
        String tipoCliente = rs.getString("tipoCliente");
        
        // Dependiendo del tipo, se llama al factory method correspondiente.
        if ("Particular".equals(tipoCliente)) {
            return Cliente.crearParticular(
                    rs.getInt("id"),
                    rs.getString("email"),
                    rs.getString("telefono"),
                    rs.getString("direccion"),
                    rs.getString("cifnif"),
                    LocalDate.parse(rs.getString("fechaAlta")), // Se convierte el texto de la BD a LocalDate.
                    rs.getString("nombre"),
                    rs.getString("apellidos"));
        } else if ("Empresa".equals(tipoCliente)) {
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
        // Si el tipo no es ni "Particular" ni "Empresa", se devuelve null.
        return null;
    }
}