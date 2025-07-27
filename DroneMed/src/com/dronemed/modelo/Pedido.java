package com.dronemed.modelo;

import java.time.LocalDateTime;

public class Pedido {
    private int id;
    private int clienteId;
    private String medicamento;
    private String descripcion;
    private double peso;
    private String direccionEntrega;
    private double latitud;
    private double longitud;
    private String estado;
    private String prioridad;
    private LocalDateTime fechaPedido;
    private LocalDateTime fechaEntrega;
    private int dronAsignado;
    private double costo;
    
    // Constructor vacío
    public Pedido() {
        this.fechaPedido = LocalDateTime.now();
        this.estado = "PENDIENTE";
        this.prioridad = "NORMAL";
    }
    
    // Constructor con parámetros
    public Pedido(int clienteId, String medicamento, String descripcion, double peso, 
                  String direccionEntrega, double latitud, double longitud) {
        this();
        this.clienteId = clienteId;
        this.medicamento = medicamento;
        this.descripcion = descripcion;
        this.peso = peso;
        this.direccionEntrega = direccionEntrega;
        this.latitud = latitud;
        this.longitud = longitud;
    }
    
    // Getters y Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getClienteId() {
        return clienteId;
    }
    
    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }
    
    public String getMedicamento() {
        return medicamento;
    }
    
    public void setMedicamento(String medicamento) {
        this.medicamento = medicamento;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public double getPeso() {
        return peso;
    }
    
    public void setPeso(double peso) {
        this.peso = peso;
    }
    
    public String getDireccionEntrega() {
        return direccionEntrega;
    }
    
    public void setDireccionEntrega(String direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
    }
    
    public double getLatitud() {
        return latitud;
    }
    
    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }
    
    public double getLongitud() {
        return longitud;
    }
    
    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public String getPrioridad() {
        return prioridad;
    }
    
    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }
    
    public LocalDateTime getFechaPedido() {
        return fechaPedido;
    }
    
    public void setFechaPedido(LocalDateTime fechaPedido) {
        this.fechaPedido = fechaPedido;
    }
    
    public LocalDateTime getFechaEntrega() {
        return fechaEntrega;
    }
    
    public void setFechaEntrega(LocalDateTime fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }
    
    public int getDronAsignado() {
        return dronAsignado;
    }
    
    public void setDronAsignado(int dronAsignado) {
        this.dronAsignado = dronAsignado;
    }
    
    public double getCosto() {
        return costo;
    }
    
    public void setCosto(double costo) {
        this.costo = costo;
    }
    
    // Métodos adicionales
    public boolean esPedidoUrgente() {
        return "URGENTE".equals(prioridad);
    }
    
    public boolean estaPendiente() {
        return "PENDIENTE".equals(estado);
    }
    
    public boolean estaEntregado() {
        return "ENTREGADO".equals(estado);
    }
    
    public void marcarComoEntregado() {
        this.estado = "ENTREGADO";
        this.fechaEntrega = LocalDateTime.now();
    }
    
    public String getTipoCarga() {
        if (peso <= 2.0) {
            return "LIGERA";
        } else if (peso <= 5.0) {
            return "MEDIA";
        } else {
            return "PESADA";
        }
    }
    
    @Override
    public String toString() {
        return "Pedido{" +
                "id=" + id +
                ", medicamento='" + medicamento + '\'' +
                ", peso=" + peso +
                ", estado='" + estado + '\'' +
                ", prioridad='" + prioridad + '\'' +
                ", fechaPedido=" + fechaPedido +
                '}';
    }
}
