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

## 5. Plan de Desarrollo Actual: Módulo de Ventas

**Objetivo**: Construir un módulo de ventas completo.

- **Fase 1**: Preparar bases (Modelos `Venta`/`LineaVenta`, `VentaDAO`, tablas en BD, añadir librería iText para PDFs).
- **Fase 2**: Implementar la lógica de selección de productos (Reutilizar tabla de productos, añadir una segunda tabla como "carrito").
- **Fase 3**: Finalizar la venta (Diálogo para seleccionar cliente, aplicar descuentos, guardar en BD).
- **Fase 4**: Generar factura en PDF.
