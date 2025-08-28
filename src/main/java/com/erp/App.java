package com.erp;

import com.erp.db.SQLiteConnector;
import com.erp.utils.DatosDePrueba;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Clase principal que inicia y configura la aplicación JavaFX del Mini ERP.
 * <p>
 * Hereda de {@link Application}, sirviendo como el punto de entrada para el framework JavaFX.
 * Se encarga de inicializar la base de datos, cargar la vista principal y aplicar los estilos.
 *
 * @author Noé
 */
public class App extends Application {

    /**
     * El método main es el primer código que se ejecuta.
     * <p>
     * Su única responsabilidad es llamar a {@link #launch(String...)}, que inicia el
     * ciclo de vida de la aplicación JavaFX, lo que eventualmente lleva a la
     * ejecución del método {@link #start(Stage)}.
     *
     * @param args Argumentos de la línea de comandos (no los usamos aquí).
     */
    public static void main(String[] args) {
        // Lanza la aplicación JavaFX, que a su vez llamará al método start().
        launch(args);
    }

    /**
     * Este método se ejecuta justo después de 'launch()'. Aquí es donde se
     * configura y se muestra la interfaz gráfica principal.
     *
     * @see <a href="https://docs.oracle.com/javase/8/javafx/api/javafx/application/Application.html#start-javafx.stage.Stage-">Documentación de Application#start</a>
     * @param stage El 'escenario' o ventana principal que nos proporciona JavaFX.
     * @throws Exception Si hay algún error al cargar el archivo FXML o CSS.
     */
    @Override
    public void start(Stage stage) throws Exception {
        // Antes de mostrar nada, me aseguro de que la base de datos esté lista.
        // Esto crea las tablas si es la primera vez que se ejecuta.
        SQLiteConnector.initDatabase();
        DatosDePrueba.cargarDatosIniciales();
        
        
        // Carga el diseño de la interfaz principal desde el archivo FXML.
        // Parent es un nodo genérico que puede contener a todos los demás nodos de la UI.
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/main.fxml"));

        // Crea la 'escena' que contendrá el diseño que se acaba de cargar.
        // Se define un tamaño inicial para la ventana.
        Scene scene = new Scene(root, 802, 600);

        // Localiza y aplica la hoja de estilos 'estilo.css' a toda la escena.
        // Esto asegura que todos los componentes tengan una apariencia consistente.
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

   
}
