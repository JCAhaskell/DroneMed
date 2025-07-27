package com.dronemed.modelo;

import java.time.LocalDateTime;

public class Mantenimiento {
    private int id;
    private String dronId;
    private String tipoMantenimiento;
    private String descripcion;
    private String estado;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String tecnicoResponsable;
    private double costo;
    private String observaciones;
    private int horasVueloAntes;
    private int horasVueloDespues;
    
    // Constructor vacío
    public Mantenimiento() {
        this.fechaInicio = LocalDateTime.now();
        this.estado = "PROGRAMADO";
    }
    
    // Constructor con parámetros
    public Mantenimiento(String dronId, String tipoMantenimiento, String descripcion, String tecnicoResponsable) {
        this();
        this.dronId = dronId;
        this.tipoMantenimiento = tipoMantenimiento;
        this.descripcion = descripcion;
        this.tecnicoResponsable = tecnicoResponsable;
    }
    
    // Getters y Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getDronId() {
        return dronId;
    }
    
    public void setDronId(String dronId) {
        this.dronId = dronId;
    }
    
    public String getTipoMantenimiento() {
        return tipoMantenimiento;
    }
    
    public void setTipoMantenimiento(String tipoMantenimiento) {
        this.tipoMantenimiento = tipoMantenimiento;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }
    
    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }
    
    public LocalDateTime getFechaFin() {
        return fechaFin;
    }
    
    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }
    
    public String getTecnicoResponsable() {
        return tecnicoResponsable;
    }
    
    public void setTecnicoResponsable(String tecnicoResponsable) {
        this.tecnicoResponsable = tecnicoResponsable;
    }
    
    public double getCosto() {
        return costo;
    }
    
    public void setCosto(double costo) {
        this.costo = costo;
    }
    
    public String getObservaciones() {
        return observaciones;
    }
    
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    public int getHorasVueloAntes() {
        return horasVueloAntes;
    }
    
    public void setHorasVueloAntes(int horasVueloAntes) {
        this.horasVueloAntes = horasVueloAntes;
    }
    
    public int getHorasVueloDespues() {
        return horasVueloDespues;
    }
    
    public void setHorasVueloDespues(int horasVueloDespues) {
        this.horasVueloDespues = horasVueloDespues;
    }
    
    // Métodos adicionales
    public boolean estaCompletado() {
        return "COMPLETADO".equals(estado);
    }
    
    public boolean estaEnProceso() {
        return "EN_PROCESO".equals(estado);
    }
    
    public void iniciarMantenimiento() {
        this.estado = "EN_PROCESO";
        this.fechaInicio = LocalDateTime.now();
    }
    
    public void completarMantenimiento() {
        this.estado = "COMPLETADO";
        this.fechaFin = LocalDateTime.now();
    }
    
    public long getDuracionHoras() {
        if (fechaInicio != null && fechaFin != null) {
            return java.time.Duration.between(fechaInicio, fechaFin).toHours();
        }
        return 0;
    }
    
    public boolean esMantenimientoPreventivo() {
        return "PREVENTIVO".equals(tipoMantenimiento);
    }
    
    public boolean esMantenimientoCorrectivo() {
        return "CORRECTIVO".equals(tipoMantenimiento);
    }
    
    @Override
    public String toString() {
        return "Mantenimiento{" +
                "id=" + id +
                ", dronId=" + dronId +
                ", tipoMantenimiento='" + tipoMantenimiento + '\'' +
                ", estado='" + estado + '\'' +
                ", fechaInicio=" + fechaInicio +
                ", tecnicoResponsable='" + tecnicoResponsable + '\'' +
                '}';
    }
}
