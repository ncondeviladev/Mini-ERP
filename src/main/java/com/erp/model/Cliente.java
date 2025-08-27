package com.erp.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Clase modelo que representa un Cliente en el sistema ERP.
 * Está diseñada para manejar tanto clientes particulares como empresas usando un único modelo.
 * Utiliza propiedades de JavaFX para facilitar el enlace de datos con la UI (TableView).
 * Emplea métodos de fábrica estáticos para una creación de objetos más segura y clara.
 * Autor: Noé
 */
public class Cliente {
    
    // --- Propiedades para enlace con JavaFX UI ---
    private final ObjectProperty<Integer> id;
    private final StringProperty tipoCliente; // "Particular" o "Empresa"
    
    // Campos para ambos tipos
    private final StringProperty email;
    private final StringProperty telefono;
    private final StringProperty direccion;
    private final StringProperty cifnif; // Almacena DNI para particulares o CIF para empresas
    private final ObjectProperty<LocalDate> fechaAlta;

    // Campos específicos
    private final StringProperty nombre;      // Para particulares
    private final StringProperty apellidos;   // Para particulares
    private final StringProperty razonSocial; // Para empresas
    private final StringProperty personaContacto; // Para empresas

    // Relación uno-a-muchos: Un cliente puede tener varios descuentos.
    private List<Descuento> descuentos;

    /**
     * Constructor privado y universal. Se invoca a través de los métodos de fábrica estáticos.
     * Esto asegura que los objetos Cliente se creen siempre de forma controlada.
     */
    private Cliente(Integer id, String tipoCliente, String email, String telefono, String direccion, String cifnif, LocalDate fechaAlta, String nombre, String apellidos, String razonSocial, String personaContacto) {
        this.id = new SimpleObjectProperty<>(id);
        this.tipoCliente = new SimpleStringProperty(tipoCliente);
        this.email = new SimpleStringProperty(email);
        this.telefono = new SimpleStringProperty(telefono);
        this.direccion = new SimpleStringProperty(direccion);
        this.cifnif = new SimpleStringProperty(cifnif);
        this.fechaAlta = new SimpleObjectProperty<>(fechaAlta);
        this.nombre = new SimpleStringProperty(nombre);
        this.apellidos = new SimpleStringProperty(apellidos);
        this.razonSocial = new SimpleStringProperty(razonSocial);
        this.personaContacto = new SimpleStringProperty(personaContacto);
        this.descuentos = new ArrayList<>(); // Inicializamos la lista vacía
    }

    /**
     * Constructor público sin argumentos.
     * Necesario para crear una instancia vacía desde los controladores de formulario
     * antes de poblarla con los datos de la UI.
     */
    public Cliente() {
        this.id = new SimpleObjectProperty<>();
        this.tipoCliente = new SimpleStringProperty();
        this.email = new SimpleStringProperty();
        this.telefono = new SimpleStringProperty();
        this.direccion = new SimpleStringProperty();
        this.cifnif = new SimpleStringProperty();
        this.fechaAlta = new SimpleObjectProperty<>(LocalDate.now()); // Por defecto, la fecha actual
        this.nombre = new SimpleStringProperty();
        this.apellidos = new SimpleStringProperty();
        this.razonSocial = new SimpleStringProperty();
        this.personaContacto = new SimpleStringProperty();
        this.descuentos = new ArrayList<>();
    }

    // --- MÉTODOS DE FÁBRICA ESTÁTICOS ---

    /**
     * Crea una instancia de Cliente de tipo "Particular" a partir de datos existentes (ej. desde la BD).
     * Este método es para "reconstruir" un cliente que ya tiene un ID.
     * @param id El ID del cliente existente.
     * @return Un nuevo objeto Cliente configurado como particular.
     */
    public static Cliente crearParticular(Integer id, String email, String telefono, String direccion, String nif, LocalDate fechaAlta, String nombre, String apellidos) {
        return new Cliente(id, "Particular", email, telefono, direccion, nif, fechaAlta, nombre, apellidos, null, null);
    }

    /**
     * Crea una instancia de Cliente de tipo "Particular" para un nuevo registro (ej. desde un formulario).
     * El ID se establece en null, ya que será asignado por la base de datos.
     * @return Un nuevo objeto Cliente configurado como particular, con ID nulo.
     */
    public static Cliente crearParticular(String email, String telefono, String direccion, String nif, LocalDate fechaAlta, String nombre, String apellidos) {
        return new Cliente(null, "Particular", email, telefono, direccion, nif, fechaAlta, nombre, apellidos, null, null);
    }

    /**
     * Crea una instancia de Cliente de tipo "Empresa" a partir de datos existentes (ej. desde la BD).
     * Este método es para "reconstruir" un cliente que ya tiene un ID.
     * @param id El ID del cliente existente.
     * @return Un nuevo objeto Cliente configurado como empresa.
     */
    public static Cliente crearEmpresa(Integer id, String email, String telefono, String direccion, String cif, LocalDate fechaAlta, String razonSocial, String personaContacto) {
        return new Cliente(id, "Empresa", email, telefono, direccion, cif, fechaAlta, null, null, razonSocial, personaContacto);
    }

    /**
     * Crea una instancia de Cliente de tipo "Empresa" para un nuevo registro (ej. desde un formulario).
     * El ID se establece en null, ya que será asignado por la base de datos.
     * @return Un nuevo objeto Cliente configurado como empresa, con ID nulo.
     */
    public static Cliente crearEmpresa(String email, String telefono, String direccion, String cif, LocalDate fechaAlta, String razonSocial, String personaContacto) {
        return new Cliente(null, "Empresa", email, telefono, direccion, cif, fechaAlta, null, null, razonSocial, personaContacto);
    }

    // --- Getters, Setters y Métodos de Propiedad (Property Methods) ---

    public Integer getId() { return id.get(); }
    public void setId(Integer id) { this.id.set(id); }
    public ObjectProperty<Integer> idProperty() { return id; }

    public String getTipoCliente() { return tipoCliente.get(); }
    public StringProperty tipoClienteProperty() { return tipoCliente; }
    public void setTipoCliente(String tipoCliente) { this.tipoCliente.set(tipoCliente); }

    

    public String getEmail() { return email.get(); }
    public void setEmail(String email) { this.email.set(email); }
    public StringProperty emailProperty() { return email; }

    public String getTelefono() { return telefono.get(); }
    public void setTelefono(String telefono) { this.telefono.set(telefono); }
    public StringProperty telefonoProperty() { return telefono; }

    public String getDireccion() { return direccion.get(); }
    public void setDireccion(String direccion) { this.direccion.set(direccion); }
    public StringProperty direccionProperty() { return direccion; }

    public String getCifnif() { return cifnif.get(); }
    public void setCifnif(String cifnif) { this.cifnif.set(cifnif); }
    public StringProperty cifnifProperty() { return cifnif; }

    public LocalDate getFechaAlta() { return fechaAlta.get(); }
    public void setFechaAlta(LocalDate fechaAlta) { this.fechaAlta.set(fechaAlta); }
    public ObjectProperty<LocalDate> fechaAltaProperty() { return fechaAlta; }

    public String getNombre() { return nombre.get(); }
    public void setNombre(String nombre) { this.nombre.set(nombre); }
    public StringProperty nombreProperty() { return nombre; }

    public String getApellidos() { return apellidos.get(); }
    public void setApellidos(String apellidos) { this.apellidos.set(apellidos); }
    public StringProperty apellidosProperty() { return apellidos; }

    public String getRazonSocial() { return razonSocial.get(); }
    public void setRazonSocial(String razonSocial) { this.razonSocial.set(razonSocial); }
    public StringProperty razonSocialProperty() { return razonSocial; }

    public String getPersonaContacto() { return personaContacto.get(); }
    public void setPersonaContacto(String personaContacto) { this.personaContacto.set(personaContacto); }
    public StringProperty personaContactoProperty() { return personaContacto; }

    public List<Descuento> getDescuentos() { return descuentos; }
    public void setDescuentos(List<Descuento> descuentos) { this.descuentos = descuentos; }
    public void añadirDescuento(Descuento descuento) { this.descuentos.add(descuento); }

    // --- Métodos de conveniencia ---

    public boolean isEmpresa() {
        return "Empresa".equals(this.tipoCliente.get());
    }

    // --- Propiedades Computadas para la TableView ---

    public StringProperty nombreCompletoProperty() {
        if (isEmpresa()) {
            return new SimpleStringProperty("");
        }
        return new SimpleStringProperty(nombre.get() + " " + apellidos.get());
    }

    public StringProperty razonSocialContactoProperty() {
        if (!isEmpresa()) {
            return new SimpleStringProperty("");
        }
        return new SimpleStringProperty(razonSocial.get() + " (" + personaContacto.get() + ")");
    }

    public StringProperty telefonoEmailProperty() {
        return new SimpleStringProperty(telefono.get() + " / " + email.get());
    }
}