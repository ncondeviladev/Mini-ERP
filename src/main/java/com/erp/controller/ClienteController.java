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
    private Button botonDescuentoCliente; // Botón añadido

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

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize() {
        configurarComboBox();
        configurarColumnasTabla();

        tablaCliente.setItems(listaClientes);
        List<Cliente> clientesDesdeDB = clienteDAO.listarClientes();
        listaClientesOriginal.addAll(clientesDesdeDB);
        listaClientes.setAll(clientesDesdeDB);

        tablaCliente.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean haySeleccion = newSelection != null;
            botonModificarCliente.setDisable(!haySeleccion);
            botonEliminarCliente.setDisable(!haySeleccion);
            botonDescuentoCliente.setDisable(!haySeleccion); // Lógica añadida
        });

        zonaFormulariosCliente.setVisible(false);
        zonaFormulariosCliente.setManaged(false);
    }

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

    private void configurarColumnasTabla() {
        colIdCliente.setCellValueFactory(new PropertyValueFactory<>("id"));
        colIdCliente.setPrefWidth(40);

        colNombreApellidos.setCellValueFactory(cell -> {
            Cliente c = cell.getValue();
            if ("Particular".equals(c.getTipoCliente())) {
                return new SimpleStringProperty(c.getNombre() + "\n" + c.getApellidos());
            }
            return new SimpleStringProperty("");
        });
        setWrappingCellFactory(colNombreApellidos);
        colNombreApellidos.setPrefWidth(100);

        colRazonContacto.setCellValueFactory(cell -> {
            Cliente c = cell.getValue();
            if ("Empresa".equals(c.getTipoCliente())) {
                return new SimpleStringProperty(c.getRazonSocial() + "\n" + c.getPersonaContacto());
            }
            return new SimpleStringProperty("");
        });
        setWrappingCellFactory(colRazonContacto);
        colRazonContacto.setPrefWidth(120);

        colTelefonoEmail.setCellValueFactory(cell -> {
            Cliente c = cell.getValue();
            return new SimpleStringProperty(c.getTelefono() + "\n" + c.getEmail());
        });
        setWrappingCellFactory(colTelefonoEmail);
        colTelefonoEmail.setPrefWidth(120);

        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        setWrappingCellFactory(colDireccion);
        colDireccion.setPrefWidth(120);

        colCifNif.setCellValueFactory(new PropertyValueFactory<>("cifnif"));
        colCifNif.setPrefWidth(90);
    }

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

    private void filtrarClientes() {
        String filtroId = buscarIdClienteField.getText().trim();
        String filtroNombre = buscarNombreClienteField.getText().trim().toLowerCase();
        String filtroNifCif = buscarCifApellidosClienteField.getText().trim().toLowerCase();

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

    @FXML
    public void guardarCliente() {
        String tipo = tipoClienteComboBox.getValue();
        if (!esFormularioValido(tipo))
            return;

        try {
            Cliente cliente;
            if (modoEdicion) {
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

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

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

    @FXML
    private void verDescuentosCliente(){
        if (mainController != null) {
            mainController.mostrarDescuentos();
        }
    }
}