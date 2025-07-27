package com.dronemed.modelo;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.List;
import java.util.ArrayList;

//Clase que representa un cliente del sistema DroneMed
//puede ser un hospital, clinica o farmacia
public class Cliente {

    private int id;
    private String nombre;
    private String tipo;
    private String direccion;
    private String telefono;
    private String email;
    private String contactoPrincipal;
    private boolean activo;
    private LocalDateTime fechaRegistro;
    private List<String> serviciosRequeridos;

    //Constructor por defecto
    public Cliente() {
        this.activo = true;
        this.fechaRegistro = LocalDateTime.now();
        this.serviciosRequeridos = new ArrayList<>();
    }

    //constructor completo
    public Cliente(int id, String nombre, String tipo, String direccion, String telefono, String email, String contactoPrincipal) {
        this();
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
        this.contactoPrincipal = contactoPrincipal;
    }

    //metodo de negocio
    public boolean esClienteVIP() {
        return serviciosRequeridos.size() > 5 || "HOSPITAL".equals(tipo);
    }

    public double calcularDescuento() {
        if (esClienteVIP()) {
            return 0.15; // 15% de descuento para VIP
        } else if ("HOSPITAL".equals(tipo)) {
            return 0.10; // 10% para hospitales
        }
        return 0.0; // sin descuento
    }

    public boolean validarEmail() {
        return email != null && email.contains("@") && email.contains(".");
    }

    public void agregarServicio(String servicio) {
        if (!serviciosRequeridos.contains(servicio)) {
            serviciosRequeridos.add(servicio);
        }
    }

    public void removerServicio(String servicio) {
        serviciosRequeridos.remove(servicio);
    }

    public boolean requiereServicio(String servicio) {
        return serviciosRequeridos.contains(servicio);
    }

    public boolean esHospital() {
        return "HOSPITAL".equals(tipo);
    }

    public boolean esClinica() {
        return "CLINICA".equals(tipo);
    }

    public boolean esFarmacia() {
        return "FARMACIA".equals(tipo);
    }

    //getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactoPrincipal() {
        return contactoPrincipal;
    }

    public void setContactoPrincipal(String contactoPrincipal) {
        this.contactoPrincipal = contactoPrincipal;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public List<String> getServiciosRequeridos() {
        return new ArrayList<>(serviciosRequeridos);
    }

    public void setServiciosRequeridos(List<String> serviciosRequeridos) {
        this.serviciosRequeridos = new ArrayList<>(serviciosRequeridos);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Cliente cliente = (Cliente) o;
        return id == cliente.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Cliente{id=%d, nombre='%s', tipo='%s', direccion='%s', activo=%s}",
                id, nombre, tipo, direccion, activo);
    }
}
