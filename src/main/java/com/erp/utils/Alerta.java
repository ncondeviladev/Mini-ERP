package com.erp.utils;

import java.util.Optional;

import javafx.animation.PauseTransition;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.util.Duration;

public class Alerta {

    /**
     * Muestra una alerta de JavaFX que permanece abierta hasta que el usuario la cierra.
     *
     * @param type    Tipo de alerta (INFORMATION, WARNING, ERROR, CONFIRMATION).
     * @param title   Título de la ventana de alerta.
     * @param header  Encabezado de la alerta (puede ser null).
     * @param content Contenido principal del mensaje de la alerta.
     */
    public static void mostrarAlerta(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Muestra una alerta de JavaFX que se cierra automáticamente después de 2 segundos.
     *
     * @param type    Tipo de alerta (INFORMATION, WARNING, ERROR, CONFIRMATION).
     * @param title   Título de la ventana de alerta.
     * @param header  Encabezado de la alerta (puede ser null).
     * @param content Contenido principal del mensaje de la alerta.
     */
    public static void mostrarAlertaTemporal(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        // Configurar la alerta para que se cierre automáticamente después de 2 segundos
        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(event -> alert.hide());
        alert.show();
        delay.play();
    }

    /**
     * Muestra una alerta de información.
     * @param title Título de la alerta.
     * @param content Contenido del mensaje.
     */
    public static void mostrarInformacion(String title, String content) {
        mostrarAlerta(AlertType.INFORMATION, title, null, content);
    }

    /**
     * Muestra una alerta de advertencia.
     * @param title Título de la alerta.
     * @param content Contenido del mensaje.
     */
    public static void mostrarAdvertencia(String title, String content) {
        mostrarAlerta(AlertType.WARNING, title, null, content);
    }

    /**
     * Muestra una alerta de error.
     * @param title Título de la alerta.
     * @param content Contenido del mensaje.
     */
    public static void mostrarError(String title, String content) {
        mostrarAlerta(AlertType.ERROR, title, null, content);
    }

    /**
     * Muestra una alerta de confirmación y devuelve true si el usuario presiona OK, false en caso contrario.
     * @param title Título de la alerta.
     * @param header Encabezado de la alerta.
     * @param content Contenido del mensaje.
     * @return true si el usuario presiona OK, false en caso contrario.
     */
    public static boolean mostrarConfirmacion(String title, String header, String content) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}