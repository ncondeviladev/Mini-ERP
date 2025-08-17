# Gemini Project Context: Mini ERP

## 1. Visión General

Este es un proyecto de un Mini-ERP de escritorio desarrollado en Java.

- **Lenguaje**: Java 17
- **Interfaz de Usuario**: JavaFX
- **Gestión de Proyecto**: Maven
- **Base de Datos**: SQLite
- **Testing**: JUnit 5, Mockito

## 2. Arquitectura y Patrones de Diseño

El proyecto sigue un patrón de diseño **Modelo-Vista-Controlador (MVC)** muy bien definido, con una capa de **Acceso a Datos (DAO)** para aislar la lógica de la base de datos.

- **Navegación Centralizada**: `MainController.java` actúa como el controlador principal. Carga las diferentes vistas (paneles) en un `StackPane` central.
- **Comunicación entre Controladores**: Se realiza mediante inyección de dependencias manual. El `MainController` pasa una referencia de sí mismo a los controladores hijos (`setMainController(this)`) para que estos puedan invocar métodos de navegación globales (ej. `mainController.mostrarDescuentos(cliente)`).
- **Patrón de UI en Módulos (Producto/Cliente)**:
    - Se usa una `TableView` para mostrar los datos principales.
    - Existen dos contenedores `VBox` (`formularioAñadir...` y `formularioBuscar...`) que se muestran u ocultan para las operaciones de CRUD y búsqueda.
    - Los métodos `mostrarVistaAñadir()` y `mostrarVistaBuscar()` gestionan la visibilidad de estos formularios.
    - El filtrado de las tablas se hace en tiempo real, añadiendo un `listener` a la propiedad `textProperty` de los campos de búsqueda que invoca a un método `filtrar...()`.
- **Validación**: Se utiliza una clase de utilidad `ValidationUtils` para validar formatos de NIF/CIF, email, etc.

## 3. Estructura de Ficheros Clave

- `pom.xml`: Define las dependencias y configuración de Maven.
- `src/main/java/com/erp/App.java`: Punto de entrada de la aplicación JavaFX.
- `src/main/java/com/erp/model/`: Contiene las clases del modelo (POJOs como `Cliente.java`, `Producto.java`).
- `src/main/java/com/erp/dao/`: Contiene las clases de Acceso a Datos (`ClienteDAO.java`, `ProductoDAO.java`).
- `src/main/java/com/erp/controller/`: Contiene los controladores de las vistas (`MainController.java`, `ClienteController.java`, etc.).
- `src/main/java/com/erp/utils/`: Clases de utilidad (`AnimationUtils.java`, `ValidationUtils.java`).
- `src/main/java/com/erp/db/`: Contiene el conector de la base de datos (`SQLiteConnector.java`).
- `src/main/resources/fxml/`: Contiene las definiciones de las vistas (archivos FXML).
- `src/main/resources/database/`: Destino de la base de datos SQLite.
- `src/main/resources/css/`: Hojas de estilo.

## 4. Dependencias Externas Notables

- `org.openjfx:javafx-controls`: Controles de JavaFX.
- `org.openjfx:javafx-fxml`: Soporte para FXML en JavaFX.
- `org.xerial:sqlite-jdbc`: Driver para la base de datos SQLite.
- `com.itextpdf:itext7-core`: Librería para la generación de documentos PDF.

## 5. Conceptos Clave de Diseño

### Transacciones en Base de Datos (`VentaDAO`)
Para asegurar la integridad de los datos al guardar una venta (que implica múltiples operaciones: `INSERT` en `ventas`, `INSERT` en `detalles_venta`, etc.), se utiliza una transacción manual.

1.  **`conn.setAutoCommit(false)`**: Se desactiva el modo auto-commit al inicio. Esto le dice a la base de datos que no guarde permanentemente ninguna operación hasta que se le indique.
2.  **`conn.commit()`**: Se ejecuta al final, solo si todas las operaciones han tenido éxito. Hace permanentes todos los cambios de la transacción.
3.  **`conn.rollback()`**: Se ejecuta en el bloque `catch` si ocurre cualquier error. Deshace todos los cambios realizados durante la transacción, dejando la base de datos en su estado original y evitando datos corruptos (ej. una venta sin detalles).

## 6. Plan de Desarrollo Actual: Módulo de Ventas

**Objetivo**: Construir un módulo de ventas completo.

- **Fase 1: Preparar las bases [COMPLETADA]**
    - Modelos `Venta`/`DetalleVenta` creados.
    - `VentaDAO` con lógica transaccional implementado.
    - Tablas `ventas` y `detalles_venta` añadidas a `SQLiteConnector`.
    - Librería iText para PDFs añadida al `pom.xml`.

- **Fase 2: Lógica de la Venta (Seleccionar Productos) [EN CURSO]**
    - **Tarea Actual**: Crear la vista `venta.fxml` y su controlador `VentaController.java` (Controlador creado, pendiente de implementar la lógica y la vista FXML).
    - Reutilizar la tabla de productos y la funcionalidad de búsqueda.
    - Implementar una segunda tabla como "carrito".

- **Fase 3: Finalizar la Venta [PENDIENTE]**
    - Diálogo para seleccionar cliente.
    - Aplicar descuentos.
    - Guardar en BD.

- **Fase 4: Generar Factura [PENDIENTE]**
    - Crear clase de utilidad para generar un PDF de la factura con iText.

## 7. Arquitectura de Componentes FXML Reutilizables

Para evitar la duplicación de código y fomentar un diseño modular, el proyecto adopta una estrategia de componentes FXML reutilizables.

- **Principio:** Cada pieza de la interfaz que se usa en más de un lugar (ej. un formulario de búsqueda, una tabla de datos) se extrae a su propio fichero FXML. Estos ficheros se almacenan en `src/main/resources/fxml/components/`.

- **Un Controlador por Componente:** Cada fichero FXML de un componente tiene su propio controlador Java asociado (`fx:controller`). Esto es fundamental por dos razones:
    1.  **Limitación Técnica de `@FXML`**: Un controlador solo puede inyectar (`@FXML`) los nodos definidos dentro de su *propio* fichero FXML. Un controlador "padre" no puede acceder directamente a los nodos de un FXML incluido.
    2.  **Encapsulación**: El componente se convierte en una "caja negra" que gestiona su propia lógica interna.

- **Patrón "Controlador de Controladores"**: La comunicación entre un controlador padre (ej. `ProductoController`) y el controlador de un componente incluido se realiza de la siguiente manera:
    1.  En el FXML padre, se le da un `fx:id` a la inclusión:
        `<fx:include fx:id="formBusqueda" source="components/producto-formulario-buscar.fxml"/>`
    2.  En el controlador padre, se inyecta el *controlador del componente* usando una convención de nombrado: el `fx:id` del `include` seguido de la palabra "Controller".
        ```java
        // Inyecta el controlador del componente, no sus piezas internas
        @FXML private ProductoFormularioBuscarController formBusquedaController;
        ```
    3.  El controlador padre interactúa con el componente a través de los métodos públicos de su controlador, dándole órdenes (`formBusquedaController.limpiarCampos()`) o pidiéndole información (`formBusquedaController.getTextoBusqueda()`), sin conocer sus detalles internos.

Este patrón asegura que el código sea limpio, mantenible y verdaderamente modular.
