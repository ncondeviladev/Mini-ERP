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
 * 
 * <p>Esta clase es fundamental en el modelo de datos, ya que encapsula toda la 
 * información relativa a un cliente. Está diseñada para ser flexible y manejar 
 * dos tipos de clientes distintos a través de un único modelo de datos:
 * <ul>
 *     <li><b>Particular:</b> Un cliente individual con nombre y apellidos.</li>
 *     <li><b>Empresa:</b> Una entidad corporativa con razón social y persona de contacto.</li>
 * </ul>
 * </p>
 * 
 * <p><b>Características Principales:</b></p>
 * <ul>
 *     <li><b>Propiedades JavaFX:</b> Todos los atributos están implementados como
 *     propiedades de JavaFX ({@code StringProperty}, {@code ObjectProperty}, etc.).
 *     Esto permite un enlace de datos (data binding) directo y eficiente con los
 *     componentes de la interfaz de usuario, como las {@code TableView}, actualizando
 *     la UI automáticamente cuando los datos cambian.</li>
 *     <li><b>Patrón de Fábrica (Factory Pattern):</b> La creación de instancias se 
 *     gestiona a través de métodos de fábrica estáticos ({@code crearParticular} y 
 *     {@code crearEmpresa}). Este enfoque proporciona una API más clara y segura, 
 *     asegurando que los objetos se creen siempre en un estado consistente y válido.</li>
 *     <li><b>Inmutabilidad Parcial:</b> Las propiedades en sí son finales para evitar 
 *     que puedan ser reasignadas a nulas, aunque su contenido interno es mutable.</li>
 * </ul>
 *
 * @see com.erp.dao.ClienteDAO
 * @see com.erp.controller.ClienteController
 * @author Noé
 */
public class Cliente {
    
    // --- PROPIEDADES OBSERVABLES PARA JAVA FX ---

    /**
     * Identificador único del cliente en la base de datos. 
     * Es un entero y su propiedad permite enlazarlo a la UI.
     */
    private final ObjectProperty<Integer> id;

    /**
     * Define el tipo de cliente. Puede ser "Particular" o "Empresa".
     * Este campo es crucial para determinar qué otros campos son relevantes (nombre vs. razón social).
     */
    private final StringProperty tipoCliente;
    
    // --- CAMPOS COMUNES PARA AMBOS TIPOS DE CLIENTE ---

    /**
     * Dirección de correo electrónico del cliente.
     */
    private final StringProperty email;

    /**
     * Número de teléfono de contacto del cliente.
     */
    private final StringProperty telefono;

    /**
     * Dirección física completa del cliente (calle, número, ciudad, etc.).
     */
    private final StringProperty direccion;

    /**
     * Documento de identificación fiscal. Almacena el DNI para clientes particulares
     * y el CIF para clientes de tipo empresa.
     */
    private final StringProperty cifnif;

    /**
     * Fecha en la que el cliente fue dado de alta en el sistema.
     */
    private final ObjectProperty<LocalDate> fechaAlta;

    // --- CAMPOS ESPECÍFICOS SEGÚN EL TIPO DE CLIENTE ---

    /**
     * Nombre del cliente. Relevante solo si {@code tipoCliente} es "Particular".
     */
    private final StringProperty nombre;

    /**
     * Apellidos del cliente. Relevante solo si {@code tipoCliente} es "Particular".
     */
    private final StringProperty apellidos;

    /**
     * Razón social o nombre legal de la empresa. Relevante solo si {@code tipoCliente} es "Empresa".
     */
    private final StringProperty razonSocial;

    /**
     * Nombre de la persona de contacto dentro de la empresa. Relevante solo si {@code tipoCliente} es "Empresa".
     */
    private final StringProperty personaContacto;

    /**
     * Relación uno-a-muchos: Un cliente puede tener una lista de descuentos asociados.
     * Esta lista se carga bajo demanda y no forma parte de las propiedades de JavaFX directamente.
     */
    private List<Descuento> descuentos;

    // --- CONSTRUCTORES ---

    /**
     * Constructor privado y universal. Su acceso está restringido para forzar la creación
     * de objetos a través de los métodos de fábrica estáticos ({@code crearParticular} y {@code crearEmpresa}).
     * Este patrón de diseño asegura que cada instancia de {@code Cliente} se cree con los
     * campos correctos según su tipo, evitando estados inconsistentes (ej. un particular con razón social).
     *
     * @param id El ID del cliente, o null si es un nuevo cliente.
     * @param tipoCliente "Particular" o "Empresa".
     * @param email Correo electrónico.
     * @param telefono Teléfono de contacto.
     * @param direccion Dirección física.
     * @param cifnif CIF o NIF.
     * @param fechaAlta Fecha de alta.
     * @param nombre Nombre (para particulares).
     * @param apellidos Apellidos (para particulares).
     * @param razonSocial Razón social (para empresas).
     * @param personaContacto Persona de contacto (para empresas).
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
        this.descuentos = new ArrayList<>(); // Se inicializa la lista para evitar NullPointerException.
    }

    /**
     * Constructor público sin argumentos.
     * Este constructor es una herramienta necesaria para los formularios de la interfaz de usuario.
     * Permite crear una instancia de {@code Cliente} vacía en el controlador, que luego se va
     * poblando con los datos introducidos por el usuario en los campos del formulario antes
     * de ser persistida.
     */
    public Cliente() {
        this.id = new SimpleObjectProperty<>();
        this.tipoCliente = new SimpleStringProperty();
        this.email = new SimpleStringProperty();
        this.telefono = new SimpleStringProperty();
        this.direccion = new SimpleStringProperty();
        this.cifnif = new SimpleStringProperty();
        this.fechaAlta = new SimpleObjectProperty<>(LocalDate.now()); // Se pre-asigna la fecha actual por defecto.
        this.nombre = new SimpleStringProperty();
        this.apellidos = new SimpleStringProperty();
        this.razonSocial = new SimpleStringProperty();
        this.personaContacto = new SimpleStringProperty();
        this.descuentos = new ArrayList<>();
    }

    // --- MÉTODOS DE FÁBRICA ESTÁTICOS (FACTORY METHODS) ---

    /**
     * Crea una instancia de Cliente de tipo "Particular" a partir de datos existentes (ej. desde la BD).
     * Este método se usa para "reconstruir" un objeto Cliente que ya tiene un ID asignado.
     *
     * @param id El ID del cliente existente en la base de datos.
     * @param email El correo electrónico del particular.
     * @param telefono El teléfono del particular.
     * @param direccion La dirección del particular.
     * @param nif El NIF del particular.
     * @param fechaAlta La fecha de alta del particular.
     * @param nombre El nombre del particular.
     * @param apellidos Los apellidos del particular.
     * @return Un nuevo objeto {@code Cliente} configurado como particular.
     */
    public static Cliente crearParticular(Integer id, String email, String telefono, String direccion, String nif, LocalDate fechaAlta, String nombre, String apellidos) {
        // Llama al constructor privado, pasando null en los campos de empresa.
        return new Cliente(id, "Particular", email, telefono, direccion, nif, fechaAlta, nombre, apellidos, null, null);
    }

    /**
     * Crea una instancia de Cliente de tipo "Particular" para un nuevo registro (ej. desde un formulario).
     * El ID se establece en {@code null}, ya que será la base de datos quien lo asigne al persistir.
     *
     * @param email El correo electrónico del nuevo particular.
     * @param telefono El teléfono del nuevo particular.
     * @param direccion La dirección del nuevo particular.
     * @param nif El NIF del nuevo particular.
     * @param fechaAlta La fecha de alta del nuevo particular.
     * @param nombre El nombre del nuevo particular.
     * @param apellidos Los apellidos del nuevo particular.
     * @return Un nuevo objeto {@code Cliente} configurado como particular y listo para ser guardado.
     */
    public static Cliente crearParticular(String email, String telefono, String direccion, String nif, LocalDate fechaAlta, String nombre, String apellidos) {
        return new Cliente(null, "Particular", email, telefono, direccion, nif, fechaAlta, nombre, apellidos, null, null);
    }

    /**
     * Crea una instancia de Cliente de tipo "Empresa" a partir de datos existentes (ej. desde la BD).
     * Este método se usa para "reconstruir" un objeto Cliente que ya tiene un ID asignado.
     *
     * @param id El ID del cliente existente en la base de datos.
     * @param email El correo electrónico de la empresa.
     * @param telefono El teléfono de la empresa.
     * @param direccion La dirección de la empresa.
     * @param cif El CIF de la empresa.
     * @param fechaAlta La fecha de alta de la empresa.
     * @param razonSocial El nombre legal de la empresa.
     * @param personaContacto La persona de contacto en la empresa.
     * @return Un nuevo objeto {@code Cliente} configurado como empresa.
     */
    public static Cliente crearEmpresa(Integer id, String email, String telefono, String direccion, String cif, LocalDate fechaAlta, String razonSocial, String personaContacto) {
        // Llama al constructor privado, pasando null en los campos de particular.
        return new Cliente(id, "Empresa", email, telefono, direccion, cif, fechaAlta, null, null, razonSocial, personaContacto);
    }

    /**
     * Crea una instancia de Cliente de tipo "Empresa" para un nuevo registro (ej. desde un formulario).
     * El ID se establece en {@code null}, ya que será la base de datos quien lo asigne al persistir.
     *
     * @param email El correo electrónico de la nueva empresa.
     * @param telefono El teléfono de la nueva empresa.
     * @param direccion La dirección de la nueva empresa.
     * @param cif El CIF de la nueva empresa.
     * @param fechaAlta La fecha de alta de la nueva empresa.
     * @param razonSocial El nombre legal de la nueva empresa.
     * @param personaContacto La persona de contacto en la nueva empresa.
     * @return Un nuevo objeto {@code Cliente} configurado como empresa y listo para ser guardado.
     */
    public static Cliente crearEmpresa(String email, String telefono, String direccion, String cif, LocalDate fechaAlta, String razonSocial, String personaContacto) {
        return new Cliente(null, "Empresa", email, telefono, direccion, cif, fechaAlta, null, null, razonSocial, personaContacto);
    }

    // --- GETTERS, SETTERS Y MÉTODOS DE PROPIEDAD (PROPERTY METHODS) ---
    // Para cada atributo, se proporciona:
    // 1. Un getter estándar (ej. getId()).
    // 2. Un setter estándar (ej. setId()).
    // 3. Un método de propiedad (ej. idProperty()) que devuelve la propiedad JavaFX.
    //    Este último es esencial para el data binding en la interfaz de usuario.

    public Integer getId() { return id.get(); }
    public void setId(Integer id) { this.id.set(id); }
    public ObjectProperty<Integer> idProperty() { return id; }

    public String getTipoCliente() { return tipoCliente.get(); }
    public void setTipoCliente(String tipoCliente) { this.tipoCliente.set(tipoCliente); }
    public StringProperty tipoClienteProperty() { return tipoCliente; }

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

    /**
     * Obtiene la lista de descuentos asociados a este cliente.
     * @return Una {@code List} de objetos {@code Descuento}.
     */
    public List<Descuento> getDescuentos() { return descuentos; }

    /**
     * Establece la lista de descuentos para este cliente.
     * @param descuentos La nueva {@code List} de objetos {@code Descuento}.
     */
    public void setDescuentos(List<Descuento> descuentos) { this.descuentos = descuentos; }

    /**
     * Añade un descuento individual a la lista de descuentos del cliente.
     * @param descuento El objeto {@code Descuento} a añadir.
     */
    public void añadirDescuento(Descuento descuento) { this.descuentos.add(descuento); }

    // --- MÉTODOS DE CONVENIENCIA ---

    /**
     * Comprueba si el cliente es de tipo "Empresa".
     * Es un método de ayuda para evitar comparaciones de strings en otras partes del código.
     * @return {@code true} si el tipo de cliente es "Empresa", {@code false} en caso contrario.
     */
    public boolean isEmpresa() {
        return "Empresa".equals(this.tipoCliente.get());
    }

    // --- PROPIEDADES COMPUTADAS PARA LA TABLEVIEW ---
    // Estos métodos no devuelven un valor simple, sino una propiedad completa.
    // Son muy útiles para las columnas de las TableView que necesitan mostrar
    // datos combinados de varios atributos del modelo.

    /**
     * Devuelve una propiedad que contiene el nombre y los apellidos concatenados.
     * Si el cliente es una empresa, devuelve una propiedad con un string vacío.
     * @return Una {@code StringProperty} con el nombre completo, ideal para enlazar a una columna.
     */
    public StringProperty nombreCompletoProperty() {
        if (isEmpresa()) {
            return new SimpleStringProperty("");
        }
        return new SimpleStringProperty(nombre.get() + " " + apellidos.get());
    }

    /**
     * Devuelve una propiedad que contiene la razón social y la persona de contacto.
     * Si el cliente es un particular, devuelve una propiedad con un string vacío.
     * @return Una {@code StringProperty} con la razón social y contacto, ideal para enlazar a una columna.
     */
    public StringProperty razonSocialContactoProperty() {
        if (!isEmpresa()) {
            return new SimpleStringProperty("");
        }
        return new SimpleStringProperty(razonSocial.get() + " (" + personaContacto.get() + ")");
    }

    /**
     * Devuelve una propiedad que contiene el teléfono y el email concatenados.
     * @return Una {@code StringProperty} con el teléfono y el email, ideal para enlazar a una columna.
     */
    public StringProperty telefonoEmailProperty() {
        return new SimpleStringProperty(telefono.get() + " / " + email.get());
    }
}
