package com.erp;

// Importo la clase que se encarga de inicializar y gestionar la conexión con la base de datos.
import com.erp.db.SQLiteConnector;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Clase principal que arranca toda la aplicación del Mini ERP.
 * Como extiende de 'Application', es el punto de entrada para JavaFX.
 * Su responsabilidad es cargar la ventana principal (main.fxml),
 * inicializar la base de datos y aplicar los estilos CSS.
 *
 * @author Noé
 */
public class App extends Application {

    /**
     * El método main es el primer código que se ejecuta.
     * Su única función es llamar a 'launch(args)', que a su vez
     * inicia el ciclo de vida de la aplicación JavaFX y llama al método start().
     *
     * @param args Argumentos de la línea de comandos (no los usamos aquí).
     */
    public static void main(String[] args) {
        // Lanza la aplicación JavaFX.
        launch(args);
    }

    /**
     * Este método se ejecuta justo después de 'launch()'. Aquí es donde se
     * configura y se muestra la interfaz gráfica principal.
     *
     * @param stage El 'escenario' o ventana principal que nos proporciona JavaFX.
     * @throws Exception Si hay algún error al cargar el archivo FXML o CSS.
     */
    @Override
    public void start(Stage stage) throws Exception {
        // Antes de mostrar nada, me aseguro de que la base de datos esté lista.
        // Esto crea las tablas si es la primera vez que se ejecuta.
        SQLiteConnector.initDatabase();

        // Cargo el diseño de la interfaz principal desde el archivo FXML.
        // Parent es un nodo genérico que puede contener a todos los demás.
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/main.fxml"));

        // Creo la 'escena' que contendrá el diseño que acabo de cargar.
        // Le doy un tamaño inicial a la ventana.
        Scene scene = new Scene(root, 802, 600);

        // Localizo y aplico mi hoja de estilos 'estilo.css' a toda la escena.
        // Así todos los componentes tendrán el mismo aspecto.
        scene.getStylesheets().add(
            getClass().getClassLoader().getResource("css/estilo.css").toExternalForm()
        );

        // Configuro la ventana principal:
        stage.setMinWidth(700); // Establezco un ancho mínimo para que no se vea mal.
        stage.setMinHeight(550); // Y también un alto mínimo.
        stage.setScene(scene); // Le digo a la ventana qué escena debe mostrar.
        stage.setTitle("Mini ERP"); // Pongo el título a la ventana.
        stage.show(); // ¡Y finalmente, la muestro!
    }

    /*
     * Dejo este bloque comentado por si necesito hacer una prueba rápida
     * de JavaFX sin tener que cargar toda la aplicación.
     * Es útil para verificar que el entorno funciona correctamente.
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
