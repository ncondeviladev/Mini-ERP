package com.erp;

// Importa clase que se encarga de inicializar la base de datos SQLite
import com.erp.db.SQLiteConnector;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Clase principal de la aplicación ERP.
 * Extiende Application para iniciar una app JavaFX.
 * Se encarga de cargar la vista FXML y aplicar estilos al iniciar.
 * Autor: Noé
 */
public class App extends Application {

    // Método principal: punto de entrada de la aplicación
    public static void main(String[] args) {
        // Inicia la aplicación JavaFX llamando al método start()
        launch(args);
    }

    /**
     * Método que se ejecuta al iniciar la aplicación.
     * @param stage Ventana principal del programa
     * @throws Exception En caso de que falle la carga del FXML o CSS
     */
    @Override
    public void start(Stage stage) throws Exception {
        // Inicializa la base de datos SQLite (crea tablas si no existen, etc.)
        SQLiteConnector.initDatabase();

        // Carga la interfaz definida en producto.fxml desde el directorio /fxml
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/main.fxml"));

        // Crea la escena principal con el contenido del FXML y establece tamaño inicial
        Scene scene = new Scene(root, 600, 450);

        // Aplica la hoja de estilos CSS desde el directorio /css
        scene.getStylesheets().add(
            getClass().getClassLoader().getResource("css/estilo.css").toExternalForm()
        );

        // Asigna la escena a la ventana y muestra el título de la aplicación
        stage.setScene(scene);
        stage.setTitle("Mini ERP: Productos");
        stage.show(); // Muestra la ventana al usuario
    }

    /*
     * Este bloque comentado es una versión alternativa de start()
     * que muestra solo una etiqueta en pantalla.
     * Se usa comúnmente para pruebas rápidas de JavaFX sin cargar FXML.
     *
     * @Override
     * public void start(Stage stage) {
     *     Label label = new Label("Hola, Noé 👋");
     *     Scene scene = new Scene(new StackPane(label), 400, 200);
     *     stage.setScene(scene);
     *     stage.setTitle("Prueba JavaFX");
     *     stage.show();
     * }
     */
}
