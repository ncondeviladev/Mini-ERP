package com.erp.utils;

import javafx.animation.ScaleTransition;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Clase de utilidad para aplicar animaciones comunes a nodos de JavaFX.
 * <p>
 * Proporciona métodos estáticos para añadir efectos visuales de forma sencilla,
 * evitando la duplicación de código en los controladores.
 * Autor: Noé
 */
public class AnimationUtils {

    /**
     * Añade una animación de escalado sutil a un nodo cuando el ratón
     * entra o sale de su área. El efecto es elegante y no invasivo.
     *
     * @param node El nodo de JavaFX al que se le aplicará la animación (ej. un Button).
     */
    public static void addHoverAnimation(Node node) {
        final Duration duration = Duration.millis(150);

        ScaleTransition scaleUp = new ScaleTransition(duration, node);
        scaleUp.setToX(1.05);
        scaleUp.setToY(1.05);

        ScaleTransition scaleDown = new ScaleTransition(duration, node);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        node.setOnMouseEntered(event -> scaleUp.play());
        node.setOnMouseExited(event -> scaleDown.play());
    }
}