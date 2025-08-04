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
 * Controlador para la vista de gestión de clientes.
 * <p>
 * Maneja la lógica de la interfaz de usuario para añadir, modificar, eliminar y
 * buscar clientes, así como la presentación de datos en la tabla.
 */
public class ClienteController {

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
    private TextField personaContactoClienteField; // NUEVO CAMPO
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
     * Inicializa el controlador después de que su elemento raíz haya sido
     * completamente procesado.
     * <p>
     * Configura la tabla, el ComboBox para el tipo de cliente, los listeners de
     * selección y el estado inicial de la vista.
     */
    @FXML
    public void initialize() {
        configurarComboBox();
        configurarColumnasTabla();

        // Asocia la lista observable a la tabla
        tablaCliente.setItems(listaClientes);
        // Carga los datos iniciales
        List<Cliente> clientesDesdeDB = clienteDAO.listarClientes();
        listaClientesOriginal.addAll(clientesDesdeDB);
        listaClientes.setAll(clientesDesdeDB);

        // Listener para habilitar/deshabilitar botones de acción
        tablaCliente.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean haySeleccion = newSelection != null;
            botonModificarCliente.setDisable(!haySeleccion);
            botonEliminarCliente.setDisable(!haySeleccion);
        });

        // Ocultar los formularios al inicio para que no haya un hueco en blanco
        zonaFormulariosCliente.setVisible(false);
        zonaFormulariosCliente.setManaged(false);
    }

    /**
     * Configura el ComboBox para seleccionar el tipo de cliente ("Particular" o
     * "Empresa").
     * <p>
     * Añade un listener para mostrar u ocultar los campos del formulario
     * correspondientes a la selección.
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
        // Selección por defecto
        tipoClienteComboBox.getSelectionModel().selectFirst();
    }

    /**
     * Configura las columnas de la `TableView` para mostrar los datos de los
     * clientes.
     * <p>
     * Utiliza `setCellValueFactory` para vincular cada columna con la propiedad
     * correspondiente del modelo `Cliente`.
     */
    private void configurarColumnasTabla() {
        // ID
        colIdCliente.setCellValueFactory(new PropertyValueFactory<>("id"));
        colIdCliente.setPrefWidth(40);

        // Nombre y Apellidos (solo para Particulares)
        colNombreApellidos.setCellValueFactory(cell -> { // Esta columna ahora es "Nombre y Apellidos"
            Cliente c = cell.getValue();
            if ("Particular".equals(c.getTipoCliente())) {
                return new SimpleStringProperty(c.getNombre() + "\n" + c.getApellidos());
            }
            return new SimpleStringProperty(""); // Vacío para empresas
        });
        setWrappingCellFactory(colNombreApellidos);
        colNombreApellidos.setPrefWidth(100);

        // Razón Social y Contacto (solo para Empresas)
        colRazonContacto.setCellValueFactory(cell -> { // Esta columna ahora es "Razón Social y Contacto"
            Cliente c = cell.getValue();
            if ("Empresa".equals(c.getTipoCliente())) {
                return new SimpleStringProperty(c.getRazonSocial() + "\n" + c.getPersonaContacto());
            }
            return new SimpleStringProperty(""); // Vacío para particulares
        });
        setWrappingCellFactory(colRazonContacto);
        colRazonContacto.setPrefWidth(120);

        // Teléfono y Email (común)
        colTelefonoEmail.setCellValueFactory(cell -> {
            Cliente c = cell.getValue();
            return new SimpleStringProperty(c.getTelefono() + "\n" + c.getEmail());
        });
        setWrappingCellFactory(colTelefonoEmail);
        colTelefonoEmail.setPrefWidth(120);

        // Dirección
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        setWrappingCellFactory(colDireccion);
        colDireccion.setPrefWidth(120);

        // CIF/NIF
        colCifNif.setCellValueFactory(new PropertyValueFactory<>("cifnif"));
        colCifNif.setPrefWidth(90);
    }

    /**
     * Crea una celda personalizada que permite el ajuste de texto (word wrap).
     * <p>
     * Esto es útil para columnas que pueden contener texto largo.
     * @param column La columna de la tabla a la que se aplicará el ajuste de texto.
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
     * Prepara y muestra la vista del formulario para añadir un nuevo cliente.
     * <p>
     * Restablece el modo de edición, limpia el formulario y establece los textos
     * adecuados.
     */
    @FXML
    public void mostrarVistaAñadir() {
        // Hacemos visible el contenedor de formularios
        zonaFormulariosCliente.setVisible(true);
        zonaFormulariosCliente.setManaged(true);

        // Ocultamos el otro formulario y lo sacamos del layout
        formularioBuscarCliente.setVisible(false);
        formularioBuscarCliente.setManaged(false);

        // Mostramos el formulario deseado y lo metemos en el layout
        formularioAñadirCliente.setVisible(true);
        formularioAñadirCliente.setManaged(true);

        // Configuramos el estado de la UI
        modoEdicion = false;
        clienteAEditar = null;
        tituloFormularioCliente.setText("Formulario Añadir Cliente");
        guardarClienteButton.setText("Añadir Cliente");
        limpiarFormulario();
        activarEnterEnFormularioAñadir(); // Activar Enter para guardar
    }

    /**
     * Prepara y muestra la vista con los campos de búsqueda de clientes.
     * <p>
     * Restaura la lista completa de clientes en la tabla y añade listeners a los campos de
     * búsqueda para filtrar en tiempo real.
     */
    @FXML
    public void mostrarVistaBuscar() {
        // Hacemos visible el contenedor de formularios
        zonaFormulariosCliente.setVisible(true);
        zonaFormulariosCliente.setManaged(true);

        // Ocultamos el otro formulario y lo sacamos del layout
        formularioAñadirCliente.setVisible(false);
        formularioAñadirCliente.setManaged(false);

        // Mostramos el formulario deseado y lo metemos en el layout
        formularioBuscarCliente.setVisible(true);
        formularioBuscarCliente.setManaged(true);

        // Restaura la tabla a su estado original antes de aplicar nuevos filtros
        tablaCliente.getItems().setAll(listaClientesOriginal);

        // Añade listeners a los campos de búsqueda para filtrar en tiempo real
        buscarIdClienteField.textProperty().addListener((obs, old, val) -> filtrarClientes());
        buscarNombreClienteField.textProperty().addListener((obs, old, val) -> filtrarClientes());
        buscarCifApellidosClienteField.textProperty().addListener((obs, old, val) -> filtrarClientes());
        activarEnterEnFormularioBuscar(); // Activar Enter para filtrar
    }

    /**
     * Filtra la lista de clientes en la tabla basándose en los criterios
     * introducidos en los campos de búsqueda.
     * <p>
     * La búsqueda es flexible y busca coincidencias en múltiples campos según el tipo de cliente.
     */
    private void filtrarClientes() {
        String filtroId = buscarIdClienteField.getText().trim();
        String filtroNombre = buscarNombreClienteField.getText().trim().toLowerCase(); // Busca en Nombre, Apellidos, Razón Social y Contacto
        String filtroNifCif = buscarCifApellidosClienteField.getText().trim().toLowerCase(); // Busca solo en NIF/CIF

        // Si todos los campos de búsqueda están vacíos, muestra la lista original
        // completa
        if (filtroId.isEmpty() && filtroNombre.isEmpty() && filtroNifCif.isEmpty()) {
            listaClientes.setAll(listaClientesOriginal);
            return;
        }

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
        listaClientes.setAll(filtrados);
    }

    /**
     * Gestiona el evento de clic del botón "Guardar".
     * <p>
     * Valida los datos del formulario y, según si está en modo de edición o no,
     * crea un nuevo cliente o actualiza uno existente en la base de datos.
     */
    @FXML
    public void guardarCliente() {
        String tipo = tipoClienteComboBox.getValue();
        if (!esFormularioValido(tipo))
            return;

        try {
            Cliente cliente;
            if (modoEdicion) {
                cliente = clienteAEditar;
                // Actualizar datos comunes
                cliente.setEmail(emailClienteField.getText());
                cliente.setTelefono(telefonoClienteField.getText());
                cliente.setDireccion(direccionClienteField.getText());
                // Actualizar datos específicos
                if ("Particular".equals(tipo)) {
                    cliente.setNombre(nombreClienteField.getText());
                    cliente.setApellidos(apellidosClienteField.getText());
                    cliente.setCifnif(nifClienteField.getText()); // Actualizar NIF
                } else {
                    cliente.setRazonSocial(razonSocialClienteField.getText());
                    cliente.setCifnif(cifClienteField.getText());
                    // Actualizar persona de contacto
                    cliente.setPersonaContacto(personaContactoClienteField.getText());
                }

                if (clienteDAO.actualizarClienteEnDb(cliente)) {
                    tablaCliente.refresh();
                    mostrarAlertaTemporal("Éxito", "Cliente actualizado correctamente.");
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error de Base de Datos", "No se pudo actualizar el cliente.");
                }

            } else {
                // --- Creación de nuevo cliente ---
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
            // Ocultar formulario después de guardar
            zonaFormulariosCliente.setVisible(false);
            zonaFormulariosCliente.setManaged(false);

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error Inesperado", "Ocurrió un error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Valida los campos del formulario antes de guardar un cliente.
     * <p>
     * Comprueba campos obligatorios y el formato de datos como NIF, CIF, email y
     * teléfono.
     * @param tipo El tipo de cliente ("Particular" o "Empresa").
     * @return {@code true} si todos los campos son válidos, {@code false} en caso
     *         contrario.
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

        // --- Validación de campos comunes ---
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
     * Prepara el formulario para la modificación de un cliente.
     * <p>
     * Rellena los campos del formulario con los datos del cliente seleccionado en
     * la tabla y activa el modo de edición.
     */
    @FXML
    public void modificarClienteSeleccionado() {
        clienteAEditar = tablaCliente.getSelectionModel().getSelectedItem();
        if (clienteAEditar != null) {
            modoEdicion = true;
            tituloFormularioCliente.setText("Modificar Cliente");
            guardarClienteButton.setText("Guardar Cambios");

            // Hacemos visible el formulario
            zonaFormulariosCliente.setVisible(true);
            zonaFormulariosCliente.setManaged(true);

            // Ocultamos el otro formulario
            formularioBuscarCliente.setVisible(false);
            formularioBuscarCliente.setManaged(false);
            // Mostramos el de añadir
            formularioAñadirCliente.setVisible(true);
            formularioAñadirCliente.setManaged(true);

            // Rellenar el formulario
            tipoClienteComboBox.setValue(clienteAEditar.getTipoCliente());
            emailClienteField.setText(clienteAEditar.getEmail());
            telefonoClienteField.setText(clienteAEditar.getTelefono());
            direccionClienteField.setText(clienteAEditar.getDireccion());

            if ("Particular".equals(clienteAEditar.getTipoCliente())) {
                nombreClienteField.setText(clienteAEditar.getNombre());
                apellidosClienteField.setText(clienteAEditar.getApellidos());
                nifClienteField.setText(clienteAEditar.getCifnif()); // Rellenar NIF
            } else { // Empresa
                razonSocialClienteField.setText(clienteAEditar.getRazonSocial());
                cifClienteField.setText(clienteAEditar.getCifnif());
                personaContactoClienteField.setText(clienteAEditar.getPersonaContacto()); // Rellenar Contacto
            }

            activarEnterEnFormularioAñadir(); // Activar Enter para guardar en modo edición

        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin Selección",
                    "Debes seleccionar un cliente de la tabla para modificarlo.");
        }
    }

    /**
     * Elimina el cliente seleccionado de la tabla tras una confirmación del
     * usuario.
     * <p>
     * Si la confirmación es positiva, elimina el cliente de la base de datos y de
     * las listas locales.
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
     * Limpia todos los campos de entrada del formulario de cliente, devolviéndolos
     * a su estado inicial y seleccionando "Particular" por defecto en el
     * ComboBox.
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
     * Muestra una ventana de alerta.
     *
     * @param tipo    El tipo de alerta (p. ej., `Alert.AlertType.ERROR`).
     * @param titulo  El título de la alerta.
     * @param mensaje El mensaje a mostrar.
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra una alerta temporal que se cierra automáticamente después de un segundo.
     * 
     * 
     * @param titulo  El título de la ventana de alerta.
     * @param mensaje El mensaje a mostrar.
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
     * Asigna un manejador de eventos de teclado a los campos del formulario de
     * añadir/modificar.
     * <p>
     * Al presionar la tecla ENTER, se intenta guardar el cliente.
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
     * Asigna un manejador de eventos de teclado a los campos de búsqueda.
     * <p>Al presionar la tecla ENTER, se activa el filtro de clientes.
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
}
