package com.erp.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Clase encargada de gestionar la conexión con la base de datos SQLite.
 * - Abre y reutiliza una conexión compartida (singleton).
 * - Inicializa la estructura de tabla al arrancar la app.
 * - Permite cerrar la conexión cuando se finaliza el uso.
 * Autor: Noé
 */
public class SQLiteConnector {

    // Ruta relativa al archivo físico de la base de datos
    private static final String DB_URL = "src/main/resources/database/erp.db";

    // Conexión única compartida en toda la app
    private static Connection connection = null;

    /**
     * Devuelve una conexión activa a la base de datos SQLite.
     * Si no existe o está cerrada, se crea una nueva.
     * @return instancia de Connection
     * @throws SQLException si falla la conexión
     */
    public static Connection connect() throws SQLException {
        // Solo crea conexión si no existe o fue cerrada previamente
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection("jdbc:sqlite:" + DB_URL);
        }
        return connection;
    }

    /**
     * Inicializa la base de datos creando la tabla 'productos' si no existe.
     * Este método debe llamarse una sola vez al inicio de la aplicación.
     */
    public static void initDatabase() {
        // Consulta SQL para crear tabla con columnas necesarias
        String createTableSQL = "CREATE TABLE IF NOT EXISTS productos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +        // ID autoincremental
                "nombre TEXT NOT NULL," +                        // Nombre obligatorio
                "descripcion TEXT," +                            // Descripción opcional
                "categoria TEXT," +                              // Categoría opcional
                "precioUnitario REAL," +                         // Precio en formato decimal
                "stock INTEGER" +                                // Stock como número entero
                ");";

        // Ejecuta la consulta usando un Statement
        try (Statement stmt = connect().createStatement()) {
            stmt.execute(createTableSQL); // Crea tabla si no existe
            System.out.println("Tabla 'productos' creada.");
        } catch (SQLException e) {
            // Muestra error si falla la creación
            System.out.println("Error al crear tabla 'productos'.. " + e.getMessage());
        }
    }

    /**
     * Cierra la conexión compartida si está activa.
     * Este método puede llamarse al cerrar la aplicación.
     */
    public static void closeConnection() {
        try {
            // Verifica que exista una conexión abierta
            if (connection != null && !connection.isClosed()) {
                connection.close(); // Cierra la conexión
                System.out.println("Conexión cerrada");
            }
        } catch (SQLException e) {
            // Muestra error si falla el cierre
            System.out.println("Error al cerrar la conexión.. " + e.getMessage());
        }
    }
}
