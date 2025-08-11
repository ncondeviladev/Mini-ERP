package com.erp.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.erp.dao.ClienteDAO;
import com.erp.model.Cliente;
import com.erp.utils.ValidationUtils;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Controlador para la vista de gestión de clientes (cliente.fxml).
 * <p>
 * Maneja toda la lógica de la interfaz de usuario para añadir, modificar, eliminar y
 * buscar clientes, así como la presentación de datos en la tabla y la navegación
 * a la vista de descuentos.
 */
public class ClienteController {

    private MainController mainController;

    // --- Paneles y Contenedores ---
    @FXML
    private StackPane zonaFormulariosCliente;
    @FXML
    private VBox formularioAñadirCliente;
    @FXML
    private VBox formularioBuscarCliente;
    @FXML
    private GridPane camposParticular;
    @FXML
    private GridPane camposEmpresa;

    // --- Botones ---
    @FXML
    private Button guardarClienteButton;
    @FXML
    private Button botonModificarCliente;
    @FXML
    private Button botonEliminarCliente;
    @FXML
    private Button botonDescuentoCliente;

    // --- Campos del Formulario (Añadir/Modificar) ---
    @FXML
    private Label tituloFormularioCliente;
    @FXML
    private ComboBox<String> tipoClienteComboBox;
    // Campos de Particular
    @FXML
    private TextField nombreClienteField;
    @FXML
    private TextField apellidosClienteField;
    @FXML
    private TextField nifClienteField;
    // Campos de Empresa
    @FXML
    private TextField razonSocialClienteField;
    @FXML
    private TextField cifClienteField;
    @FXML
    private TextField personaContactoClienteField;
    // Comunes
    @FXML
    private TextField direccionClienteField;
    @FXML
    private TextField telefonoClienteField;
    @FXML
    private TextField emailClienteField;

    // --- Campos del Formulario (Buscar) ---
    @FXML
    private TextField buscarIdClienteField;
    @FXML
    private TextField buscarNombreClienteField;
    @FXML
    private TextField buscarCifApellidosClienteField;

    // --- Tabla y Columnas ---
    @FXML
    private TableView<Cliente> tablaCliente;
    @FXML
    private TableColumn<Cliente, Integer> colIdCliente;
    @FXML
    private TableColumn<Cliente, String> colNombreApellidos;
    @FXML
    private TableColumn<Cliente, String> colRazonContacto;
    @FXML
    private TableColumn<Cliente, String> colTelefonoEmail;
    @FXML
    private TableColumn<Cliente, String> colDireccion;
    @FXML
    private TableColumn<Cliente, String> colCifNif;

    // --- Lógica de Negocio ---
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ObservableList<Cliente> listaClientes = FXCollections.observableArrayList();
    private final List<Cliente> listaClientesOriginal = new ArrayList<>();
    private boolean modoEdicion = false;
    private Cliente clienteAEditar = null;

    /**
     * Inyecta una instancia del controlador principal para permitir la comunicación
     * entre controladores, como por ejemplo para abrir la vista de descuentos.
     * @param mainController El controlador principal de la aplicación.
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * Método de inicialización que se llama automáticamente después de que se cargue el FXML.
     * Configura los componentes de la UI, carga los datos iniciales y establece los listeners
     * necesarios para la interactividad.
     */
    @FXML
    public void initialize() {
        configurarComboBox();
        configurarColumnasTabla();

        tablaCliente.setItems(listaClientes);
        List<Cliente> clientesDesdeDB = clienteDAO.listarClientes();
        listaClientesOriginal.addAll(clientesDesdeDB);
        listaClientes.setAll(clientesDesdeDB);

        // Añade un listener a la selección de la tabla para habilitar/deshabilitar botones.
        tablaCliente.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean haySeleccion = newSelection != null;
            botonModificarCliente.setDisable(!haySeleccion);
            botonEliminarCliente.setDisable(!haySeleccion);
            botonDescuentoCliente.setDisable(!haySeleccion);
        });

        // Oculta los formularios al inicio para que no ocupen espacio.
        zonaFormulariosCliente.setVisible(false);
        zonaFormulariosCliente.setManaged(false);
    }

    /**
     * Configura el ComboBox para seleccionar el tipo de cliente ("Particular" o "Empresa")
     * y añade un listener para mostrar los campos de formulario correspondientes.
     */
    private void configurarComboBox() {
        tipoClienteComboBox.getItems().addAll("Particular", "Empresa");
        tipoClienteComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if ("Particular".equals(newVal)) {
                camposParticular.setVisible(true);
                camposEmpresa.setVisible(false);
            } else if ("Empresa".equals(newVal)) {
                camposParticular.setVisible(false);
                camposEmpresa.setVisible(true);
            }
        });
        tipoClienteComboBox.getSelectionModel().selectFirst();
    }

    /**
     * Configura las columnas de la TableView para mostrar los datos de los clientes.
     * Utiliza PropertyValueFactory para enlazar columnas a propiedades del modelo Cliente
     * y CellFactory personalizadas para columnas con formato complejo.
     */
    private void configurarColumnasTabla() {
        // Columna ID: simple, enlazada directamente a la propiedad 'id'.
        colIdCliente.setCellValueFactory(new PropertyValueFactory<>("id"));
        colIdCliente.setPrefWidth(40);

        // Columna Nombre/Apellidos: usa una CellFactory para mostrar nombre y apellidos
        // en dos líneas solo si el cliente es "Particular".
        colNombreApellidos.setCellValueFactory(cell -> {
            Cliente c = cell.getValue();
            if ("Particular".equals(c.getTipoCliente())) {
                return new SimpleStringProperty(c.getNombre() + "\n" + c.getApellidos());
            }
            return new SimpleStringProperty(""); // Vacío si es empresa
        });
        setWrappingCellFactory(colNombreApellidos);
        colNombreApellidos.setPrefWidth(100);

        // Columna Razón Social/Contacto: similar a la anterior, pero para "Empresa".
        colRazonContacto.setCellValueFactory(cell -> {
            Cliente c = cell.getValue();
            if ("Empresa".equals(c.getTipoCliente())) {
                return new SimpleStringProperty(c.getRazonSocial() + "\n" + c.getPersonaContacto());
            }
            return new SimpleStringProperty(""); // Vacío si es particular
        });
        setWrappingCellFactory(colRazonContacto);
        colRazonContacto.setPrefWidth(120);

        // Columna Teléfono/Email: combina dos campos en uno, separados por un salto de línea.
        colTelefonoEmail.setCellValueFactory(cell -> {
            Cliente c = cell.getValue();
            return new SimpleStringProperty(c.getTelefono() + "\n" + c.getEmail());
        });
        setWrappingCellFactory(colTelefonoEmail);
        colTelefonoEmail.setPrefWidth(120);

        // Columna Dirección: enlazada directamente, pero con ajuste de texto.
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        setWrappingCellFactory(colDireccion);
        colDireccion.setPrefWidth(120);

        // Columna CIF/NIF: enlazada directamente.
        colCifNif.setCellValueFactory(new PropertyValueFactory<>("cifnif"));
        colCifNif.setPrefWidth(90);
    }

    /**
     * Helper para configurar una celda de tabla que ajuste automáticamente el texto
     * en múltiples líneas si no cabe en el ancho de la columna.
     * @param column La columna a la que se aplicará la CellFactory.
     */
    private void setWrappingCellFactory(TableColumn<Cliente, String> column) {
        column.setCellFactory(tc -> {
            TableCell<Cliente, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(javafx.scene.control.Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(column.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell;
        });
    }

    /**
     * Muestra el formulario para añadir un nuevo cliente.
     * Configura la interfaz para el modo de creación, limpiando campos, ajustando
     * textos de botones y títulos, y activando los listeners de teclado.
     */
    @FXML
    public void mostrarVistaAñadir() {
        zonaFormulariosCliente.setVisible(true);
        zonaFormulariosCliente.setManaged(true);

        formularioBuscarCliente.setVisible(false);
        formularioBuscarCliente.setManaged(false);

        formularioAñadirCliente.setVisible(true);
        formularioAñadirCliente.setManaged(true);

        modoEdicion = false;
        clienteAEditar = null;
        tituloFormularioCliente.setText("Formulario Añadir Cliente");
        guardarClienteButton.setText("Añadir Cliente");
        limpiarFormulario();
        activarEnterEnFormularioAñadir();
    }

    /**
     * Muestra el formulario de búsqueda de clientes.
     * Carga la lista completa de clientes y añade listeners a los campos de
     * búsqueda para filtrar la tabla en tiempo real.
     */
    @FXML
    public void mostrarVistaBuscar() {
        zonaFormulariosCliente.setVisible(true);
        zonaFormulariosCliente.setManaged(true);

        formularioAñadirCliente.setVisible(false);
        formularioAñadirCliente.setManaged(false);

        formularioBuscarCliente.setVisible(true);
        formularioBuscarCliente.setManaged(true);

        tablaCliente.getItems().setAll(listaClientesOriginal);

        buscarIdClienteField.textProperty().addListener((obs, old, val) -> filtrarClientes());
        buscarNombreClienteField.textProperty().addListener((obs, old, val) -> filtrarClientes());
        buscarCifApellidosClienteField.textProperty().addListener((obs, old, val) -> filtrarClientes());
        activarEnterEnFormularioBuscar();
    }

    /**
     * Filtra la lista de clientes en la tabla basándose en los criterios de los campos de búsqueda.
     * La búsqueda es insensible a mayúsculas/minúsculas y combina los filtros de ID, Nombre y NIF/CIF.
     */
    private void filtrarClientes() {
        String filtroId = buscarIdClienteField.getText().trim();
        String filtroNombre = buscarNombreClienteField.getText().trim().toLowerCase();
        String filtroNifCif = buscarCifApellidosClienteField.getText().trim().toLowerCase();

        // Si todos los filtros están vacíos, muestra la lista original completa.
        if (filtroId.isEmpty() && filtroNombre.isEmpty() && filtroNifCif.isEmpty()) {
            listaClientes.setAll(listaClientesOriginal);
            return;
        }

        // Recorre la lista original y añade a una lista temporal los que coinciden.
        List<Cliente> filtrados = new ArrayList<>();
        for (Cliente c : listaClientesOriginal) {
            boolean matchId = filtroId.isEmpty() || String.valueOf(c.getId()).equals(filtroId.toLowerCase());

            boolean matchNombre = filtroNombre.isEmpty();
            if (!matchNombre) {
                if ("Particular".equals(c.getTipoCliente())) {
                    matchNombre = (c.getNombre() != null && c.getNombre().toLowerCase().contains(filtroNombre)) ||
                                  (c.getApellidos() != null && c.getApellidos().toLowerCase().contains(filtroNombre));
                } else { // Empresa
                    matchNombre = (c.getRazonSocial() != null && c.getRazonSocial().toLowerCase().contains(filtroNombre)) ||
                                  (c.getPersonaContacto() != null && c.getPersonaContacto().toLowerCase().contains(filtroNombre));
                }
            }

            boolean matchNifCif = filtroNifCif.isEmpty() ||
                    (c.getCifnif() != null && c.getCifnif().toLowerCase().contains(filtroNifCif));

            if (matchId && matchNombre && matchNifCif) {
                filtrados.add(c);
            }
        }
        // Actualiza la tabla con la lista filtrada.
        listaClientes.setAll(filtrados);
    }

    /**
     * Maneja el evento del botón de guardar.
     * <p>
     * Valida el formulario y luego decide si crear un nuevo cliente o actualizar
     * uno existente basándose en la variable {@code modoEdicion}.
     */
    @FXML
    public void guardarCliente() {
        String tipo = tipoClienteComboBox.getValue();
        if (!esFormularioValido(tipo))
            return;

        try {
            Cliente cliente;
            if (modoEdicion) {
                // --- Lógica de Actualización ---
                cliente = clienteAEditar;
                cliente.setEmail(emailClienteField.getText());
                cliente.setTelefono(telefonoClienteField.getText());
                cliente.setDireccion(direccionClienteField.getText());
                if ("Particular".equals(tipo)) {
                    cliente.setNombre(nombreClienteField.getText());
                    cliente.setApellidos(apellidosClienteField.getText());
                    cliente.setCifnif(nifClienteField.getText());
                } else {
                    cliente.setRazonSocial(razonSocialClienteField.getText());
                    cliente.setCifnif(cifClienteField.getText());
                    cliente.setPersonaContacto(personaContactoClienteField.getText());
                }

                if (clienteDAO.actualizarClienteEnDb(cliente)) {
                    tablaCliente.refresh();
                    mostrarAlertaTemporal("Éxito", "Cliente actualizado correctamente.");
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error de Base de Datos", "No se pudo actualizar el cliente.");
                }

            } else {
                // --- Lógica de Creación ---
                if ("Particular".equals(tipo)) {
                    cliente = Cliente.crearParticular(0, emailClienteField.getText(), telefonoClienteField.getText(),
                            direccionClienteField.getText(), nifClienteField.getText(), LocalDate.now(),
                            nombreClienteField.getText(),
                            apellidosClienteField.getText());
                } else { // Empresa
                    cliente = Cliente.crearEmpresa(0, emailClienteField.getText(), telefonoClienteField.getText(),
                            direccionClienteField.getText(), cifClienteField.getText(), LocalDate.now(),
                            razonSocialClienteField.getText(), personaContactoClienteField.getText());
                }

                if (clienteDAO.guardarClienteDb(cliente)) {
                    listaClientesOriginal.add(cliente);
                    listaClientes.add(cliente);
                    mostrarAlertaTemporal("Éxito", "Cliente añadido correctamente.");
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error de Base de Datos", "No se pudo guardar el cliente.");
                }
            }
            limpiarFormulario();
            zonaFormulariosCliente.setVisible(false);
            zonaFormulariosCliente.setManaged(false);

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error Inesperado", "Ocurrió un error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Valida los campos del formulario antes de guardar.
     * Comprueba campos obligatorios y formatos (NIF/CIF, email, teléfono).
     * @param tipo El tipo de cliente ("Particular" o "Empresa") para validar los campos correctos.
     * @return {@code true} si el formulario es válido, {@code false} en caso contrario.
     */
    private boolean esFormularioValido(String tipo) {
        if ("Particular".equals(tipo)) {
            if (nombreClienteField.getText().isEmpty() || apellidosClienteField.getText().isEmpty()) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Validación",
                        "Nombre y Apellidos son obligatorios para un particular.");
                return false;
            }
            if (nifClienteField.getText() != null && !nifClienteField.getText().isEmpty()
                    && !ValidationUtils.isValidNifCif(nifClienteField.getText())) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Validación", "El formato del NIF no es válido.");
                return false;
            }
        } else if ("Empresa".equals(tipo)) {
            if (razonSocialClienteField.getText().isEmpty() || cifClienteField.getText().isEmpty()) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Validación",
                        "Razón Social y CIF son obligatorios para una empresa.");
                return false;
            }
            if (!ValidationUtils.isValidNifCif(cifClienteField.getText())) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Validación", "El formato del CIF no es válido.");
                return false;
            }
        }

        if (!ValidationUtils.isValidEmail(emailClienteField.getText())) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Validación", "El formato del email no es válido.");
            return false;
        }
        if (!ValidationUtils.isValidTlf(telefonoClienteField.getText())) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Validación",
                    "El formato del teléfono no es válido. Debe ser un número español válido (9 dígitos, empezando por 6, 7, 8 o 9).");
            return false;
        }

        return true;
    }

    /**
     * Prepara el formulario para modificar el cliente seleccionado en la tabla.
     * <p>
     * Rellena los campos con los datos del cliente, activa el modo de edición,
     * y muestra el formulario correspondiente.
     */
    @FXML
    public void modificarClienteSeleccionado() {
        clienteAEditar = tablaCliente.getSelectionModel().getSelectedItem();
        if (clienteAEditar != null) {
            modoEdicion = true;
            tituloFormularioCliente.setText("Modificar Cliente");
            guardarClienteButton.setText("Guardar Cambios");

            zonaFormulariosCliente.setVisible(true);
            zonaFormulariosCliente.setManaged(true);

            formularioBuscarCliente.setVisible(false);
            formularioBuscarCliente.setManaged(false);

            formularioAñadirCliente.setVisible(true);
            formularioAñadirCliente.setManaged(true);

            tipoClienteComboBox.setValue(clienteAEditar.getTipoCliente());
            emailClienteField.setText(clienteAEditar.getEmail());
            telefonoClienteField.setText(clienteAEditar.getTelefono());
            direccionClienteField.setText(clienteAEditar.getDireccion());

            if ("Particular".equals(clienteAEditar.getTipoCliente())) {
                nombreClienteField.setText(clienteAEditar.getNombre());
                apellidosClienteField.setText(clienteAEditar.getApellidos());
                nifClienteField.setText(clienteAEditar.getCifnif());
            } else { // Empresa
                razonSocialClienteField.setText(clienteAEditar.getRazonSocial());
                cifClienteField.setText(clienteAEditar.getCifnif());
                personaContactoClienteField.setText(clienteAEditar.getPersonaContacto());
            }

            activarEnterEnFormularioAñadir();

        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin Selección",
                    "Debes seleccionar un cliente de la tabla para modificarlo.");
        }
    }

    /**
     * Elimina el cliente seleccionado de la tabla.
     * <p>
     * Pide confirmación al usuario antes de proceder con la eliminación en la base
     * de datos y la actualización de la UI.
     */
    @FXML
    public void eliminarClienteSeleccionado() {
        Cliente clienteAEliminar = tablaCliente.getSelectionModel().getSelectedItem();
        if (clienteAEliminar == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin Selección", "Por favor, selecciona un cliente para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Estás seguro de que quieres eliminar a este cliente?");
        String nombreCompleto = "Particular".equals(clienteAEliminar.getTipoCliente())
                ? clienteAEliminar.getNombre() + " " + clienteAEliminar.getApellidos()
                : clienteAEliminar.getRazonSocial();
        confirmacion.setContentText(nombreCompleto);

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            if (clienteDAO.eliminarClientePorId(clienteAEliminar.getId())) {
                listaClientes.remove(clienteAEliminar);
                listaClientesOriginal.remove(clienteAEliminar);
                mostrarAlertaTemporal("Éxito", "Cliente eliminado correctamente.");
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo eliminar el cliente de la base de datos.");
            }
        }
    }

    /**
     * Limpia todos los campos de texto del formulario de cliente.
     */
    private void limpiarFormulario() {
        nombreClienteField.clear();
        apellidosClienteField.clear();
        nifClienteField.clear();
        razonSocialClienteField.clear();
        cifClienteField.clear();
        personaContactoClienteField.clear();
        direccionClienteField.clear();
        telefonoClienteField.clear();
        emailClienteField.clear();
        tipoClienteComboBox.getSelectionModel().selectFirst();
    }

    /**
     * Muestra una ventana de alerta estándar.
     * @param tipo El tipo de alerta (ERROR, WARNING, INFORMATION, etc.).
     * @param titulo El título de la ventana de alerta.
     * @param mensaje El mensaje de contenido de la alerta.
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra una alerta de información que se cierra automáticamente tras un breve período.
     * @param titulo El título de la ventana de alerta.
     * @param mensaje El mensaje de contenido de la alerta.
     */
    private void mostrarAlertaTemporal(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.show();

        javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1));
        delay.setOnFinished(event -> alert.close());
        delay.play();
    }

    /**
     * Asigna un manejador de eventos de teclado a los campos del formulario de añadir/modificar.
     * Al presionar la tecla ENTER, se invoca el método {@link #guardarCliente()}.
     */
    private void activarEnterEnFormularioAñadir() {
        TextField[] campos = {
                nombreClienteField, apellidosClienteField, nifClienteField,
                razonSocialClienteField, cifClienteField, personaContactoClienteField,
                direccionClienteField, telefonoClienteField, emailClienteField
        };
        for (TextField campo : campos) {
            campo.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    guardarCliente();
                }
            });
        }
    }

    /**
     * Asigna un manejador de eventos de teclado a los campos del formulario de búsqueda.
     * Al presionar la tecla ENTER, se invoca el método {@link #filtrarClientes()}.
     */
    private void activarEnterEnFormularioBuscar() {
        TextField[] camposBusqueda = { buscarIdClienteField, buscarNombreClienteField, buscarCifApellidosClienteField };
        for (TextField campo : camposBusqueda) {
            campo.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    filtrarClientes();
                }
            });
        }
    }

    /**
     * Navega a la vista de descuentos para el cliente actualmente seleccionado en la tabla.
     * Llama a un método en el {@link MainController} para realizar el cambio de vista.
     */
    @FXML
    private void verDescuentosCliente() {
        Cliente clienteSeleccionado = tablaCliente.getSelectionModel().getSelectedItem();
        if (clienteSeleccionado != null) {
            if (mainController != null) {
                mainController.mostrarDescuentos(clienteSeleccionado);
            } else {
                System.err.println("Error: MainController no está disponible en ClienteController.");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error de Carga");
                alert.setHeaderText(null);
                alert.setContentText("No se pudo comunicar con el controlador principal.");
                alert.showAndWait();
            }
        }
    }
}